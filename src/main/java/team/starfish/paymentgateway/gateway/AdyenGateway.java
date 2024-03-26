package team.starfish.paymentgateway.gateway;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import team.starfish.paymentgateway.config.EnvVariableConfigs;
import team.starfish.paymentgateway.constant.FundingSourceEnum;
import team.starfish.paymentgateway.constant.GlobalConstants;
import team.starfish.paymentgateway.constant.TransactionStatusEnum;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.dto.payment.PaymentQueryDto;
import team.starfish.paymentgateway.dto.payment.PaymentResultDto;
import team.starfish.paymentgateway.error.ExternalApiException;
import team.starfish.paymentgateway.error.InitializationException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdyenGateway {
    @Resource
    private Gson gson;

    private final EnvVariableConfigs envVariableConfigs;
    private final PaymentsApi paymentsApi;


    public AdyenGateway(EnvVariableConfigs envVariableConfigs) {
        this.envVariableConfigs = envVariableConfigs;

        if (Objects.isNull(envVariableConfigs.getAdyenApiKey())) {
            log.error("ADYEN_API_KEY is null.");
            throw new InitializationException("ADYEN_API_KEY is null.");
        }

        Environment adyenEnv = Objects.equals(envVariableConfigs.getSpringProfileActive(), GlobalConstants.ENV_PROD) ?
                Environment.LIVE : Environment.TEST;
        Client client = new Client(envVariableConfigs.getAdyenApiKey(), adyenEnv);
        this.paymentsApi = new PaymentsApi(client);
    }


    /**
     * Get payment methods based on merchant account
     *
     * @param fundingSources used to filter the funding source
     * @return available payment types in default merchant
     */
    @Retryable(value = IOException.class, backoff = @Backoff(delay = 2000))
    public List<String> getMerchantAvailableBrands(List<FundingSourceEnum> fundingSources)
            throws ExternalApiException, IOException {
        PaymentMethodsRequest request = new PaymentMethodsRequest()
                .merchantAccount(envVariableConfigs.getAdyenMerchantAccount())
                .splitCardFundingSources(true);

        try {
            PaymentMethodsResponse response = paymentsApi.paymentMethods(request);
            List<PaymentMethod> paymentMethods = ListUtils.emptyIfNull(response.getPaymentMethods());
            log.info("paymentsApi.paymentMethods request:\n{}\n response:\n{}\n", request, response);

            Set<String> fundingSourceSet = fundingSources.stream()
                    .map(FundingSourceEnum::getValue)
                    .collect(Collectors.toSet());

            // filter funding source
            // reduce available brands
            return paymentMethods.stream()
                    .filter(pm -> Objects.nonNull(pm.getFundingSource()))
                    .filter(pm -> fundingSourceSet.contains(pm.getFundingSource().getValue()))
                    .map(PaymentMethod::getBrands)
                    .filter(CollectionUtils::isNotEmpty)
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());

        } catch (ApiException ex) {
            log.warn("getMerchantAvailableBrands ApiException", ex);
            throw new ExternalApiException(ex.getMessage(), ex);
        } catch (IOException ex) {
            log.warn("getMerchantAvailableBrands IOException", ex);
            throw ex;
        }
    }


    /**
     * Check the available payment methods for a specific card
     *
     * @param cardNumber    card number to be checked
     * @param limitedBrands used to limit supported brands
     * @return available card payment types for default merchant
     */
    @Retryable(value = IOException.class, backoff = @Backoff(delay = 2000))
    public List<String> getCardAvailableBrands(String cardNumber, List<String> limitedBrands)
            throws ExternalApiException, IOException {
        CardDetailsRequest request = new CardDetailsRequest()
                .merchantAccount(envVariableConfigs.getAdyenMerchantAccount())
                .cardNumber(cardNumber);

        if (CollectionUtils.isNotEmpty(limitedBrands)) {
            request.setSupportedBrands(limitedBrands);
        }

        try {
            CardDetailsResponse response = paymentsApi.cardDetails(request);
            List<CardBrandDetails> brandDetails = ListUtils.emptyIfNull(response.getBrands());
            log.info("paymentsApi.paymentMethods request:\n{}\n response:\n{}\n", request, response);

            // return the brands after filtering supported
            return brandDetails.stream()
                    .filter(CardBrandDetails::getSupported)
                    .map(CardBrandDetails::getType)
                    .distinct()
                    .collect(Collectors.toList());

        } catch (ApiException ex) {
            log.warn("getMerchantAvailableBrands ApiException", ex);
            throw new ExternalApiException(ex.getMessage(), ex);
        } catch (IOException ex) {
            log.warn("getCardAvailableBrands IOException", ex);
            throw ex;
        }
    }


    /**
     * Payment API
     *
     * @param paymentQuery customised query with card information and transaction ref
     * @return transaction result with psp reference
     */
    @Retryable(value = IOException.class, backoff = @Backoff(delay = 2000))
    public PaymentResultDto paymentByCard(PaymentQueryDto paymentQuery)
            throws ExternalApiException, IOException {
        PaymentRequest request = getPaymentRequest(paymentQuery);

        try {
            PaymentResponse response = paymentsApi.payments(request);
            log.info("paymentsApi.paymentMethods request:\n{}\n response:\n{}\n", request, response);

            PaymentResultDto paymentResult = new PaymentResultDto();
            paymentResult.setPaymentQuery(paymentQuery);
            // ease the result code: only AUTHORIZED presents success
            paymentResult.setStatus(Objects.equals(response.getResultCode(), PaymentResponse.ResultCodeEnum.AUTHORISED)
                    ? TransactionStatusEnum.AUTHORIZED : TransactionStatusEnum.DECLINED);
            paymentResult.setPspReference(response.getPspReference());
            paymentResult.setPayload(gson.toJson(response));

            return paymentResult;
        } catch (ApiException ex) {
            log.warn("getMerchantAvailableBrands ApiException", ex);
            throw new ExternalApiException(ex.getMessage(), ex);
        } catch (IOException ex) {
            log.warn("getCardAvailableBrands IOException", ex);
            throw ex;
        }
    }

    private PaymentRequest getPaymentRequest(PaymentQueryDto paymentQuery) {
        Amount amount = new Amount()
                .currency(paymentQuery.getCurrency().getValue())
                .value(paymentQuery.getValue());

        CardDto card = paymentQuery.getCard();
        CardDetails cardDetails = new CardDetails()
                .type(CardDetails.TypeEnum.fromValue(paymentQuery.getPaymentType().getValue()))
                .number(card.getNumber())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .cvc(card.getCvc())
                .holderName(card.getHolderName());
        CheckoutPaymentMethod paymentMethod = new CheckoutPaymentMethod(cardDetails);

        return new PaymentRequest()
                .amount(amount)
                .paymentMethod(paymentMethod)
                .merchantAccount(envVariableConfigs.getAdyenMerchantAccount())
                .reference(paymentQuery.getReference());
    }
}

package team.starfish.paymentgateway.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import team.starfish.paymentgateway.constant.FundingSourceEnum;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.error.ExternalApiException;
import team.starfish.paymentgateway.gateway.AdyenGateway;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class CardValidationService {

    @Resource
    private AdyenGateway adyenGateway;


    /**
     * Check availability of payment methods
     *
     * @param cardNumber     card to check
     * @param fundingSources credit, debit
     * @return available brands
     */
    public List<String> getValidatedCardBrands(String cardNumber,
                                               List<FundingSourceEnum> fundingSources)
            throws ExternalApiException, IOException {
        List<String> merchantBrands = adyenGateway.getMerchantAvailableBrands(fundingSources);
        return adyenGateway.getCardAvailableBrands(cardNumber, merchantBrands);
    }

    public boolean isExpired(CardDto cardDto) {
        LocalDate currentDate = LocalDate.now();
        YearMonth expiryYearMonth = YearMonth.of(Integer.parseInt(cardDto.getExpiryYear()),
                Integer.parseInt(cardDto.getExpiryMonth()));
        LocalDate expiryDate = expiryYearMonth.atEndOfMonth();
        return currentDate.isAfter(expiryDate);
    }

}

package team.starfish.paymentgateway.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.starfish.paymentgateway.constant.TransactionStatusEnum;
import team.starfish.paymentgateway.dto.ApiResponseDto;
import team.starfish.paymentgateway.dto.auth.WalletDto;
import team.starfish.paymentgateway.dto.auth.WalletUserDetails;
import team.starfish.paymentgateway.dto.payment.CardTransactionReqDto;
import team.starfish.paymentgateway.dto.payment.PaymentQueryDto;
import team.starfish.paymentgateway.dto.payment.PaymentResultDto;
import team.starfish.paymentgateway.dto.payment.TransactionDto;
import team.starfish.paymentgateway.error.BadRequestException;
import team.starfish.paymentgateway.error.DataNotFoundException;
import team.starfish.paymentgateway.error.ExternalApiException;
import team.starfish.paymentgateway.gateway.AdyenGateway;
import team.starfish.paymentgateway.service.CardService;
import team.starfish.paymentgateway.service.TransactionService;
import team.starfish.paymentgateway.utils.HttpUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Resource
    private CardService cardService;

    @Resource
    private TransactionService transactionService;

    @Resource
    private AdyenGateway adyenGateway;


    @PostMapping("/cardTransaction")
    public ResponseEntity<ApiResponseDto> payByCardTransaction(
            @AuthenticationPrincipal WalletUserDetails walletUserDetails,
            @RequestBody CardTransactionReqDto body)
            throws BadRequestException, DataNotFoundException {

        body.setWalletId(walletUserDetails.getWalletId());

        // 1. Register a transaction with reference number
        // Adyen is the only platform right now, where PaymentQueryDto can be extended
        PaymentQueryDto paymentQuery = transactionService.register2PaymentQuery(body);
        TransactionDto transaction = transactionService
                .getByReference(paymentQuery.getReference())
                .orElseThrow(() -> new DataNotFoundException("Transaction not found with query: "
                        + paymentQuery));

        try {
            // 2. Call Adyen payment API and get result status
            PaymentResultDto paymentResult = adyenGateway.paymentByCard(paymentQuery);

            // 3. Store the psp reference and finalize payment with status
            transactionService.finalizePayment(paymentResult);
            transaction.setStatus(paymentResult.getStatus().getValue());
            transaction.setPspReference(paymentResult.getPspReference());
        } catch (IOException ex) {
            log.warn("PaymentController.payByCardTransaction IOException with paymentQuery: {}",
                    paymentQuery);

        } catch (ExternalApiException ex) {
            log.warn("PaymentController.payByCardTransaction ExternalApiException with paymentQuery: {}",
                    paymentQuery);

            transactionService.cancelByReference(paymentQuery.getReference());
            transaction.setStatus(TransactionStatusEnum.CANCELED.getValue());
            log.info("PaymentController.payByCardTransaction cancel transaction with ref: {}",
                    paymentQuery.getReference());

        } catch (Exception ex) {
            log.error("PaymentController.payByCardTransaction", ex);
            throw ex;
        }

        return HttpUtils.respond(HttpStatus.OK, true, null, transaction);
    }

    @GetMapping("/allTransactions")
    public ResponseEntity<ApiResponseDto> getAllTransactions(
            @AuthenticationPrincipal WalletUserDetails walletUserDetails) {

        List<TransactionDto> transactions = transactionService
                .getByWalletId(walletUserDetails.getWalletId());

        WalletDto walletDto = new WalletDto();
        walletDto.setId(walletUserDetails.getWalletId());
        walletDto.setEmail(walletUserDetails.getUsername());
        walletDto.setTransactions(transactions);

        return HttpUtils.respond(HttpStatus.CREATED, true, null, walletDto);
    }

}

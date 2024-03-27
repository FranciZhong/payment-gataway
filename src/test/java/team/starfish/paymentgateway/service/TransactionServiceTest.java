package team.starfish.paymentgateway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.starfish.paymentgateway.constant.CurrencyEnum;
import team.starfish.paymentgateway.constant.TransactionStatusEnum;
import team.starfish.paymentgateway.dto.payment.CardTransactionReqDto;
import team.starfish.paymentgateway.dto.payment.PaymentQueryDto;
import team.starfish.paymentgateway.dto.payment.PaymentResultDto;
import team.starfish.paymentgateway.error.BadRequestException;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;


    @Test
    public void registerValid() {
        CardTransactionReqDto transactionReq = new CardTransactionReqDto();
        transactionReq.setWalletId(1L);
        transactionReq.setCardId(2L);
        transactionReq.setCurrency(CurrencyEnum.USD.getValue());
        transactionReq.setValue(1L);

        Assertions.assertDoesNotThrow(() -> {
            mockSuccessPayment(transactionReq);
        });
    }

    @Test
    public void registerNotPairingWallet() {
        CardTransactionReqDto transactionReq = new CardTransactionReqDto();
        transactionReq.setWalletId(2L);
        transactionReq.setCardId(2L);
        transactionReq.setCurrency(CurrencyEnum.USD.getValue());
        transactionReq.setValue(1L);

        Assertions.assertThrows(BadRequestException.class, () -> {
            mockSuccessPayment(transactionReq);
        });
    }

    @Test
    public void registerWithInvalidCard() {
        CardTransactionReqDto transactionReq = new CardTransactionReqDto();
        transactionReq.setWalletId(1L);
        transactionReq.setCardId(1L);
        transactionReq.setCurrency(CurrencyEnum.USD.getValue());
        transactionReq.setValue(1L);

        Assertions.assertThrows(BadRequestException.class, () -> {
            mockSuccessPayment(transactionReq);
        });
    }

    private void mockSuccessPayment(CardTransactionReqDto transactionReq) throws Exception {
        PaymentQueryDto paymentQuery = transactionService
                .register2PaymentQuery(transactionReq);
        PaymentResultDto paymentResult = new PaymentResultDto();
        paymentResult.setPaymentQuery(paymentQuery);
        paymentResult.setPspReference("test_psp_ref");
        paymentResult.setStatus(TransactionStatusEnum.AUTHORIZED);
        paymentResult.setPayload("{}");
        transactionService.finalizePayment(paymentResult);
    }
}

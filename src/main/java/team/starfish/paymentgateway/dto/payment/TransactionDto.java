package team.starfish.paymentgateway.dto.payment;

import lombok.Data;

@Data
public class TransactionDto {
    private Long id;
    private Long walletId;
    private Long cardId;
    private String platform;
    private String type;
    private String currency;
    private Long value;
    private String reference;
    private String pspReference;
    private String status;
}

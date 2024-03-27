package team.starfish.paymentgateway.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardTransactionReqDto {
    private Long walletId;

    @NotNull
    private Long cardId;

    @NotBlank
    private String currency;

    @NotNull
    private Long value;
}

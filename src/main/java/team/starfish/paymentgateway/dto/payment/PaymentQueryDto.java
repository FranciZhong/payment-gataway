package team.starfish.paymentgateway.dto.payment;

import lombok.Data;
import team.starfish.paymentgateway.constant.CurrencyEnum;
import team.starfish.paymentgateway.constant.PaymentTypeEnum;
import team.starfish.paymentgateway.constant.PlatformEnum;
import team.starfish.paymentgateway.dto.card.CardDto;

@Data
public class PaymentQueryDto {
    private CardDto card;
    private PlatformEnum platform;
    private PaymentTypeEnum paymentType;
    private CurrencyEnum currency;
    private Long value;
    private String reference;
}

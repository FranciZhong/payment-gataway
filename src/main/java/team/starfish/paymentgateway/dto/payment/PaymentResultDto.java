package team.starfish.paymentgateway.dto.payment;

import lombok.Data;
import team.starfish.paymentgateway.constant.TransactionStatusEnum;

@Data
public class PaymentResultDto {

    private PaymentQueryDto paymentQuery;
    private TransactionStatusEnum status;
    private String pspReference;
    private String payload;

}

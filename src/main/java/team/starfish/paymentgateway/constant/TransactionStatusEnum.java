package team.starfish.paymentgateway.constant;

import lombok.Getter;

@Getter
public enum TransactionStatusEnum {
    PENDING("pending"),
    CANCELED("canceled"),
    DECLINED("declined"),
    AUTHORIZED("authorized");

    private final String value;

    TransactionStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

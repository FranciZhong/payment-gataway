package team.starfish.paymentgateway.constant;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    SCHEME("scheme");

    private final String value;

    PaymentTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

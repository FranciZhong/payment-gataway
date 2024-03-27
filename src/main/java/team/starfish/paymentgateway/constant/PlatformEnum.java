package team.starfish.paymentgateway.constant;

import lombok.Getter;

@Getter
public enum PlatformEnum {
    ADYEN("adyen");

    private final String value;

    PlatformEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

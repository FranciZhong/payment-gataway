package team.starfish.paymentgateway.constant;

import lombok.Getter;

@Getter
public enum FundingSourceEnum {
    CREDIT("credit"),
    DEBIT("debit"),
    // can be further extended
    ;

    private final String value;

    FundingSourceEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

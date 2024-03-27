package team.starfish.paymentgateway.constant;

import com.google.common.base.Objects;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum CurrencyEnum {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    JPY("JPY"),
    AUD("AUD"),
    CAD("CAD"),
    CHF("CHF"),
    CNY("CNY"),
    HKD("HKD"),
    SGD("SGD")

    // other currencies to be extended
    ;

    private final String value;

    CurrencyEnum(String value) {
        this.value = value;
    }

    public static Optional<CurrencyEnum> fromValue(String value) {
        return Arrays.stream(CurrencyEnum.values())
                .filter(currencyEnum -> Objects.equal(currencyEnum.getValue(), value))
                .findAny();
    }

    @Override
    public String toString() {
        return value;
    }
}

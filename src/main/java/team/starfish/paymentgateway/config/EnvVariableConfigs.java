package team.starfish.paymentgateway.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EnvVariableConfigs {
    @Value("${spring.profiles.active}")
    private String springProfileActive;

    @Value("${ADYEN_API_KEY}")
    private String adyenApiKey;

    @Value("${ADYEN_MERCHANT_ACCOUNT}")
    private String adyenMerchantAccount;

}

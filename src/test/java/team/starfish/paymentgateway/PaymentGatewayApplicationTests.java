package team.starfish.paymentgateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.starfish.paymentgateway.config.EnvVariableConfigs;

@SpringBootTest
class PaymentGatewayApplicationTests {

    @Autowired
    private EnvVariableConfigs envVariableConfigs;

    @Test
    void configAdyen() {
        Assertions.assertNotNull(envVariableConfigs.getAdyenApiKey());
        Assertions.assertNotNull(envVariableConfigs.getAdyenMerchantAccount());
    }

}

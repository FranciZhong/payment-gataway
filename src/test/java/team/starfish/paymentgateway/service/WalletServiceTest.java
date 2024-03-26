package team.starfish.paymentgateway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import team.starfish.paymentgateway.dto.auth.WalletRawDto;

@SpringBootTest
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Test
    public void insertWalletWithDuplicatedEmail() {
        WalletRawDto walletRawDto = new WalletRawDto();
        walletRawDto.setEmail("admin@example.com");
        walletRawDto.setPassword("password");
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> walletService.registerWallet(walletRawDto));
    }

    @Test
    public void authByValidPassword() {
        WalletRawDto walletRawDto = new WalletRawDto();
        walletRawDto.setEmail("admin@example.com");
        walletRawDto.setPassword("password");
        Assertions.assertTrue(walletService.authByPassword(walletRawDto).isPresent());
    }

    @Test
    public void authByInvalidPassword() {
        WalletRawDto walletRawDto = new WalletRawDto();
        walletRawDto.setEmail("admin@example.com");
        walletRawDto.setPassword("invalidPassword");
        Assertions.assertFalse(walletService.authByPassword(walletRawDto).isPresent());
    }
}

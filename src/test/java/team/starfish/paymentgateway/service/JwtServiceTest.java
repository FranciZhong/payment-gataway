package team.starfish.paymentgateway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.starfish.paymentgateway.dto.auth.WalletDto;

@SpringBootTest
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;


    private String getTestToken() {
        WalletDto walletDto = new WalletDto();
        walletDto.setId(1L);
        walletDto.setEmail("admin@example.com");
        return jwtService.generateJwtToken(walletDto);
    }

    private String getExpiredToken() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJ3YWxsZXQiOiJ7XCJpZFwiOjEsXCJlbWFpbFwiOlwiYWRtaW5AZXhhbXBsZS5jb21cIn0iLCJpYXQiOjE3MTEzNzk3NzgsImV4cCI6MTcxMTM3OTc3OH0.PHnJk3p2_CgNJApKtCg4CAtbSJX_o_bLjUCv9r-UVww";
    }

    @Test
    public void parseValidToken() {
        String token = this.getTestToken();
        System.out.println(token);
        WalletDto walletDto = jwtService.parseFromJwtToken(token);
        Assertions.assertEquals(1L, walletDto.getId());
        Assertions.assertEquals("admin@example.com", walletDto.getEmail());
    }

    @Test
    public void validateNonExpiredToken() {
        String token = this.getTestToken();
        Assertions.assertTrue(jwtService.validateJwtToken(token).isValid());
    }

    @Test
    public void validateExpiredToken() {
        String token = this.getExpiredToken();
        Assertions.assertFalse(jwtService.validateJwtToken(token).isValid());
    }
}

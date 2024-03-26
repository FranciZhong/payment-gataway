package team.starfish.paymentgateway.dto.auth;

import lombok.Data;

@Data
public class JwtValidResultDto {
    private boolean valid;
    private String message;
    private WalletDto wallet;

    public JwtValidResultDto(boolean valid, WalletDto wallet) {
        this.valid = valid;
        this.wallet = wallet;
    }

    public JwtValidResultDto(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}

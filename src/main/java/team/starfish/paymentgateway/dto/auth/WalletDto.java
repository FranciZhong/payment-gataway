package team.starfish.paymentgateway.dto.auth;

import lombok.Data;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.dto.payment.TransactionDto;

import java.util.List;

@Data
public class WalletDto {
    private Long id;
    private String email;

    // other information further extendable

    private List<CardDto> cards;
    private List<TransactionDto> transactions;
}

package team.starfish.paymentgateway.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.error.DuplicatedDataException;

import java.util.List;

@SpringBootTest
public class CardServiceTest {

    @Autowired
    private CardService cardService;


    @Test()
    public void getOnlyValidCardsByWalletId() {
        Long walletId = 1L;
        List<CardDto> cardDtoList = cardService.getCardsByWalletId(walletId);
        Assertions.assertEquals(1, cardDtoList.size());
    }

    @Test
    public void addDuplicatedCard2Wallet() {
        CardDto cardDto = new CardDto();
        cardDto.setWalletId(1L);
        cardDto.setNumber("4111111111111111");
        Assertions.assertThrows(DuplicatedDataException.class, () -> cardService.addCard2Wallet(cardDto));
    }

    @Test
    public void addNewCard2Wallet() {
        CardDto cardDto = new CardDto();
        cardDto.setWalletId(1L);
        cardDto.setNumber("4212345678901237");
        cardDto.setExpiryMonth("03");
        cardDto.setExpiryYear("2030");
        cardDto.setHolderName("John Smith");
        cardDto.setCvc("737");
        Assertions.assertDoesNotThrow(() -> cardService.addCard2Wallet(cardDto));
    }
}

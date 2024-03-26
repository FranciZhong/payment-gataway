package team.starfish.paymentgateway.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.starfish.paymentgateway.constant.FundingSourceEnum;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.error.ExternalApiException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class CardValidationServiceTest {

    @Autowired
    private CardValidationService cardValidationService;

    private CardDto getTestCard() {
        CardDto cardDto = new CardDto();
        cardDto.setWalletId(1L);
        cardDto.setNumber("4871049999999910");
        cardDto.setExpiryMonth("03");
        cardDto.setExpiryYear("2030");
        cardDto.setCvc("737");
        cardDto.setHolderName("user");
        return cardDto;
    }


    @Test
    public void validCardWithCredit() {
        CardDto cardDto = this.getTestCard();
        Assertions.assertDoesNotThrow(() ->
                cardValidationService.getValidatedCardBrands(cardDto.getNumber(),
                        Collections.singletonList(FundingSourceEnum.CREDIT)));
    }

    @Test
    public void validCardWithCreditCountBrand() throws ExternalApiException, IOException {
        CardDto cardDto = this.getTestCard();
        List<String> validTypes = cardValidationService
                .getValidatedCardBrands(cardDto.getNumber(), Collections.singletonList(FundingSourceEnum.CREDIT));
        System.out.println(new Gson().toJson(validTypes));
        Assertions.assertEquals(2, validTypes.size());
    }

    @Test
    public void invalidateCardWithCredit() {
        Assertions.assertThrows(ExternalApiException.class, () ->
                cardValidationService.getValidatedCardBrands("1234567890123456",
                        Collections.singletonList(FundingSourceEnum.CREDIT)));
    }
}

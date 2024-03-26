package team.starfish.paymentgateway.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CardDto {
    private static final String COVERED_LETTER = "*";
    private static final int UNCOVERED_NUMBER = 4;
    private Long id;
    private Long walletId;

    @NotBlank
    @Size(message = "Card number is invalid.", min = 15, max = 32)
    private String number;

    @NotBlank
    private String expiryMonth;

    @NotBlank
    private String expiryYear;

    @NotBlank
    private String holderName;

    @NotBlank
    private String cvc;


    public static CardDto getCoveredCard(CardDto card) {
        CardDto coveredCard = new CardDto();
        coveredCard.setId(card.getId());
        coveredCard.setWalletId(card.getWalletId());
        coveredCard.setNumber(getCoveredNumber(card.getNumber()));
        coveredCard.setExpiryMonth(fullCover(card.getExpiryMonth()));
        coveredCard.setExpiryYear(fullCover(card.getExpiryYear()));
        coveredCard.setHolderName(card.getHolderName());
        coveredCard.setCvc(fullCover(card.getCvc()));

        return coveredCard;
    }

    private static String getCoveredNumber(String number) {
        int numberLength = number.length();
        return COVERED_LETTER.repeat(numberLength - UNCOVERED_NUMBER) +
                StringUtils.substring(number, numberLength - UNCOVERED_NUMBER);
    }

    private static String fullCover(String s) {
        return COVERED_LETTER.repeat(s.length());
    }
}

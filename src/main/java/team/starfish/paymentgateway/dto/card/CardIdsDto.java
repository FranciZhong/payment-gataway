package team.starfish.paymentgateway.dto.card;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CardIdsDto {

    @NotEmpty
    private List<Long> cardIds;
}

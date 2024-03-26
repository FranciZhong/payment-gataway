package team.starfish.paymentgateway.controller;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team.starfish.paymentgateway.constant.FundingSourceEnum;
import team.starfish.paymentgateway.dto.ApiResponseDto;
import team.starfish.paymentgateway.dto.auth.WalletDto;
import team.starfish.paymentgateway.dto.auth.WalletUserDetails;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.dto.card.CardIdsDto;
import team.starfish.paymentgateway.error.BadRequestException;
import team.starfish.paymentgateway.service.CardService;
import team.starfish.paymentgateway.service.CardValidationService;
import team.starfish.paymentgateway.utils.HttpUtils;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/card")
public class CardController {

    @Resource
    private CardService cardService;

    @Resource
    private CardValidationService cardValidationService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponseDto> addCard(
            @AuthenticationPrincipal WalletUserDetails walletUserDetails,
            @Valid @RequestBody CardDto body)
            throws Exception {

        // 1. evaluate whether this card expires, and maybe some others
        if (cardValidationService.isExpired(body)) {
            throw new BadRequestException("Card expired.");
        }

        // 2. currently only credit card available
        List<String> availableBrands = cardValidationService.getValidatedCardBrands(body.getNumber(),
                Lists.newArrayList(FundingSourceEnum.CREDIT));
        if (CollectionUtils.isEmpty(availableBrands)) {
            throw new BadRequestException("No available payment methods on this card.");
        }

        // TODO 0.01$ payment to fully validate this card?

        // 3. associate to user's wallet
        Long walletId = walletUserDetails.getWalletId();
        body.setWalletId(walletId);
        CardDto cardDto = cardService.addCard2Wallet(body);

        return HttpUtils.respond(HttpStatus.CREATED, true,
                "Successfully add this card to your wallet.", cardDto);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseDto> getAll(
            @AuthenticationPrincipal WalletUserDetails walletUserDetails) {

        Long walletId = walletUserDetails.getWalletId();
        WalletDto walletDto = walletUserDetails.toDto();
        List<CardDto> cards = cardService.getCardsByWalletId(walletId).stream()
                .map(CardDto::getCoveredCard)
                .collect(Collectors.toList());
        walletDto.setCards(cards);

        return HttpUtils.respond(HttpStatus.CREATED, true, null, walletDto);
    }

    @DeleteMapping("/batchRemove")
    public ResponseEntity<ApiResponseDto> deleteByCardId(
            @AuthenticationPrincipal WalletUserDetails walletUserDetails,
            @Valid @RequestBody CardIdsDto body)
            throws BadRequestException {

        cardService.removeCards(walletUserDetails.getWalletId(), body.getCardIds());

        return HttpUtils.respond(HttpStatus.CREATED, true, "Selected cards are removed");
    }
}

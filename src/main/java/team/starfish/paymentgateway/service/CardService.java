package team.starfish.paymentgateway.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.entity.Card;
import team.starfish.paymentgateway.error.BadRequestException;
import team.starfish.paymentgateway.error.DuplicatedDataException;
import team.starfish.paymentgateway.repository.CardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CardService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private CardRepository cardRepository;


    @Transactional
    public CardDto addCard2Wallet(CardDto cardDto) throws DuplicatedDataException {
        Optional<Card> cardOpt = cardRepository.findOneByWalletIdNumber(cardDto.getWalletId(),
                cardDto.getNumber(), true);
        if (cardOpt.isPresent()) {
            log.error("Duplicated data by addCard2Wallet with cardDto: {}", cardDto);
            throw new DuplicatedDataException("Card is already added.");
        }

        Card card = modelMapper.map(cardDto, Card.class);
        cardRepository.save(card);

        return modelMapper.map(card, CardDto.class);
    }

    public List<CardDto> getCardsByWalletId(Long walletId) {
        return cardRepository.findByWalletId(walletId, true)
                .stream()
                .map((element) -> modelMapper.map(element, CardDto.class))
                .collect(Collectors.toList());
    }

    public Optional<CardDto> getCardByCardId(Long cardId) {
        return cardRepository.findByCardId(cardId, true)
                .map(card -> modelMapper.map(card, CardDto.class));
    }

    @Transactional
    public void removeCards(Long walletId, List<Long> cardIds) throws BadRequestException {
        List<Long> associatedCardIds = cardRepository.findByWalletId(walletId, true)
                .stream()
                .map(Card::getId)
                .toList();

        if (!CollectionUtils.containsAll(associatedCardIds, cardIds)) {
            throw new BadRequestException("Invalid cardIds to be removed.");
        }

        cardRepository.updateValidByCardIds(cardIds, false);
    }

}

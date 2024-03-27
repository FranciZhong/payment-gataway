package team.starfish.paymentgateway.service;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.starfish.paymentgateway.constant.CurrencyEnum;
import team.starfish.paymentgateway.constant.PaymentTypeEnum;
import team.starfish.paymentgateway.constant.PlatformEnum;
import team.starfish.paymentgateway.constant.TransactionStatusEnum;
import team.starfish.paymentgateway.dto.card.CardDto;
import team.starfish.paymentgateway.dto.payment.CardTransactionReqDto;
import team.starfish.paymentgateway.dto.payment.PaymentQueryDto;
import team.starfish.paymentgateway.dto.payment.PaymentResultDto;
import team.starfish.paymentgateway.dto.payment.TransactionDto;
import team.starfish.paymentgateway.entity.Card;
import team.starfish.paymentgateway.entity.Transaction;
import team.starfish.paymentgateway.error.BadRequestException;
import team.starfish.paymentgateway.error.DataNotFoundException;
import team.starfish.paymentgateway.repository.CardRepository;
import team.starfish.paymentgateway.repository.TransactionRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Resource
    private Gson gson;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private CardRepository cardRepository;

    @Resource
    private TransactionRepository transactionRepository;


    @Transactional
    public PaymentQueryDto register2PaymentQuery(CardTransactionReqDto transactionReq)
            throws BadRequestException {
        Optional<Card> cardOpt = cardRepository.findByCardId(transactionReq.getCardId(), true);

        // check wallet-card pair is correct
        if (cardOpt.isEmpty() || !Objects.equals(cardOpt.get().getWalletId(),
                transactionReq.getWalletId())) {
            throw new BadRequestException("Card is not available.");
        }

        // handle unsupported currency types
        CurrencyEnum currency = CurrencyEnum.fromValue(transactionReq.getCurrency())
                .orElseThrow(() -> new BadRequestException("Currency not supported."));

        Card card = cardOpt.get();
        Transaction transaction = new Transaction();
        transaction.setWalletId(card.getWalletId());
        transaction.setCardId(card.getId());
        // reference is recommended to be UUID
        transaction.setReference(UUID.randomUUID().toString());
        // platform and payment type
        transaction.setPlatform(PlatformEnum.ADYEN.getValue());
        transaction.setType(PaymentTypeEnum.SCHEME.getValue());
        transaction.setCurrency(currency.getValue());
        transaction.setValue(transactionReq.getValue());
        transaction.setStatus(TransactionStatusEnum.PENDING.getValue());

        transactionRepository.save(transaction);

        return getPaymentQueryDto(currency, transactionReq.getValue(), card, transaction);
    }

    private PaymentQueryDto getPaymentQueryDto(CurrencyEnum currency, Long value, Card card, Transaction transaction) {
        PaymentQueryDto paymentQueryDto = new PaymentQueryDto();
        paymentQueryDto.setCard(modelMapper.map(card, CardDto.class));
        paymentQueryDto.setPlatform(PlatformEnum.ADYEN);
        paymentQueryDto.setPaymentType(PaymentTypeEnum.SCHEME);
        paymentQueryDto.setReference(transaction.getReference());
        paymentQueryDto.setCurrency(currency);
        paymentQueryDto.setValue(value);

        return paymentQueryDto;
    }

    @Transactional
    public void finalizePayment(PaymentResultDto paymentResult)
            throws DataNotFoundException {
        int updatedRowCount = transactionRepository
                .updateResultByRef(paymentResult.getPaymentQuery().getReference(),
                        TransactionStatusEnum.PENDING.getValue(),
                        paymentResult.getStatus().getValue(),
                        paymentResult.getPspReference(),
                        paymentResult.getPayload());

        if (updatedRowCount == 0) {
            throw new DataNotFoundException("Transaction not found with paymentResult: " +
                    gson.toJson(paymentResult));
        }
    }

    @Transactional
    public void cancelByReference(String reference) {
        transactionRepository.updateStatusBy(reference,
                TransactionStatusEnum.PENDING.getValue(),
                TransactionStatusEnum.CANCELED.getValue());
    }

    public Optional<TransactionDto> getByReference(String reference) {
        return transactionRepository.findByRef(reference)
                .map((transaction) -> modelMapper.map(transaction, TransactionDto.class));
    }

    public List<TransactionDto> getByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId).stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }
}

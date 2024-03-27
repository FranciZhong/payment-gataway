package team.starfish.paymentgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.starfish.paymentgateway.entity.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

//    @Modifying
//    @Query(value = "INSERT INTO card (wallet_id, number, expiry_month, expiry_year, holder_name, cvc) " +
//            "VALUES (:#{#query.walletId}, :#{#query.number}, :#{#query.expiryMonth}, :#{#query.expiryYear}, :#{#query.holderName}, :#{#query.cvc})",
//            nativeQuery = true)
//    void insertCard(@Param("query") Card query);

    @Query(value = "SELECT * FROM card WHERE wallet_id = :walletId AND number = :number AND valid = :valid LIMIT 1", nativeQuery = true)
    Optional<Card> findOneByWalletIdNumber(@Param("walletId") Long walletId, @Param("number") String number, @Param("valid") Boolean valid);

    @Query(value = "SELECT * FROM card WHERE wallet_id = :walletId AND valid = :valid", nativeQuery = true)
    List<Card> findByWalletId(@Param("walletId") Long walletId, @Param("valid") Boolean valid);

    @Query(value = "SELECT * FROM card WHERE id = :cardId AND valid = :valid", nativeQuery = true)
    Optional<Card> findByCardId(@Param("cardId") Long cardId, @Param("valid") Boolean valid);

    @Modifying
    @Query(value = "UPDATE card SET valid = :valid WHERE id IN :cardIds", nativeQuery = true)
    void updateValidByCardIds(@Param("cardIds") List<Long> cardIds, @Param("valid") Boolean valid);
}

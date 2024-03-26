package team.starfish.paymentgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.starfish.paymentgateway.entity.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Modifying
    @Query(value = "UPDATE transaction SET status = :status, psp_reference = :pspReference, payload = :payload " +
            "WHERE reference = :reference AND status = :previousStatus", nativeQuery = true)
    int updateResultByRef(@Param("reference") String reference,
                          @Param("previousStatus") String previousStatus,
                          @Param("status") String status,
                          @Param("pspReference") String pspReference,
                          @Param("payload") String payload);


    @Query(value = "SELECT * FROM transaction WHERE reference = :reference", nativeQuery = true)
    Optional<Transaction> findByRef(@Param("reference") String reference);

    @Modifying
    @Query(value = "UPDATE transaction SET status = :status " +
            "WHERE reference = :reference AND status = :previousStatus", nativeQuery = true)
    void updateStatusBy(@Param("reference") String reference,
                        @Param("previousStatus") String previousStatus,
                        @Param("status") String status);

    @Query(value = "SELECT * FROM transaction WHERE wallet_id = :walletId", nativeQuery = true)
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);
}

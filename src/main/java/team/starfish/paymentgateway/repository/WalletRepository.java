package team.starfish.paymentgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.starfish.paymentgateway.entity.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Modifying
    @Query(value = "INSERT INTO wallet (email, credential) VALUES (:email, :credential)", nativeQuery = true)
    void insertWallet(@Param("email") String email, @Param("credential") String credential);

    @Query(value = "SELECT * FROM wallet WHERE email = :email AND valid = :valid", nativeQuery = true)
    Optional<Wallet> findByEmail(@Param("email") String email, @Param("valid") Boolean valid);

    @Query(value = "SELECT * FROM wallet WHERE id = :walletId AND valid = :valid", nativeQuery = true)
    Optional<Wallet> findById(@Param("walletId") Long walletId, @Param("valid") Boolean valid);
}

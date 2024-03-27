package team.starfish.paymentgateway.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "number", nullable = false, length = 256)
    private String number;

    @Column(name = "expiry_month", nullable = false, length = 256)
    private String expiryMonth;

    @Column(name = "expiry_year", nullable = false, length = 256)
    private String expiryYear;

    @Column(name = "holder_name", nullable = false, length = 256)
    private String holderName;

    @Column(name = "cvc", nullable = false, length = 3)
    private String cvc;

    @Column(name = "valid", insertable = false)
    private Boolean valid;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}

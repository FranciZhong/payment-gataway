package team.starfish.paymentgateway.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "platform", nullable = false, length = 256)
    private String platform;

    @Column(name = "type", nullable = false, length = 256)
    private String type;

    @Column(name = "currency", nullable = false, length = 256)
    private String currency;

    @Column(name = "value", nullable = false)
    private Long value;

    @Column(name = "reference", nullable = false, length = 256)
    private String reference;

    @Column(name = "psp_reference", length = 256)
    private String pspReference;

    @Column(name = "payload", length = 4096)
    private String payload;

    @Column(name = "status", nullable = false, length = 64)
    private String status;

    @Column(name = "created_at", insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

}

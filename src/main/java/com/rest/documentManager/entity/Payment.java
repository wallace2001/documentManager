package com.rest.documentManager.entity;

import com.rest.documentManager.response.SpriteResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payments")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String payment_id;
    @Column(nullable = false)
    private String intent;
    @Column(nullable = false)
    private String state;
    @OneToOne
    private User user;
    @OneToMany
    private List<Sprite> sprite;
    @Column(nullable = false)
    private String cart;
    @Column(nullable = false)
    private String payer_status;
    @Column(nullable = false)
    private String payer_email;
    @Column(nullable = false)
    private String payer_name;
    @Column(nullable = false)
    private String payer_country;
    @Column(nullable = false)
    private String payer_id;
    @Column(nullable = false)
    private String total;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Payment(
            String payment_id,
            String intent,
            String state,
            List<Sprite> sprite,
            User user,
            String cart,
            String payer_status,
            String payer_email,
            String payer_name,
            String payer_country,
            String payer_id,
            String total,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.payment_id = payment_id;
        this.intent = intent;
        this.state = state;
        this.sprite = sprite;
        this.user = user;
        this.cart = cart;
        this.payer_status = payer_status;
        this.payer_email = payer_email;
        this.payer_name = payer_name;
        this.payer_country = payer_country;
        this.payer_id = payer_id;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

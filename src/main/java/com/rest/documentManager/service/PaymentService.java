package com.rest.documentManager.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.rest.documentManager.entity.Sprite;
import com.rest.documentManager.response.CategoriesBoughtResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    Payment createPayment(Double total,
                          String currency,
                          String method,
                          String intent,
                          String description,
                          String cancelUrl,
                          String successUrl)
            throws PayPalRESTException;

    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    com.rest.documentManager.entity.Payment savePayment(Payment payment, String idsSpritesString, Long idUser);

    Page<com.rest.documentManager.entity.Payment> findAll(Pageable pageable);

    Page<com.rest.documentManager.entity.Payment> findByUser(Pageable pageable, Long id);

    Page<Sprite> findSpriteByUser(Pageable pageable, Long id);

    CategoriesBoughtResponse fetchProductsMostBought();
}

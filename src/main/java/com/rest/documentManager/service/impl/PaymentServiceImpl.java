package com.rest.documentManager.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.rest.documentManager.entity.Sprite;
import com.rest.documentManager.entity.User;
import com.rest.documentManager.repository.PaymentRepository;
import com.rest.documentManager.repository.UserRepository;
import com.rest.documentManager.response.CategoriesBoughtResponse;
import com.rest.documentManager.response.CategoryInstance;
import com.rest.documentManager.response.SpriteResponse;
import com.rest.documentManager.service.PaymentService;
import com.rest.documentManager.services.exceptions.ErrorLoginException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private APIContext apiContext;

    @Autowired
    private SpriteServiceImpl spriteService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(total.toString());

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        System.out.println(successUrl);
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        Payment paymentCreated = payment.create(apiContext);
        return paymentCreated;
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{

        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecute);
    }

    @Override
    public com.rest.documentManager.entity.Payment savePayment(Payment payment, String idsSprites, Long idUser) {

        List<Sprite> spritesFiltered = new ArrayList<>();
        Optional<User> userPayment = userRepository.findById(idUser);

        if (!userPayment.isPresent()) {
            throw new ErrorLoginException("User dont exixsts!");
        }

        for (String id : idsSprites.split(",")) {
            Optional<Sprite> spriteFind = spriteService.findById(Long.valueOf(id));
            Sprite newSprite = new Sprite();

            if (spriteFind.isPresent() && spriteFind.get().getActive() && spriteFind.get().getIsUnique()) {

                BeanUtils.copyProperties(spriteFind.get(), newSprite);
                newSprite.setActive(false);
                newSprite.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

                spriteService.saveSprite(newSprite);
                spritesFiltered.add(newSprite);
            } else {
                if (spriteFind.isPresent()) {
                    spritesFiltered.add(spriteFind.get());
                }
            }
        }

        com.rest.documentManager.entity.Payment paymentUser = new com.rest.documentManager.entity.Payment(
                payment.getId(),
                payment.getIntent(),
                payment.getState(),
                spritesFiltered,
                userPayment.get(),
                payment.getCart(),
                payment.getPayer().getStatus(),
                payment.getPayer().getPayerInfo().getEmail(),
                payment.getPayer().getPayerInfo().getFirstName(),
                payment.getPayer().getPayerInfo().getCountryCode(),
                payment.getPayer().getPayerInfo().getPayerId(),
                payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getTotal(),
                LocalDateTime.now(ZoneId.of("UTC")),
                LocalDateTime.now(ZoneId.of("UTC"))
        );

       return paymentRepository.save(paymentUser);
    }

    @Override
    public Page<com.rest.documentManager.entity.Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    @Override
    public Page<com.rest.documentManager.entity.Payment> findByUser(Pageable pageable, Long id) {
        return paymentRepository.findByUser(pageable, id);
    }

    @Override
    public Page<Sprite> findSpriteByUser(Pageable pageable, Long id) {
        Page<com.rest.documentManager.entity.Payment> payments = paymentRepository.findByUser(pageable, id);
        List<Sprite> spritesUsers = new ArrayList<>();

        for (com.rest.documentManager.entity.Payment payment : payments) {
            for (Sprite sprite : payment.getSprite()) {
                spritesUsers.add(sprite);
            }
        }
        return new PageImpl<>(spritesUsers);
    }

    @Override
    public CategoriesBoughtResponse fetchProductsMostBought() {
        List<com.rest.documentManager.entity.Payment> payments = paymentRepository.findAll();
        int monsters = 0;
        int outfits = 0;
        int envs = 0;
        int spells = 0;
        int items = 0;
        int maps = 0;

        int categoryMaior = 0;

        Double totalPrice = 0.0;
        CategoriesBoughtResponse categoriesBoughtResponse = new CategoriesBoughtResponse();

        for (com.rest.documentManager.entity.Payment payment : payments) {
            for (int i = 0; i < payment.getSprite().size(); i++) {
                Sprite sprite = payment.getSprite().get(i);
                if (Objects.equals(sprite.getCategory().getInstance(), "Monster")) {
                    monsters += i;

                    if (categoryMaior >= monsters) {
                        categoryMaior = monsters;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                if (Objects.equals(sprite.getCategory().getInstance(), "Outfits")) {
                    outfits += i;

                    if (categoryMaior >= outfits) {
                        categoryMaior = outfits;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                if (Objects.equals(sprite.getCategory().getInstance(), "Environments")) {
                    envs += i;

                    if (categoryMaior >= envs) {
                        categoryMaior = envs;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                if (Objects.equals(sprite.getCategory().getInstance(), "Spells")) {
                    spells += i;

                    if (categoryMaior >= spells) {
                        categoryMaior = spells;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                if (Objects.equals(sprite.getCategory().getInstance(), "Maps")) {
                    maps += i;

                    if (categoryMaior >= maps) {
                        categoryMaior = maps;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                if (Objects.equals(sprite.getCategory().getInstance(), "Items")) {
                    items += i;

                    if (categoryMaior >= items) {
                        categoryMaior = items;
                        categoriesBoughtResponse.setCategory(sprite.getCategory().getInstance());
                    }
                }
                totalPrice += sprite.getPrice().doubleValue();
            }
        }

        categoriesBoughtResponse.setTotalBought(payments.size());
        categoriesBoughtResponse.setTotalPrice(totalPrice);
        return categoriesBoughtResponse;
    }
}

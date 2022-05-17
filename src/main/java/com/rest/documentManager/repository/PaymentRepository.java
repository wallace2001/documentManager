package com.rest.documentManager.repository;

import com.rest.documentManager.entity.Payment;
import com.rest.documentManager.entity.Sprite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p")
    Page<Payment> findAll(Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :id")
    Page<Payment> findByUser(Pageable pageable, Long id);
}

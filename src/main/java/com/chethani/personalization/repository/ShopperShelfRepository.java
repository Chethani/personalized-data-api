package com.chethani.personalization.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chethani.personalization.dto.ShopperProductResponse;
import com.chethani.personalization.entity.ShopperShelf;
import com.chethani.personalization.entity.ShopperShelfId;


public interface ShopperShelfRepository extends JpaRepository<ShopperShelf, ShopperShelfId> {

    void deleteByIdShopperId(String shopperId);

    // Returns personalized products for a shopper, applying optional filters and ordering by highest relevancy score first.
    @Query("""
        SELECT new com.chethani.personalization.dto.ShopperProductResponse(
            s.id.productId,
            p.category,
            p.brand,
            s.relevancyScore
        )
        FROM ShopperShelf s
        JOIN ProductMetadata p ON s.id.productId = p.productId
        WHERE s.id.shopperId = :shopperId
        AND (:category IS NULL OR p.category = :category)
        AND (:brand IS NULL OR p.brand = :brand)
        ORDER BY s.relevancyScore DESC
        """)
    List<ShopperProductResponse> findProductsByShopper(
        String shopperId,
        String category,
        String brand,
        Pageable pageable
    );

}

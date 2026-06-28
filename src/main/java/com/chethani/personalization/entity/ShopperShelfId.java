package com.chethani.personalization.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Composite key ensures a shopper can have only one relevancy score per product.
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ShopperShelfId implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column(name = "shopper_id", nullable = false, length = 50)
    private String shopperId;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;
    
}

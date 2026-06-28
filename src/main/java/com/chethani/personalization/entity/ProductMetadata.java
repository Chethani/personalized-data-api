package com.chethani.personalization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_metadata")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductMetadata {

    @Id
    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand;
    
}

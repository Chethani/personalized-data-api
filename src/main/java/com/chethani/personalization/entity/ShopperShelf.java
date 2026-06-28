package com.chethani.personalization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shopper_shelf")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopperShelf {

    @EmbeddedId
    private ShopperShelfId id;
    
    @Column(name = "relevancy_score", nullable = false)
    private Double relevancyScore;

}

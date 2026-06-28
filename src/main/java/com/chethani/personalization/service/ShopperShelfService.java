package com.chethani.personalization.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.chethani.personalization.dto.ShopperProductResponse;
import com.chethani.personalization.dto.ShopperShelfRequest;
import com.chethani.personalization.entity.ShopperShelf;
import com.chethani.personalization.entity.ShopperShelfId;
import com.chethani.personalization.repository.ShopperShelfRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopperShelfService {

    private final ShopperShelfRepository shopperShelfRepository;

    @Transactional
    public void addShopperShelfData(ShopperShelfRequest request) {
        log.info("Replacing shopper shelf for shopperId={} with {} products",
            request.shopperId(),
            request.shelf().size());

        // Replace the existing shelf for the shopper so the latest personalization data becomes the source of truth.
        shopperShelfRepository.deleteByIdShopperId(request.shopperId());

        List<ShopperShelf> shopperShelfList = request.shelf().stream()
        .map(shelfItem -> {
            ShopperShelfId id = new ShopperShelfId(request.shopperId(), shelfItem.productId());

            ShopperShelf shopperShelf = new ShopperShelf();
            shopperShelf.setId(id);
            shopperShelf.setRelevancyScore(shelfItem.relevancyScore());
            
            return shopperShelf;
        }).toList();
        shopperShelfRepository.saveAll(shopperShelfList);
    }

    public List<ShopperProductResponse> getShopperShelfData(String shopperId, String category, String brand, int limit) {
        log.info("Fetching shopper shelf for shopperId={}, category={}, brand={}, limit={}",
                shopperId, category, brand, limit);

        PageRequest pageRequest = PageRequest.of(0, limit);
        return shopperShelfRepository.findProductsByShopper(shopperId, category, brand, pageRequest);
    }

}

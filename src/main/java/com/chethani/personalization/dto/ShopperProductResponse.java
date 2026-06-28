package com.chethani.personalization.dto;

public record ShopperProductResponse(String productId, String category, String brand, double relevancyScore) {}
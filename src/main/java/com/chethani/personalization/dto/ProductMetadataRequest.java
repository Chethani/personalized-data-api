package com.chethani.personalization.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductMetadataRequest(
    @NotBlank String productId, 
    @NotBlank String category, 
    @NotBlank String brand
) {}
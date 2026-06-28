package com.chethani.personalization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShelfItemRequest(
    @NotBlank String productId, 
    @NotNull Double relevancyScore
) {}
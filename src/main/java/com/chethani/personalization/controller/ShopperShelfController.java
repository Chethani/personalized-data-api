package com.chethani.personalization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chethani.personalization.dto.ApiResponse;
import com.chethani.personalization.dto.ShopperProductResponse;
import com.chethani.personalization.dto.ShopperShelfRequest;
import com.chethani.personalization.service.ShopperShelfService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/shopper-shelf")
@RequiredArgsConstructor
@Validated
public class ShopperShelfController {

    private final ShopperShelfService shopperShelfService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse addShopperShelfData(@Valid @RequestBody ShopperShelfRequest request) {
        shopperShelfService.addShopperShelfData(request);
        return new ApiResponse("Shopper shelf data added successfully");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShopperProductResponse> getShopperShelfData(
        @RequestParam @NotBlank String shopperId,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String brand,
        @RequestParam(defaultValue = "10") 
        @Min(value = 1, message = "Limit must be greater than or equal to 1") 
        @Max(value = 100, message = "Limit must be less than or equal to 100") 
        int limit) {
        return shopperShelfService.getShopperShelfData(shopperId, category, brand, limit);
    }

}
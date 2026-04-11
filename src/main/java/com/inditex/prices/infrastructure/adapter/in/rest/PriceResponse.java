package com.inditex.prices.infrastructure.adapter.in.rest;

import com.inditex.prices.domain.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponse(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal amount,
        String currency
) {
    public static PriceResponse from(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.price(),
                price.currency()
        );
    }
}

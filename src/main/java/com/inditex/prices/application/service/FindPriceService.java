package com.inditex.prices.application.service;

import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.in.FindPriceUseCase;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;

import java.time.LocalDateTime;

public class FindPriceService implements FindPriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    public FindPriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    @Override
    public Price findApplicablePrice(LocalDateTime date, Long productId, Long brandId) {
        return priceRepositoryPort.findTopProductByIdAndBrandIdAndDate(productId, brandId, date)
                .orElseThrow(() -> new PriceNotFoundException(productId, brandId, date));
    }
}

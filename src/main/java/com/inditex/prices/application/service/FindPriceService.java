package com.inditex.prices.application.service;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.in.FindPriceUseCase;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class FindPriceService implements FindPriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    public FindPriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    @Override
    public Optional<Price> findApplicablePrice(LocalDateTime date, Long productId, Long brandId) {
        List<Price> priceCandidates = priceRepositoryPort.findByProductIdAndBrandIdAndDate(productId, brandId, date);
        return priceCandidates.stream()
                .max(Comparator.comparingInt(Price::priority));
    }
}

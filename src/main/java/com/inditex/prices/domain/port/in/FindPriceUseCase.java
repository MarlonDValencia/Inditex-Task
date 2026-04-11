package com.inditex.prices.domain.port.in;

import com.inditex.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FindPriceUseCase {

    Optional<Price> findApplicablePrice(LocalDateTime date, Long productId, Long brandId);
}

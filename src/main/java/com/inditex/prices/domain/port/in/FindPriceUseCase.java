package com.inditex.prices.domain.port.in;

import com.inditex.prices.domain.model.Price;

import java.time.LocalDateTime;

public interface FindPriceUseCase {

    Price findApplicablePrice(LocalDateTime date, Long productId, Long brandId);
}

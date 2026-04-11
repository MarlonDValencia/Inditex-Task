package com.inditex.prices.infrastructure.adapter.out.persistence;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final JpaPriceRepository jpaPriceRepository;

    public PriceRepositoryAdapter(JpaPriceRepository jpaPriceRepository) {
        this.jpaPriceRepository = jpaPriceRepository;
    }

    @Override
    public Optional<Price> findTopProductByIdAndBrandIdAndDate(Long productId, Long brandId, LocalDateTime date) {
        return jpaPriceRepository.findTopByProductIdAndBrandIdAndDate(productId, brandId, date)
                .map(this::toDomain);
    }

    private Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurrency()
        );
    }
}

package com.inditex.prices.infrastructure.config;

import com.inditex.prices.application.service.FindPriceService;
import com.inditex.prices.domain.port.in.FindPriceUseCase;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public FindPriceUseCase findPriceUseCase(PriceRepositoryPort priceRepositoryPort) {
        return new FindPriceService(priceRepositoryPort);
    }
}

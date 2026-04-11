package com.inditex.prices.application;

import com.inditex.prices.application.service.FindPriceService;
import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.out.PriceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindPriceServiceTest {

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    private FindPriceService findPriceService;

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime DATE = LocalDateTime.of(2020, 6, 14, 10, 0);

    @BeforeEach
    void setUp() {
        findPriceService = new FindPriceService(priceRepositoryPort);
    }

    @Test
    void shouldReturnApplicablePrice() {
        Price expectedPrice = buildPrice(1, 0, new BigDecimal("35.50"));

        when(priceRepositoryPort.findTopProductByIdAndBrandIdAndDate(PRODUCT_ID, BRAND_ID, DATE))
                .thenReturn(Optional.of(expectedPrice));

        Price result = findPriceService.findApplicablePrice(DATE, PRODUCT_ID, BRAND_ID);

        assertThat(result.priceList()).isEqualTo(1);
        assertThat(result.price()).isEqualByComparingTo("35.50");
        assertThat(result.priority()).isEqualTo(0);
    }

    @Test
    void shouldReturnHighPriorityPrice() {
        Price highPriority = buildPrice(2, 1, new BigDecimal("25.45"));

        when(priceRepositoryPort.findTopProductByIdAndBrandIdAndDate(PRODUCT_ID, BRAND_ID, DATE))
                .thenReturn(Optional.of(highPriority));

        Price result = findPriceService.findApplicablePrice(DATE, PRODUCT_ID, BRAND_ID);

        assertThat(result.priceList()).isEqualTo(2);
        assertThat(result.price()).isEqualByComparingTo("25.45");
        assertThat(result.priority()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWhenNoPriceFound() {
        when(priceRepositoryPort.findTopProductByIdAndBrandIdAndDate(PRODUCT_ID, BRAND_ID, DATE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> findPriceService.findApplicablePrice(DATE, PRODUCT_ID, BRAND_ID))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining(String.valueOf(PRODUCT_ID))
                .hasMessageContaining(String.valueOf(BRAND_ID));
    }

    private Price buildPrice(Integer priceList, Integer priority, BigDecimal amount) {
        return new Price(
                BRAND_ID,
                PRODUCT_ID,
                priceList,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                priority,
                amount,
                "EUR"
        );
    }
}

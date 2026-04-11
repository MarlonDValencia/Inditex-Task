package com.inditex.prices.infrastructure.adapter.in.rest;

import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.in.FindPriceUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindPriceUseCase findPriceUseCase;

    private static final String BASE_URL = "/api/prices";
    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime DATE = LocalDateTime.of(2020, 6, 14, 10, 0);

    @Test
    void shouldReturn200WhenPriceFound() throws Exception {
        Price price = buildPrice(1, new BigDecimal("35.50"));

        when(findPriceUseCase.findApplicablePrice(eq(DATE), eq(PRODUCT_ID), eq(BRAND_ID)))
                .thenReturn(price);

        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.brandId").value(BRAND_ID))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.amount").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturn404WhenPriceNotFound() throws Exception {
        when(findPriceUseCase.findApplicablePrice(eq(DATE), eq(PRODUCT_ID), eq(BRAND_ID)))
                .thenThrow(new PriceNotFoundException(PRODUCT_ID, BRAND_ID, DATE));

        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Price Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn400WhenDateIsInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "FECHA-INVALIDA")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Parameters"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400WhenParameterIsMissing() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Parameters"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400WhenProductIdIsNegative() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest());
    }

    private Price buildPrice(Integer priceList, BigDecimal amount) {
        return new Price(
                BRAND_ID,
                PRODUCT_ID,
                priceList,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59),
                0,
                amount,
                "EUR"
        );
    }
}

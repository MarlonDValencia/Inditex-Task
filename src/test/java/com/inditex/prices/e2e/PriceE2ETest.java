package com.inditex.prices.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceE2ETest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/prices";
    private static final String PRODUCT_ID = "35455";
    private static final String BRAND_ID   = "1";


    @Test
    void shouldReturnPriceList1At10h() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.amount").value(35.50))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnPriceList2At16h() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T16:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.amount").value(25.45));
    }

    @Test
    void shouldReturnPriceList1At21h() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T21:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.amount").value(35.50));
    }

    @Test
    void shouldReturnPriceList3On15thAt10h() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-15T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.amount").value(30.50));
    }

    @Test
    void shouldReturnPriceList4On16thAt21h() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-16T21:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.amount").value(38.95));
    }


    @Test
    void shouldReturnHighestPriorityWhenFourTariffsOverlap() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-07-10T15:00:00")
                        .param("productId", "99001")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.amount").value(40.00));
    }

    @Test
    void shouldReturnWeeklyPriceOutsideFlashSale() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-07-11T12:00:00")
                        .param("productId", "99001")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.amount").value(80.00));
    }


    @Test
    void shouldReturnHigherPriorityEvenIfMoreExpensive() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-08-05T10:00:00")
                        .param("productId", "99002")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.amount").value(120.00));
    }

    @Test
    void shouldReturnBasePriceOutsideHighPriorityRange() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-08-01T09:00:00")
                        .param("productId", "99002")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.amount").value(50.00));
    }


    @Test
    void shouldReturnZaraPriceForProduct99003() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-09-15T12:00:00")
                        .param("productId", "99003")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.amount").value(45.00));
    }

    @Test
    void shouldReturnPullAndBearPriceForProduct99003() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-09-15T12:00:00")
                        .param("productId", "99003")
                        .param("brandId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandId").value(2))
                .andExpect(jsonPath("$.amount").value(25.00));
    }


    @Test
    void shouldReturn404ForUnknownProduct() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", "99999")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Price Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn404ForUnknownBrand() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Price Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn404WhenDateOutOfRange() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2019-01-01T00:00:00")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn400WhenDateIsInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "FECHA-INVALIDA")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Parameters"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400WhenParameterIsMissing() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("date", "2020-06-14T10:00:00")
                        .param("brandId", BRAND_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

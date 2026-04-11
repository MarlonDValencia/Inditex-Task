package com.inditex.prices.infrastructure.adapter.in.rest;

import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.port.in.FindPriceUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final FindPriceUseCase findPriceUseCase;

    public PriceController(FindPriceUseCase findPriceUseCase) {
        this.findPriceUseCase = findPriceUseCase;
    }

    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam Long productId,
            @RequestParam Long brandId
    ) {
        return findPriceUseCase.findApplicablePrice(date, productId, brandId)
                .map(PriceResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PriceNotFoundException(productId, brandId, date));
    }
}

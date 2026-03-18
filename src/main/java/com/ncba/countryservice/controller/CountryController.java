package com.ncba.countryservice.controller;

import com.ncba.countryservice.dto.CountryDTOs.*;
import com.ncba.countryservice.service.CountryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private static final Logger log = LoggerFactory.getLogger(CountryController.class);

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CountryResponse>> fetchAndSaveCountry(
            @Valid @RequestBody CountryRequest request) {
        log.info("POST /api/countries - name: {}", request.getName());
        CountryResponse response = countryService.fetchAndSaveCountry(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Country fetched and saved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryResponse>>> getAllCountries() {
        log.info("GET /api/countries");
        List<CountryResponse> countries = countryService.getAllCountries();
        return ResponseEntity.ok(ApiResponse.success("Retrieved " + countries.size() + " countries", countries));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryResponse>> getCountryById(@PathVariable Long id) {
        log.info("GET /api/countries/{}", id);
        return ResponseEntity.ok(ApiResponse.success("Country found", countryService.getCountryById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryResponse>> updateCountry(
            @PathVariable Long id, @RequestBody CountryUpdateRequest request) {
        log.info("PUT /api/countries/{}", id);
        return ResponseEntity.ok(ApiResponse.success("Country updated successfully", countryService.updateCountry(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable Long id) {
        log.info("DELETE /api/countries/{}", id);
        countryService.deleteCountry(id);
        return ResponseEntity.ok(ApiResponse.success("Country deleted successfully", null));
    }
}

package com.ncba.countryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CountryDTOs {

    @Data
    public static class CountryRequest {
        @NotBlank(message = "Country name must not be blank")
        @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "Country name must contain letters only")
        @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters")
        private String name;
    }

    @Data
    public static class CountryResponse {
        private Long id;
        private String countryName;
        private String isoCode;
        private String capitalCity;
        private String phoneCode;
        private String continentCode;
        private String currencyIsoCode;
        private String currencyName;
        private String countryFlag;
        private String createdAt;
        private String updatedAt;
    }

    @Data
    public static class CountryUpdateRequest {
        private String countryName;
        private String capitalCity;
        private String phoneCode;
        private String continentCode;
        private String currencyIsoCode;
        private String currencyName;
        private String countryFlag;
    }
}
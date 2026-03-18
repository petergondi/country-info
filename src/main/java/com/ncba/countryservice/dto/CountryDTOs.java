package com.ncba.countryservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CountryDTOs {

    public static class CountryRequest {
        @NotBlank(message = "Country name must not be blank")
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCountryName() { return countryName; }
        public void setCountryName(String v) { this.countryName = v; }
        public String getIsoCode() { return isoCode; }
        public void setIsoCode(String v) { this.isoCode = v; }
        public String getCapitalCity() { return capitalCity; }
        public void setCapitalCity(String v) { this.capitalCity = v; }
        public String getPhoneCode() { return phoneCode; }
        public void setPhoneCode(String v) { this.phoneCode = v; }
        public String getContinentCode() { return continentCode; }
        public void setContinentCode(String v) { this.continentCode = v; }
        public String getCurrencyIsoCode() { return currencyIsoCode; }
        public void setCurrencyIsoCode(String v) { this.currencyIsoCode = v; }
        public String getCurrencyName() { return currencyName; }
        public void setCurrencyName(String v) { this.currencyName = v; }
        public String getCountryFlag() { return countryFlag; }
        public void setCountryFlag(String v) { this.countryFlag = v; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String v) { this.createdAt = v; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String v) { this.updatedAt = v; }
    }

    public static class CountryUpdateRequest {
        private String countryName;
        private String capitalCity;
        private String phoneCode;
        private String continentCode;
        private String currencyIsoCode;
        private String currencyName;
        private String countryFlag;

        public String getCountryName() { return countryName; }
        public void setCountryName(String v) { this.countryName = v; }
        public String getCapitalCity() { return capitalCity; }
        public void setCapitalCity(String v) { this.capitalCity = v; }
        public String getPhoneCode() { return phoneCode; }
        public void setPhoneCode(String v) { this.phoneCode = v; }
        public String getContinentCode() { return continentCode; }
        public void setContinentCode(String v) { this.continentCode = v; }
        public String getCurrencyIsoCode() { return currencyIsoCode; }
        public void setCurrencyIsoCode(String v) { this.currencyIsoCode = v; }
        public String getCurrencyName() { return currencyName; }
        public void setCurrencyName(String v) { this.currencyName = v; }
        public String getCountryFlag() { return countryFlag; }
        public void setCountryFlag(String v) { this.countryFlag = v; }
    }

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        public static <T> ApiResponse<T> success(String message, T data) {
            ApiResponse<T> r = new ApiResponse<>();
            r.setSuccess(true);
            r.setMessage(message);
            r.setData(data);
            return r;
        }

        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> r = new ApiResponse<>();
            r.setSuccess(false);
            r.setMessage(message);
            return r;
        }
    }
}

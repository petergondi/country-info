package com.ncba.countryservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "country_info")
public class CountryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String countryName;

    @Column(unique = true)
    private String isoCode;

    private String capitalCity;
    private String phoneCode;
    private String continentCode;
    private String currencyIsoCode;
    private String currencyName;
    private String countryFlag;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public CountryInfo() {}

    private CountryInfo(Builder b) {
        this.countryName   = b.countryName;
        this.isoCode       = b.isoCode;
        this.capitalCity   = b.capitalCity;
        this.phoneCode     = b.phoneCode;
        this.continentCode = b.continentCode;
        this.currencyIsoCode = b.currencyIsoCode;
        this.currencyName  = b.currencyName;
        this.countryFlag   = b.countryFlag;
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String countryName, isoCode, capitalCity, phoneCode;
        private String continentCode, currencyIsoCode, currencyName, countryFlag;

        public Builder countryName(String v)    { this.countryName = v; return this; }
        public Builder isoCode(String v)        { this.isoCode = v; return this; }
        public Builder capitalCity(String v)    { this.capitalCity = v; return this; }
        public Builder phoneCode(String v)      { this.phoneCode = v; return this; }
        public Builder continentCode(String v)  { this.continentCode = v; return this; }
        public Builder currencyIsoCode(String v){ this.currencyIsoCode = v; return this; }
        public Builder currencyName(String v)   { this.currencyName = v; return this; }
        public Builder countryFlag(String v)    { this.countryFlag = v; return this; }
        public CountryInfo build()              { return new CountryInfo(this); }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

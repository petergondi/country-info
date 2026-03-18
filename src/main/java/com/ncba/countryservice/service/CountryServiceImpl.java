package com.ncba.countryservice.service;

import com.ncba.countryservice.dto.CountryDTOs.*;
import com.ncba.countryservice.exception.CountryNotFoundException;
import com.ncba.countryservice.exception.DuplicateCountryException;
import com.ncba.countryservice.exception.InvalidCountryException;
import com.ncba.countryservice.exception.SoapIntegrationException;
import com.ncba.countryservice.model.CountryInfo;
import com.ncba.countryservice.repository.CountryInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    // Exact message returned by the SOAP service for unrecognised country names
    private static final String SOAP_NOT_FOUND_RESPONSE = "No country found by that name";

    private final CountryInfoRepository repository;
    private final SoapClientService soapClientService;

    public CountryServiceImpl(CountryInfoRepository repository, SoapClientService soapClientService) {
        this.repository = repository;
        this.soapClientService = soapClientService;
    }

    @Override
    @Transactional
    public CountryResponse fetchAndSaveCountry(String rawName) {
        String countryName = toSentenceCase(rawName);
        log.info("Processing country request for: {}", countryName);

        // Single DB call — reuse the result instead of querying twice
        Optional<CountryInfo> existing = repository.findByCountryNameIgnoreCase(countryName);
        if (existing.isPresent()) {
            log.info("Country '{}' already exists. Returning existing record.", countryName);
            return toResponse(existing.get());
        }

        // SoapIntegrationException bubbles up → 503
        String isoCode = soapClientService.getCountryIsoCode(countryName);

        // SOAP returns a message string instead of null for unrecognised countries
        if (isoCode == null || isoCode.isBlank() || isoCode.equalsIgnoreCase(SOAP_NOT_FOUND_RESPONSE)) {
            log.warn("Unrecognised country name: {}", countryName);
            throw new InvalidCountryException(
                    "'" + countryName + "' is not a recognised country name.");
        }

        if (repository.existsByIsoCode(isoCode)) {
            log.warn("Country with ISO code '{}' already exists.", isoCode);
            throw new DuplicateCountryException("Country with ISO code '" + isoCode + "' already exists.");
        }

        // SoapIntegrationException bubbles up → 503
        Map<String, String> fullInfo = soapClientService.getFullCountryInfo(isoCode);

        if (fullInfo == null || fullInfo.isEmpty()) {
            log.error("SOAP returned empty country info for ISO code: {}", isoCode);
            throw new SoapIntegrationException(
                    "No country information returned for ISO code: " + isoCode);
        }

        CountryInfo country = CountryInfo.builder()
                .countryName(countryName)
                .isoCode(isoCode)
                .capitalCity(fullInfo.getOrDefault("sCapitalCity", ""))
                .phoneCode(fullInfo.getOrDefault("sPhoneCode", ""))
                .continentCode(fullInfo.getOrDefault("sContinentCode", ""))
                .currencyIsoCode(fullInfo.getOrDefault("sCurrencyISOCode", ""))
                .currencyName(fullInfo.getOrDefault("sCurrencyName", ""))
                .countryFlag(fullInfo.getOrDefault("sCountryFlag", ""))
                .build();

        CountryInfo saved = repository.save(country);
        log.info("Successfully saved country: {} (ISO: {})", countryName, isoCode);
        return toResponse(saved);
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        log.info("Fetching all countries");
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CountryResponse getCountryById(Long id) {
        log.info("Fetching country by id: {}", id);
        CountryInfo country = repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + id));
        return toResponse(country);
    }

    @Override
    @Transactional
    public CountryResponse updateCountry(Long id, CountryUpdateRequest request) {
        log.info("Updating country with id: {}", id);
        CountryInfo country = repository.findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + id));

        if (request.getCountryName() != null)     country.setCountryName(toSentenceCase(request.getCountryName()));
        if (request.getCapitalCity() != null)     country.setCapitalCity(request.getCapitalCity());
        if (request.getPhoneCode() != null)       country.setPhoneCode(request.getPhoneCode());
        if (request.getContinentCode() != null)   country.setContinentCode(request.getContinentCode());
        if (request.getCurrencyIsoCode() != null) country.setCurrencyIsoCode(request.getCurrencyIsoCode());
        if (request.getCurrencyName() != null)    country.setCurrencyName(request.getCurrencyName());
        if (request.getCountryFlag() != null)     country.setCountryFlag(request.getCountryFlag());

        CountryInfo updated = repository.save(country);
        log.info("Country updated: {}", updated.getCountryName());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCountry(Long id) {
        log.info("Deleting country with id: {}", id);
        if (!repository.existsById(id)) {
            throw new CountryNotFoundException("Country not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Country with id {} deleted successfully", id);
    }

    // Helper

    private String toSentenceCase(String input) {
        if (input == null || input.isBlank()) return input;
        String trimmed = input.trim().toLowerCase();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }

    private CountryResponse toResponse(CountryInfo c) {
        CountryResponse r = new CountryResponse();
        r.setId(c.getId());
        r.setCountryName(c.getCountryName());
        r.setIsoCode(c.getIsoCode());
        r.setCapitalCity(c.getCapitalCity());
        r.setPhoneCode(c.getPhoneCode());
        r.setContinentCode(c.getContinentCode());
        r.setCurrencyIsoCode(c.getCurrencyIsoCode());
        r.setCurrencyName(c.getCurrencyName());
        r.setCountryFlag(c.getCountryFlag());
        r.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        r.setUpdatedAt(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null);
        return r;
    }
}
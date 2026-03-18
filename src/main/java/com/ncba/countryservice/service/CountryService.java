package com.ncba.countryservice.service;

import com.ncba.countryservice.dto.CountryDTOs.*;
import java.util.List;

public interface CountryService {
    CountryResponse fetchAndSaveCountry(String rawName);
    List<CountryResponse> getAllCountries();
    CountryResponse getCountryById(Long id);
    CountryResponse updateCountry(Long id, CountryUpdateRequest request);
    void deleteCountry(Long id);
}
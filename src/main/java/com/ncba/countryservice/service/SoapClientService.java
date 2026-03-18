package com.ncba.countryservice.service;

import java.util.Map;

public interface SoapClientService {
    String getCountryIsoCode(String countryName);
    Map<String, String> getFullCountryInfo(String isoCode);
}
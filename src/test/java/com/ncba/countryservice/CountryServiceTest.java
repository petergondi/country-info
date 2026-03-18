package com.ncba.countryservice;

import com.ncba.countryservice.dto.CountryDTOs.*;
import com.ncba.countryservice.exception.CountryNotFoundException;
import com.ncba.countryservice.model.CountryInfo;
import com.ncba.countryservice.repository.CountryInfoRepository;
import com.ncba.countryservice.service.CountryServiceImpl;
import com.ncba.countryservice.service.SoapClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock private CountryInfoRepository repository;
    @Mock private SoapClientService soapClientService;
    @InjectMocks private CountryServiceImpl countryService;

    private CountryInfo sampleCountry;

    @BeforeEach
    void setUp() {
        sampleCountry = new CountryInfo();
        sampleCountry.setCountryName("Kenya");
        sampleCountry.setIsoCode("KE");
        sampleCountry.setCapitalCity("Nairobi");
        sampleCountry.setPhoneCode("+254");
        sampleCountry.setContinentCode("AF");
        sampleCountry.setCurrencyIsoCode("KES");
        sampleCountry.setCurrencyName("Kenyan Shilling");
        sampleCountry.setCountryFlag("https://example.com/ke.png");
    }

    @Test
    void fetchAndSaveCountry_shouldConvertToSentenceCase() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(Map.of(
                "sCapitalCity", "Nairobi", "sPhoneCode", "+254",
                "sContinentCode", "AF", "sCurrencyISOCode", "KES",
                "sCurrencyName", "Kenyan Shilling", "sCountryFlag", ""
        ));
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryResponse response = countryService.fetchAndSaveCountry("kenya");

        assertThat(response).isNotNull();
        verify(soapClientService).getCountryIsoCode("Kenya");
    }

    @Test
    void getAllCountries_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(sampleCountry));
        List<CountryResponse> result = countryService.getAllCountries();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountryName()).isEqualTo("Kenya");
    }

    @Test
    void getCountryById_shouldReturnCountry() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCountry));
        CountryResponse result = countryService.getCountryById(1L);
        assertThat(result.getIsoCode()).isEqualTo("KE");
    }

    @Test
    void getCountryById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> countryService.getCountryById(99L))
                .isInstanceOf(CountryNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteCountry_shouldThrowWhenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> countryService.deleteCountry(99L))
                .isInstanceOf(CountryNotFoundException.class);
    }
}
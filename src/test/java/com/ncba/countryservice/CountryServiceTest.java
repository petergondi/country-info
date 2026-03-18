package com.ncba.countryservice;

import com.ncba.countryservice.dto.CountryDTOs.*;
import com.ncba.countryservice.exception.CountryNotFoundException;
import com.ncba.countryservice.exception.DuplicateCountryException;
import com.ncba.countryservice.exception.InvalidCountryException;
import com.ncba.countryservice.exception.SoapIntegrationException;
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

import java.util.Collections;
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
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(fullInfoMap());
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryResponse response = countryService.fetchAndSaveCountry("kenya");

        assertThat(response).isNotNull();
        verify(soapClientService).getCountryIsoCode("Kenya");
    }

    @Test
    void fetchAndSaveCountry_shouldConvertAllCapsToSentenceCase() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(fullInfoMap());
        when(repository.save(any())).thenReturn(sampleCountry);

        countryService.fetchAndSaveCountry("KENYA");

        verify(soapClientService).getCountryIsoCode("Kenya");
    }

    @Test
    void fetchAndSaveCountry_shouldReturnExistingRecordWithoutCallingSoap() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.of(sampleCountry));

        CountryResponse response = countryService.fetchAndSaveCountry("kenya");

        assertThat(response.getCountryName()).isEqualTo("Kenya");
        verifyNoInteractions(soapClientService);
        verify(repository, never()).save(any());
    }

    @Test
    void fetchAndSaveCountry_shouldThrowInvalidCountryWhenSoapReturnsNull() {
        when(repository.findByCountryNameIgnoreCase("Zzzzzz")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Zzzzzz")).thenReturn(null);

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("zzzzzz"))
                .isInstanceOf(InvalidCountryException.class)
                .hasMessageContaining("Zzzzzz");
    }

    @Test
    void fetchAndSaveCountry_shouldThrowInvalidCountryWhenSoapReturnsNotFoundMessage() {
        when(repository.findByCountryNameIgnoreCase("Zzzzzz")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Zzzzzz")).thenReturn("No country found by that name");

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("zzzzzz"))
                .isInstanceOf(InvalidCountryException.class)
                .hasMessageContaining("Zzzzzz");
    }

    @Test
    void fetchAndSaveCountry_shouldThrowInvalidCountryWhenSoapReturnsBlankIsoCode() {
        when(repository.findByCountryNameIgnoreCase("Zzzzzz")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Zzzzzz")).thenReturn("   ");

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("zzzzzz"))
                .isInstanceOf(InvalidCountryException.class);
    }

    @Test
    void fetchAndSaveCountry_shouldThrowDuplicateWhenIsoCodeAlreadyExists() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(true);

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("kenya"))
                .isInstanceOf(DuplicateCountryException.class)
                .hasMessageContaining("KE");
    }

    @Test
    void fetchAndSaveCountry_shouldThrowSoapIntegrationWhenFullInfoIsEmpty() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(Collections.emptyMap());

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("kenya"))
                .isInstanceOf(SoapIntegrationException.class)
                .hasMessageContaining("KE");
    }

    @Test
    void fetchAndSaveCountry_shouldThrowSoapIntegrationWhenFullInfoIsNull() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(null);

        assertThatThrownBy(() -> countryService.fetchAndSaveCountry("kenya"))
                .isInstanceOf(SoapIntegrationException.class);
    }

    @Test
    void fetchAndSaveCountry_shouldPersistAndReturnFullData() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(fullInfoMap());
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryResponse response = countryService.fetchAndSaveCountry("kenya");

        assertThat(response.getIsoCode()).isEqualTo("KE");
        assertThat(response.getCountryName()).isEqualTo("Kenya");
        assertThat(response.getCapitalCity()).isEqualTo("Nairobi");
        assertThat(response.getCurrencyIsoCode()).isEqualTo("KES");
        verify(repository, times(1)).save(any(CountryInfo.class));
    }

    @Test
    void fetchAndSaveCountry_shouldCallFullInfoWithCorrectIsoCode() {
        when(repository.findByCountryNameIgnoreCase("Kenya")).thenReturn(Optional.empty());
        when(soapClientService.getCountryIsoCode("Kenya")).thenReturn("KE");
        when(repository.existsByIsoCode("KE")).thenReturn(false);
        when(soapClientService.getFullCountryInfo("KE")).thenReturn(fullInfoMap());
        when(repository.save(any())).thenReturn(sampleCountry);

        countryService.fetchAndSaveCountry("kenya");

        verify(soapClientService).getCountryIsoCode("Kenya");
        verify(soapClientService).getFullCountryInfo("KE");
    }


    @Test
    void getAllCountries_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(sampleCountry));
        List<CountryResponse> result = countryService.getAllCountries();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountryName()).isEqualTo("Kenya");
    }

    @Test
    void getAllCountries_shouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        List<CountryResponse> result = countryService.getAllCountries();
        assertThat(result).isEmpty();
    }

    @Test
    void getAllCountries_shouldMapAllFieldsCorrectly() {
        when(repository.findAll()).thenReturn(List.of(sampleCountry));
        CountryResponse r = countryService.getAllCountries().get(0);
        assertThat(r.getCountryName()).isEqualTo("Kenya");
        assertThat(r.getIsoCode()).isEqualTo("KE");
        assertThat(r.getCapitalCity()).isEqualTo("Nairobi");
        assertThat(r.getPhoneCode()).isEqualTo("+254");
        assertThat(r.getContinentCode()).isEqualTo("AF");
        assertThat(r.getCurrencyIsoCode()).isEqualTo("KES");
        assertThat(r.getCurrencyName()).isEqualTo("Kenyan Shilling");
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
    void updateCountry_shouldUpdateProvidedFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCountry));
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryUpdateRequest request = new CountryUpdateRequest();
        request.setCapitalCity("Mombasa");
        request.setCurrencyName("KES Updated");

        CountryResponse result = countryService.updateCountry(1L, request);

        assertThat(result).isNotNull();
        verify(repository).save(argThat(c ->
                "Mombasa".equals(c.getCapitalCity()) &&
                        "KES Updated".equals(c.getCurrencyName())
        ));
    }

    @Test
    void updateCountry_shouldNotOverwriteNullFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCountry));
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryUpdateRequest request = new CountryUpdateRequest();
        request.setPhoneCode("001");
        // all other fields null — should remain unchanged

        countryService.updateCountry(1L, request);

        verify(repository).save(argThat(c ->
                "Nairobi".equals(c.getCapitalCity()) && // unchanged
                        "001".equals(c.getPhoneCode())          // updated
        ));
    }

    @Test
    void updateCountry_shouldConvertNameToSentenceCase() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCountry));
        when(repository.save(any())).thenReturn(sampleCountry);

        CountryUpdateRequest request = new CountryUpdateRequest();
        request.setCountryName("east africa");

        countryService.updateCountry(1L, request);

        verify(repository).save(argThat(c -> "East africa".equals(c.getCountryName())));
    }

    @Test
    void updateCountry_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        CountryUpdateRequest request = new CountryUpdateRequest();
        request.setCapitalCity("Mombasa");

        assertThatThrownBy(() -> countryService.updateCountry(99L, request))
                .isInstanceOf(CountryNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    void deleteCountry_shouldDeleteSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);
        countryService.deleteCountry(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCountry_shouldThrowWhenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> countryService.deleteCountry(99L))
                .isInstanceOf(CountryNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    private Map<String, String> fullInfoMap() {
        return Map.of(
                "sCapitalCity",    "Nairobi",
                "sPhoneCode",      "+254",
                "sContinentCode",  "AF",
                "sCurrencyISOCode","KES",
                "sCurrencyName",   "Kenyan Shilling",
                "sCountryFlag",    "https://example.com/ke.png"
        );
    }
}
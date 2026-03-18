package com.ncba.countryservice.repository;

import com.ncba.countryservice.model.CountryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryInfoRepository extends JpaRepository<CountryInfo, Long> {
    Optional<CountryInfo> findByIsoCode(String isoCode);
    Optional<CountryInfo> findByCountryNameIgnoreCase(String countryName);
    boolean existsByIsoCode(String isoCode);
}

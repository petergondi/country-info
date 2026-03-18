package com.ncba.countryservice.config;

import com.ncba.countryservice.service.SoapClientService;
import com.ncba.countryservice.service.SoapClientServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SoapClientConfig {

    @Value("${soap.endpoint.url}")
    private String endpointUrl;

    /**
     * Minimal marshaller — our SOAP client uses raw DOM/XML,
     * so no generated classes contextPath is needed.
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.ncba.countryservice");
        return marshaller;
    }

    @Bean
    public SoapClientService soapClientService(Jaxb2Marshaller marshaller) {
        SoapClientServiceImpl client = new SoapClientServiceImpl();
        client.setDefaultUri(endpointUrl);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
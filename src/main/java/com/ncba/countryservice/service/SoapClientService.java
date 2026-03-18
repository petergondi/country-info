package com.ncba.countryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import java.util.Map;

public class SoapClientService extends WebServiceGatewaySupport {

    private static final Logger log = LoggerFactory.getLogger(SoapClientService.class);
    private static final String NAMESPACE = "http://www.oorsprong.org/websamples.countryinfo";

    public String getCountryIsoCode(String countryName) {
        log.info("Calling SOAP: CountryISOCode for country: {}", countryName);
        try {
            Document requestDoc = buildRequest("CountryISOCode", "sCountryName", countryName);
            DOMResult result = new DOMResult();
            getWebServiceTemplate().sendSourceAndReceiveToResult(
                    getDefaultUri(),
                    new DOMSource(requestDoc),
                    new SoapActionCallback(""),
                    result
            );
            String isoCode = extractValue((Document) result.getNode(), "CountryISOCodeResult");
            log.info("Received ISO code: {} for country: {}", isoCode, countryName);
            return isoCode;
        } catch (Exception e) {
            log.error("SOAP error fetching ISO code for {}: {}", countryName, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch ISO code for country: " + countryName, e);
        }
    }

    public Map<String, String> getFullCountryInfo(String isoCode) {
        log.info("Calling SOAP: FullCountryInfo for ISO code: {}", isoCode);
        try {
            Document requestDoc = buildRequest("FullCountryInfo", "sCountryISOCode", isoCode);
            DOMResult result = new DOMResult();
            getWebServiceTemplate().sendSourceAndReceiveToResult(
                    getDefaultUri(),
                    new DOMSource(requestDoc),
                    new SoapActionCallback(""),
                    result
            );
            Map<String, String> countryData = parseFullCountryInfo((Document) result.getNode());
            log.info("Received full country info for ISO code: {}", isoCode);
            return countryData;
        } catch (Exception e) {
            log.error("SOAP error fetching full country info for ISO {}: {}", isoCode, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch full country info for ISO: " + isoCode, e);
        }
    }

    private Document buildRequest(String operation, String paramName, String paramValue) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElementNS(NAMESPACE, operation);
        Element param = doc.createElementNS(NAMESPACE, paramName);
        param.setTextContent(paramValue);
        root.appendChild(param);
        doc.appendChild(root);
        return doc;
    }

    private String extractValue(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagNameNS("*", tagName);
        if (nodes.getLength() > 0) return nodes.item(0).getTextContent().trim();
        nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) return nodes.item(0).getTextContent().trim();
        log.warn("Tag not found in SOAP response: {}", tagName);
        return null;
    }

    private Map<String, String> parseFullCountryInfo(Document doc) {
        Map<String, String> info = new HashMap<>();
        String[] fields = {"sISOCode","sName","sCapitalCity","sPhoneCode","sContinentCode","sCurrencyISOCode","sCurrencyName","sCountryFlag"};
        for (String field : fields) {
            String value = extractValue(doc, field);
            if (value != null) info.put(field, value);
        }
        log.debug("Parsed country info fields: {}", info);
        return info;
    }
}

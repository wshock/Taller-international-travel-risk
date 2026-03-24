package edu.unac.service.external;

import edu.unac.domain.country.Country;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class CountryClient {
    private final RestTemplate restTemplate;

    private static final String URL =
            "https://restcountries.com/v3.1/alpha/%s";

    public CountryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Country> getCountry(String countryCode) {

        String finalUrl = buildUrl(countryCode);

        Country[] response =
                restTemplate.getForObject(finalUrl, Country[].class);

        if (response == null) {
            return List.of();
        }

        return Arrays.asList(response);
    }

    private String buildUrl(String countryCode){
        return String.format(URL, countryCode);
    }
}

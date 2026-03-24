package edu.unac.service.external;

import edu.unac.domain.country.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountryClientTest {
    private RestTemplate restTemplate;
    private CountryClient countryClient;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        countryClient = new CountryClient(restTemplate);
    }

    @Test
    void shouldReturnCountryListWhenApiRespondsCorrectly() {

        Country country = Country.builder()
                .population(50000000)
                .languages(Map.of("spa", "Spanish"))
                .build();

        Country[] response = new Country[]{country};

        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        List<Country> result = countryClient.getCountry("CO");

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(50000000, result.getFirst().getPopulation());
        assertEquals("Spanish", result.getFirst().getLanguages().get("spa"));
    }

    @Test
    void shouldHandleMultipleCountriesInResponse() {

        Country country1 = Country.builder()
                .population(1000000)
                .languages(Map.of())
                .build();

        Country country2 = Country.builder()
                .population(2000000)
                .languages(Map.of())
                .build();

        Country[] response = new Country[]{country1, country2};

        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        List<Country> result = countryClient.getCountry("FR");

        assertEquals(2, result.size());
        assertEquals(1000000, result.getFirst().getPopulation());
        assertEquals(2000000, result.get(1).getPopulation());
    }
}
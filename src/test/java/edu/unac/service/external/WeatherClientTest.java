package edu.unac.service.external;
import edu.unac.domain.weather.Daily;
import edu.unac.domain.weather.WeatherResponse;
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

public class WeatherClientTest {

    private WeatherClient WeatherClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        WeatherClient = new WeatherClient(restTemplate);
    }
    @Test
    void shouldReturnWeatherWhenApiRespondsCorrectly() {

        Daily daily = Daily.builder()
                .temperature_2m_max(List.of(25.0))
                .precipitation_probability_max(List.of(60))
                .build();

        WeatherResponse response = WeatherResponse.builder()
                .daily(daily)
                .build();

        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(response);

        WeatherResponse result = WeatherClient.getWeather(4.71, -74.07);

        Assertions.assertNotNull(result);
        assertEquals(25.0, result.getDaily().getTemperature_2m_max().getFirst());
        assertEquals(60, result.getDaily().getPrecipitation_probability_max().getFirst());
    }
    @Test
    void shouldReturnEmptyWeatherWhenResponseIsNull(){
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
        WeatherResponse result = WeatherClient.getWeather(4.71, -74.07);

        Assertions.assertNotNull(result);
        Assertions.assertNull(result.getDaily());
    }

}

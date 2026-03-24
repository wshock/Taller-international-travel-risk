package edu.unac.service.external;

import edu.unac.domain.weather.WeatherResponse;
import org.springframework.web.client.RestTemplate;

public class WeatherClient {
    private final RestTemplate restTemplate;

    private static final String URL =
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&daily=temperature_2m_max,precipitation_probability_max";

    public WeatherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeather(double latitude, double longitude) {

        String finalUrl = String.format(URL, latitude, longitude);

        WeatherResponse response =
                restTemplate.getForObject(finalUrl, WeatherResponse.class);

        if (response == null) {
            return new WeatherResponse();
        }

        return response;
    }
}

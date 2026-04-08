package edu.unac.service;

import edu.unac.domain.exception.ExternalServiceException;
import edu.unac.domain.travel.RiskLevel;
import edu.unac.domain.travel.TravelRequest;
import edu.unac.domain.travel.TravelRiskResponse;
import edu.unac.domain.weather.WeatherResponse;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TravelRiskAssessmentService {
    private final WeatherClient weatherClient;
    private final HolidayClient holidayClient;
    private final CountryClient countryClient;

    public TravelRiskAssessmentService(WeatherClient weatherClient, HolidayClient holidayClient, CountryClient countryClient) {
        this.weatherClient = weatherClient;
        this.holidayClient = holidayClient;
        this.countryClient = countryClient;
    }

    public TravelRiskResponse assessRisk(TravelRequest request) {
        return new TravelRiskResponse();
    }

    public TravelRiskResponse weatherValidation(double latitude, double longitude){
        try {
            WeatherResponse weatherResponse = this.weatherClient.getWeather(latitude, longitude);
            if (weatherResponse == null) throw new ExternalServiceException("Error en la API");

            double max_temperature = Collections.max(weatherResponse.getDaily().getTemperature_2m_max());
            if (max_temperature < 0) {
                return new TravelRiskResponse(RiskLevel.HIGH_RISK, "Extreme sub-zero temperatures detected");
            }

            double max_precipitation = Collections.max(weatherResponse.getDaily().getPrecipitation_probability_max());
            if (max_precipitation > 80) {
                return new TravelRiskResponse(RiskLevel.MEDIUM_RISK, "High probability of rain during the trip");
            }

            return new TravelRiskResponse(RiskLevel.SAFE, "Optimal conditions for travel");
        } catch (Exception e) {
            throw new ExternalServiceException("Error en la API");
        }
    }
}

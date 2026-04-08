package edu.unac.service;

import edu.unac.domain.country.Country;
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
import java.util.List;

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
        TravelRiskResponse response = new TravelRiskResponse(RiskLevel.SAFE, "Optimal conditions for travel");



        // Validaciones iniciales



        // Evauación del clima
        if (!(request.getTravelerExperienceYears() > 10)){
            response = evaluateRiskPriority(
                    response, // Current response
                    weatherValidation(request.getLatitude(), request.getLongitude()) // New Weather response
            );
        }

        // Evaulación del pais
        response = evaluateRiskPriority(
                response, // Current response
                countryValidation(request.getCountryCode(), request.getTravelerExperienceYears()) // New Country response
        );

        // Evluación del budget


        // ¿Desea reason?
        if (!request.isIncludeReason()) response.setReason(null);






        return response;
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

    public TravelRiskResponse countryValidation(String countryCode, int travelerExperienceYears){
        List<Country> countries = countryClient.getCountry(countryCode);

        if (countries == null || countries.isEmpty()) {
            throw new ExternalServiceException("No se encontró el pais");
        }

        Country country = countries.get(0);


        if(country.getPopulation() > 100000000 && travelerExperienceYears < 2){
            return new TravelRiskResponse(RiskLevel.HIGH_RISK, "Destination with high population density and low traveler experience");

        }  // HAY UN ERROR AQUÍ
        else if (!country.getLanguages().containsKey("eng") || !country.getLanguages().containsKey("spa")){
            return new TravelRiskResponse(RiskLevel.MEDIUM_RISK, "The language of the destination may present a barrier");
        } else {
            return new TravelRiskResponse(RiskLevel.SAFE, "Optimal conditions for travel");
        }


    }

    public TravelRiskResponse evaluateRiskPriority(TravelRiskResponse currentReponse, TravelRiskResponse newResponse){
        if (newResponse.getRiskLevel() == RiskLevel.HIGH_RISK){
            return newResponse;
        }

        else if (newResponse.getRiskLevel() == RiskLevel.MEDIUM_RISK && currentReponse.getRiskLevel() != RiskLevel.HIGH_RISK) {
            return newResponse;
        }

        else if (newResponse.getRiskLevel() == RiskLevel.SAFE && currentReponse.getRiskLevel() != RiskLevel.SAFE) {
            return currentReponse;
        }

        else {
            return currentReponse;
        }

    }

}

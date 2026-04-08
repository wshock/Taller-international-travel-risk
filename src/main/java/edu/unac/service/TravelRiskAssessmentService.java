package edu.unac.service;

import edu.unac.domain.country.Country;
import edu.unac.domain.exception.ExternalServiceException;
import edu.unac.domain.travel.RiskLevel;
import edu.unac.domain.travel.TravelRequest;
import edu.unac.domain.travel.TravelRiskResponse;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;

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
        return new TravelRiskResponse();
    }

    public RiskLevel countryValidation(String countryCode, int travelerExperienceYears){
        List<Country> countries = countryClient.getCountry(countryCode);

        if (countries == null || countries.isEmpty()) {
            throw new ExternalServiceException("No se encontró el pais");
        }

        Country country = countries.get(0);


        if(country.getPopulation() > 100000000 && travelerExperienceYears < 2){
            return RiskLevel.HIGH_RISK;

        } else if (!country.getLanguages().containsKey("eng") || !country.getLanguages().containsKey("spa")){
            return RiskLevel.MEDIUM_RISK;
        } else {
            return RiskLevel.SAFE;
        }


    }

}

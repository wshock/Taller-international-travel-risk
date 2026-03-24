package edu.unac.service;

import edu.unac.domain.travel.TravelRequest;
import edu.unac.domain.travel.TravelRiskResponse;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;

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
}

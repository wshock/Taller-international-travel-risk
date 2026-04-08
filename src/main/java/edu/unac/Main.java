package edu.unac;

import edu.unac.service.TravelRiskAssessmentService;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;
import org.springframework.web.client.RestTemplate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {






        // Prueba para validar pais
        RestTemplate restTemplate = new RestTemplate();
        CountryClient countryClient = new CountryClient(restTemplate);
        WeatherClient weatherClient = new WeatherClient(restTemplate);
        HolidayClient holidayClient = new HolidayClient(restTemplate);
        TravelRiskAssessmentService travelRiskAssessmentService = new TravelRiskAssessmentService(weatherClient, holidayClient, countryClient);
    }
}

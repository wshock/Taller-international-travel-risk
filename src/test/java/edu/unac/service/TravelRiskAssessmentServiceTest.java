package edu.unac.service;

import edu.unac.domain.exception.ExternalServiceException;
import edu.unac.domain.holiday.Holiday;
import edu.unac.domain.travel.RiskLevel;
import edu.unac.domain.travel.TravelRequest;
import edu.unac.domain.travel.TravelRiskResponse;
import edu.unac.domain.weather.Daily;
import edu.unac.domain.weather.WeatherResponse;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;
import org.junit.jupiter.api.BeforeEach;
import edu.unac.domain.country.Country;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class TravelRiskAssessmentServiceTest {
    private WeatherClient weatherClient;
    private HolidayClient holidayClient;
    private CountryClient countryClient;

    private TravelRiskAssessmentService service;

    @BeforeEach
    void setUp() {
        weatherClient = Mockito.mock(WeatherClient.class);
        holidayClient = Mockito.mock(HolidayClient.class);
        countryClient = Mockito.mock(CountryClient.class);

        service = new TravelRiskAssessmentService(
                weatherClient,
                holidayClient,
                countryClient
        );
    }

    private WeatherResponse buildWeather(double temp, int rain) {
        Daily daily = Daily.builder()
                .temperature_2m_max(
                        List.of(temp)
                )
                .precipitation_probability_max(
                        List.of(rain)
                )
                .build();

        return WeatherResponse.builder().daily(daily).build();
    }

    private Country buildCountry(long population, Map<String, String> languages) {
        return Country.builder()
                .population(population)
                .languages(languages)
                .build();
    }

    private TravelRequest buildRequest(boolean includeReason, int experience, double budget) {
        return TravelRequest.builder()
                .countryCode("CO")
                .travelDate(LocalDate.of(2026, 1, 1))
                .travelerExperienceYears(experience)
                .budget(budget)
                .includeReason(includeReason)
                .latitude(4.71)
                .longitude(-74.07)
                .build();
    }

    private Holiday buildHoliday(String date) {
        return Holiday.builder().date(date).build();
    }

    @Test
    void shouldReturnHighRiskWhenTemperatureBelowZero() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(-5, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5000000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 1, 5000)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Extreme sub-zero temperatures detected", result.getReason());

        verify(weatherClient).getWeather(anyDouble(), anyDouble());
        verify(holidayClient).getHolidays(anyInt(), anyString());
        verify(countryClient).getCountry(anyString());
    }

    @Test
    void shouldReturnMediumRiskWhenRainIsHigh() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 90));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5000000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 1, 5000)
        );

        assertEquals(RiskLevel.MEDIUM_RISK, result.getRiskLevel());
        assertEquals("High probability of rain during the trip", result.getReason());
    }

    @Test
    void shouldReturnHighRiskWhenTooManyHolidaysInWeek() {

        List<Holiday> holidays = List.of(
                buildHoliday("2026-01-01"),
                buildHoliday("2026-01-02"),
                buildHoliday("2026-01-03")
        );

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(holidays);

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5000000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 1, 5000)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("High concentration of holidays during the week of travel", result.getReason());
    }

    @Test
    void shouldReturnMediumRiskWhenExactHoliday() {

        List<Holiday> holidays = List.of(
                buildHoliday("2026-01-01")
        );

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(holidays);

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5000000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 1, 5000)
        );

        assertEquals(RiskLevel.MEDIUM_RISK, result.getRiskLevel());
        assertEquals("The trip coincides with a national holiday", result.getReason());
    }

    @Test
    void shouldReturnHighRiskWhenPopulationHighAndLowExperience() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(200_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 1, 5000)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Destination with high population density and low traveler experience", result.getReason());
    }

    @Test
    void shouldReturnMediumRiskWhenLanguageNotSupported() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5000000, Map.of("ger", "German"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 3, 5000)
        );

        assertEquals(RiskLevel.MEDIUM_RISK, result.getRiskLevel());
        assertEquals("The language of the destination may present a barrier", result.getReason());
    }

    @Test
    void shouldReturnHighRiskWhenBudgetTooLow() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(20, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 3, 500)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Insufficient budget for the destination", result.getReason());
    }

    @Test
    void shouldReturnSafeWhenNoConditionsApply() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 5000)
        );

        assertEquals(RiskLevel.SAFE, result.getRiskLevel());
        assertEquals("Optimal conditions for travel", result.getReason());
    }

    @Test
    void shouldIgnoreWeatherWhenExperienceGreaterThan10() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(-10, 90));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 15, 5000)
        );

        assertEquals(RiskLevel.SAFE, result.getRiskLevel());
    }

    @Test
    void shouldReturnNullReasonWhenIncludeReasonIsFalse() {

        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(false, 5, 5000)
        );

        assertNull(result.getReason());
    }


    // =========================
    // NEW TESTS HERE
    // =========================

    @Test
    void shouldThrowExceptionIfWeatherResponseIsNull() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(null);

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(5_000_000, Map.of("spa", "Spanish"))));


        assertThrows(ExternalServiceException.class,
                () -> service.assessRisk(buildRequest(true, 5, 5000)));
    }

    @Test
    void shouldReturnHighRiskIfPopulationGreaterThan100MAndBudgetLessThan3000() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(200_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 2000)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Insufficient budget for the destination", result.getReason());

    }

    @Test
    void shouldReturnHighRiskIfPopulationGBetween10MAnd100MAndBudgetLessThan2000() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(90_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 1000)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Insufficient budget for the destination", result.getReason());

    }

    @Test
    void shouldReturnSafeIfPopulationBetween10MAnd100MAndBudgetIsAtLeast2000() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(90_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 2000)
        );

        assertEquals(RiskLevel.SAFE, result.getRiskLevel());
        assertEquals("Optimal conditions for travel", result.getReason());
    }

    @Test
    void shouldReturnHighRiskIfPopulationIsExactly10MAndBudgetLessThan2000() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(10_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 1500)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Insufficient budget for the destination", result.getReason());
    }

    @Test
    void shouldReturnHighRiskIfPopulationIsExactly100MAndBudgetLessThan2000() {
        when(weatherClient.getWeather(anyDouble(), anyDouble()))
                .thenReturn(buildWeather(25, 10));

        when(holidayClient.getHolidays(anyInt(), anyString()))
                .thenReturn(List.of());

        when(countryClient.getCountry(anyString()))
                .thenReturn(List.of(buildCountry(100_000_000, Map.of("spa", "Spanish"))));

        TravelRiskResponse result = service.assessRisk(
                buildRequest(true, 5, 1500)
        );

        assertEquals(RiskLevel.HIGH_RISK, result.getRiskLevel());
        assertEquals("Insufficient budget for the destination", result.getReason());
    }

}
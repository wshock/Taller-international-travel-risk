package edu.unac.service;

import edu.unac.domain.holiday.Holiday;
import edu.unac.domain.travel.RiskLevel;
import edu.unac.domain.travel.TravelRequest;
import edu.unac.domain.travel.TravelRiskResponse;
import edu.unac.service.external.CountryClient;
import edu.unac.service.external.HolidayClient;
import edu.unac.service.external.WeatherClient;

import java.time.LocalDate;
import java.util.List;

public class TravelRiskAssessmentService {

    private final WeatherClient weatherClient;
    private final HolidayClient holidayClient;
    private final CountryClient countryClient;

    public TravelRiskAssessmentService(WeatherClient weatherClient,
                                       HolidayClient holidayClient,
                                       CountryClient countryClient) {
        this.weatherClient = weatherClient;
        this.holidayClient = holidayClient;
        this.countryClient = countryClient;
    }

    public TravelRiskResponse assessRisk(TravelRequest request) {

        List<Holiday> holidays = holidayClient.getHolidays(
                request.getTravelDate().getYear(),
                request.getCountryCode()
        );

        TravelRiskResponse holidayResult = evaluateHolidays(request, holidays);

        if (holidayResult != null) {
            return new TravelRiskResponse(
                    holidayResult.getRiskLevel(),
                    request.isIncludeReason() ? holidayResult.getReason() : null
            );
        }

        return new TravelRiskResponse(
                RiskLevel.SAFE,
                request.isIncludeReason() ? "Optimal conditions for travel" : null
        );
    }

    private TravelRiskResponse evaluateHolidays(TravelRequest request, List<Holiday> holidays) {

        LocalDate travelDate = request.getTravelDate();

        List<LocalDate> holidayDates = holidays.stream()
                .map(h -> LocalDate.parse(h.getDate()))
                .toList();

        long count = holidayDates.stream()
                .filter(date ->
                        !date.isBefore(travelDate) &&
                                !date.isAfter(travelDate.plusDays(7))
                )
                .count();

        if (count >= 3) {
            return new TravelRiskResponse(
                    RiskLevel.HIGH_RISK,
                    "High concentration of holidays during the week of travel"
            );
        }
        if (holidayDates.contains(travelDate)) {
            return new TravelRiskResponse(
                    RiskLevel.MEDIUM_RISK,
                    "The trip coincides with a national holiday"
            );
        }

        return null;
    }
}
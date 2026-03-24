package edu.unac.service.external;

import edu.unac.domain.holiday.Holiday;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class HolidayClient {
    private final RestTemplate restTemplate;

    private static final String URL =
            "https://date.nager.at/api/v3/PublicHolidays/%s/%s";

    public HolidayClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Holiday> getHolidays(int year, String countryCode) {

        String finalUrl = String.format(URL, year, countryCode);

        Holiday[] response =
                restTemplate.getForObject(finalUrl, Holiday[].class);

        if (response == null) {
            return List.of();
        }

        return Arrays.asList(response);
    }
}

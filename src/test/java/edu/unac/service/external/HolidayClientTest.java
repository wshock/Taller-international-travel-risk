package edu.unac.service.external;

import edu.unac.domain.holiday.Holiday;
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

public class HolidayClientTest {
    private HolidayClient HolidayClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        HolidayClient = new HolidayClient(restTemplate);
    }
    @Test
    void shouldReturnHolidayListWhenApiRespondsCorrectly(){
        Holiday holiday = Holiday.builder().date("2026-01-01")
                .name("New Year")
                .countryCode("CO").build();
        Holiday[] response = new Holiday[]{holiday};
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);
        List<Holiday> result = HolidayClient.getHolidays(2026, "CO");
        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2026-01-01", result.getFirst().getDate());
        assertEquals("New Year", result.getFirst().getName());
        assertEquals("CO", result.getFirst().getCountryCode());
    }
    @Test
    void shouldReturnNullListHoliday(){
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
        List<Holiday> result = HolidayClient.getHolidays(2026, "CO");
        Assertions.assertNotNull(result);

    }
    @Test
    void shouldHandleMultipleHolidaysInResponse() {

        Holiday h1 = Holiday.builder()
                .date("2026-01-01")
                .name("New Year")
                .countryCode("CO")
                .build();

        Holiday h2 = Holiday.builder()
                .date("2026-01-06")
                .name("Epiphany")
                .countryCode("CO")
                .build();

        Holiday[] response = new Holiday[]{h1, h2};

        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(response);

        List<Holiday> result = HolidayClient.getHolidays(2026, "CO");

        assertEquals(2, result.size());
        assertEquals("2026-01-01", result.getFirst().getDate());
        assertEquals("2026-01-06", result.get(1).getDate());
    }

}

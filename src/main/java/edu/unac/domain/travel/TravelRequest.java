package edu.unac.domain.travel;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TravelRequest {
    private String countryCode;
    private LocalDate travelDate;
    private double budget;
    private int travelerExperienceYears;
    private boolean includeReason;
    private double latitude;
    private double longitude;
}

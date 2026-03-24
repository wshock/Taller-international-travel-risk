package edu.unac.domain.travel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TravelRiskResponse {
    private RiskLevel riskLevel;
    private String reason;
}

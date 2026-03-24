package edu.unac.domain.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Daily {
    private List<Double> temperature_2m_max;
    private List<Integer> precipitation_probability_max;
}

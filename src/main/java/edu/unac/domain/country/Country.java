package edu.unac.domain.country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Country {
    private long population;
    private Map<String, String> languages;
}

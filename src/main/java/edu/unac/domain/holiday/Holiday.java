package edu.unac.domain.holiday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Holiday {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean global;
    private List<String> types;
}

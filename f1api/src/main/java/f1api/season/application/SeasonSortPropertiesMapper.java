package f1api.season.application;

import static java.util.Map.entry;

import f1api.sort.SortPropertiesMapper;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class SeasonSortPropertiesMapper implements SortPropertiesMapper {
    private final Map<String, String> sortProperties = Map.ofEntries(entry("seasonYear", "year"));
}

package f1api.team.application;

import static java.util.Map.entry;

import f1api.queryparameter.sort.SortPropertyMapper;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter
public class TeamSortPropertyMapper implements SortPropertyMapper {
    private final Map<String, String> sortProperties = Map.ofEntries(entry("teamName", "name"));
}

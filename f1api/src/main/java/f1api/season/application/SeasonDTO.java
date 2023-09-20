package f1api.season.application;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeasonDTO {
    private UUID seasonId;

    @NotNull
    private Year seasonYear;

    @JsonGetter("seasonYear")
    public int getSeasonYearValue() {
        return seasonYear.getValue();
    }

    @Getter
    @JsonIgnore
    private static final List<String> properties = Arrays.stream(SeasonDTO.class.getDeclaredFields())
            .map(Field::getName)
            .toList();
}

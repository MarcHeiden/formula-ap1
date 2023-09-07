package f1api.season.application;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
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
}

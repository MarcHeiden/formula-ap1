package api.driverofrace.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class FastestPitStop {
    // Use of LocalDateTime to store milliseconds in postgres as this did not work with LocalTime.
    private LocalDateTime duration;

    public LocalTime getDuration() {
        return duration.toLocalTime();
    }

    public String getDurationAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("mm:ss.SSS");
        return duration.format(dateTimeFormatter);
    }

    public static FastestPitStop ofDurationString(
            @NotNull @Pattern(regexp = "^([0-5]?[0-9]):([0-5][0-9])\\.([0-9]{3})$") String durationString) {
        if (durationString.matches("^([0-5][0-9]):([0-5][0-9])\\.([0-9]{3})$")) {
            durationString = "00:" + durationString;
        } else {
            durationString = "00:0" + durationString;
        }
        // Use dummy date 0001-01-01 as it is never accessed (see comment above)
        return new FastestPitStop(LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.parse(durationString)));
    }
}

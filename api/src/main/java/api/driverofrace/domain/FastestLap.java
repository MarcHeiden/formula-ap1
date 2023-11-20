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
public class FastestLap {
    // Use of LocalDateTime to store milliseconds in postgres as this did not work with LocalTime.
    private LocalDateTime time;

    public LocalTime getTime() {
        return time.toLocalTime();
    }

    public String getTimeAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("m:ss.SSS");
        return time.format(dateTimeFormatter);
    }

    public static FastestLap ofTimeString(
            @NotNull @Pattern(regexp = "^([0-9]):([0-5][0-9])\\.([0-9]{3})$") String timeString) {
        timeString = "00:0" + timeString;
        // Use dummy date 0001-01-01 as it is never accessed (see comment above)
        return new FastestLap(LocalDateTime.of(LocalDate.of(1, 1, 1), LocalTime.parse(timeString)));
    }
}

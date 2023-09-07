package f1api.season.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "Season", schema = "public")
public class Season {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "year", unique = true, nullable = false)
    private Year year;

    public Season(Year year) {
        this.year = year;
    }
}

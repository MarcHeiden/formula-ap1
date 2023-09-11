package f1api.season.domain;

import f1api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "Season", schema = "public")
public class Season extends BaseEntity {
    @NotNull
    @Column(name = "year", unique = true, nullable = false)
    private Year year;

    public Season(Year year) {
        this.year = year;
    }
}

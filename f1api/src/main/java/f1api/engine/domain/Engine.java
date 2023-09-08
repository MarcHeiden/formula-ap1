package f1api.engine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "Engine", schema = "public")
public class Engine {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Setter
    @NotNull
    @Column(name = "manufacturer", unique = true, nullable = false)
    private String manufacturer;

    public Engine(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}

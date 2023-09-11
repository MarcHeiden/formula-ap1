package f1api.engine.domain;

import f1api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "Engine", schema = "public")
public class Engine extends BaseEntity {
    @Setter
    @NotNull
    @Column(name = "manufacturer", unique = true, nullable = false)
    private String manufacturer;

    public Engine(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}

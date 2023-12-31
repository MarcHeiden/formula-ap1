package api.engine.domain;

import api.baseentity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "Engine", schema = "public")
public class Engine extends BaseEntity {
    @NotNull
    @Column(name = "manufacturer", unique = true, nullable = false)
    private String manufacturer;
}

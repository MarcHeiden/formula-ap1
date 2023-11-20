package api.baseentity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;

/**
 * Simple base entity with a generated uuid that other entities can extend.
 */
@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
}

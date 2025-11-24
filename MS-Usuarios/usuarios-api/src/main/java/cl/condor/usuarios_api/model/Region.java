package cl.condor.usuarios_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "region")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}

package cl.condor.rutas_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dificultad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dificultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dificultad")
    private Integer id_dificultad;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}

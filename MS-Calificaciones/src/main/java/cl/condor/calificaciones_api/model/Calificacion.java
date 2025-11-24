package cl.condor.calificaciones_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer idUsuario;  // id del usuario (referencia al microservicio Usuarios)

    @Column(nullable = false)
    private Integer idRuta;     // id de la ruta (referencia al microservicio Rutas)

    @Column(nullable = false)
    private Integer puntuacion; // Valor entre 1 y 5

    @Column(length = 120)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}

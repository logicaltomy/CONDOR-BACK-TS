package cl.condor.rutas_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer id_ruta;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "LONGTEXT")
    private String descripcion;

    @Column(name = "distancia", precision = 10, scale = 2)
    private BigDecimal distancia;

    @Column(name = "f_public")
    private LocalDateTime f_public;

    @Column(name = "f_baneo")
    private LocalDateTime f_baneo;

    @Column(name = "geometria_polyline", columnDefinition = "TEXT")
    private String geometriaPolyline;

    @Column(name = "tiempo_segundos")
    private Integer tiempoSegundos;

    @Column(name = "prom_calificacion", precision = 3, scale = 2)
    private BigDecimal prom_calificacion;

    @Column(name = "f_creacion", updatable = false, insertable = false)
    private LocalDateTime f_creacion;

    @Column(name = "f_actualizacion", insertable = false)
    private LocalDateTime f_actualizacion;

    @Column(name = "id_estado")
    private Integer id_estado;

    @Column(name = "id_region")
    private Integer id_region;

    @Column(name = "id_tipo")
    private Integer id_tipo;

    @Column(name = "id_dificultad")
    private Integer id_dificultad;

}



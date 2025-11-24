package cl.condor.logros_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "logro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logro")
    private Integer idLogro;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Lob
    @Column(name = "icono", columnDefinition = "LONGBLOB")
    private byte[] icono;

    @Column(name = "f_creacion", updatable = false, insertable = false)
    private java.time.LocalDateTime f_creacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "id_estado")
    private Integer id_estado;

    @Column(name = "id_condicion")
    private Integer id_condicion;
}

package cl.condor.logros_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trofeo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trofeo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trofeo")
    private Integer id_trofeo;

    @Column(name = "f_obtencion", nullable = false)
    private java.time.LocalDateTime f_obtencion;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_logro")
    private Integer idLogro;
}

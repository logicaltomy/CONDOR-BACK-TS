package cl.condor.usuarios_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "foto_perfil", columnDefinition = "LONGBLOB")
    private byte[] fotoPerfil;

    @Column(name = "rutas_recorridas", nullable = false)
    private Integer rutasRecorridas = 0;

    @Column(name = "km_recorridos", nullable = false, precision = 10, scale = 2)
    private BigDecimal kmRecorridos = BigDecimal.ZERO;

    @Column(name = "pregunta_seguridad1")
    private String preguntaSeguridad1;

    @Column(name = "respuesta_seguridad1")
    private String respuestaSeguridad1;

    @Column(name = "pregunta_seguridad2")
    private String preguntaSeguridad2;

    @Column(name = "respuesta_seguridad2")
    private String respuestaSeguridad2;

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "id_region")
    private Integer idRegion;

    @Column(name = "id_estado")
    private Integer idEstado;
}

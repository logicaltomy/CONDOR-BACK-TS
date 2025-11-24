package cl.condor.iniciar_rutas_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "abrir_ruta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AbrirRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abrir_ruta")
    private Integer id;

    // Ruta depende de tener un inicio y final (no debe incluirse como restriccion
    // ya que dichos datos que corresponden a atributos mapeados a columnas se crean con la instanciación
    @Column(name = "f_inicio", insertable = false)
    private LocalDateTime fInicio;

    @Column(name = "f_final")
    private LocalDateTime fFinal;

    // FKs manejadas por servicio/webclient; aquí sólo guardamos los IDs
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_ruta")
    private Integer idRuta;

    @Column(name = "id_estado")
    private Integer idEstado;
}

package cl.condor.contacto_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "contacto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre",nullable = false, length = 150)
    private String nombre;

    @Column(name = "correo",nullable = false, length = 150)
    private String correo;

    @Column(name = "mensaje",nullable = false)
    private String mensaje;

    @Column(name = "id_usuario",nullable = false)
    private Integer idUsuario;

    @Column(name = "f_creacion")
    private Date fCreacion;

    @Column(name = "respuesta", nullable = true, length = 1000)
    private String respuesta;

    @Column(name = "resuelto", nullable = false)
    private Boolean resuelto = false;

}

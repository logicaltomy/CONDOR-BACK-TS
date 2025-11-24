package cl.condor.usuarios_api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UsuarioDTO {

    // Identificador
    private Integer id;

    // Datos Personales
    private String nombre;
    private String correo;
    private byte[] fotoPerfil; // Se enviará como Base64 en el JSON

    // Datos de Actividad
    private Integer rutasRecorridas;
    private BigDecimal kmRecorridos;

    // IDs de Foráneas
    private Integer idRol;
    private Integer idRegion;
    private Integer idEstado;
}

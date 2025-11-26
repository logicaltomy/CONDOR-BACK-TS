package cl.condor.rutas_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal distancia;
    private Boolean f_public;
    private Boolean f_baneo;
    private String geometria_polyline;
    private Integer tiempo_segundos;
    private BigDecimal prom_calificacion;
    private Integer id_estado;
    private Integer id_region;
    private Integer id_tipo;
    private Integer id_dificultad;
    private List<String> foto; // URLs
}

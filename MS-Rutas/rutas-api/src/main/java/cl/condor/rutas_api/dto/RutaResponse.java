package cl.condor.rutas_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaResponse {
    private Integer idRuta;
    private String nombre;
    private String descripcion;
    private String dificultad;
    private String tipo;
    private Integer id_tipo;
    private Integer id_dificultad;
    private Integer id_region;
    private Integer id_estado;
    private List<String> foto;
    private BigDecimal distancia;
    private Integer tiempo_segundos;
    private BigDecimal prom_calificacion;
}

package cl.condor.logros_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "condicion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_condicion")
    private Integer id_condicion;

    @Column(name = "condicion", length = 255)
    private String condicion;

    @Column(name = "restriccion", nullable = false, precision = 10, scale = 2)
    private BigDecimal restriccion;

    @Column(name = "id_tipo_condicion")
    private Integer id_tipo_condicion;
}

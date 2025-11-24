package cl.condor.logros_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_condicion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tipo_condicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tip_cond")
    private Integer id_tip_cond;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;
}
package cl.condor.rutas_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Integer id_foto;

    // Allow long URLs (photo names may be external URLs). Increase length to avoid truncation.
    @Column(name = "nombre", length = 2048)
    private String nombre;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;

    @Column(name = "id_ruta")
    private Integer idRuta;
}


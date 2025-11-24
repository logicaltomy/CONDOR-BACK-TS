package cl.condor.rutas_api.repository;

import cl.condor.rutas_api.model.Foto;
import cl.condor.rutas_api.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {
    List<Foto> findByIdRuta(Integer id_ruta);
}

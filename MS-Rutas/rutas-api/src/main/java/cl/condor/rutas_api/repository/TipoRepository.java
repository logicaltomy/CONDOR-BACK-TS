package cl.condor.rutas_api.repository;

import cl.condor.rutas_api.model.Ruta;
import cl.condor.rutas_api.model.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Integer> {
}

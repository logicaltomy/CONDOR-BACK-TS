package cl.condor.rutas_api.repository;

import cl.condor.rutas_api.model.Dificultad;
import cl.condor.rutas_api.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DificultadRepository extends JpaRepository<Dificultad, Integer> {
}

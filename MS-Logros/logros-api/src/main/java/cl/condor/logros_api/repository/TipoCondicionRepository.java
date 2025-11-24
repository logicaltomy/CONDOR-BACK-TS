package cl.condor.logros_api.repository;

import cl.condor.logros_api.model.Tipo_condicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCondicionRepository extends JpaRepository<Tipo_condicion, Integer> {
}

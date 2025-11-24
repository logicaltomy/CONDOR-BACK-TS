package cl.condor.logros_api.repository;

import cl.condor.logros_api.model.Condicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CondicionRepository extends JpaRepository<Condicion, Integer> {

}

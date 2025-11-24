// EstadoRepository.java
package cl.condor.usuarios_api.repository;

import cl.condor.usuarios_api.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
}

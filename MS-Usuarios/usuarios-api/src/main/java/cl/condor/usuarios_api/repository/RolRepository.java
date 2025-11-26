// RolRepository.java
package cl.condor.usuarios_api.repository;

import cl.condor.usuarios_api.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
	java.util.Optional<Rol> findByNombreIgnoreCase(String nombre);
}

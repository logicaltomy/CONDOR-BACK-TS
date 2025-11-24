// RegionRepository.java
package cl.condor.usuarios_api.repository;

import cl.condor.usuarios_api.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
}

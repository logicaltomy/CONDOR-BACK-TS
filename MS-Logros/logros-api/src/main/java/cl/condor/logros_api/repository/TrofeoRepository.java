package cl.condor.logros_api.repository;

import cl.condor.logros_api.model.Trofeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrofeoRepository extends CrudRepository<Trofeo, Integer> {
    boolean existsByIdUsuarioAndIdLogro(Integer idUsuario, Integer idLogro);
}
package cl.condor.logros_api.repository;

import cl.condor.logros_api.model.Trofeo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrofeoRepository extends CrudRepository<Trofeo, Integer> {
    boolean existsByIdUsuarioAndIdLogro(Integer idUsuario, Integer idLogro);
    long countByIdLogro(Integer idLogro);

    // Permite listar todos los trofeos ganados por un usuario
    java.util.List<Trofeo> findByIdUsuario(Integer idUsuario);
}
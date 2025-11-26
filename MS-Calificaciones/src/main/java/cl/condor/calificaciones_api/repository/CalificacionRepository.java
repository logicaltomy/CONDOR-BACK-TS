package cl.condor.calificaciones_api.repository;

import cl.condor.calificaciones_api.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
	java.util.List<Calificacion> findByIdRuta(Integer idRuta);
	java.util.List<Calificacion> findByIdUsuario(Integer idUsuario);
	boolean existsByIdUsuarioAndIdRuta(Integer idUsuario, Integer idRuta);
}

package cl.condor.calificaciones_api.service;

import cl.condor.calificaciones_api.model.Calificacion;
import cl.condor.calificaciones_api.repository.CalificacionRepository;
import cl.condor.calificaciones_api.webclient.RutaClient;
import cl.condor.calificaciones_api.webclient.UsuarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;
    @Autowired
    private UsuarioClient usuarioClient;
    @Autowired
    private RutaClient rutaClient;

    public List<Calificacion> findAll() {
        return calificacionRepository.findAll();
    }

    public Calificacion findById(Integer id) {
        return calificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
    }

    public Calificacion save(Calificacion calificacion) {
        if (calificacion.getIdUsuario() == null)
            throw new RuntimeException("id_usuario es obligatorio");
        if (calificacion.getIdRuta() == null)
            throw new RuntimeException("id_ruta es obligatorio");
        if (calificacion.getPuntuacion() == null
                || calificacion.getPuntuacion() < 1
                || calificacion.getPuntuacion() > 5)
            throw new RuntimeException("La puntuación debe estar entre 1 y 5");

        // Validar existencia remota (si no existen, los clients lanzan RuntimeException)
        usuarioClient.getUsuarioById(calificacion.getIdUsuario());
        rutaClient.getRutaById(calificacion.getIdRuta());

        return calificacionRepository.save(calificacion);
    }
}

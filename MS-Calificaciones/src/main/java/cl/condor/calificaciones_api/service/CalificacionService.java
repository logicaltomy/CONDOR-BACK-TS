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
        try {
            usuarioClient.getUsuarioById(calificacion.getIdUsuario());
        } catch (RuntimeException e) {
            throw new RuntimeException("Usuario no encontrado");
        }
        try {
            rutaClient.getRutaById(calificacion.getIdRuta());
        } catch (RuntimeException e) {
            throw new RuntimeException("Ruta no encontrada");
        }

        // Verificar que el usuario no haya calificado ya esta ruta
        boolean yaExiste = calificacionRepository.existsByIdUsuarioAndIdRuta(calificacion.getIdUsuario(), calificacion.getIdRuta());
        if (yaExiste) {
            throw new RuntimeException("El usuario ya calificó esta ruta");
        }

        Calificacion saved = calificacionRepository.save(calificacion);

        // Calcular promedio actualizado y notificar al servicio de Rutas
        java.util.Map<String, Object> promedioMap = getPromedioPorRuta(saved.getIdRuta());
        Object promObj = promedioMap.get("promedio");
        if (promObj != null) {
            double promDouble = ((Number) promObj).doubleValue();
            java.math.BigDecimal promDec = java.math.BigDecimal.valueOf(promDouble).setScale(2, java.math.RoundingMode.HALF_UP);
            try {
                rutaClient.actualizarPromedioRuta(saved.getIdRuta(), promDec);
            } catch (RuntimeException e) {
                // No bloquear el guardado por fallos en la actualización remota; sólo loguear
                System.err.println("No se pudo actualizar promedio en Rutas: " + e.getMessage());
            }
        }

        return saved;
    }

    public java.util.List<Calificacion> findByRuta(Integer idRuta) {
        return calificacionRepository.findByIdRuta(idRuta);
    }

    public java.util.List<Calificacion> findByUsuario(Integer idUsuario) {
        return calificacionRepository.findByIdUsuario(idUsuario);
    }

    public java.util.Map<String, Object> getPromedioPorRuta(Integer idRuta) {
        java.util.List<Calificacion> lista = findByRuta(idRuta);
        java.util.Map<String, Object> out = new java.util.HashMap<>();
        if (lista == null || lista.isEmpty()) {
            out.put("idRuta", idRuta);
            out.put("promedio", null);
            out.put("conteo", 0);
            return out;
        }
        double promedio = lista.stream().mapToInt(Calificacion::getPuntuacion).average().orElse(0.0);
        out.put("idRuta", idRuta);
        out.put("promedio", Math.round(promedio * 100.0) / 100.0); // redondeo a 2 decimales
        out.put("conteo", lista.size());
        return out;
    }

    public boolean existsByUsuarioRuta(Integer idUsuario, Integer idRuta) {
        return calificacionRepository.existsByIdUsuarioAndIdRuta(idUsuario, idRuta);
    }
}

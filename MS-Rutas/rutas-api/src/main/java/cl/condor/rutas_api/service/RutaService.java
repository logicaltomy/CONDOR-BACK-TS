package cl.condor.rutas_api.service;

import cl.condor.rutas_api.model.Dificultad;
import cl.condor.rutas_api.model.Foto;
import cl.condor.rutas_api.model.Ruta;
import cl.condor.rutas_api.model.Tipo;
import cl.condor.rutas_api.repository.DificultadRepository;
import cl.condor.rutas_api.repository.FotoRepository;
import cl.condor.rutas_api.repository.RutaRepository;
import cl.condor.rutas_api.repository.TipoRepository;
import cl.condor.rutas_api.webclient.EstadoClient;
import cl.condor.rutas_api.webclient.RegionClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import cl.condor.rutas_api.dto.RutaResponse;

@Service
@Transactional
public class RutaService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private RegionClient regionClient;

    @Autowired
    private DificultadRepository dificultadRepository;

    @Autowired
    private EstadoClient estadoClient;

    @Autowired
    private FotoRepository fotoRepository;

    //Nos entrega una lista de rutas
    public List<Ruta> findAll() {
        return rutaRepository.findAll();
    }

    public RutaResponse toResponse(Ruta r) {
        if (r == null) return null;
        String tipoNombre = null;
        String difNombre = null;
        try {
            Tipo t = tipoRepository.findById(r.getId_tipo()).orElse(null);
            if (t != null) tipoNombre = t.getNombre();
        } catch (Exception ignored) {}
        try {
            Dificultad d = dificultadRepository.findById(r.getId_dificultad()).orElse(null);
            if (d != null) difNombre = d.getNombre();
        } catch (Exception ignored) {}

        List<Foto> fotos = fotoRepository.findByIdRuta(r.getId_ruta());
        List<String> fotosUrls = fotos == null ? List.of() : fotos.stream()
                .map(Foto::getNombre)
                .filter(u -> u != null && !u.isBlank())
                .collect(Collectors.toList());

        RutaResponse resp = new RutaResponse();
        resp.setIdRuta(r.getId_ruta());
        resp.setNombre(r.getNombre());
        resp.setDescripcion(r.getDescripcion());
        resp.setDificultad(difNombre);
        resp.setTipo(tipoNombre);
        resp.setId_tipo(r.getId_tipo());
        resp.setId_dificultad(r.getId_dificultad());
        resp.setId_region(r.getId_region());
        resp.setId_estado(r.getId_estado());
        resp.setFoto(fotosUrls);
        resp.setDistancia(r.getDistancia());
        resp.setTiempo_segundos(r.getTiempoSegundos());
        resp.setProm_calificacion(r.getProm_calificacion());
        return resp;
    }

    public List<RutaResponse> findAllResponses() {
        return findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RutaResponse findResponseById(Integer id) {
        Ruta r = findById(id);
        return toResponse(r);
    }

    //Busca una ruta especifica por el id
    public Ruta findById(int id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"Ruta no encontrada"
                ));
    }

    public List<Foto> findByIdRuta(Integer  idRuta) {
        List<Foto> fotos = fotoRepository.findByIdRuta(idRuta);

        if (fotos.isEmpty()) {
            throw new RuntimeException("Ruta no encontrada");
        }
        return fotos;
    }


    //El save no sera creado hasta que podemos restringirlo con las FK que no
    //pueden ser nulas.
    @Transactional
    public Ruta save(Ruta ruta) {
        Map<String, Object> region = regionClient.getRegionesById(ruta.getId_region());
        Map<String, Object> estado = estadoClient.getEstadosById(ruta.getId_estado());
        if (region == null || region.isEmpty()) {
            throw new RuntimeException("Region no encontrado, no se puede guardar la ruta");
        }
        if (estado == null || estado.isEmpty()) {
            throw new RuntimeException("Estado no encontrado, no se puede guardar la ruta");
        }
        if (!dificultadRepository.existsById(ruta.getId_dificultad())) {
            throw new RuntimeException("Dificultad no encontrada, no se puede guardar la ruta");
        }
        if (!tipoRepository.existsById(ruta.getId_tipo())) {
            throw new RuntimeException("Tipo ruta no encontrada, no se puede guardar la ruta");
        }
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Foto save(Foto foto) {
        if (!rutaRepository.existsById(foto.getIdRuta())) {
            throw new RuntimeException("Ruta no encontrada");
        }
        return fotoRepository.save(foto);
    }

    //Update de ciertos datos de la ruta

    @Transactional
    public Ruta updateNombre(Integer id, String nombre) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setNombre(cleanString(nombre));
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta updateDescripcion(Integer id, String descripcion) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setDescripcion(cleanString(descripcion));
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta updatePolyLine(Integer id, String PolyLine) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setGeometriaPolyline(PolyLine);
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta banearRuta(Integer id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setF_baneo(LocalDateTime.now());
        ruta.setId_estado(3); //Cambia el estado de la ruta a baneado
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta desbanearRuta(Integer id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setF_baneo(null);
        ruta.setId_estado(1);//Cambia el estado de la ruta a activo
        return rutaRepository.save(ruta);
    }

    @Transactional
    public void deleteFoto(Integer id) {
        Foto foto = fotoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foto no encontrada"));
        fotoRepository.delete(foto);
    }

    @Transactional
    public void deleteRuta(Integer id) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Eliminar fotos asociadas primero
        try {
            java.util.List<Foto> fotos = fotoRepository.findByIdRuta(id);
            if (fotos != null && !fotos.isEmpty()) {
                fotoRepository.deleteAll(fotos);
            }
        } catch (Exception ignored) {}

        rutaRepository.delete(ruta);
    }

    public List<Tipo> findAllTipos() {
        return tipoRepository.findAll();
    }

    public List<Dificultad> findAllDificultades() {
        return dificultadRepository.findAll();
    }

    // Helper to remove enclosing quotes and trim whitespace
    private String cleanString(String s) {
        if (s == null) return null;
        String t = s.trim();
        // remove leading and trailing double quotes if present
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1);
        }
        // also remove single quotes
        if (t.length() >= 2 && t.startsWith("'") && t.endsWith("'")) {
            t = t.substring(1, t.length() - 1);
        }
        return t.trim();
    }

    @Transactional
    public Ruta updateRuta(Integer id, Ruta payload) {
        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // update fields if provided (allow full replace from payload where appropriate)
        if (payload.getNombre() != null) existing.setNombre(payload.getNombre());
        existing.setDescripcion(payload.getDescripcion());
        if (payload.getDistancia() != null) existing.setDistancia(payload.getDistancia());
        if (payload.getGeometriaPolyline() != null) existing.setGeometriaPolyline(payload.getGeometriaPolyline());
        if (payload.getTiempoSegundos() != null) existing.setTiempoSegundos(payload.getTiempoSegundos());
        if (payload.getProm_calificacion() != null) existing.setProm_calificacion(payload.getProm_calificacion());
        if (payload.getId_estado() != null) existing.setId_estado(payload.getId_estado());
        if (payload.getId_region() != null) existing.setId_region(payload.getId_region());
        if (payload.getId_tipo() != null) existing.setId_tipo(payload.getId_tipo());
        if (payload.getId_dificultad() != null) existing.setId_dificultad(payload.getId_dificultad());

        return rutaRepository.save(existing);
    }

    @Transactional
    public Ruta updateRutaFromRequest(Integer id, cl.condor.rutas_api.dto.RutaRequest req) {
        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        if (req.getNombre() != null) existing.setNombre(cleanString(req.getNombre()));
        existing.setDescripcion(cleanString(req.getDescripcion()));
        if (req.getDistancia() != null) existing.setDistancia(req.getDistancia());
        if (req.getGeometria_polyline() != null) existing.setGeometriaPolyline(req.getGeometria_polyline());
        if (req.getTiempo_segundos() != null) existing.setTiempoSegundos(req.getTiempo_segundos());
        if (req.getProm_calificacion() != null) existing.setProm_calificacion(req.getProm_calificacion());
        if (req.getId_estado() != null) existing.setId_estado(req.getId_estado());
        if (req.getId_region() != null) existing.setId_region(req.getId_region());
        if (req.getId_tipo() != null) existing.setId_tipo(req.getId_tipo());
        if (req.getId_dificultad() != null) existing.setId_dificultad(req.getId_dificultad());

        Ruta saved = rutaRepository.save(existing);

        // If fotos provided, replace existing foto records with new ones (store URL in nombre)
        if (req.getFoto() != null) {
            List<Foto> existingFotos = fotoRepository.findByIdRuta(id);
            if (existingFotos != null && !existingFotos.isEmpty()) {
                fotoRepository.deleteAll(existingFotos);
            }
            for (String url : req.getFoto()) {
                Foto f = new Foto();
                f.setNombre(url);
                f.setImagen(null);
                f.setIdRuta(id);
                fotoRepository.save(f);
            }
        }

        return saved;
    }

}
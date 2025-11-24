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
        ruta.setNombre(nombre);
        return rutaRepository.save(ruta);
    }

    @Transactional
    public Ruta updateDescripcion(Integer id, String descripcion) {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
        ruta.setDescripcion(descripcion);
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

    public Tipo findTipoById(Integer id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo no encontrada"));
    }

    public List<Tipo> findAllTipos() {
        return tipoRepository.findAll();
    }

    public Dificultad findDificultadById(Integer id) {
        return dificultadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dificultad no encontrada"));
    }

    public List<Dificultad> findAllDificultades() {
        return dificultadRepository.findAll();
    }

}
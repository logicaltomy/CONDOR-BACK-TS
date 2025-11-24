package cl.condor.logros_api.service;

import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Logro;
import cl.condor.logros_api.model.Tipo_condicion;
import cl.condor.logros_api.model.Trofeo;
import cl.condor.logros_api.repository.CondicionRepository;
import cl.condor.logros_api.repository.LogroRepository;
import cl.condor.logros_api.repository.TipoCondicionRepository;
import cl.condor.logros_api.repository.TrofeoRepository;
import cl.condor.logros_api.webclient.EstadoClient;
import cl.condor.logros_api.webclient.IniciarRutaClient;
import cl.condor.logros_api.webclient.RutaClient;
import cl.condor.logros_api.webclient.UsuarioClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class LogroService {

    //Aqui lo mas importante es la logica de condiciones
    //Tenemos que comprobar si es que el usuario cumple
    //con las condiciones propuestas en el logro.

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private TrofeoRepository trofeoRepository;

    @Autowired
    private EstadoClient estadoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private CondicionRepository condicionRepository;

    @Autowired
    private IniciarRutaClient iniciarRutaClient;

    @Autowired
    private RutaClient rutaClient;

    public List<Logro> findAll(){return logroRepository.findAll();}

    public Logro findById(Integer id){
        return logroRepository.findById(id).orElseThrow(() -> new RuntimeException("Logro no encontrado"));
    }

    @Transactional
    public Logro save(Logro logro){
        Map<String, Object> estado = estadoClient.getEstadosById(logro.getId_estado());
        condicionRepository.findById(logro.getId_condicion())
                .orElseThrow(() -> new RuntimeException("Condicion no encontrado"));

        return logroRepository.save(logro);
    }

    @Transactional
    public Trofeo ganarLogro(Integer IdUser){
        List<Logro> logros = logroRepository.findAll();
        Map<String, Object> usuario = usuarioClient.getUsuariosById(IdUser);

        BigDecimal km_recorridos = new BigDecimal(usuario.get("kmRecorridos").toString());

        //Traemos los iniciar rutas para ver cuantas rutas a realizado el usuario
        List<Map<String, Object>> iniciarRutas = iniciarRutaClient.getRutasByUsuario(IdUser);

        // Lista donde guardaremos todas las rutas con los datos completos
        List<Map<String, Object>> rutas = new ArrayList<>();

        // Recorremos cada entrada de iniciarRutas
        for (Map<String, Object> iniciarRutaItem : iniciarRutas) {
            // Obtenemos el idRuta de cada elemento
            Integer idRuta = (Integer) iniciarRutaItem.get("idRuta");

            if (iniciarRutaItem.get("ffinal") != null) {
                // Llamamos al cliente para traer los datos completos de la ruta
                Map<String, Object> rutaCompleta = rutaClient.getRutaById(idRuta);


                // Agregamos la ruta completa a nuestra lista
                rutas.add(rutaCompleta);
            }
        }

        Set<String> regionesUnicas = new HashSet<>();

        for (Map<String, Object> ruta : rutas) {
            String region = ruta.get("id_region").toString();
            if (region != null) {
                regionesUnicas.add(region); // HashSet elimina duplicados automáticamente
            }
        }

        // Cantidad de regiones distintas
        BigDecimal cantidadRegiones = BigDecimal.valueOf(regionesUnicas.size());


        //Cantidad de rutas recorridas por el usuario (contamos si completo una mas de una vez)
        BigDecimal cantidadRutas = BigDecimal.valueOf(rutas.size());


        for (Logro log : logros) {
            Condicion condicion = condicionRepository.getReferenceById(log.getId_condicion());boolean ifExist = trofeoRepository.existsByIdUsuarioAndIdLogro(IdUser,log.getIdLogro());

            if (ifExist) {
                continue;
            }


            if (Objects.equals(condicion.getId_tipo_condicion(), 1)) {
                if (km_recorridos.compareTo(condicion.getRestriccion()) >= 0) {
                    Trofeo trofeo = new Trofeo(
                            null,
                            LocalDateTime.now(),
                            IdUser,
                            log.getIdLogro()
                            );
                    return trofeoRepository.save(trofeo);
                }

            }

            if (Objects.equals(condicion.getId_tipo_condicion(), 2)) {
                if (cantidadRegiones.compareTo(condicion.getRestriccion()) >= 0) {
                    Trofeo trofeo = new Trofeo(
                            null,
                            LocalDateTime.now(),
                            IdUser,
                            log.getIdLogro()
                    );
                    return trofeoRepository.save(trofeo);
                }
            }

            if (Objects.equals(condicion.getId_tipo_condicion(), 3)) {
                if (cantidadRutas.compareTo(condicion.getRestriccion()) >= 0){
                    Trofeo trofeo = new Trofeo(
                            null,
                            LocalDateTime.now(),
                            IdUser,
                            log.getIdLogro()
                    );
                    return trofeoRepository.save(trofeo);
                }
            }

        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se ha ganado el logro");

    }

    @Transactional
    public Logro updateNombre(Integer id, String nombre){
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado"));
        logro.setNombre(nombre);
        return logroRepository.save(logro);
    }

    @Transactional
    public Logro updateDescripcion(Integer id, String descripcion){
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado"));
        logro.setDescripcion(descripcion);
        return logroRepository.save(logro);
    }

    @Transactional
    public Logro updateEstado(Integer id, Integer estado){
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado"));
        logro.setId_estado(estado);
        return logroRepository.save(logro);
    }

    @Transactional
    public Logro updateIcono(Integer id, byte[] icono){
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado"));
        logro.setIcono(icono);
        return logroRepository.save(logro);
    }

}

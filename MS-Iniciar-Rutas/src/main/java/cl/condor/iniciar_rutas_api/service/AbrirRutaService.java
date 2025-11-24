package cl.condor.iniciar_rutas_api.service;

import cl.condor.iniciar_rutas_api.model.AbrirRuta;
import cl.condor.iniciar_rutas_api.repository.AbrirRutaRepository;
import cl.condor.iniciar_rutas_api.webclient.EstadoClient;
import cl.condor.iniciar_rutas_api.webclient.RutaClient;
import cl.condor.iniciar_rutas_api.webclient.UsuarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional

public class AbrirRutaService {
    @Autowired
    private AbrirRutaRepository abrirRutaRepository;
    //para posibilitar los clientes que otorgarán su fk mediante el webclient
    @Autowired
    private EstadoClient estadoClient;
    @Autowired
    private RutaClient rutaClient;
    @Autowired
    private UsuarioClient usuarioClient;

    public List<AbrirRuta> findAll() {
        return abrirRutaRepository.findAll();
    }

    public AbrirRuta findById(Integer id) {
        return abrirRutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AbrirRuta no encontrada"));
    }

    //Se agrego esto con el fin de que sea ocupado por API Logros (No esta probado)
    public List<AbrirRuta> findByIdUsuario(Integer id) {
        List<AbrirRuta> rutas = abrirRutaRepository.findAbrirRutaByIdUsuario(id);

        if (rutas.isEmpty()) {
            throw new RuntimeException("No se encontraron rutas para el usuario con id " + id);
        }

        return rutas;
    }

    // AbrirRuta depende de muchas tablas para crearla
    // Un total de 3 tablas/clases instanciadas y mapeadas
    public AbrirRuta save(AbrirRuta abrirRuta) {
        // mapeando los string para traer sus id a partir de web client
        // dichos campos a rellenar ya se declararon previamente en el model

        Map<String, Object> usuario = usuarioClient.getUsuarioById(abrirRuta.getIdUsuario());
        Map<String, Object> ruta = rutaClient.getRutaById(abrirRuta.getIdRuta());
        Map<String, Object> estado = estadoClient.getEstadosById(abrirRuta.getIdEstado());

        if (usuario == null || usuario.isEmpty()) throw new RuntimeException("Usuario no encontrado");
        if (ruta == null ||  ruta.isEmpty()) throw new RuntimeException("Ruta no encontrada");
        if (estado == null || estado.isEmpty()) throw new RuntimeException("Estado no encontrada");

        return abrirRutaRepository.save(abrirRuta);

    }


    // a futuro se tiene que corregir
    // puesto que es un valor que depende de lo que el usuario ingrese en el aplicativo movil
    public AbrirRuta marcarFin(Integer id) {
        AbrirRuta abrirRuta = abrirRutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AbrirRuta no encontrada"));
        abrirRuta.setFFinal(java.time.LocalDateTime.now());
        return abrirRutaRepository.save(abrirRuta); // UPDATE
    }
}

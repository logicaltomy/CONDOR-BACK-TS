package cl.condor.logros_api.Service;

import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Logro;
import cl.condor.logros_api.model.Trofeo;
import cl.condor.logros_api.repository.CondicionRepository;
import cl.condor.logros_api.repository.LogroRepository;
import cl.condor.logros_api.repository.TrofeoRepository;
import cl.condor.logros_api.service.LogroService;
import cl.condor.logros_api.webclient.EstadoClient;
import cl.condor.logros_api.webclient.IniciarRutaClient;
import cl.condor.logros_api.webclient.RutaClient;
import cl.condor.logros_api.webclient.UsuarioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LogroServiceTest {

    @InjectMocks
    private LogroService logroService;

    // Repositorios
    @Mock private LogroRepository logroRepository;
    @Mock private TrofeoRepository trofeoRepository;
    @Mock private CondicionRepository condicionRepository;

    // WebClients (Servicios Externos)
    @Mock private EstadoClient estadoClient;
    @Mock private UsuarioClient usuarioClient;
    @Mock private IniciarRutaClient iniciarRutaClient;
    @Mock private RutaClient rutaClient;

    private Logro logroKm;
    private Logro logroRegion;
    private Logro logroRutas;
    private Condicion condicionKm;
    private Condicion condicionRegion;
    private Condicion condicionRutas;
    private Trofeo trofeoEjemplo;

    private final Integer VALID_ID = 1;
    private final Integer VALID_USER_ID = 100;
    private final String LOGRO_NOT_FOUND_MSG = "Logro no encontrado";
    private final String CONDICION_NOT_FOUND_MSG = "Condicion no encontrado";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // --- Modelos de Condiciones ---
        condicionKm = new Condicion(1, "Recorrer 50.00 km", new BigDecimal("50.00"), 1); // Tipo 1: Kilometraje
        condicionRegion = new Condicion(2, "Terminar 3 rutas en diferentes regiones", new BigDecimal("3"), 2); // Tipo 2: Regiones
        condicionRutas = new Condicion(3, "Termina 5 rutas", new BigDecimal("5"), 3); // Tipo 3: Cantidad Rutas

        // --- Modelos de Logros ---
        logroKm = new Logro(1, "Distanciero", null, null, "Desc Km", 1, 1);
        logroRegion = new Logro(2, "Explorador", null,null, "Desc Region", 1, 2);
        logroRutas = new Logro(3, "Consistente", null,null, "Desc Rutas", 1, 3);

        // --- Modelo de Trofeo ---
        trofeoEjemplo = new Trofeo(1, LocalDateTime.now(), VALID_USER_ID, 1);
    }

    // ==========================================
    // Tests para findById / findAll
    // ==========================================

    @Test
    void findAll_retornaListaLogros() {
        when(logroRepository.findAll()).thenReturn(List.of(logroKm));
        List<Logro> resultado = logroService.findAll();
        assertFalse(resultado.isEmpty());
        verify(logroRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaLogro() {
        when(logroRepository.findById(VALID_ID)).thenReturn(Optional.of(logroKm));
        Logro resultado = logroService.findById(VALID_ID);
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getIdLogro());
        verify(logroRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(logroRepository.findById(VALID_ID)).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> logroService.findById(VALID_ID));
        assertEquals(LOGRO_NOT_FOUND_MSG, excepcion.getMessage());
        verify(logroRepository, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para save(Logro)
    // ==========================================

    private void simularValidacionesSaveValidas() {
        // EstadoClient: Simular que el estado (de Usuarios) existe
        when(estadoClient.getEstadosById(anyInt())).thenReturn(Map.of("id", 1, "nombre", "Activo"));
        // CondicionRepository: Simular que la condición existe
        when(condicionRepository.findById(anyInt())).thenReturn(Optional.of(condicionKm));
        // LogroRepository: Simular el guardado
        when(logroRepository.save(any(Logro.class))).thenReturn(logroKm);
    }

    @Test
    void save_valido_guardaYRetornaLogro() {
        simularValidacionesSaveValidas();

        Logro resultado = logroService.save(logroKm);

        assertNotNull(resultado);
        verify(estadoClient, times(1)).getEstadosById(logroKm.getId_estado());
        verify(condicionRepository, times(1)).findById(logroKm.getId_condicion());
        verify(logroRepository, times(1)).save(logroKm);
    }

    @Test
    void save_condicionNoExiste_lanzaExcepcion() {
        // Simular: Estado OK, pero Condicion falla
        when(estadoClient.getEstadosById(anyInt())).thenReturn(Map.of("id", 1));
        when(condicionRepository.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> logroService.save(logroKm));

        assertEquals(CONDICION_NOT_FOUND_MSG, excepcion.getMessage());
        verify(estadoClient, times(1)).getEstadosById(logroKm.getId_estado());
        verify(condicionRepository, times(1)).findById(logroKm.getId_condicion());
        verify(logroRepository, never()).save(any());
    }

    // ==========================================
    // Tests para ganarLogro(IdUser)
    // ==========================================

    /**
     * Simula un usuario que cumple TODAS las condiciones:
     * - KM: 60.00 (Cumple > 50.00)
     * - Regiones: 4 (Cumple > 3)
     * - Rutas Completadas: 7 (Cumple > 5)
     */
    private void simularDatosDeUsuarioCumplidor() {
        // 1. Usuario: Simular 60 KM recorridos
        when(usuarioClient.getUsuariosById(VALID_USER_ID))
                .thenReturn(Map.of("kmRecorridos", new BigDecimal("60.00")));

        // 2. Rutas Iniciadas: 7 rutas completadas
        List<Map<String, Object>> iniciarRutasList = List.of(
                Map.of("idRuta", 10, "ffinal", LocalDateTime.now()), // Ruta 1, región A
                Map.of("idRuta", 11, "ffinal", LocalDateTime.now()), // Ruta 2, región B
                Map.of("idRuta", 10, "ffinal", LocalDateTime.now()), // Ruta 3, región A (Duplicada, cuenta como 1 región)
                Map.of("idRuta", 12, "ffinal", LocalDateTime.now()), // Ruta 4, región C
                Map.of("idRuta", 13, "ffinal", LocalDateTime.now()), // Ruta 5, región D
                Map.of("idRuta", 14, "ffinal", LocalDateTime.now()), // Ruta 6, región D
                Map.of("idRuta", 15, "ffinal", LocalDateTime.now())  // Ruta 7, región D
        );
        when(iniciarRutaClient.getRutasByUsuario(VALID_USER_ID)).thenReturn(iniciarRutasList);

        // 3. Rutas Completas: Simular las regiones
        // Rutas 1, 3 (ID 10) -> Región 1
        when(rutaClient.getRutaById(10)).thenReturn(Map.of("id_region", 1));
        // Ruta 2 (ID 11) -> Región 2
        when(rutaClient.getRutaById(11)).thenReturn(Map.of("id_region", 2));
        // Ruta 4 (ID 12) -> Región 3
        when(rutaClient.getRutaById(12)).thenReturn(Map.of("id_region", 3));
        // Rutas 5, 6, 7 (ID 13, 14, 15) -> Región 4
        when(rutaClient.getRutaById(13)).thenReturn(Map.of("id_region", 4));
        when(rutaClient.getRutaById(14)).thenReturn(Map.of("id_region", 4));
        when(rutaClient.getRutaById(15)).thenReturn(Map.of("id_region", 4));
    }


    @Test
    void ganarLogro_cumplePrimerLogroKm_guardaTrofeoYRetorna() {
        simularDatosDeUsuarioCumplidor();
        // 1. Logros: Simular la lista de logros a revisar (KM, luego Region, luego Rutas)
        when(logroRepository.findAll()).thenReturn(List.of(logroKm, logroRegion, logroRutas));

        // 2. Condiciones: Mapear cada Logro a su Condicion
        when(condicionRepository.getReferenceById(1)).thenReturn(condicionKm); // Km > 50
        when(condicionRepository.getReferenceById(2)).thenReturn(condicionRegion);
        when(condicionRepository.getReferenceById(3)).thenReturn(condicionRutas);

        // 3. Trofeo: Simular que aún no existe
        when(trofeoRepository.existsByIdUsuarioAndIdLogro(anyInt(), anyInt())).thenReturn(false);
        // Simular que el trofeo se guarda
        when(trofeoRepository.save(any(Trofeo.class))).thenReturn(trofeoEjemplo);

        Trofeo resultado = logroService.ganarLogro(VALID_USER_ID);

        // Verificar que el Trofeo fue guardado y retornado
        assertNotNull(resultado);
        assertEquals(logroKm.getIdLogro(), resultado.getIdLogro());

        // Verificar interacciones: Solo debe guardar el primer logro que encuentra
        verify(trofeoRepository, times(1)).save(any(Trofeo.class));
    }

    @Test
    void ganarLogro_noCumpleNinguno_lanzaResponseStatusException() {
        // Simular: Usuario con 1 KM (no cumple 50), 1 Región (no cumple 3), 1 Ruta (no cumple 5)
        when(usuarioClient.getUsuariosById(VALID_USER_ID))
                .thenReturn(Map.of("kmRecorridos", new BigDecimal("1.00")));
        when(iniciarRutaClient.getRutasByUsuario(VALID_USER_ID))
                .thenReturn(List.of(Map.of("idRuta", 10, "ffinal", LocalDateTime.now())));
        when(rutaClient.getRutaById(10)).thenReturn(Map.of("id_region", 1));

        // Simular Logros y Condiciones
        when(logroRepository.findAll()).thenReturn(List.of(logroKm, logroRegion, logroRutas));
        when(condicionRepository.getReferenceById(1)).thenReturn(condicionKm);
        when(condicionRepository.getReferenceById(2)).thenReturn(condicionRegion);
        when(condicionRepository.getReferenceById(3)).thenReturn(condicionRutas);
        when(trofeoRepository.existsByIdUsuarioAndIdLogro(anyInt(), anyInt())).thenReturn(false);

        // Verificar que lanza la excepción 403 (FORBIDDEN)
        assertThrows(ResponseStatusException.class, () -> logroService.ganarLogro(VALID_USER_ID));

        // Verificar que NADA fue guardado
        verify(trofeoRepository, never()).save(any());
    }

    @Test
    void ganarLogro_logroYaExiste_pasaAlSiguiente() {
        simularDatosDeUsuarioCumplidor();
        when(logroRepository.findAll()).thenReturn(List.of(logroKm, logroRegion));
        when(condicionRepository.getReferenceById(1)).thenReturn(condicionKm); // KM (cumple)
        when(condicionRepository.getReferenceById(2)).thenReturn(condicionRegion); // Region (cumple)

        // Simular: Logro KM (ID 1) YA EXISTE
        when(trofeoRepository.existsByIdUsuarioAndIdLogro(VALID_USER_ID, 1)).thenReturn(true);
        // Simular: Logro Region (ID 2) NO EXISTE
        when(trofeoRepository.existsByIdUsuarioAndIdLogro(VALID_USER_ID, 2)).thenReturn(false);
        // Simular guardado del segundo logro
        when(trofeoRepository.save(any(Trofeo.class))).thenReturn(new Trofeo(2, LocalDateTime.now(), VALID_USER_ID, 2));


        Trofeo resultado = logroService.ganarLogro(VALID_USER_ID);

        // Verificar que se saltó el logro 1 y guardó el logro 2
        assertEquals(2, resultado.getIdLogro());

        // Verificar que el exist se llamó para ambos logros
        verify(trofeoRepository, times(1)).existsByIdUsuarioAndIdLogro(VALID_USER_ID, 1);
        verify(trofeoRepository, times(1)).existsByIdUsuarioAndIdLogro(VALID_USER_ID, 2);
        // Solo un guardado
        verify(trofeoRepository, times(1)).save(any(Trofeo.class));
    }

    // ==========================================
    // Tests para Métodos PATCH (Update)
    // ==========================================

    @Test
    void updateNombre_existe_actualizaYGuarda() {
        String nuevoNombre = "Nuevo Nombre";
        when(logroRepository.findById(VALID_ID)).thenReturn(Optional.of(logroKm));
        when(logroRepository.save(any(Logro.class))).thenReturn(logroKm);

        Logro resultado = logroService.updateNombre(VALID_ID, nuevoNombre);

        assertEquals(nuevoNombre, resultado.getNombre());
        verify(logroRepository, times(1)).findById(VALID_ID);
        verify(logroRepository, times(1)).save(logroKm);
    }

    @Test
    void updateDescripcion_noExiste_lanzaExcepcion() {
        when(logroRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () ->
                logroService.updateDescripcion(VALID_ID, "Nueva desc")
        );

        assertEquals(LOGRO_NOT_FOUND_MSG, excepcion.getMessage());
        verify(logroRepository, never()).save(any());
    }

    // Nota: Los tests para updateEstado, updateIcono, y updateDescripcion
    // siguen una estructura idéntica a updateNombre/updateDescripcion (findById + save).
}

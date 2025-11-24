package cl.condor.iniciar_rutas_api.Service;

import cl.condor.iniciar_rutas_api.model.AbrirRuta;
import cl.condor.iniciar_rutas_api.repository.AbrirRutaRepository;
import cl.condor.iniciar_rutas_api.service.AbrirRutaService;
import cl.condor.iniciar_rutas_api.webclient.EstadoClient;
import cl.condor.iniciar_rutas_api.webclient.RutaClient;
import cl.condor.iniciar_rutas_api.webclient.UsuarioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbrirRutaServiceTest {

    @InjectMocks
    private AbrirRutaService abrirRutaService;

    // Repositorio
    @Mock
    private AbrirRutaRepository abrirRutaRepository;

    // WebClients
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private RutaClient rutaClient;
    @Mock
    private EstadoClient estadoClient;

    private AbrirRuta rutaValida;
    private final Integer VALID_ID = 1;
    private final Integer VALID_USER_ID = 10;
    private final Integer VALID_RUTA_ID = 50;
    private final Integer VALID_ESTADO_ID = 1;

    // Mapa simulado que devuelven los WebClients al encontrar un recurso
    private final Map<String, Object> VALID_EXTERNAL_RESPONSE = Map.of("id", VALID_ID, "nombre", "Test");
    private final Map<String, Object> EMPTY_EXTERNAL_RESPONSE = Collections.emptyMap();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rutaValida = AbrirRuta.builder()
                .id(VALID_ID)
                .idUsuario(VALID_USER_ID)
                .idRuta(VALID_RUTA_ID)
                .idEstado(VALID_ESTADO_ID)
                .fInicio(LocalDateTime.of(2025, 1, 1, 10, 0))
                .fFinal(null)
                .build();
    }

    // ==========================================
    // Tests para findAll()
    // ==========================================

    @Test
    void findAll_retornaLista() {
        when(abrirRutaRepository.findAll()).thenReturn(List.of(rutaValida));

        List<AbrirRuta> resultado = abrirRutaService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(abrirRutaRepository, times(1)).findAll();
    }

    // ==========================================
    // Tests para findById(id)
    // ==========================================

    @Test
    void findById_existe_retornaAbrirRuta() {
        when(abrirRutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaValida));

        AbrirRuta resultado = abrirRutaService.findById(VALID_ID);

        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());
        verify(abrirRutaRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(abrirRutaRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            abrirRutaService.findById(VALID_ID);
        });

        assertEquals("AbrirRuta no encontrada", excepcion.getMessage());
        verify(abrirRutaRepository, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para findByIdUsuario(id)
    // ==========================================

    @Test
    void findByIdUsuario_existe_retornaLista() {
        when(abrirRutaRepository.findAbrirRutaByIdUsuario(VALID_USER_ID)).thenReturn(List.of(rutaValida));

        List<AbrirRuta> resultado = abrirRutaService.findByIdUsuario(VALID_USER_ID);

        assertFalse(resultado.isEmpty());
        assertEquals(VALID_USER_ID, resultado.get(0).getIdUsuario());
        verify(abrirRutaRepository, times(1)).findAbrirRutaByIdUsuario(VALID_USER_ID);
    }

    @Test
    void findByIdUsuario_noExiste_lanzaExcepcion() {
        when(abrirRutaRepository.findAbrirRutaByIdUsuario(VALID_USER_ID)).thenReturn(Collections.emptyList());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            abrirRutaService.findByIdUsuario(VALID_USER_ID);
        });

        assertEquals("No se encontraron rutas para el usuario con id " + VALID_USER_ID, excepcion.getMessage());
        verify(abrirRutaRepository, times(1)).findAbrirRutaByIdUsuario(VALID_USER_ID);
    }

    // ==========================================
    // Tests para save(abrirRuta) - CON VALIDACIONES REMOTAS
    // ==========================================

    private void simularClientesValidos() {
        // Simular que los 3 clientes remotos responden exitosamente
        when(usuarioClient.getUsuarioById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(rutaClient.getRutaById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
    }

    @Test
    void save_todoValido_guardaRuta() {
        simularClientesValidos();
        // Simular que el repositorio guarda y devuelve la entidad
        when(abrirRutaRepository.save(any(AbrirRuta.class))).thenReturn(rutaValida);

        AbrirRuta resultado = abrirRutaService.save(rutaValida);

        assertNotNull(resultado);
        verify(usuarioClient, times(1)).getUsuarioById(VALID_USER_ID);
        verify(rutaClient, times(1)).getRutaById(VALID_RUTA_ID);
        verify(estadoClient, times(1)).getEstadosById(VALID_ESTADO_ID);
        verify(abrirRutaRepository, times(1)).save(rutaValida);
    }

    @Test
    void save_usuarioNoEncontrado_lanzaExcepcion() {
        // Simular: Usuario no encontrado
        when(usuarioClient.getUsuarioById(anyInt())).thenReturn(EMPTY_EXTERNAL_RESPONSE);
        when(rutaClient.getRutaById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            abrirRutaService.save(rutaValida);
        });

        assertEquals("Usuario no encontrado", excepcion.getMessage());
        verify(abrirRutaRepository, never()).save(any());
        // Ruta y Estado sí fueron llamados antes de la validación del usuario
        verify(usuarioClient, times(1)).getUsuarioById(VALID_USER_ID);
        verify(rutaClient, times(1)).getRutaById(VALID_RUTA_ID);
        verify(estadoClient, times(1)).getEstadosById(VALID_ESTADO_ID);
    }

    @Test
    void save_rutaNoEncontrada_lanzaExcepcion() {
        // Simular: Ruta no encontrada (nota: el cliente debe devolver Map vacío/nulo si es 404)
        when(usuarioClient.getUsuarioById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(rutaClient.getRutaById(anyInt())).thenReturn(EMPTY_EXTERNAL_RESPONSE); // Ruta falla
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            abrirRutaService.save(rutaValida);
        });

        assertEquals("Ruta no encontrada", excepcion.getMessage());
        verify(abrirRutaRepository, never()).save(any());
    }

    // ==========================================
    // Tests para marcarFin(id)
    // ==========================================

    @Test
    void marcarFin_existe_actualizaFechaYGuarda() {
        // Preparar una ruta sin fecha final
        AbrirRuta rutaSinFin = AbrirRuta.builder().id(VALID_ID).fFinal(null).build();
        when(abrirRutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaSinFin));

        // Simular el guardado: el repositorio devuelve el objeto modificado
        when(abrirRutaRepository.save(any(AbrirRuta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AbrirRuta resultado = abrirRutaService.marcarFin(VALID_ID);

        // Verificar que la fecha final se haya establecido
        assertNotNull(resultado.getFFinal());
        // Verificar que se haya llamado al repositorio para guardar
        verify(abrirRutaRepository, times(1)).save(resultado);
    }

    @Test
    void marcarFin_noExiste_lanzaExcepcion() {
        when(abrirRutaRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            abrirRutaService.marcarFin(VALID_ID);
        });

        assertEquals("AbrirRuta no encontrada", excepcion.getMessage());
        verify(abrirRutaRepository, never()).save(any());
    }
}

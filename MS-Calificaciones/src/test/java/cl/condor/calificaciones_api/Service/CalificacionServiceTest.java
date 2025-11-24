package cl.condor.calificaciones_api.Service;

import cl.condor.calificaciones_api.model.Calificacion;
import cl.condor.calificaciones_api.repository.CalificacionRepository;
import cl.condor.calificaciones_api.service.CalificacionService;
import cl.condor.calificaciones_api.webclient.RutaClient;
import cl.condor.calificaciones_api.webclient.UsuarioClient;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Usamos @SpringBootTest o @ExtendWith(MockitoExtension.class)
// En este caso, @SpringBootTest como en tu ejemplo
@SpringBootTest
public class CalificacionServiceTest {

    // Inyecta el servicio que vamos a probar
    @InjectMocks
    private CalificacionService calificacionService;

    // Mocks de dependencias internas (Repository)
    @Mock
    private CalificacionRepository calificacionRepository;

    // Mocks de dependencias externas (WebClients)
    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private RutaClient rutaClient;

    private Calificacion calificacionValida;
    private final Integer VALID_ID = 1;

    private Calificacion calificacionValida2;
    private final Integer VALID_ID2 = 2;

    @BeforeEach
    void setUp() {
        // Inicializa los Mocks
        MockitoAnnotations.openMocks(this);

        // 1. Preparar una Calificación válida
        calificacionValida = new Calificacion();
        calificacionValida.setId(VALID_ID);
        calificacionValida.setIdUsuario(10); // ID de usuario simulado
        calificacionValida.setIdRuta(50);    // ID de ruta simulada
        calificacionValida.setPuntuacion(4);
        calificacionValida.setComentario("Excelente ruta!");
        calificacionValida.setFechaCreacion(LocalDateTime.now());

        calificacionValida2 = new Calificacion();
        calificacionValida2.setId(VALID_ID2);
        calificacionValida2.setIdUsuario(1); // ID de usuario simulado
        calificacionValida2.setIdRuta(2);    // ID de ruta simulada
        calificacionValida2.setPuntuacion(4);
        calificacionValida2.setComentario("Excelente ruta!");
        calificacionValida2.setFechaCreacion(LocalDateTime.now());
    }

    // --- Tests para findAll ---

    @Test
    void findAll_retornaListaDeCalificaciones() {
        // Simular: el repositorio devuelve una lista con la calificación válida
        when(calificacionRepository.findAll()).thenReturn(List.of(calificacionValida,calificacionValida2));

        List<Calificacion> resultado = calificacionService.findAll();

        // Verificar
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());

        // Verificar interacción
        verify(calificacionRepository, times(1)).findAll();
    }

    // --- Tests para findById ---

    @Test
    void findById_existe_retornaCalificacion() {
        // Simular: el repositorio devuelve la calificación envuelta en Optional
        when(calificacionRepository.findById(VALID_ID)).thenReturn(Optional.of(calificacionValida));

        Calificacion resultado = calificacionService.findById(VALID_ID);

        // Verificar
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());

        // Verificar interacción
        verify(calificacionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        // Simular: el repositorio devuelve Optional vacío
        when(calificacionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        // Verificar
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            calificacionService.findById(VALID_ID);
        });

        assertEquals("Calificación no encontrada", excepcion.getMessage());

        // Verificar interacción
        verify(calificacionRepository, times(1)).findById(VALID_ID);
    }

    // --- Tests para save ---

    @Test
    void save_valido_guardaYRetornaCalificacion() {
        // 1. Simular: Los clientes remotos confirman que el Usuario y la Ruta existen
        when(usuarioClient.getUsuarioById(calificacionValida.getIdUsuario()))
                .thenReturn(new JSONObject());

        when(rutaClient.getRutaById(calificacionValida.getIdRuta()))
                .thenReturn(new JSONObject());

        // 2. Simular: El repositorio guarda y devuelve la calificación
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionValida);

        // ... (El resto del test permanece igual)
        Calificacion resultado = calificacionService.save(calificacionValida);

        // Verificar
        assertNotNull(resultado);
        assertEquals(4, resultado.getPuntuacion());

        // Verificar interacciones críticas
        verify(usuarioClient, times(1)).getUsuarioById(calificacionValida.getIdUsuario());
        verify(rutaClient, times(1)).getRutaById(calificacionValida.getIdRuta());
        verify(calificacionRepository, times(1)).save(calificacionValida);
    }

    // --- Tests de Validación de Negocio ---

    @Test
    void save_puntuacionInvalida_lanzaExcepcion() {
        // Caso 1: Puntuación demasiado alta
        calificacionValida.setPuntuacion(6);
        assertThrows(RuntimeException.class, () -> calificacionService.save(calificacionValida));

        // Caso 2: Puntuación demasiado baja
        calificacionValida.setPuntuacion(0);
        assertThrows(RuntimeException.class, () -> calificacionService.save(calificacionValida));

        // Verificar que NADA se guardó ni se llamó a los clientes remotos
        verify(usuarioClient, never()).getUsuarioById(anyInt());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void save_idRutaNulo_lanzaExcepcion() {
        calificacionValida.setIdRuta(null);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            calificacionService.save(calificacionValida);
        });

        assertEquals("id_ruta es obligatorio", excepcion.getMessage());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void save_idUsuarioNulo_lanzaExcepcion() {
        calificacionValida.setIdUsuario(null);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            calificacionService.save(calificacionValida);
        });

        assertEquals("id_usuario es obligatorio", excepcion.getMessage());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void save_usuarioNoExiste_lanzaExcepcion() {
        // Simular: El cliente de Usuario lanza una excepción (simulando 404/not found)
        doThrow(new RuntimeException("Usuario no encontrado")).when(usuarioClient).getUsuarioById(calificacionValida.getIdUsuario());

        // La excepción debería propagarse
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            calificacionService.save(calificacionValida);
        });

        // La excepción de la validación remota se propaga
        assertEquals("Usuario no encontrado", excepcion.getMessage());

        // Verificar que NADA se guardó
        verify(calificacionRepository, never()).save(any());
        // Verificar que la validación de ruta no se ejecutó si la de usuario falló inmediatamente
        verify(rutaClient, never()).getRutaById(anyInt());
    }
}

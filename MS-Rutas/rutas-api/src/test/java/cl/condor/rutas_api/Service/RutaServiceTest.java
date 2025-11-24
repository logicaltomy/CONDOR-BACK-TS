package cl.condor.rutas_api.Service;

import cl.condor.rutas_api.model.Foto;
import cl.condor.rutas_api.model.Ruta;
import cl.condor.rutas_api.repository.DificultadRepository;
import cl.condor.rutas_api.repository.FotoRepository;
import cl.condor.rutas_api.repository.RutaRepository;
import cl.condor.rutas_api.repository.TipoRepository;
import cl.condor.rutas_api.service.RutaService;
import cl.condor.rutas_api.webclient.EstadoClient;
import cl.condor.rutas_api.webclient.RegionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

public class RutaServiceTest {

    @InjectMocks
    private RutaService rutaService;

    // Repositorios Mockeados
    @Mock private RutaRepository rutaRepository;
    @Mock private TipoRepository tipoRepository;
    @Mock private DificultadRepository dificultadRepository;
    @Mock private FotoRepository fotoRepository;

    // WebClients Mockeados
    @Mock private RegionClient regionClient;
    @Mock private EstadoClient estadoClient;

    private Ruta rutaEjemplo;
    private Foto fotoEjemplo;
    private final Integer VALID_ID = 1;
    private final String RUTA_NOT_FOUND_MSG = "Ruta no encontrada";
    private final String FOTO_NOT_FOUND_MSG = "Foto no encontrada";
    private final Map<String, Object> VALID_EXTERNAL_RESPONSE = Map.of("id", 1, "nombre", "Test");
    private final Map<String, Object> EMPTY_EXTERNAL_RESPONSE = Collections.emptyMap();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Ruta de ejemplo (Activa, Dificultad 1, Tipo 1, Región 7, Estado 1)
        rutaEjemplo = new Ruta(
                VALID_ID, "Cerro Test", "Desc Test", new BigDecimal("10.00"),
                null, null, "polyline_data", 3600, new BigDecimal("4.00"),
                LocalDateTime.now(), LocalDateTime.now(),
                1, 7, 1, 1
        );

        // Foto de ejemplo
        fotoEjemplo = new Foto(10, "Vista Cumbre", new byte[]{1, 2, 3}, VALID_ID);
    }

    // ==========================================
    // Tests: GET Rutas
    // ==========================================

    @Test
    void findAll_retornaListaRutas() {
        when(rutaRepository.findAll()).thenReturn(List.of(rutaEjemplo));

        List<Ruta> resultado = rutaService.findAll();

        assertFalse(resultado.isEmpty());
        verify(rutaRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaRuta() {
        when(rutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaEjemplo));

        Ruta resultado = rutaService.findById(VALID_ID);

        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId_ruta());
        verify(rutaRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaResponseStatusException() {
        when(rutaRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class, () -> {
            rutaService.findById(VALID_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, excepcion.getStatusCode());
        assertEquals(RUTA_NOT_FOUND_MSG, excepcion.getReason());
        verify(rutaRepository, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests: GET Fotos
    // ==========================================

    @Test
    void findByIdRuta_existe_retornaListaFotos() {
        when(fotoRepository.findByIdRuta(VALID_ID)).thenReturn(List.of(fotoEjemplo));

        List<Foto> resultado = rutaService.findByIdRuta(VALID_ID);

        assertFalse(resultado.isEmpty());
        verify(fotoRepository, times(1)).findByIdRuta(VALID_ID);
    }

    @Test
    void findByIdRuta_noHayFotos_lanzaRuntimeException() {
        when(fotoRepository.findByIdRuta(VALID_ID)).thenReturn(Collections.emptyList());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rutaService.findByIdRuta(VALID_ID);
        });

        assertEquals(RUTA_NOT_FOUND_MSG, excepcion.getMessage());
        verify(fotoRepository, times(1)).findByIdRuta(VALID_ID);
    }

    // ==========================================
    // Tests: SAVE Ruta (con Validaciones)
    // ==========================================

    private void simularTodasValidacionesRutaValidas() {
        // 1. WebClients (remotas)
        when(regionClient.getRegionesById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        // 2. Repositorios (locales)
        when(dificultadRepository.existsById(anyInt())).thenReturn(true);
        when(tipoRepository.existsById(anyInt())).thenReturn(true);
        // 3. Guardado
        when(rutaRepository.save(any(Ruta.class))).thenReturn(rutaEjemplo);
    }

    @Test
    void saveRuta_todoValido_guardaRuta() {
        simularTodasValidacionesRutaValidas();

        Ruta resultado = rutaService.save(rutaEjemplo);

        assertNotNull(resultado);
        verify(rutaRepository, times(1)).save(rutaEjemplo);
        verify(regionClient, times(1)).getRegionesById(rutaEjemplo.getId_region());
        verify(estadoClient, times(1)).getEstadosById(rutaEjemplo.getId_estado());
    }

    @Test
    void saveRuta_regionNoEncontrada_lanzaExcepcion() {
        // Simular: RegionClient devuelve respuesta vacía/nula
        when(regionClient.getRegionesById(anyInt())).thenReturn(EMPTY_EXTERNAL_RESPONSE);
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(dificultadRepository.existsById(anyInt())).thenReturn(true);
        when(tipoRepository.existsById(anyInt())).thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> rutaService.save(rutaEjemplo));

        assertEquals("Region no encontrado, no se puede guardar la ruta", excepcion.getMessage());
        verify(rutaRepository, never()).save(any());
    }

    @Test
    void saveRuta_dificultadNoExiste_lanzaExcepcion() {
        // Simular: Dificultad local no existe
        when(regionClient.getRegionesById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(estadoClient.getEstadosById(anyInt())).thenReturn(VALID_EXTERNAL_RESPONSE);
        when(dificultadRepository.existsById(anyInt())).thenReturn(false); // <--- Fallo aquí
        when(tipoRepository.existsById(anyInt())).thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> rutaService.save(rutaEjemplo));

        assertEquals("Dificultad no encontrada, no se puede guardar la ruta", excepcion.getMessage());
        verify(rutaRepository, never()).save(any());
    }

    // ==========================================
    // Tests: SAVE Foto
    // ==========================================

    @Test
    void saveFoto_rutaExiste_guardaFoto() {
        // Simular: Ruta existe
        when(rutaRepository.existsById(fotoEjemplo.getIdRuta())).thenReturn(true);
        when(fotoRepository.save(any(Foto.class))).thenReturn(fotoEjemplo);

        Foto resultado = rutaService.save(fotoEjemplo);

        assertNotNull(resultado);
        verify(rutaRepository, times(1)).existsById(fotoEjemplo.getIdRuta());
        verify(fotoRepository, times(1)).save(fotoEjemplo);
    }

    @Test
    void saveFoto_rutaNoExiste_lanzaExcepcion() {
        // Simular: Ruta no existe
        when(rutaRepository.existsById(fotoEjemplo.getIdRuta())).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> rutaService.save(fotoEjemplo));

        assertEquals(RUTA_NOT_FOUND_MSG, excepcion.getMessage());
        verify(fotoRepository, never()).save(any());
    }

    // ==========================================
    // Tests: Updates (PATCH)
    // ==========================================

    @Test
    void updateNombre_existe_actualizaYGuarda() {
        String nuevoNombre = "Ruta Modificada";
        when(rutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaEjemplo));
        when(rutaRepository.save(any(Ruta.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Devuelve el objeto modificado

        Ruta resultado = rutaService.updateNombre(VALID_ID, nuevoNombre);

        assertEquals(nuevoNombre, resultado.getNombre());
        verify(rutaRepository, times(1)).findById(VALID_ID);
        verify(rutaRepository, times(1)).save(rutaEjemplo);
    }

    @Test
    void banearRuta_existe_actualizaFechaYEstado() {
        LocalDateTime fBaneoAntes = rutaEjemplo.getF_baneo();

        when(rutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaEjemplo));
        when(rutaRepository.save(any(Ruta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ruta resultado = rutaService.banearRuta(VALID_ID);

        // Verifica que la fecha se actualizó
        assertNotNull(resultado.getF_baneo());
        // Verifica que el estado cambió a 3 (Baneado)
        assertEquals(3, resultado.getId_estado());
        assertNotEquals(fBaneoAntes, resultado.getF_baneo());
    }

    @Test
    void desbanearRuta_existe_limpiaFechaYEstado() {
        // Simular que la ruta ya está baneada
        rutaEjemplo.setF_baneo(LocalDateTime.now().minusDays(1));
        rutaEjemplo.setId_estado(3);

        when(rutaRepository.findById(VALID_ID)).thenReturn(Optional.of(rutaEjemplo));
        when(rutaRepository.save(any(Ruta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ruta resultado = rutaService.desbanearRuta(VALID_ID);

        // Verifica que la fecha de baneo es nula
        assertNull(resultado.getF_baneo());
        // Verifica que el estado cambió a 1 (Activo)
        assertEquals(1, resultado.getId_estado());
    }

    // ==========================================
    // Tests: DELETE Foto
    // ==========================================

    @Test
    void deleteFoto_existe_eliminaFoto() {
        when(fotoRepository.findById(fotoEjemplo.getId_foto())).thenReturn(Optional.of(fotoEjemplo));
        doNothing().when(fotoRepository).delete(fotoEjemplo);

        assertDoesNotThrow(() -> rutaService.deleteFoto(fotoEjemplo.getId_foto()));

        verify(fotoRepository, times(1)).findById(fotoEjemplo.getId_foto());
        verify(fotoRepository, times(1)).delete(fotoEjemplo);
    }

    @Test
    void deleteFoto_noExiste_lanzaExcepcion() {
        when(fotoRepository.findById(anyInt())).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            rutaService.deleteFoto(123);
        });

        assertEquals(FOTO_NOT_FOUND_MSG, excepcion.getMessage());
        verify(fotoRepository, never()).delete(any());
    }
}
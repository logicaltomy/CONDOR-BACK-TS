package cl.condor.logros_api.Service;

import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Tipo_condicion;
import cl.condor.logros_api.repository.CondicionRepository;
import cl.condor.logros_api.repository.TipoCondicionRepository;
import cl.condor.logros_api.service.CondicionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class CondicionServiceTest {

    @InjectMocks
    private CondicionService condicionService;

    // Repositorios Mockeados
    @Mock
    private CondicionRepository condicionRepository;

    @Mock
    private TipoCondicionRepository tipo_condicionRepository;

    private Condicion condicionKm;
    private Condicion condicionRegion;
    private Tipo_condicion tipoCondicionKm;

    private final int VALID_ID = 1;

    // Mensajes de error esperados
    private final String RUTA_NOT_FOUND_MSG = "Ruta no encontrada"; // findById(id)
    private final String CONDICION_NOT_FOUND_MSG = "Condicion no encontrada"; // update*
    private final String TIPO_CONDICION_NOT_FOUND_MSG = "Tipo condicion no encontrado"; // findTipoCondicionById

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Objeto de prueba para Tipo_condicion
        tipoCondicionKm = new Tipo_condicion(1, "Kilometraje");

        // Objeto de prueba para Condicion (Tipo 1: Kilometraje)
        condicionKm = new Condicion(VALID_ID, "Recorrer 10.00 km", new BigDecimal("10.00"), 1);

        // Objeto de prueba para Condicion (Tipo 2: Regiones)
        condicionRegion = new Condicion(2, "Terminar 3.00 rutas en diferentes regiones", new BigDecimal("3.00"), 2);
    }

    // ==========================================
    // Tests para Métodos Básicos de Condicion
    // ==========================================

    @Test
    void findAll_retornaListaCondiciones() {
        when(condicionRepository.findAll()).thenReturn(List.of(condicionKm, condicionRegion));
        List<Condicion> resultado = condicionService.findAll();
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        verify(condicionRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaCondicion() {
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.of(condicionKm));
        Condicion resultado = condicionService.findById(VALID_ID);
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId_condicion());
        verify(condicionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            condicionService.findById(VALID_ID);
        });
        // NOTA: El service usa "Ruta no encontrada" como mensaje de error aquí
        assertEquals(RUTA_NOT_FOUND_MSG, excepcion.getMessage());
        verify(condicionRepository, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para Métodos Básicos de Tipo_condicion
    // ==========================================

    @Test
    void findAllTipoCondicion_retornaLista() {
        when(tipo_condicionRepository.findAll()).thenReturn(List.of(tipoCondicionKm));
        List<Tipo_condicion> resultado = condicionService.findAllTipoCondicion();
        assertFalse(resultado.isEmpty());
        verify(tipo_condicionRepository, times(1)).findAll();
    }

    @Test
    void findTipoCondicionById_existe_retornaTipoCondicion() {
        when(tipo_condicionRepository.findById(VALID_ID)).thenReturn(Optional.of(tipoCondicionKm));
        Tipo_condicion resultado = condicionService.findTipoCondicionById(VALID_ID);
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId_tip_cond());
        verify(tipo_condicionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findTipoCondicionById_noExiste_lanzaExcepcion() {
        when(tipo_condicionRepository.findById(VALID_ID)).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            condicionService.findTipoCondicionById(VALID_ID);
        });
        assertEquals(TIPO_CONDICION_NOT_FOUND_MSG, excepcion.getMessage());
        verify(tipo_condicionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void saveTipoCondicion_guardaYRetorna() {
        when(tipo_condicionRepository.save(any(Tipo_condicion.class))).thenReturn(tipoCondicionKm);
        Tipo_condicion resultado = condicionService.save(tipoCondicionKm);
        assertNotNull(resultado);
        verify(tipo_condicionRepository, times(1)).save(tipoCondicionKm);
    }

    // ==========================================
    // Tests para save(Condicion) - Lógica de Autogeneración
    // ==========================================

    private void simularGuardadoCondicion(Condicion condicion) {
        // Simular que el repositorio devuelve el objeto después de la modificación
        when(condicionRepository.save(any(Condicion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        condicionService.save(condicion);
    }

    @Test
    void saveCondicion_tipo1Km_generaTextoCorrecto() {
        // ID 1: Kilometraje
        Condicion nuevaCondicion = new Condicion(null, null, new BigDecimal("75.50"), 1);
        simularGuardadoCondicion(nuevaCondicion);

        // Verifica que la cadena de condición es correcta
        assertEquals("Recorrer 75.50 km", nuevaCondicion.getCondicion());
        verify(condicionRepository, times(1)).save(nuevaCondicion);
    }

    @Test
    void saveCondicion_tipo2Regiones_generaTextoCorrecto() {
        // ID 2: Regiones
        Condicion nuevaCondicion = new Condicion(null, null, new BigDecimal("4"), 2);
        simularGuardadoCondicion(nuevaCondicion);

        // Verifica que la cadena de condición es correcta
        assertEquals("Terminar 4 rutas en diferentes regiones", nuevaCondicion.getCondicion());
    }

    @Test
    void saveCondicion_tipo3CantidadRutas_generaTextoCorrecto() {
        // ID 3: Cantidad Rutas
        Condicion nuevaCondicion = new Condicion(null, null, new BigDecimal("10"), 3);
        simularGuardadoCondicion(nuevaCondicion);

        // Verifica que la cadena de condición es correcta
        assertEquals("Termina 10 rutas", nuevaCondicion.getCondicion());
    }

    // ==========================================
    // Tests para updateRestriccion - Lógica de Actualización
    // ==========================================

    @Test
    void updateRestriccion_existe_actualizaValorYTexto() {
        BigDecimal nuevaRestriccion = new BigDecimal("15.00");

        // Simular que la condición existe (es la de Kilometraje, tipo 1)
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.of(condicionKm));
        // Simular guardado
        when(condicionRepository.save(any(Condicion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Condicion resultado = condicionService.updateRestriccion(VALID_ID, nuevaRestriccion);

        // Verifica la restricción numérica
        assertEquals(nuevaRestriccion, resultado.getRestriccion());
        // Verifica el texto autogenerado
        assertEquals("Recorrer 15.00 km", resultado.getCondicion());
        verify(condicionRepository, times(1)).save(resultado);
    }

    @Test
    void updateRestriccion_noExiste_lanzaExcepcion() {
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            condicionService.updateRestriccion(VALID_ID, new BigDecimal("100"));
        });

        assertEquals(CONDICION_NOT_FOUND_MSG, excepcion.getMessage());
        verify(condicionRepository, never()).save(any());
    }

    // ==========================================
    // Tests para updateidTipo - Lógica de Actualización
    // ==========================================

    @Test
    void updateidTipo_existe_cambiaTipoYActualizaTexto() {
        Integer nuevoTipoId = 2; // Cambiar a Regiones

        // Simular que la condición existe (es la de Kilometraje, tipo 1, restricción 10.00)
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.of(condicionKm));
        // Simular guardado
        when(condicionRepository.save(any(Condicion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Condicion resultado = condicionService.updateidTipo(VALID_ID, nuevoTipoId);

        // Verifica el nuevo ID de tipo
        assertEquals(nuevoTipoId, resultado.getId_tipo_condicion());
        // Verifica el texto autogenerado para el NUEVO TIPO
        assertEquals("Terminar 10.00 rutas en diferentes regiones", resultado.getCondicion());
        verify(condicionRepository, times(1)).save(resultado);
    }

    @Test
    void updateidTipo_noExiste_lanzaExcepcion() {
        when(condicionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            condicionService.updateidTipo(VALID_ID, 3);
        });

        assertEquals(CONDICION_NOT_FOUND_MSG, excepcion.getMessage());
        verify(condicionRepository, never()).save(any());
    }
}

package cl.condor.usuarios_api.service;

import cl.condor.usuarios_api.model.Estado;
import cl.condor.usuarios_api.repository.EstadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstadoServiceTest {

    @InjectMocks
    private EstadoService estadoService;

    @Mock
    private EstadoRepository estadoRepository;

    private Estado estadoEjemplo;
    private final Integer VALID_ID = 1;
    private final String ESTADO_NOT_FOUND_MSG = "Estado no encontrado";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        estadoEjemplo = new Estado();
        estadoEjemplo.setId(VALID_ID);
        estadoEjemplo.setNombre("Activo");
    }

    @Test
    void findAll_retornaListaEstados() {
        // Arrange
        when(estadoRepository.findAll()).thenReturn(Arrays.asList(estadoEjemplo));

        // Act
        List<Estado> resultado = estadoService.findAll();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(estadoRepository, times(1)).findAll();
    }

    @Test
    void findAll_retornaListaVacia() {
        // Arrange
        when(estadoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Estado> resultado = estadoService.findAll();

        // Assert
        assertTrue(resultado.isEmpty());
        verify(estadoRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaEstado() {
        // Arrange
        when(estadoRepository.findById(VALID_ID)).thenReturn(Optional.of(estadoEjemplo));

        // Act
        Estado resultado = estadoService.findById(VALID_ID);

        // Assert
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());
        verify(estadoRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        // Arrange
        when(estadoRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estadoService.findById(VALID_ID);
        });
        assertEquals(ESTADO_NOT_FOUND_MSG, exception.getMessage());
        verify(estadoRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void save_guardaEstadoCorrectamente() {
        // Arrange
        when(estadoRepository.save(any(Estado.class))).thenReturn(estadoEjemplo);

        // Act
        Estado resultado = estadoService.save(estadoEjemplo);

        // Assert
        assertNotNull(resultado);
        assertEquals(estadoEjemplo.getId(), resultado.getId());
        assertEquals(estadoEjemplo.getNombre(), resultado.getNombre());
        verify(estadoRepository, times(1)).save(estadoEjemplo);
    }
}
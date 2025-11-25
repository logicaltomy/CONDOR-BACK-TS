package cl.condor.usuarios_api.service;

import cl.condor.usuarios_api.model.Rol;
import cl.condor.usuarios_api.repository.RolRepository;
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

public class RolServiceTest {

    @InjectMocks
    private RolService rolService;

    @Mock
    private RolRepository rolRepository;

    private Rol rolEjemplo;
    private final Integer VALID_ID = 1;
    private final String ROL_NOT_FOUND_MSG = "Rol no encontrado";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rolEjemplo = new Rol();
        rolEjemplo.setId(VALID_ID);
        rolEjemplo.setNombre("Administrador");
    }

    @Test
    void findAll_retornaListaRoles() {
        when(rolRepository.findAll()).thenReturn(Arrays.asList(rolEjemplo));

        List<Rol> resultado = rolService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void findAll_retornaListaVacia() {
        when(rolRepository.findAll()).thenReturn(Collections.emptyList());

        List<Rol> resultado = rolService.findAll();

        assertTrue(resultado.isEmpty());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaRol() {
        when(rolRepository.findById(VALID_ID)).thenReturn(Optional.of(rolEjemplo));

        Rol resultado = rolService.findById(VALID_ID);

        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());
        verify(rolRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(rolRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rolService.findById(VALID_ID);
        });
        assertEquals(ROL_NOT_FOUND_MSG, exception.getMessage());
        verify(rolRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void save_guardaRolCorrectamente() {
        when(rolRepository.save(any(Rol.class))).thenReturn(rolEjemplo);

        Rol resultado = rolService.save(rolEjemplo);

        assertNotNull(resultado);
        assertEquals(rolEjemplo.getId(), resultado.getId());
        assertEquals(rolEjemplo.getNombre(), resultado.getNombre());
        verify(rolRepository, times(1)).save(rolEjemplo);
    }
}
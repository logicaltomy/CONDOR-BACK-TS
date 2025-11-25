package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Rol;
import cl.condor.usuarios_api.service.RolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolControllerTest {

    @InjectMocks
    private RolController rolController;

    @Mock
    private RolService rolService;

    private Rol rolEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rolEjemplo = new Rol();
        rolEjemplo.setId(VALID_ID);
        rolEjemplo.setNombre("Administrador");
    }

    @Test
    void getAll_retornaListaYOK() {
        when(rolService.findAll()).thenReturn(Arrays.asList(rolEjemplo));

        ResponseEntity<List<Rol>> response = rolController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(rolService, times(1)).findAll();
    }

    @Test
    void getAll_retornaNoContentSiVacio() {
        when(rolService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Rol>> response = rolController.getAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rolService, times(1)).findAll();
    }

    @Test
    void getById_retornaRolYOK() {
        when(rolService.findById(VALID_ID)).thenReturn(rolEjemplo);

        ResponseEntity<Rol> response = rolController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getId());
        verify(rolService, times(1)).findById(VALID_ID);
    }

    @Test
    void getById_retornaNotFoundSiNoExiste() {
        when(rolService.findById(VALID_ID)).thenThrow(new RuntimeException("Rol no encontrado"));

        ResponseEntity<Rol> response = rolController.getById(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(rolService, times(1)).findById(VALID_ID);
    }

    @Test
    void create_retornaRolYCreated() {
        when(rolService.save(any(Rol.class))).thenReturn(rolEjemplo);

        ResponseEntity<Rol> response = rolController.create(rolEjemplo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rolEjemplo, response.getBody());
        verify(rolService, times(1)).save(rolEjemplo);
    }
}
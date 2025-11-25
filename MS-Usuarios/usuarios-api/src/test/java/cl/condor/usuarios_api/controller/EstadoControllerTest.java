package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Estado;
import cl.condor.usuarios_api.service.EstadoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EstadoControllerTest {

    @InjectMocks
    private EstadoController estadoController;

    @Mock
    private EstadoService estadoService;

    private Estado estadoEjemplo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        estadoEjemplo = new Estado();
        estadoEjemplo.setId(1);
        estadoEjemplo.setNombre("Activo");
    }

    @Test
    void getAll_retornaListaYOK() {
        when(estadoService.findAll()).thenReturn(Arrays.asList(estadoEjemplo));

        ResponseEntity<List<Estado>> response = estadoController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAll_retornaNoContentSiVacio() {
        when(estadoService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Estado>> response = estadoController.getAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getById_retornaEstadoYOK() {
        when(estadoService.findById(1)).thenReturn(estadoEjemplo);

        ResponseEntity<Estado> response = estadoController.getById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getById_retornaNotFound() {
        when(estadoService.findById(any()))
                .thenThrow(new RuntimeException());

        ResponseEntity<Estado> response = estadoController.getById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void create_retornaCreatedYEstado() {
        when(estadoService.save(any(Estado.class))).thenReturn(estadoEjemplo);

        ResponseEntity<Estado> response = estadoController.create(estadoEjemplo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
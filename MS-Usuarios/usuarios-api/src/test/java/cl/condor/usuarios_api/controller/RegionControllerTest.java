package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Region;
import cl.condor.usuarios_api.service.RegionService;
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

public class RegionControllerTest {

    @InjectMocks
    private RegionController regionController;

    @Mock
    private RegionService regionService;

    private Region regionEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        regionEjemplo = new Region();
        regionEjemplo.setId(VALID_ID);
        regionEjemplo.setNombre("Región Metropolitana");
    }

    @Test
    void getAll_retornaListaYOK() {
        when(regionService.findAll()).thenReturn(Arrays.asList(regionEjemplo));

        ResponseEntity<List<Region>> response = regionController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(regionService, times(1)).findAll();
    }

    @Test
    void getAll_retornaNoContentSiVacio() {
        when(regionService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Region>> response = regionController.getAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(regionService, times(1)).findAll();
    }

    @Test
    void getById_retornaRegionYOK() {
        when(regionService.findById(VALID_ID)).thenReturn(regionEjemplo);

        ResponseEntity<Region> response = regionController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getId());
        verify(regionService, times(1)).findById(VALID_ID);
    }

    @Test
    void getById_retornaNotFoundSiNoExiste() {
        when(regionService.findById(VALID_ID)).thenThrow(new RuntimeException("Región no encontrada"));

        ResponseEntity<Region> response = regionController.getById(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(regionService, times(1)).findById(VALID_ID);
    }

    @Test
    void create_retornaRegionYCreated() {
        when(regionService.save(any(Region.class))).thenReturn(regionEjemplo);

        ResponseEntity<Region> response = regionController.create(regionEjemplo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(regionEjemplo, response.getBody());
        verify(regionService, times(1)).save(regionEjemplo);
    }
}
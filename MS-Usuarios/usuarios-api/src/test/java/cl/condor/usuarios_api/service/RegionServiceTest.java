package cl.condor.usuarios_api.service;

import cl.condor.usuarios_api.model.Region;
import cl.condor.usuarios_api.repository.RegionRepository;
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

public class RegionServiceTest {

    @InjectMocks
    private RegionService regionService;

    @Mock
    private RegionRepository regionRepository;

    private Region regionEjemplo;
    private final Integer VALID_ID = 1;
    private final String REGION_NOT_FOUND_MSG = "Región no encontrada";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        regionEjemplo = new Region();
        regionEjemplo.setId(VALID_ID);
        regionEjemplo.setNombre("Región Metropolitana");
    }

    @Test
    void findAll_retornaListaRegiones() {
        when(regionRepository.findAll()).thenReturn(Arrays.asList(regionEjemplo));

        List<Region> resultado = regionService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void findAll_retornaListaVacia() {
        when(regionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Region> resultado = regionService.findAll();

        assertTrue(resultado.isEmpty());
        verify(regionRepository, times(1)).findAll();
    }

    @Test
    void findById_existe_retornaRegion() {
        when(regionRepository.findById(VALID_ID)).thenReturn(Optional.of(regionEjemplo));

        Region resultado = regionService.findById(VALID_ID);

        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());
        verify(regionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(regionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            regionService.findById(VALID_ID);
        });
        assertEquals(REGION_NOT_FOUND_MSG, exception.getMessage());
        verify(regionRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void save_guardaRegionCorrectamente() {
        when(regionRepository.save(any(Region.class))).thenReturn(regionEjemplo);

        Region resultado = regionService.save(regionEjemplo);

        assertNotNull(resultado);
        assertEquals(regionEjemplo.getId(), resultado.getId());
        assertEquals(regionEjemplo.getNombre(), resultado.getNombre());
        verify(regionRepository, times(1)).save(regionEjemplo);
    }
}
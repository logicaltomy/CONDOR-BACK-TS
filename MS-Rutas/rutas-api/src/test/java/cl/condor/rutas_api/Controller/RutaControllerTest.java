package cl.condor.rutas_api.Controller;

import cl.condor.rutas_api.controller.RutaController;
import cl.condor.rutas_api.model.Foto;
import cl.condor.rutas_api.model.Ruta;
import cl.condor.rutas_api.service.RutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class RutaControllerTest {

    @InjectMocks
    private RutaController rutaController;

    @Mock
    private RutaService rutaService;

    private Ruta rutaEjemplo;
    private Foto fotoEjemplo;

    private final Integer VALID_ID = 1;
    private final String RUTA_NOT_FOUND_MSG = "Ruta no encontrada";
    private final String FOTO_NOT_FOUND_MSG = "Foto no encontrada";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rutaEjemplo = new Ruta();
        rutaEjemplo.setId_ruta(VALID_ID);
        rutaEjemplo.setNombre("Cerro Manquehue");
        rutaEjemplo.setDistancia(new BigDecimal("5.50"));
        rutaEjemplo.setId_estado(1);

        fotoEjemplo = new Foto();
        fotoEjemplo.setId_foto(10);
        fotoEjemplo.setNombre("Vista_Cumbre");
        fotoEjemplo.setIdRuta(VALID_ID);
        fotoEjemplo.setImagen(new byte[]{1, 2, 3});
    }

    // ==========================================
    // Tests: GET Rutas
    // ==========================================

    @Test
    void findAll_retornaListaYOK() {
        when(rutaService.findAll()).thenReturn(List.of(rutaEjemplo));

        ResponseEntity<List<Ruta>> response = rutaController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(rutaService, times(1)).findAll();
    }

    @Test
    void findAll_retornaNoContentSiVacio() {
        when(rutaService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Ruta>> response = rutaController.findAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rutaService, times(1)).findAll();
    }

    @Test
    void findAll_errorConexion_retornaServiceUnavailable() {
        // Simular: Falla al llamar a un servicio externo (e.g., Estado o Tipo)
        when(rutaService.findAll()).thenThrow(new WebClientRequestException(
                new RuntimeException("Timeout"),
                HttpMethod.GET,
                URI.create("http://mock"),
                HttpHeaders.EMPTY
        ));

        ResponseEntity<List<Ruta>> response = rutaController.findAll();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        verify(rutaService, times(1)).findAll();
    }

    @Test
    void getById_retornaRutaYOK() {
        when(rutaService.findById(VALID_ID)).thenReturn(rutaEjemplo);

        ResponseEntity<Ruta> response = rutaController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getId_ruta());
        verify(rutaService, times(1)).findById(VALID_ID);
    }

    @Test
    void getById_retornaNotFoundSiNoExiste() {
        when(rutaService.findById(VALID_ID)).thenThrow(new RuntimeException(RUTA_NOT_FOUND_MSG));

        ResponseEntity<Ruta> response = rutaController.getById(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(rutaService, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests: POST Rutas y Fotos
    // ==========================================

    @Test
    void createRuta_valida_retornaRutaYOK() {
        when(rutaService.save(any(Ruta.class))).thenReturn(rutaEjemplo);

        ResponseEntity<Ruta> response = rutaController.createRuta(rutaEjemplo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rutaEjemplo, response.getBody());
        verify(rutaService, times(1)).save(rutaEjemplo);
    }

    @Test
    void createRuta_errorValidacion_retornaBadRequest() {
        // Simular: Error interno del service (e.g., FK no encontrada)
        when(rutaService.save(any(Ruta.class))).thenThrow(new RuntimeException("Tipo no encontrado"));

        ResponseEntity<Ruta> response = rutaController.createRuta(rutaEjemplo);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(rutaService, times(1)).save(rutaEjemplo);
    }

    @Test
    void createFoto_valida_retornaFotoYOK() {
        when(rutaService.save(any(Foto.class))).thenReturn(fotoEjemplo);

        ResponseEntity<Foto> response = rutaController.createFoto(fotoEjemplo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fotoEjemplo, response.getBody());
        verify(rutaService, times(1)).save(fotoEjemplo);
    }

    @Test
    void createFoto_errorWebClient_retornaServiceUnavailable() {
        // Simular: Falla de conexión del service
        when(rutaService.save(any(Foto.class))).thenThrow(new WebClientRequestException(
                new RuntimeException("I/O Error"),
                HttpMethod.POST,
                URI.create("http://mock"),
                HttpHeaders.EMPTY
        ));

        ResponseEntity<Foto> response = rutaController.createFoto(fotoEjemplo);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        verify(rutaService, times(1)).save(fotoEjemplo);
    }

    // ==========================================
    // Tests: GET Fotos por Ruta ID
    // ==========================================

    @Test
    void getFotosByIdRutas_retornaListaYOK() {
        when(rutaService.findByIdRuta(VALID_ID)).thenReturn(List.of(fotoEjemplo));

        ResponseEntity<List<Foto>> response = rutaController.getFotosByIdRutas(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(rutaService, times(1)).findByIdRuta(VALID_ID);
    }

    @Test
    void getFotosByIdRutas_retornaNotFoundSiRutaNoExiste() {
        // Simular: El service lanza excepción si la ruta no existe o no tiene fotos
        when(rutaService.findByIdRuta(VALID_ID)).thenThrow(new RuntimeException(FOTO_NOT_FOUND_MSG));

        ResponseEntity<List<Foto>> response = rutaController.getFotosByIdRutas(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(rutaService, times(1)).findByIdRuta(VALID_ID);
    }

    // ==========================================
    // Tests: PATCH Actualizaciones
    // ==========================================

    @Test
    void updateNombre_exito_retornaRutaYOK() {
        String nuevoNombre = "Cerro Manquehue Nuevo";
        when(rutaService.updateNombre(eq(VALID_ID), eq(nuevoNombre))).thenReturn(rutaEjemplo);

        ResponseEntity<Ruta> response = rutaController.updateNombre(VALID_ID, nuevoNombre);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rutaService, times(1)).updateNombre(VALID_ID, nuevoNombre);
    }

    @Test
    void updateNombre_retornaNotFoundSiRutaNoExiste() {
        when(rutaService.updateNombre(eq(VALID_ID), anyString())).thenThrow(new RuntimeException(RUTA_NOT_FOUND_MSG));

        ResponseEntity<Ruta> response = rutaController.updateNombre(VALID_ID, "Nuevo");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(rutaService, times(1)).updateNombre(VALID_ID, "Nuevo");
    }

    @Test
    void updateBanear_exito_retornaRutaYOK() {
        // Simular que el service devuelve la ruta con la fecha de baneo
        Ruta rutaBaneada = rutaEjemplo;
        rutaBaneada.setF_baneo(LocalDateTime.now());
        when(rutaService.banearRuta(VALID_ID)).thenReturn(rutaBaneada);

        ResponseEntity<Ruta> response = rutaController.updateBanear(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rutaService, times(1)).banearRuta(VALID_ID);
    }

    @Test
    void updateDesbanear_errorConexion_retornaServiceUnavailable() {
        doThrow(new WebClientRequestException(
                new RuntimeException("Host down"),
                HttpMethod.PATCH,
                URI.create("http://mock"),
                HttpHeaders.EMPTY
        )).when(rutaService).desbanearRuta(VALID_ID);

        ResponseEntity<Ruta> response = rutaController.updateDesbanear(VALID_ID);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        verify(rutaService, times(1)).desbanearRuta(VALID_ID);
    }

    // ==========================================
    // Tests: DELETE Foto
    // ==========================================

    @Test
    void deleteFoto_exito_retornaNoContent() {
        doNothing().when(rutaService).deleteFoto(VALID_ID);

        ResponseEntity<Void> response = rutaController.deleteFoto(VALID_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rutaService, times(1)).deleteFoto(VALID_ID);
    }

    @Test
    void deleteFoto_retornaNotFoundSiNoExiste() {
        doThrow(new RuntimeException(FOTO_NOT_FOUND_MSG)).when(rutaService).deleteFoto(VALID_ID);

        ResponseEntity<Void> response = rutaController.deleteFoto(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(rutaService, times(1)).deleteFoto(VALID_ID);
    }
}

package cl.condor.iniciar_rutas_api.Controller;

import cl.condor.iniciar_rutas_api.controller.AbrirRutaController;
import cl.condor.iniciar_rutas_api.model.AbrirRuta;
import cl.condor.iniciar_rutas_api.service.AbrirRutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbrirRutaControllerTest {

    @InjectMocks
    private AbrirRutaController abrirRutaController;

    @Mock
    private AbrirRutaService abrirRutaService;

    private AbrirRuta rutaEjemplo;
    private final Integer VALID_ID = 1;
    private final Integer VALID_USER_ID = 10;
    private final String NOT_FOUND_MSG = "AbrirRuta no encontrada";
    private final String VALIDATION_ERROR_MSG = "Ruta no encontrada"; // Error de validación del Service

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rutaEjemplo = AbrirRuta.builder()
                .id(VALID_ID)
                .idUsuario(VALID_USER_ID)
                .idRuta(50)
                .idEstado(1)
                .fInicio(LocalDateTime.now().minusHours(1))
                .fFinal(null)
                .build();
    }

    // ==========================================
    // Tests para GET /api/v1/abrir-ruta (findAll)
    // ==========================================

    @Test
    void getAllAbrirRuta_retornaListaYOK() {
        when(abrirRutaService.findAll()).thenReturn(List.of(rutaEjemplo));

        ResponseEntity<List<AbrirRuta>> response = abrirRutaController.getAllAbrirRuta();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(abrirRutaService, times(1)).findAll();
    }

    @Test
    void getAllAbrirRuta_retornaNoContentSiVacio() {
        when(abrirRutaService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<AbrirRuta>> response = abrirRutaController.getAllAbrirRuta();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).findAll();
    }

    // ==========================================
    // Tests para GET /api/v1/abrir-ruta/{id} (findById)
    // ==========================================

    @Test
    void getAbrirRutaById_retornaRutaYOK() {
        when(abrirRutaService.findById(VALID_ID)).thenReturn(rutaEjemplo);

        ResponseEntity<AbrirRuta> response = abrirRutaController.getAbrirRutaById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(abrirRutaService, times(1)).findById(VALID_ID);
    }

    @Test
    void getAbrirRutaById_retornaNotFoundSiNoExiste() {
        // Simular la RuntimeException lanzada por el Service
        when(abrirRutaService.findById(VALID_ID)).thenThrow(new RuntimeException(NOT_FOUND_MSG));

        ResponseEntity<AbrirRuta> response = abrirRutaController.getAbrirRutaById(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para GET /api/v1/abrir-ruta/usuario/{id} (findByIdUsuario)
    // ==========================================

    @Test
    void getAbrirRutaByIdUsuario_retornaListaYOK() {
        when(abrirRutaService.findByIdUsuario(VALID_USER_ID)).thenReturn(List.of(rutaEjemplo));

        ResponseEntity<List<AbrirRuta>> response = abrirRutaController.getAbrirRutaByIdUsuario(VALID_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(abrirRutaService, times(1)).findByIdUsuario(VALID_USER_ID);
    }

    @Test
    void getAbrirRutaByIdUsuario_retornaNotFoundSiNoHayRutas() {
        // El Service lanza RuntimeException cuando no encuentra rutas para el usuario
        when(abrirRutaService.findByIdUsuario(VALID_USER_ID)).thenThrow(new RuntimeException("No se encontraron rutas para el usuario con id " + VALID_USER_ID));

        ResponseEntity<List<AbrirRuta>> response = abrirRutaController.getAbrirRutaByIdUsuario(VALID_USER_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).findByIdUsuario(VALID_USER_ID);
    }

    // ==========================================
    // Tests para POST /api/v1/abrir-ruta (createAbrirRuta)
    // ==========================================

    @Test
    void createAbrirRuta_valido_retornaCreatedYBody() {
        when(abrirRutaService.save(any(AbrirRuta.class))).thenReturn(rutaEjemplo);

        ResponseEntity<AbrirRuta> response = abrirRutaController.createAbrirRuta(rutaEjemplo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(rutaEjemplo, response.getBody());
        verify(abrirRutaService, times(1)).save(rutaEjemplo);
    }

    @Test
    void createAbrirRuta_errorValidacionService_retornaBadRequest() {
        // Simular: El service lanza una excepción por validación (e.g., Ruta no encontrada)
        when(abrirRutaService.save(any(AbrirRuta.class))).thenThrow(new RuntimeException(VALIDATION_ERROR_MSG));

        ResponseEntity<AbrirRuta> response = abrirRutaController.createAbrirRuta(rutaEjemplo);

        // Verifica la captura de 'Exception e' -> ResponseEntity.badRequest().build()
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).save(rutaEjemplo);
    }

    @Test
    void createAbrirRuta_errorWebClient_retornaServiceUnavailable() {
        // Simular: Falla al contactar un microservicio remoto.
        // Usamos el constructor de 4 argumentos, asegurando que el último (HttpHeaders) no sea null.
        doThrow(new WebClientRequestException(
                new RuntimeException("Timeout"),
                HttpMethod.POST,
                URI.create("http://mock"),
                HttpHeaders.EMPTY // SOLUCIÓN: El cuarto argumento debe ser HttpHeaders válido.
        )).when(abrirRutaService).save(any(AbrirRuta.class));

        ResponseEntity<AbrirRuta> response = abrirRutaController.createAbrirRuta(rutaEjemplo);

        // ... Verificaciones
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).save(rutaEjemplo);
    }

    // ==========================================
    // Tests para PATCH /api/v1/abrir-ruta/marcarFin/{id}
    // ==========================================

    @Test
    void marcarFin_existe_retornaRutaActualizadaYOK() {
        // Simular: el service marca el fin (pone fFinal) y devuelve la ruta actualizada
        AbrirRuta rutaFinalizada = AbrirRuta.builder().id(VALID_ID).fFinal(LocalDateTime.now()).build();
        when(abrirRutaService.marcarFin(VALID_ID)).thenReturn(rutaFinalizada);

        ResponseEntity<AbrirRuta> response = abrirRutaController.marcarFin(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rutaFinalizada.getId(), response.getBody().getId());
        verify(abrirRutaService, times(1)).marcarFin(VALID_ID);
    }

    @Test
    void marcarFin_noExiste_retornaNotFound() {
        // Simular: El service lanza la excepción específica de "no encontrada"
        when(abrirRutaService.marcarFin(VALID_ID)).thenThrow(new RuntimeException(NOT_FOUND_MSG));

        ResponseEntity<AbrirRuta> response = abrirRutaController.marcarFin(VALID_ID);

        // Verifica la captura de 'RuntimeException e' y el if condicional
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).marcarFin(VALID_ID);
    }

    @Test
    void marcarFin_errorInternoService_retornaInternalServerError() {
        // Simular: El service lanza otra RuntimeException
        when(abrirRutaService.marcarFin(VALID_ID)).thenThrow(new RuntimeException(VALIDATION_ERROR_MSG));

        ResponseEntity<AbrirRuta> response = abrirRutaController.marcarFin(VALID_ID);

        // Verifica la captura de 'RuntimeException e' y el else final
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).marcarFin(VALID_ID);
    }

    @Test
    void marcarFin_errorWebClient_retornaServiceUnavailable() {
        // Simular: Falla al contactar un microservicio remoto.
        // Usamos el constructor de 4 argumentos:
        // (Throwable cause, HttpMethod method, URI uri, HttpHeaders headers)
        when(abrirRutaService.marcarFin(VALID_ID))
                .thenThrow(new WebClientRequestException(
                        new RuntimeException("I/O Error"),
                        HttpMethod.PATCH,
                        URI.create("http://mock"),
                        HttpHeaders.EMPTY // <--- CORRECCIÓN CLAVE
                ));

        ResponseEntity<AbrirRuta> response = abrirRutaController.marcarFin(VALID_ID);

        // Verifica la captura de 'WebClientRequestException e' -> ResponseEntity.status(503)
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNull(response.getBody());
        verify(abrirRutaService, times(1)).marcarFin(VALID_ID);
    }
}

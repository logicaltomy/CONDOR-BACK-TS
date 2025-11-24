package cl.condor.logros_api.Controller;

import cl.condor.logros_api.controller.LogroController;
import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Logro;
import cl.condor.logros_api.model.Tipo_condicion;
import cl.condor.logros_api.model.Trofeo;
import cl.condor.logros_api.service.CondicionService;
import cl.condor.logros_api.service.LogroService;
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

public class LogroControllerTest {

    @InjectMocks
    private LogroController logroController;

    @Mock
    private LogroService logroService;

    @Mock
    private CondicionService condicionService;

    private Logro logroEjemplo;
    private Condicion condicionEjemplo;
    private Trofeo trofeoEjemplo;
    private Tipo_condicion tipoCondicionEjemplo;

    private final int VALID_ID = 1;
    private final int VALID_USER_ID = 100;
    private final String LOGRO_NOT_FOUND_MSG = "Logro no encontrado";
    private final String CONDICION_NOT_FOUND_MSG = "Condicion no encontrada";
    private final String TIPO_CONDICION_NOT_FOUND_MSG = "Tipo condicion no encontrado";
    private final String LOGRO_NOT_EARNED_MSG = "No se ha ganado el logro";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        logroEjemplo = new Logro();
        logroEjemplo.setIdLogro(VALID_ID);
        logroEjemplo.setNombre("Test Logro");
        logroEjemplo.setDescripcion("Descripcion de prueba");
        logroEjemplo.setId_estado(1);
        logroEjemplo.setId_condicion(1);

        condicionEjemplo = new Condicion();
        condicionEjemplo.setId_condicion(VALID_ID);
        condicionEjemplo.setCondicion("Recorrer 40 km");
        condicionEjemplo.setRestriccion(new BigDecimal("40.00"));
        condicionEjemplo.setId_tipo_condicion(1);

        trofeoEjemplo = new Trofeo();
        trofeoEjemplo.setId_trofeo(1);
        trofeoEjemplo.setIdUsuario(VALID_USER_ID);
        trofeoEjemplo.setF_obtencion(LocalDateTime.now());

        tipoCondicionEjemplo = new Tipo_condicion();
        tipoCondicionEjemplo.setId_tip_cond(1);
        tipoCondicionEjemplo.setNombre("Kilometraje");
    }

    @Test
    void getLogros_retornaListaYOK() {
        when(logroService.findAll()).thenReturn(List.of(logroEjemplo));

        ResponseEntity<List<Logro>> response = logroController.getLogros();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(logroService, times(1)).findAll();
    }

    @Test
    void getLogros_retornaNoContentSiVacio() {
        when(logroService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Logro>> response = logroController.getLogros();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(logroService, times(1)).findAll();
    }

    @Test
    void getLogroById_retornaLogroYOK() {
        when(logroService.findById(VALID_ID)).thenReturn(logroEjemplo);

        ResponseEntity<Logro> response = logroController.getLogro(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getIdLogro());
        verify(logroService, times(1)).findById(VALID_ID);
    }

    @Test
    void getLogroById_retornaNotFoundSiNoExiste() {
        when(logroService.findById(VALID_ID)).thenReturn(null); // findById devuelve null si no encuentra

        ResponseEntity<Logro> response = logroController.getLogro(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(logroService, times(1)).findById(VALID_ID);
    }

    @Test
    void createLogro_valido_retornaOK() {
        when(logroService.save(any(Logro.class))).thenReturn(logroEjemplo);

        ResponseEntity<Logro> response = logroController.createLogro(logroEjemplo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(logroService, times(1)).save(logroEjemplo);
    }

    @Test
    void createLogro_errorValidacion_retornaBadRequest() {
        // Simular: El service lanza una excepción por dato inválido o FK faltante
        when(logroService.save(any(Logro.class))).thenThrow(new RuntimeException("Error de validación de condición"));

        ResponseEntity<Logro> response = logroController.createLogro(logroEjemplo);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Captura de catch(Exception e)
        verify(logroService, times(1)).save(logroEjemplo);
    }

    @Test
    void ganarLogro_exito_retornaTrofeoYOK() {
        when(logroService.ganarLogro(VALID_USER_ID)).thenReturn(trofeoEjemplo);

        ResponseEntity<Trofeo> response = logroController.ganarLogro(VALID_USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trofeoEjemplo.getId_trofeo(), response.getBody().getId_trofeo());
        verify(logroService, times(1)).ganarLogro(VALID_USER_ID);
    }

    @Test
    void ganarLogro_noCumpleCondicion_retornaForbidden() {
        // Simular: El service lanza la excepción específica de negocio (403)
        when(logroService.ganarLogro(VALID_USER_ID)).thenThrow(new RuntimeException(LOGRO_NOT_EARNED_MSG));

        ResponseEntity<Trofeo> response = logroController.ganarLogro(VALID_USER_ID);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // Captura de if(e.getMessage().equals("No se ha ganado el logro"))
        verify(logroService, times(1)).ganarLogro(VALID_USER_ID);
    }

    @Test
    void ganarLogro_errorConexion_retornaServiceUnavailable() {
        // Simular: Falla al contactar un microservicio remoto (503)
        when(logroService.ganarLogro(VALID_USER_ID)).thenThrow(new WebClientRequestException(
                new RuntimeException("Connection refused"),
                org.springframework.http.HttpMethod.GET,
                URI.create("http://mock"),
                org.springframework.http.HttpHeaders.EMPTY
        ));

        ResponseEntity<Trofeo> response = logroController.ganarLogro(VALID_USER_ID);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()); // Captura de catch(WebClientRequestException e)
        verify(logroService, times(1)).ganarLogro(VALID_USER_ID);
    }

    @Test
    void ganarLogro_errorInterno_retornaInternalServerError() {
        // Simular: Cualquier otra RuntimeException (500)
        when(logroService.ganarLogro(VALID_USER_ID)).thenThrow(new RuntimeException("Error inesperado en DB"));

        ResponseEntity<Trofeo> response = logroController.ganarLogro(VALID_USER_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Captura del catch(RuntimeException e) genérico
        verify(logroService, times(1)).ganarLogro(VALID_USER_ID);
    }

    @Test
    void getCondiciones_retornaListaYOK() {
        when(condicionService.findAll()).thenReturn(List.of(condicionEjemplo));

        ResponseEntity<List<Condicion>> response = logroController.getCondiciones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(condicionService, times(1)).findAll();
    }

    @Test
    void getCondicionById_retornaCondicionYOK() {
        when(condicionService.findById(VALID_ID)).thenReturn(condicionEjemplo);

        ResponseEntity<Condicion> response = logroController.getCondicion(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getId_condicion());
        verify(condicionService, times(1)).findById(VALID_ID);
    }

    @Test
    void getCondicionById_retornaNotFoundSiNoExiste() {
        // Simular: El service lanza la RuntimeException con el mensaje específico (Ruta no encontrada)
        when(condicionService.findById(VALID_ID)).thenThrow(new RuntimeException("Ruta no encontrada"));

        ResponseEntity<Condicion> response = logroController.getCondicion(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(condicionService, times(1)).findById(VALID_ID);
    }

    @Test
    void getCondicionById_manejaErrorInterno() {
        // Simular: Otro error del Service
        when(condicionService.findById(VALID_ID)).thenThrow(new RuntimeException(CONDICION_NOT_FOUND_MSG));

        ResponseEntity<Condicion> response = logroController.getCondicion(VALID_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(condicionService, times(1)).findById(VALID_ID);
    }

    @Test
    void getTipoCondicion_retornaListaYOK() {
        when(condicionService.findAllTipoCondicion()).thenReturn(List.of(tipoCondicionEjemplo));

        ResponseEntity<List<Tipo_condicion>> response = logroController.getTipoCondicion();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        verify(condicionService, times(1)).findAllTipoCondicion();
    }

    @Test
    void getTipoCondicionById_retornaTipoCondicionYOK() {
        when(condicionService.findTipoCondicionById(VALID_ID)).thenReturn(tipoCondicionEjemplo);

        ResponseEntity<Tipo_condicion> response = logroController.getTipoCondicion(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VALID_ID, response.getBody().getId_tip_cond());
        verify(condicionService, times(1)).findTipoCondicionById(VALID_ID);
    }

    @Test
    void getTipoCondicionById_retornaNotFoundSiNoExiste() {
        when(condicionService.findTipoCondicionById(VALID_ID)).thenThrow(new RuntimeException(TIPO_CONDICION_NOT_FOUND_MSG));

        ResponseEntity<Tipo_condicion> response = logroController.getTipoCondicion(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(condicionService, times(1)).findTipoCondicionById(VALID_ID);
    }

    @Test
    void updateNombreLogro_exito_retornaLogroYOK() {
        String nuevoNombre = "Nombre Actualizado";
        when(logroService.updateNombre(VALID_ID, nuevoNombre)).thenReturn(logroEjemplo);

        ResponseEntity<Logro> response = logroController.updateNombre(VALID_ID, nuevoNombre);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(logroService, times(1)).updateNombre(VALID_ID, nuevoNombre);
    }

    @Test
    void updateNombreLogro_retornaNotFoundSiLogroNoExiste() {
        // Simular que, independientemente de los argumentos, se lanza la excepción.
        when(logroService.updateNombre(anyInt(), anyString())).thenThrow(new RuntimeException(LOGRO_NOT_FOUND_MSG));

        // El valor que se envía al controlador
        String valorEnviado = "Cualquier cosa";
        ResponseEntity<Logro> response = logroController.updateNombre(VALID_ID, valorEnviado);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(logroService, times(1)).updateNombre(eq(VALID_ID), eq(valorEnviado));
    }

    @Test
    void updateRestriccionCondicion_exito_retornaCondicionYOK() {
        BigDecimal nuevaRestriccion = new BigDecimal("50.00");
        when(condicionService.updateRestriccion(VALID_ID, nuevaRestriccion)).thenReturn(condicionEjemplo);

        ResponseEntity<Condicion> response = logroController.updateRestriccion(VALID_ID, nuevaRestriccion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(condicionService, times(1)).updateRestriccion(VALID_ID, nuevaRestriccion);
    }

    @Test
    void updateRestriccionCondicion_retornaNotFoundSiCondicionNoExiste() {
        BigDecimal nuevaRestriccion = new BigDecimal("50.00");
        when(condicionService.updateRestriccion(VALID_ID, nuevaRestriccion)).thenThrow(new RuntimeException(CONDICION_NOT_FOUND_MSG));

        ResponseEntity<Condicion> response = logroController.updateRestriccion(VALID_ID, nuevaRestriccion);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(condicionService, times(1)).updateRestriccion(VALID_ID, nuevaRestriccion);
    }

}

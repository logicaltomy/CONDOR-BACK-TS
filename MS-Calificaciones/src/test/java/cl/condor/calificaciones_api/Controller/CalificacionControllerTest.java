package cl.condor.calificaciones_api.Controller;

import cl.condor.calificaciones_api.controller.CalificacionController;
import cl.condor.calificaciones_api.model.Calificacion;
import cl.condor.calificaciones_api.service.CalificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalificacionControllerTest {

    // El controlador que inyecta las dependencias simuladas
    @InjectMocks
    private CalificacionController calificacionController;

    // Simulación del servicio (la dependencia del controlador)
    @Mock
    private CalificacionService calificacionService;

    private Calificacion calificacionEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        // Inicializar los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);

        // Configurar el objeto de prueba
        calificacionEjemplo = new Calificacion();
        calificacionEjemplo.setId(VALID_ID);
        calificacionEjemplo.setIdUsuario(10);
        calificacionEjemplo.setIdRuta(50);
        calificacionEjemplo.setPuntuacion(5);
        calificacionEjemplo.setComentario("Una ruta de 5 estrellas");
        calificacionEjemplo.setFechaCreacion(LocalDateTime.now());
    }

    // ==========================================
    // Tests para GET /api/v1/calificaciones
    // ==========================================

    @Test
    void getAllCalificaciones_retornaListaYOK() {
        // Simular: El servicio devuelve una lista con la calificación
        when(calificacionService.findAll()).thenReturn(List.of(calificacionEjemplo));

        // Ejecutar el método del controlador
        ResponseEntity<List<Calificacion>> response = calificacionController.getAllCalificaciones();

        // Verificar la respuesta HTTP (200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        // Verificar que el método del servicio fue llamado
        verify(calificacionService, times(1)).findAll();
    }

    @Test
    void getAllCalificaciones_retornaNoContentSiVacio() {
        // Simular: El servicio devuelve una lista vacía
        when(calificacionService.findAll()).thenReturn(Collections.emptyList());

        // Ejecutar el método del controlador
        ResponseEntity<List<Calificacion>> response = calificacionController.getAllCalificaciones();

        // Verificar la respuesta HTTP (204 No Content)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody()); // El body debe ser nulo en 204

        // Verificar que el método del servicio fue llamado
        verify(calificacionService, times(1)).findAll();
    }

    // ==========================================
    // Tests para GET /api/v1/calificaciones/{id}
    // ==========================================

    @Test
    void getCalificacionById_retornaCalificacionYOK() {
        // Simular: El servicio encuentra la calificación
        when(calificacionService.findById(VALID_ID)).thenReturn(calificacionEjemplo);

        // Ejecutar el método del controlador
        ResponseEntity<Calificacion> response = calificacionController.getCalificacionById(VALID_ID);

        // Verificar la respuesta HTTP (200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VALID_ID, response.getBody().getId());

        // Verificar que el método del servicio fue llamado
        verify(calificacionService, times(1)).findById(VALID_ID);
    }

    @Test
    void getCalificacionById_retornaNotFoundSiNoExiste() {
        // Simular: El servicio lanza la RuntimeException definida cuando no encuentra el ID
        when(calificacionService.findById(VALID_ID)).thenThrow(new RuntimeException("Calificación no encontrada"));

        // Ejecutar el método del controlador
        ResponseEntity<Calificacion> response = calificacionController.getCalificacionById(VALID_ID);

        // Verificar la respuesta HTTP (404 Not Found)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verificar que el método del servicio fue llamado
        verify(calificacionService, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para POST /api/v1/calificaciones
    // ==========================================

    @Test
    void createCalificacion_retornaCalificacionYCreated() {
        // Simular: El servicio procesa y guarda la calificación, devolviendo la misma
        when(calificacionService.save(any(Calificacion.class))).thenReturn(calificacionEjemplo);

        // Ejecutar el método del controlador
        ResponseEntity<Calificacion> response = calificacionController.createCalificacion(calificacionEjemplo);

        // Verificar la respuesta HTTP (201 Created)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VALID_ID, response.getBody().getId());

        // Verificar que el método save del servicio fue llamado
        verify(calificacionService, times(1)).save(calificacionEjemplo);
    }

    @Test
    void createCalificacion_manejaErrorDeValidacionDelService() {
        // Simular: El servicio lanza una excepción (ej: validación de puntuación fallida)
        when(calificacionService.save(any(Calificacion.class))).thenThrow(new RuntimeException("id_usuario es obligatorio"));

        // Ejecutar y capturar la excepción.
        // Nota: Si el Controller no tiene un @ExceptionHandler, la excepción puede subir y ser manejada por Spring
        // con un 500 Internal Server Error, o 400 Bad Request si Spring/Hibernate lo intercepta.
        // Para este test unitario simple, verificamos que el servicio sea llamado.

        // Dado que tu controlador no tiene manejo de excepciones para POST,
        // asumimos que Spring lo manejará (típicamente 500 o 400 dependiendo de la excepción).
        // Aquí solo verificaremos que la llamada al servicio ocurra y que se lance la excepción.

        assertThrows(RuntimeException.class, () -> {
            calificacionController.createCalificacion(calificacionEjemplo);
        });

        // Verificar que el método save del servicio fue llamado
        verify(calificacionService, times(1)).save(calificacionEjemplo);
    }
}
package cl.condor.contacto_api.Controller;

import cl.condor.contacto_api.controller.ContactoController;
import cl.condor.contacto_api.model.Contacto;
import cl.condor.contacto_api.service.ContactoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactoControllerTest {

    // Controlador a testear
    @InjectMocks
    private ContactoController contactoController;

    // Dependencia simulada
    @Mock
    private ContactoService contactoService;

    private Contacto contactoEjemplo;
    private final Integer VALID_ID = 1;
    private final String NOT_FOUND_MSG = "Contacto no encontrado";
    private final String GENERIC_ERROR_MSG = "Error de base de datos";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        contactoEjemplo = new Contacto();
        contactoEjemplo.setId(VALID_ID);
        contactoEjemplo.setNombre("Tester User");
        contactoEjemplo.setCorreo("test@example.com");
        contactoEjemplo.setMensaje("Mensaje de prueba.");
        contactoEjemplo.setIdUsuario(100);
        contactoEjemplo.setFCreacion(new Date());
    }

    // ==========================================
    // Tests para GET /api/v1/contacto (findAll)
    // ==========================================

    @Test
    void findAll_retornaListaYOK() {
        // Simular: El servicio devuelve una lista con un contacto
        when(contactoService.findAll()).thenReturn(List.of(contactoEjemplo));

        ResponseEntity<List<Contacto>> response = contactoController.findAll();

        // Verificar la respuesta HTTP (200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        verify(contactoService, times(1)).findAll();
    }

    @Test
    void findAll_retornaListaVaciaYOK() {
        // Simular: El servicio devuelve una lista vacía
        when(contactoService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Contacto>> response = contactoController.findAll();

        // Nota: A diferencia de otros endpoints, si la lista está vacía, devuelve 200 OK con lista vacía.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(contactoService, times(1)).findAll();
    }

    @Test
    void findAll_manejaErrorInterno() {
        // Simular: El servicio lanza una excepción no controlada
        when(contactoService.findAll()).thenThrow(new RuntimeException(GENERIC_ERROR_MSG));

        ResponseEntity<List<Contacto>> response = contactoController.findAll();

        // Verificar la respuesta HTTP (500 Internal Server Error)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(contactoService, times(1)).findAll();
    }

    // ==========================================
    // Tests para GET /api/v1/contacto/{id} (findById)
    // ==========================================

    @Test
    void findById_existe_retornaContactoYOK() {
        // Simular: El servicio encuentra el contacto
        when(contactoService.findById(VALID_ID)).thenReturn(contactoEjemplo);

        ResponseEntity<Contacto> response = contactoController.findById(VALID_ID);

        // Verificar la respuesta HTTP (200 OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VALID_ID, response.getBody().getId());

        verify(contactoService, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_retornaNoContent() {
        // Simular: El servicio lanza la excepción específica de "no encontrado"
        when(contactoService.findById(VALID_ID)).thenThrow(new RuntimeException(NOT_FOUND_MSG));

        ResponseEntity<Contacto> response = contactoController.findById(VALID_ID);

        // Verificar la respuesta HTTP (204 No Content)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(contactoService, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_manejaErrorInterno() {
        // Simular: El servicio lanza una excepción genérica
        when(contactoService.findById(VALID_ID)).thenThrow(new RuntimeException(GENERIC_ERROR_MSG));

        ResponseEntity<Contacto> response = contactoController.findById(VALID_ID);

        // Verificar la respuesta HTTP (500 Internal Server Error)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(contactoService, times(1)).findById(VALID_ID);
    }

    // ==========================================
    // Tests para POST /api/v1/contacto (create)
    // ==========================================

    @Test
    void create_guardaContactoYRetornaCreated() {
        // Simular: El servicio guarda el contacto (podríamos simular que lo devuelve o solo la llamada)
        // El controller devuelve 201 y un body vacío, por lo que solo simulamos que el save es exitoso.
        when(contactoService.save(any(Contacto.class))).thenReturn(contactoEjemplo);

        ResponseEntity<Contacto> response = contactoController.create(contactoEjemplo);

        // Verificar la respuesta HTTP (201 Created)
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody()); // El controller usa .build() que implica body nulo

        verify(contactoService, times(1)).save(contactoEjemplo);
    }

    @Test
    void create_manejaErrorInterno() {
        // Simular: El servicio lanza una excepción durante el guardado
        when(contactoService.save(any(Contacto.class))).thenThrow(new RuntimeException(GENERIC_ERROR_MSG));

        ResponseEntity<Contacto> response = contactoController.create(contactoEjemplo);

        // Verificar la respuesta HTTP (500 Internal Server Error)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(contactoService, times(1)).save(contactoEjemplo);
    }

    // ==========================================
    // Tests para DELETE /api/v1/contacto/{id} (delete)
    // ==========================================

    @Test
    void delete_existe_retornaNoContent() {
        // Simular: El servicio ejecuta el borrado sin lanzar excepción
        doNothing().when(contactoService).deleteById(VALID_ID);

        ResponseEntity<Void> response = contactoController.delete(VALID_ID);

        // Verificar la respuesta HTTP (204 No Content)
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(contactoService, times(1)).deleteById(VALID_ID);
    }

    @Test
    void delete_noExiste_manejaErrorInterno() {
        // Corregido: Usamos doThrow().when() para simular que un método void lanza excepción
        doThrow(new RuntimeException(NOT_FOUND_MSG)).when(contactoService).deleteById(VALID_ID);

        ResponseEntity<Void> response = contactoController.delete(VALID_ID);

        // Verifica que el error es 500 según la implementación del controller
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(contactoService, times(1)).deleteById(VALID_ID);
    }
}
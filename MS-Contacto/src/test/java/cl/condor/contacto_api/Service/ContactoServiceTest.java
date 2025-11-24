package cl.condor.contacto_api.Service;

import cl.condor.contacto_api.model.Contacto;
import cl.condor.contacto_api.repository.ContactoRepository;
import cl.condor.contacto_api.service.ContactoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactoServiceTest {

    // Servicio a testear
    @InjectMocks
    private ContactoService contactoService;

    // Repositorio simulado (única dependencia)
    @Mock
    private ContactoRepository contactoRepository;

    private Contacto contactoEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar objeto Contacto de prueba
        contactoEjemplo = new Contacto();
        contactoEjemplo.setId(VALID_ID);
        contactoEjemplo.setNombre("Claudio Bravo");
        contactoEjemplo.setCorreo("claudio@condor.cl");
        contactoEjemplo.setMensaje("Reporte de error en la ruta 10.");
        contactoEjemplo.setIdUsuario(99); // ID de usuario externo
        contactoEjemplo.setFCreacion(new Date());
    }

    // --- Tests para findAll() ---

    @Test
    void findAll_retornaListaDeContactos() {
        // Simular: el repositorio devuelve una lista
        when(contactoRepository.findAll()).thenReturn(List.of(contactoEjemplo));

        List<Contacto> resultado = contactoService.findAll();

        // Verificar
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        // Verificar interacción
        verify(contactoRepository, times(1)).findAll();
    }

    // --- Tests para findById(id) ---

    @Test
    void findById_existe_retornaContacto() {
        // Simular: el repositorio encuentra el contacto
        when(contactoRepository.findById(VALID_ID)).thenReturn(Optional.of(contactoEjemplo));

        Contacto resultado = contactoService.findById(VALID_ID);

        // Verificar
        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());

        // Verificar interacción
        verify(contactoRepository, times(1)).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        // Simular: el repositorio devuelve Optional vacío
        when(contactoRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        // Verificar que se lance la excepción correcta
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            contactoService.findById(VALID_ID);
        });

        assertEquals("Contacto no encontrado", excepcion.getMessage());

        // Verificar interacción
        verify(contactoRepository, times(1)).findById(VALID_ID);
    }

    // --- Tests para save(contacto) ---

    @Test
    void save_guardaYRetornaContacto() {
        // Simular: el repositorio devuelve el objeto guardado (con ID)
        when(contactoRepository.save(contactoEjemplo)).thenReturn(contactoEjemplo);

        Contacto resultado = contactoService.save(contactoEjemplo);

        // Verificar
        assertNotNull(resultado);
        assertEquals(contactoEjemplo.getNombre(), resultado.getNombre());

        // Verificar interacción
        verify(contactoRepository, times(1)).save(contactoEjemplo);
    }

    // --- Tests para deleteById(id) ---

    @Test
    void deleteById_existe_eliminaContacto() {
        // Simular: el contacto existe y el método delete no devuelve nada (void)
        when(contactoRepository.findById(VALID_ID)).thenReturn(Optional.of(contactoEjemplo));
        doNothing().when(contactoRepository).deleteById(VALID_ID);

        // Ejecutar
        assertDoesNotThrow(() -> contactoService.deleteById(VALID_ID));

        // Verificar interacciones: Buscar y luego Eliminar
        verify(contactoRepository, times(1)).findById(VALID_ID);
        verify(contactoRepository, times(1)).deleteById(VALID_ID);
    }

    @Test
    void deleteById_noExiste_lanzaExcepcion() {
        // Simular: el contacto no existe
        when(contactoRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            contactoService.deleteById(VALID_ID);
        });

        assertEquals("Contacto no encontrado", excepcion.getMessage());

        // Verificar que NO se intentó eliminar
        verify(contactoRepository, times(1)).findById(VALID_ID);
        verify(contactoRepository, never()).deleteById(anyInt());
    }
}

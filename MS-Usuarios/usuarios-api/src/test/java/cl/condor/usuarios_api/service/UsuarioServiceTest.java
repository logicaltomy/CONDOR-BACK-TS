package cl.condor.usuarios_api.service;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.PreguntasResponseDTO;
import cl.condor.usuarios_api.dto.RecuperacionDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.repository.EstadoRepository;
import cl.condor.usuarios_api.repository.RegionRepository;
import cl.condor.usuarios_api.repository.RolRepository;
import cl.condor.usuarios_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
// IMPORTANTE: Clase de utilidad para inyección por reflexión
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    // Se declara sin @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        // 1. Inicializa todos los @Mock
        MockitoAnnotations.openMocks(this);

        // 2. Inicializa el servicio usando su constructor (para el PasswordEncoder final)
        try {
            this.usuarioService = new UsuarioService(passwordEncoder);
        } catch (Exception e) {
            throw new RuntimeException("Error al instanciar UsuarioService: " + e.getMessage());
        }

        // 3. INYECCIÓN FORZADA DE LOS CAMPOS @Autowired FALTANTES (REPOSITORIOS)
        // Se usa ReflectionTestUtils para inyectar los Mocks a los campos privados.
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);
        ReflectionTestUtils.setField(usuarioService, "estadoRepository", estadoRepository);
        ReflectionTestUtils.setField(usuarioService, "regionRepository", regionRepository);
        ReflectionTestUtils.setField(usuarioService, "rolRepository", rolRepository);

        // La inyección del 'encoder' ya fue manejada por el constructor en el paso 2.

        usuarioEjemplo = Usuario.builder()
                .id(VALID_ID)
                .nombre("Usuario Test")
                .correo("test@test.com")
                .contrasena("password123")
                .rutasRecorridas(0)
                .kmRecorridos(BigDecimal.ZERO)
                .preguntaSeguridad1("¿Cuál es tu color favorito?")
                .respuestaSeguridad1("azul")
                .preguntaSeguridad2("¿Cuál es tu comida favorita?")
                .respuestaSeguridad2("pizza")
                .idRol(1)
                .idRegion(1)
                .idEstado(1)
                .build();
    }

    // ... (El resto de los métodos @Test se mantienen igual)
    @Test
    void findAll_retornaListaUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioEjemplo));

        assertFalse(usuarioService.findAll().isEmpty());
        verify(usuarioRepository).findAll();
    }

    @Test
    void findById_existe_retornaUsuario() {
        when(usuarioRepository.findById(VALID_ID)).thenReturn(Optional.of(usuarioEjemplo));

        Usuario resultado = usuarioService.findById(VALID_ID);

        assertNotNull(resultado);
        assertEquals(VALID_ID, resultado.getId());
        verify(usuarioRepository).findById(VALID_ID);
    }

    @Test
    void findById_noExiste_lanzaExcepcion() {
        when(usuarioRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.findById(VALID_ID));
        verify(usuarioRepository).findById(VALID_ID);
    }

    @Test
    void save_guardaUsuarioCorrectamente() {
        when(regionRepository.existsById(anyInt())).thenReturn(true);
        when(rolRepository.existsById(anyInt())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        Usuario resultado = usuarioService.save(usuarioEjemplo);

        assertNotNull(resultado);
        verify(passwordEncoder).encode(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void updateNombre_actualizaCorrectamente() {
        when(usuarioRepository.findById(VALID_ID)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        Usuario resultado = usuarioService.updateNombre(VALID_ID, "Nuevo Nombre");

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void updateCorreo_actualizaCorrectamente() {
        when(usuarioRepository.findById(VALID_ID)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        Usuario resultado = usuarioService.updateCorreo(VALID_ID, "nuevo@test.com");

        assertNotNull(resultado);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void login_exitoso() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("test@test.com");
        loginDTO.setPassword("password123");

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> usuarioService.login(loginDTO));
        verify(usuarioRepository).findByCorreo(loginDTO.getCorreo());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void login_credencialesInvalidas_lanzaExcepcion() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setCorreo("test@test.com");
        loginDTO.setPassword("wrongpassword");

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> usuarioService.login(loginDTO));
        verify(usuarioRepository).findByCorreo(loginDTO.getCorreo());
        verify(passwordEncoder).matches(anyString(), anyString());
    }

    @Test
    void obtenerPreguntasSeguridad_retornaPreguntasDTO() {
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioEjemplo));

        PreguntasResponseDTO resultado = usuarioService.obtenerPreguntasSeguridad("test@test.com");

        assertNotNull(resultado);
        assertEquals(usuarioEjemplo.getPreguntaSeguridad1(), resultado.getPregunta1());
        assertEquals(usuarioEjemplo.getPreguntaSeguridad2(), resultado.getPregunta2());
        verify(usuarioRepository).findByCorreo(anyString());
    }

    @Test
    void recuperarContrasena_exitoso() {
        RecuperacionDTO dto = new RecuperacionDTO();
        dto.setCorreo("test@test.com");
        dto.setRespuestaSeguridad1("azul");
        dto.setRespuestaSeguridad2("pizza");
        dto.setNuevaPassword("newpassword123");

        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPassword");

        assertDoesNotThrow(() -> usuarioService.recuperarContrasena(dto));
        verify(usuarioRepository).findByCorreo(dto.getCorreo());
        verify(passwordEncoder).encode(dto.getNuevaPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
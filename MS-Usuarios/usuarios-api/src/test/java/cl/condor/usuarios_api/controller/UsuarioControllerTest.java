package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.RecuperacionDTO;
import cl.condor.usuarios_api.dto.UsuarioDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.service.UsuarioService;
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

class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private UsuarioDTO usuarioDTOEjemplo;
    private final Integer VALID_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(VALID_ID);
        usuarioEjemplo.setNombre("Usuario Test");
        usuarioEjemplo.setCorreo("test@test.com");
        usuarioEjemplo.setContrasena("password123");
        usuarioEjemplo.setIdRol(1);
        usuarioEjemplo.setIdRegion(1);

        usuarioDTOEjemplo = UsuarioDTO.builder()
                .id(VALID_ID)
                .nombre("Usuario Test")
                .correo("test@test.com")
                .idRol(1)
                .idRegion(1)
                .build();
    }

    @Test
    void getAll_retornaListaYOK() {
        when(usuarioService.findAll()).thenReturn(Arrays.asList(usuarioEjemplo));

        ResponseEntity<List<Usuario>> response = usuarioController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        verify(usuarioService, times(1)).findAll();
    }

    @Test
    void getAll_retornaNoContentSiVacio() {
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Usuario>> response = usuarioController.getAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService, times(1)).findAll();
    }

    @Test
    void getById_retornaUsuarioYOK() {
        when(usuarioService.findById(VALID_ID)).thenReturn(usuarioEjemplo);
        when(usuarioService.mapToDTO(usuarioEjemplo)).thenReturn(usuarioDTOEjemplo);

        ResponseEntity<UsuarioDTO> response = usuarioController.getById(VALID_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VALID_ID, response.getBody().getId());
        verify(usuarioService, times(1)).findById(VALID_ID);
    }

    @Test
    void getById_retornaNotFoundSiNoExiste() {
        when(usuarioService.findById(VALID_ID)).thenThrow(new RuntimeException("Usuario no encontrado"));

        ResponseEntity<UsuarioDTO> response = usuarioController.getById(VALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(usuarioService, times(1)).findById(VALID_ID);
    }

    @Test
    void create_retornaUsuarioYCreated() {
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        ResponseEntity<?> response = usuarioController.create(usuarioEjemplo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(usuarioService, times(1)).save(any(Usuario.class));
    }

    @Test
    void create_retornaBadRequestSiError() {
        when(usuarioService.save(any(Usuario.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = usuarioController.create(usuarioEjemplo);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(usuarioService, times(1)).save(any(Usuario.class));
    }

    @Test
    void updateNombre_retornaUsuarioYOK() {
        when(usuarioService.updateNombre(VALID_ID, "Nuevo Nombre")).thenReturn(usuarioEjemplo);

        ResponseEntity<Usuario> response = usuarioController.updateNombre(VALID_ID, "Nuevo Nombre");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(usuarioService, times(1)).updateNombre(VALID_ID, "Nuevo Nombre");
    }

    @Test
    void login_retornaOK() {
        LoginDTO loginDTO = new LoginDTO();
        doNothing().when(usuarioService).login(any(LoginDTO.class));

        ResponseEntity<Void> response = usuarioController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService, times(1)).login(any(LoginDTO.class));
    }

    @Test
    void login_retornaUnauthorized() {
        LoginDTO loginDTO = new LoginDTO();
        doThrow(new RuntimeException("Credenciales invalidas"))
                .when(usuarioService).login(any(LoginDTO.class));

        ResponseEntity<Void> response = usuarioController.login(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usuarioService, times(1)).login(any(LoginDTO.class));
    }

    @Test
    void recuperarContrasena_retornaOK() {
        RecuperacionDTO recuperacionDTO = new RecuperacionDTO();
        doNothing().when(usuarioService).recuperarContrasena(any(RecuperacionDTO.class));

        ResponseEntity<?> response = usuarioController.recuperarContrasena(recuperacionDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService, times(1)).recuperarContrasena(any(RecuperacionDTO.class));
    }
}
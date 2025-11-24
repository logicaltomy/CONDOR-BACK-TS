package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.PreguntasResponseDTO; // IMPORTANTE: DTO Nuevo
import cl.condor.usuarios_api.dto.RecuperacionDTO;      // IMPORTANTE: DTO Nuevo
import cl.condor.usuarios_api.dto.UsuarioDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Usuarios",
        description = """
            Controlador principal del microservicio de Usuarios.
            Gestiona la creación, consulta, listado y seguridad (login/recuperación).
            """
)
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ==================================================================
    //  ENDPOINTS EXISTENTES (GET, POST, PATCH) - SE MANTIENEN IGUAL
    // ==================================================================

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> lista = usuarioService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Integer id) {
        try {
            Usuario usuario = usuarioService.findById(id);
            UsuarioDTO usuarioDTO = usuarioService.mapToDTO(usuario);
            return ResponseEntity.ok(usuarioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar usuario por Correo")
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> getByCorreo(@RequestParam String correo) {
        try {
            Usuario usuario = usuarioService.findByCorreo(correo); 
            if (usuario == null) return ResponseEntity.notFound().build();
            
            UsuarioDTO usuarioDTO = usuarioService.mapToDTO(usuario);
            return ResponseEntity.ok(usuarioDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear un nuevo usuario (Registro)",
            description = """
                Registra un nuevo usuario. 
                REQUIERE: nombre, correo, contraseña, region, rol 
                y AHORA TAMBIÉN las 2 preguntas y respuestas de seguridad.
                """
    )
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            Usuario saved = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            // Capturamos errores de validación (ej: faltan preguntas de seguridad)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar solo el nombre de un usuario")
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<Usuario> updateNombre(@PathVariable Integer id, @RequestParam String nombre) {
        try {
            Usuario actualizado = usuarioService.updateNombre(id, nombre);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar solo el correo de un usuario")
    @PatchMapping("/{id}/correo")
    public ResponseEntity<Usuario> updateCorreo(@PathVariable Integer id, @RequestParam String correo) {
        try {
            Usuario actualizado = usuarioService.updateCorreo(id, correo);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar solo la región de un usuario")
    @PatchMapping("/{id}/region")
    public ResponseEntity<Usuario> updateRegion(@PathVariable Integer id, @RequestParam Integer idRegion) {
        try {
            Usuario actualizado = usuarioService.updateRegion(id, idRegion);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            if(e.getMessage().equals("Region no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Login de usuario")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginDTO loginDTO) {
        try {
            usuarioService.login(loginDTO);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e) {
            if(e.getMessage().equals("Credenciales invalidas")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar foto de perfil (Base64)")
    @PatchMapping("/{id}/foto")
    public ResponseEntity<Usuario> updateFotoPerfil(@PathVariable Integer id, @RequestBody String fotoBase64) {
        try {
            Usuario actualizado = usuarioService.updateFoto(id, fotoBase64);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    // ==================================================================
    //  NUEVOS ENDPOINTS PARA RECUPERACIÓN DE CONTRASEÑA
    // ==================================================================

    @Operation(
            summary = "Obtener preguntas de seguridad",
            description = "Dado un correo, devuelve las 2 preguntas que el usuario configuró para poder mostrarlas en el Frontend."
    )
    @GetMapping("/preguntas")
    public ResponseEntity<?> obtenerPreguntas(@RequestParam String correo) {
        try {
            PreguntasResponseDTO response = usuarioService.obtenerPreguntasSeguridad(correo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retorna 404 si el usuario no existe o 400 si no tiene preguntas configuradas
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Recuperar Contraseña",
            description = """
                Recibe correo, las 2 respuestas de seguridad y la nueva contraseña.
                Si las respuestas coinciden (case-insensitive), actualiza la password.
                """
    )
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarContrasena(@RequestBody RecuperacionDTO recuperacionDTO) {
        try {
            usuarioService.recuperarContrasena(recuperacionDTO);
            // Devolvemos un mensaje simple o un JSON 200 OK
            return ResponseEntity.ok("Contraseña restablecida con éxito.");
        } catch (RuntimeException e) {
            // Si las respuestas son incorrectas, devolvemos 400 Bad Request con el mensaje del error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Rol;
import cl.condor.usuarios_api.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Tag(
        name = "Roles",
        description = """
            Controlador del catálogo de Roles de usuario.
            Permite gestionar los distintos roles disponibles en el sistema, 
            tales como Administrador, Moderador o Usuario. 
            Estos valores son utilizados por el microservicio de Usuarios 
            para definir los permisos y niveles de acceso.
            """
)
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(
            summary = "Listar todos los roles",
            description = "Devuelve la lista completa de roles existentes. Si no existen registros, devuelve HTTP 204 No Content."
    )
    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> lista = rolService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar un rol por ID",
            description = "Devuelve un rol específico según su identificador. Si no existe, responde con HTTP 404 Not Found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rolService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear un nuevo rol",
            description = "Permite registrar un nuevo rol dentro del catálogo, por ejemplo: 'Administrador', 'Moderador' o 'Usuario'."
    )
    @PostMapping
    public ResponseEntity<Rol> create(@RequestBody Rol rol) {
        Rol saved = rolService.save(rol);
        return ResponseEntity.status(201).body(saved);
    }
}

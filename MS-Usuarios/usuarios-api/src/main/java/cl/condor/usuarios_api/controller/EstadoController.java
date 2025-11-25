package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Estado;
import cl.condor.usuarios_api.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Estados",
        description = """
            Controlador del catálogo de Estados de usuario.
            Contiene endpoints para listar, buscar y crear nuevos estados.
            Estos valores son utilizados por el microservicio de Usuarios 
            para representar el estado actual de cada usuario.
            """
)
@RestController
@RequestMapping("/api/v1/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @Operation(
            summary = "Listar todos los estados",
            description = "Obtiene la lista completa de estados disponibles. Si no hay registros, devuelve HTTP 204 No Content."
    )
    @GetMapping
    public ResponseEntity<List<Estado>> getAll() {
        List<Estado> lista = estadoService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar un estado por ID",
            description = "Devuelve un estado específico según su identificador. Si no existe, responde con HTTP 404 Not Found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Estado> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(estadoService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear un nuevo estado",
            description = "Permite registrar un nuevo estado dentro del catálogo (por ejemplo: Activo, Inactivo, Suspendido, etc.)."
    )
    @PostMapping
    public ResponseEntity<Estado> create(@RequestBody Estado estado) {
        Estado saved = estadoService.save(estado);
        return ResponseEntity.status(201).body(saved);
    }
}

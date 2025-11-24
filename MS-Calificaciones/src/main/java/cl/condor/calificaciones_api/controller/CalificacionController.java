package cl.condor.calificaciones_api.controller;

import cl.condor.calificaciones_api.model.Calificacion;
import cl.condor.calificaciones_api.service.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Calificaciones",
        description = """
            Controlador del microservicio de Calificaciones.
            Gestiona la creación, consulta por ID y listado de calificaciones.
            """
)
@RestController
@RequestMapping("/api/v1/calificaciones")
public class    CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @Operation(
            summary = "Listar todas las calificaciones",
            description = """
                Retorna la lista completa de calificaciones registradas.
                Si no hay registros, devuelve HTTP 204 No Content.
                """
    )
    @GetMapping
    public ResponseEntity<List<Calificacion>> getAllCalificaciones() {
        List<Calificacion> lista = calificacionService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar calificación por ID",
            description = """
                Devuelve una calificación específica según su identificador.
                Si no existe, responde con HTTP 404 Not Found.
                """
    )
    @GetMapping("/{id}")
    public ResponseEntity<Calificacion> getCalificacionById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(calificacionService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear una nueva calificación",
            description = """
                Registra una nueva calificación en el sistema.
                """
    )
    @PostMapping
    public ResponseEntity<Calificacion> createCalificacion(@RequestBody Calificacion calificacion) {
        Calificacion saved = calificacionService.save(calificacion);
        return ResponseEntity.status(201).body(saved);
    }
}

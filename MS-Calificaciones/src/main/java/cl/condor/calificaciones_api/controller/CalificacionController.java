package cl.condor.calificaciones_api.controller;

import cl.condor.calificaciones_api.model.Calificacion;
import cl.condor.calificaciones_api.service.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Calificaciones",
        description = """
            Controlador del microservicio de Calificaciones.
            Gestiona la creación, consulta por ID y listado de calificaciones.
            """
)
@CrossOrigin(origins = "http://localhost:5173")
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
    public ResponseEntity<Object> createCalificacion(@RequestBody Calificacion calificacion) {
        try {
            Calificacion saved = calificacionService.save(calificacion);

            // Obtener promedio actualizado
            Map<String, Object> promedio = calificacionService.getPromedioPorRuta(saved.getIdRuta());

            Map<String, Object> out = new java.util.HashMap<>();
            out.put("calificacion", saved);
            out.put("promedio", promedio.get("promedio"));
            out.put("conteo", promedio.get("conteo"));

            return ResponseEntity.status(201).body(out);
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            Map<String, String> err = Map.of("error", e.getMessage() == null ? "Error interno" : e.getMessage());
            if (msg.contains("id_usuario") || msg.contains("id_ruta") || msg.contains("puntuación")
                    || msg.contains("puntuacion") || msg.contains("obligatorio")) {
                return ResponseEntity.badRequest().body(err);
            }
            if (msg.contains("usuario no encontrado") || msg.contains("ruta no encontrada")) {
                return ResponseEntity.status(404).body(err);
            }
            if (msg.contains("ya calificó") || msg.contains("ya califico") || msg.contains("el usuario ya calificó")) {
                return ResponseEntity.status(409).body(err);
            }
            return ResponseEntity.status(500).body(err);
        }
    }

    @Operation(
            summary = "Comprobar si un usuario ya calificó una ruta",
            description = "Devuelve { 'existe': true/false } según si el usuario ya registró una calificación para la ruta."
    )
    @GetMapping("/existe")
    public ResponseEntity<Map<String, Boolean>> existeCalificacion(@RequestParam Integer usuario, @RequestParam Integer ruta) {
        boolean existe = calificacionService.existsByUsuarioRuta(usuario, ruta);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @Operation(
            summary = "Listar calificaciones por ruta",
            description = "Retorna la lista de calificaciones asociadas a una ruta específica."
    )
    @GetMapping("/ruta/{idRuta}")
    public ResponseEntity<List<Calificacion>> getCalificacionesPorRuta(@PathVariable Integer idRuta) {
        List<Calificacion> lista = calificacionService.findByRuta(idRuta);
        if (lista == null || lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Listar calificaciones por usuario",
            description = "Retorna la lista de calificaciones realizadas por un usuario específico."
    )
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Calificacion>> getCalificacionesPorUsuario(@PathVariable Integer idUsuario) {
        List<Calificacion> lista = calificacionService.findByUsuario(idUsuario);
        if (lista == null || lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Promedio y conteo de calificaciones por ruta",
            description = "Devuelve el promedio de puntuación y la cantidad de calificaciones para una ruta dada."
    )
    @GetMapping("/ruta/{idRuta}/promedio")
    public ResponseEntity<java.util.Map<String, Object>> getPromedioPorRuta(@PathVariable Integer idRuta) {
        java.util.Map<String, Object> resultado = calificacionService.getPromedioPorRuta(idRuta);
        if (resultado == null || ((Integer) resultado.getOrDefault("conteo", 0)) == 0) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(resultado);
    }
}

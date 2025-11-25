package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Region;
import cl.condor.usuarios_api.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Regiones",
        description = """
            Controlador del catálogo de Regiones de Chile.
            Este recurso contiene todas las regiones disponibles en el sistema 
            y es utilizado por el microservicio de Usuarios para asociar 
            una ubicación geográfica a cada registro de usuario.
            """
)
@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @Operation(
            summary = "Listar todas las regiones",
            description = "Devuelve la lista completa de regiones disponibles. Si no existen registros, devuelve HTTP 204 No Content."
    )
    @GetMapping
    public ResponseEntity<List<Region>> getAll() {
        List<Region> lista = regionService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar región por ID",
            description = "Devuelve una región específica según su identificador. Si no existe, responde con HTTP 404 Not Found."
    )
    @GetMapping("/{id}")
    public ResponseEntity<Region> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(regionService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Crear una nueva región",
            description = "Permite registrar una nueva región dentro del catálogo de regiones disponibles."
    )
    @PostMapping
    public ResponseEntity<Region> create(@RequestBody Region region) {
        Region saved = regionService.save(region);
        return ResponseEntity.status(201).body(saved);
    }
}

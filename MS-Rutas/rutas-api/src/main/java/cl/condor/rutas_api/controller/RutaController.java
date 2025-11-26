package cl.condor.rutas_api.controller;

import cl.condor.rutas_api.model.Dificultad;
import cl.condor.rutas_api.model.Foto;
import cl.condor.rutas_api.model.Ruta;
import cl.condor.rutas_api.model.Tipo;
import cl.condor.rutas_api.service.RutaService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(
        name = "Gestión de Rutas y Fotos",
        description = "Controlador principal del microservicio de Rutas. Permite crear, consultar y modificar datos de rutas y sus fotos asociadas."
)
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {
    @Autowired
    private RutaService rutaService;

    // --- ENDPOINTS: RUTAS (CRUD BÁSICO) --------------------------------------

    @Operation(
            summary = "Obtener todas las rutas",
            description = "Retorna una lista de todas las rutas disponibles. Si no hay registros, devuelve HTTP 204 No Content.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de rutas obtenida exitosamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No hay rutas registradas (No Content)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible (e.g., API de Usuarios o Estados).")
            }
    )
    @GetMapping
    public ResponseEntity<List<cl.condor.rutas_api.dto.RutaResponse>> findAll() {
        try {
            List<cl.condor.rutas_api.dto.RutaResponse> rutas = rutaService.findAllResponses();

            if (rutas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(rutas);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Obtener ruta por ID",
            description = "Busca y retorna una ruta específica utilizando su identificador único.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ruta encontrada."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ruta no encontrada."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<cl.condor.rutas_api.dto.RutaResponse> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rutaService.findResponseById(id));
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Crear una nueva ruta",
            description = "Registra una nueva ruta. Requiere IDs válidos para estado, región, tipo y dificultad (gestionados por servicios externos).",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ruta creada y retornada exitosamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida (e.g., FKs no válidas, datos faltantes)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible.")
            }
    )
    @PostMapping
    public ResponseEntity<?> createRuta(@RequestBody cl.condor.rutas_api.dto.RutaRequest req) {
        try {
            // map request to entity, applying safe defaults for non-nullable DB columns
            Ruta ruta = new Ruta();
            ruta.setNombre(req.getNombre());
            ruta.setDescripcion(req.getDescripcion());
            ruta.setDistancia(req.getDistancia() != null ? req.getDistancia() : java.math.BigDecimal.ZERO);
            // f_public/f_baneo stored as LocalDateTime in entity; for create we keep null or set now if requested true
            if (Boolean.TRUE.equals(req.getF_public())) {
                ruta.setF_public(java.time.LocalDateTime.now());
            } else {
                ruta.setF_public(null);
            }
            ruta.setF_baneo(null);
            ruta.setGeometriaPolyline(req.getGeometria_polyline() != null ? req.getGeometria_polyline() : "");
            ruta.setTiempoSegundos(req.getTiempo_segundos() != null ? req.getTiempo_segundos() : 0);
            ruta.setProm_calificacion(req.getProm_calificacion() != null ? req.getProm_calificacion() : java.math.BigDecimal.ZERO);
            ruta.setId_estado(req.getId_estado());
            ruta.setId_region(req.getId_region());
            ruta.setId_tipo(req.getId_tipo());
            ruta.setId_dificultad(req.getId_dificultad());

            Ruta savedRuta = rutaService.save(ruta);

            // if fotos provided as URLs, save them as Foto records with imagen=null and nombre=url
            if (req.getFoto() != null && !req.getFoto().isEmpty()) {
                for (String url : req.getFoto()) {
                    Foto f = new Foto();
                    f.setNombre(url);
                    f.setImagen(null);
                    f.setIdRuta(savedRuta.getId_ruta());
                    rutaService.save(f);
                }
            }

            return ResponseEntity.ok(rutaService.findResponseById(savedRuta.getId_ruta()));
        } catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Servicio externo no disponible."));
        } catch (RuntimeException e) {
            // return the runtime exception message in the response body for client-friendly error display
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // --- ENDPOINTS: FOTOS ----------------------------------------------------

    @Operation(
            summary = "Subir una nueva foto a una ruta",
            description = "Registra una nueva foto asociada a una ruta existente (id_ruta). Requiere datos binarios (byte[]) para el campo 'imagen'.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Foto registrada exitosamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida (e.g., id_ruta no existe, formato incorrecto)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible.")
            }
    )
    @PostMapping("/foto")
    public ResponseEntity<Foto> createFoto(@RequestBody Foto foto) {
        try {
            Foto savedFoto = rutaService.save(foto);
            return ResponseEntity.ok(savedFoto);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Obtener fotos por ID de Ruta",
            description = "Devuelve una lista de todas las fotos asociadas a un ID de ruta específico.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de fotos obtenida (puede ser vacía)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Error interno o ID de ruta no válido.")
            }
    )
    @GetMapping("/foto/{id}")
    public ResponseEntity<List<Foto>> getFotosByIdRutas(@PathVariable Integer id) {
        try{
            List<Foto> fotos = rutaService.findByIdRuta(id);
            return ResponseEntity.ok(fotos);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Borrar foto por ID",
            description = "Elimina permanentemente una foto de la base de datos utilizando su identificador único (id_foto).",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Foto eliminada exitosamente (No Content)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Foto no encontrada."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible.")
            }
    )
    @DeleteMapping("/{id}/borrarFoto")
    public ResponseEntity<Void> deleteFoto(@PathVariable Integer id) {
        try{
            rutaService.deleteFoto(id);
            return ResponseEntity.noContent().build();
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Actualizar nombre de la ruta",
            description = "Modifica el nombre de una ruta específica.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nombre actualizado."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ruta no encontrada."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio externo no disponible.")
            }
    )
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<Ruta> updateNombre(@PathVariable Integer id, @RequestBody String nombre) {
        try {
            Ruta ruta = rutaService.updateNombre(id, nombre);
            return ResponseEntity.ok(ruta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Actualizar descripción de la ruta",
            description = "Modifica el campo 'descripcion' de una ruta específica."
    )
    @PatchMapping("/{id}/descripcion")
    public  ResponseEntity<Ruta> updateDescripcion(@PathVariable Integer id, @RequestBody String descripcion) {
        try{
            Ruta ruta = rutaService.updateDescripcion(id, descripcion);
            return ResponseEntity.ok(ruta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Actualizar Polyline (Geometría de la Ruta)",
            description = "Actualiza la cadena de texto Polyline que define la geografía de la ruta."
    )
    @PatchMapping("/{id}/polyline")
    public ResponseEntity<Ruta> updatePolyline(@PathVariable Integer id, @RequestBody String polyline) {
        try{
            Ruta ruta = rutaService.updatePolyLine(id, polyline);
            return ResponseEntity.ok(ruta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Actualizar ruta (reemplazo completo)",
            description = "Actualiza todos los campos editables de una ruta mediante PUT."
    )
    @PutMapping("/{id}")
    public ResponseEntity<cl.condor.rutas_api.dto.RutaResponse> updateRuta(@PathVariable Integer id, @RequestBody cl.condor.rutas_api.dto.RutaRequest req) {
        try {
            rutaService.updateRutaFromRequest(id, req);
            return ResponseEntity.ok(rutaService.findResponseById(id));
        } catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Banear una ruta",
            description = "Marca la ruta como baneada, estableciendo la fecha de baneo (f_baneo)."
    )
    @PatchMapping("/{id}/banear")
    public ResponseEntity<Ruta> updateBanear(@PathVariable Integer id) {
        try {
            Ruta ruta = rutaService.banearRuta(id);
            return ResponseEntity.ok(ruta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Desbanear una ruta",
            description = "Remueve la marca de baneo de una ruta, eliminando la fecha de baneo (f_baneo)."
    )
    @PatchMapping("/{id}/desbanear")
    public ResponseEntity<Ruta> updateDesbanear(@PathVariable Integer id) {
        try {
            Ruta ruta = rutaService.desbanearRuta(id);
            return ResponseEntity.ok(ruta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Eliminar una ruta",
            description = "Elimina permanentemente una ruta y sus fotos asociadas."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRuta(@PathVariable Integer id) {
        try {
            rutaService.deleteRuta(id);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.web.reactive.function.client.WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Funcion que te trae el tipo de la ruta"
    )
    @GetMapping("/tipo/{id}")
    public ResponseEntity<Tipo> tipoRuta(@PathVariable Integer id) {
        try {
            Tipo tipo = rutaService.findTipoById(id);
            return ResponseEntity.ok(tipo);
        }catch (RuntimeException e) {
            if (e.getMessage().equals("Tipo no encontrada")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<Tipo>> getAllTipo() {
        try {
            List<Tipo> tipos = rutaService.findAllTipos();
            return ResponseEntity.ok(tipos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    @Operation(
            summary = "Funcion que trae que dificultad es la ruta"
    )
    @GetMapping("/dificultad/{id}")
    public ResponseEntity<Dificultad> dificultadRuta(@PathVariable Integer id) {
        try{
            Dificultad dificultad = rutaService.findDificultadById(id);
            return ResponseEntity.ok(dificultad);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Dificultad no encontrada")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dificultad")
    public ResponseEntity<List<Dificultad>> getAllDificultad() {
        try {
            List<Dificultad> dificultades = rutaService.findAllDificultades();
            return ResponseEntity.ok(dificultades);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

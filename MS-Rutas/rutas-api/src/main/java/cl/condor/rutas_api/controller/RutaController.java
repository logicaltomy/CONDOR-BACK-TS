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
    public ResponseEntity<List<Ruta>> findAll() {
        try {
            List<Ruta> rutas = rutaService.findAll();

            if (rutas.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(rutas);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            // Asume que cualquier RuntimeException no WebClient es un error interno o Not Found
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
    public ResponseEntity<Ruta> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rutaService.findById(id));
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
    public ResponseEntity<Ruta> createRuta(@RequestBody Ruta ruta) {
        try {
            Ruta savedRuta = rutaService.save(ruta);
            return ResponseEntity.ok(savedRuta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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

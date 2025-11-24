package cl.condor.logros_api.controller;

import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Logro;
import cl.condor.logros_api.model.Tipo_condicion;
import cl.condor.logros_api.model.Trofeo;
import cl.condor.logros_api.service.CondicionService;
import cl.condor.logros_api.service.LogroService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@Tag(
        name = "Logros y Condiciones",
        description = "Gestión de la lógica de logros, trofeos, condiciones y tipos de condición."
)
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/logros")
public class LogroController {

    @Autowired
    private LogroService logroService;

    @Autowired
    private CondicionService condicionService;

    // --- ENDPOINTS: LOGROS (CRUD BÁSICO) --------------------------------------

    @Operation(
            summary = "Listar todos los logros",
            description = "Retorna una lista de todos los logros definidos en el sistema. Si no hay logros, devuelve HTTP 204 No Content."
    )
    @GetMapping
    public ResponseEntity<List<Logro>> getLogros(){
        List<Logro> logros = logroService.findAll();

        if(logros.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logros);
    }

    @Operation(
            summary = "Crear un nuevo logro",
            description = "Registra un nuevo logro en la base de datos. Se espera que la condición asociada (id_condicion) ya exista.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logro creado exitosamente"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida (e.g., falta una FK o datos obligatorios).")
            }
    )
    @PostMapping
    public ResponseEntity<Logro> createLogro(@RequestBody Logro logro){
        try {
            Logro resultado = logroService.save(logro);
            return ResponseEntity.ok(resultado);
        }catch(WebClientRequestException e){
            // Falla de conexión con servicio externo (503)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch(RuntimeException e){
            if (e.getMessage().equals("Estado no encontrado")){
                // Falla de dato que debería existir en otra API (404)
                return ResponseEntity.notFound().build();
            }
            // y se mapea a 400 Bad Request.
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Obtener un logro por ID",
            description = "Busca y devuelve un logro específico utilizando su identificador único.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logro encontrado"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Logro no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Logro> getLogro(@PathVariable int id){
        Logro resultado = logroService.findById(id);
        if(resultado == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resultado);
    }

    // --- ENDPOINTS: TROFEOS (LÓGICA DE NEGOCIO) --------------------------------

    @Operation(
            summary = "Intentar ganar un logro (Transaccional)",
            description = """
            Procesa el intento de un usuario de ganar un logro. El servicio verifica si el usuario (idUsuario)
            cumple con las restricciones de todas las condiciones asociadas a los logros que aún no ha obtenido.
            Si un logro se ha cumplido, se registra un Trofeo para el usuario.
            """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Trofeo ganado y registrado exitosamente."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "El usuario no ha cumplido las condiciones necesarias ('No se ha ganado el logro')."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Error al conectar con microservicios externos (Usuarios, Rutas, etc.)."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor no manejado.")
            }
    )
    @PostMapping("/ganarLogro/{idUsuario}")
    public ResponseEntity<Trofeo> ganarLogro(@PathVariable int idUsuario){
        try{
            Trofeo resultado = logroService.ganarLogro(idUsuario);
            return ResponseEntity.ok(resultado);
        }catch(WebClientRequestException e){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch(RuntimeException e){
            if(e.getMessage().equals("No se ha ganado el logro")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINTS: CONDICIONES (CONSULTA Y CREACIÓN) --------------------------

    @Operation(
            summary = "Listar todas las condiciones",
            description = "Retorna la lista completa de condiciones que deben cumplirse para obtener los logros."
    )
    @GetMapping("/condiciones")
    public ResponseEntity<List<Condicion>> getCondiciones(){
        try{
            List<Condicion> condiciones = condicionService.findAll();
            if(condiciones.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(condiciones);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Obtener una condición por ID",
            description = "Busca y devuelve una condición específica utilizando su identificador."
    )
    @GetMapping("/condiciones/{id}")
    public ResponseEntity<Condicion> getCondicion(@PathVariable int id){
        try{
            Condicion resultado = condicionService.findById(id);
            return ResponseEntity.ok(resultado);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Ruta no encontrada")){ // NOTA: Aquí debería ser "Condicion no encontrada" para ser más claro.
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Crear una nueva condición",
            description = "Registra una nueva condición, estableciendo automáticamente el campo 'condicion' basado en la 'restriccion' y el 'id_tipo_condicion'."
    )
    @PostMapping("/condicion")
    public ResponseEntity<Condicion> createCondicion(@RequestBody Condicion condicion){
        try {
            Condicion resultado = condicionService.save(condicion);
            return ResponseEntity.ok(resultado);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINTS: TIPOS DE CONDICIÓN ----------------------------------------

    @Operation(
            summary = "Listar todos los tipos de condición",
            description = "Retorna la lista de tipos de condición (e.g., Kilometraje, Regiones, Cantidad Rutas)."
    )
    @GetMapping("/tipoCondicion")
    public ResponseEntity<List<Tipo_condicion>> getTipoCondicion(){
        try{
            List<Tipo_condicion> tipoCondiciones = condicionService.findAllTipoCondicion();
            return ResponseEntity.ok(tipoCondiciones);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Obtener un tipo de condición por ID",
            description = "Busca y devuelve un tipo de condición específico por su identificador."
    )
    @GetMapping("/tipoCondicion/{id}")
    public ResponseEntity<Tipo_condicion> getTipoCondicion(@PathVariable int id){
        try{
            Tipo_condicion tipoCondicion = condicionService.findTipoCondicionById(id);
            return ResponseEntity.ok(tipoCondicion);
        }catch (RuntimeException e){
            if(e.getMessage().equals("Tipo condicion no encontrado")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINTS: ACTUALIZACIONES PARCIALES (PATCH) --------------------------

    @Operation(
            summary = "Actualizar nombre del logro",
            description = "Actualiza el campo 'nombre' de un logro específico por su ID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nombre actualizado"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Logro no encontrado")
            }
    )
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<Logro> updateNombre(@PathVariable int id, @RequestBody String nombre){
        try{
            Logro logro = logroService.updateNombre(id, nombre);
            return  ResponseEntity.ok(logro);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Logro no encontrado")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar descripción del logro",
            description = "Actualiza el campo 'descripcion' de un logro específico por su ID."
    )
    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<Logro> updateDescripcion(@PathVariable int id, @RequestBody String descripcion){
        try{
            Logro logro = logroService.updateDescripcion(id, descripcion);
            return  ResponseEntity.ok(logro);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Logro no encontrado")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar estado del logro",
            description = "Actualiza el campo 'id_estado' (activo/inactivo/baneado) de un logro. Se espera el ID de estado del microservicio de Usuarios."
    )
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Logro> updateEstado(@PathVariable int id, @RequestBody Integer estado){
        try{
            Logro logro = logroService.updateEstado(id, estado);
            return  ResponseEntity.ok(logro);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Logro no encontrado")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar icono del logro",
            description = "Actualiza el campo binario 'icono' (byte[]) de un logro."
    )
    @PatchMapping("/{id}/icono")
    public ResponseEntity<Logro> updateIcono(@PathVariable int id, @RequestBody byte[] icono){
        try{
            Logro logro = logroService.updateIcono(id, icono);
            return  ResponseEntity.ok(logro);
        }catch (RuntimeException e){
            if(e.getMessage().equals("Logro no encontrado")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar restricción de una condición",
            description = "Actualiza el valor numérico 'restriccion' (e.g., 40 km, 3 regiones) de una condición por su ID. La descripción de la condición se actualiza automáticamente."
    )
    @PatchMapping("/condicion/{id}/restriccion")
    public ResponseEntity<Condicion> updateRestriccion(@PathVariable int id, @RequestBody BigDecimal restriccion){
        try{
            Condicion condicion = condicionService.updateRestriccion(id, restriccion);
            return ResponseEntity.ok(condicion);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Condicion no encontrada")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Actualizar tipo de condición",
            description = "Actualiza el 'id_tipo_condicion' de una condición. La descripción de la condición se actualiza automáticamente con el nuevo tipo."
    )
    @PatchMapping("/condicion/{id}/tipoCondicion")
    public ResponseEntity<Condicion> updateTipoCondicion(@PathVariable int id, @RequestBody Integer tipoCondicion){
        try {
            Condicion condicion = condicionService.updateidTipo(id, tipoCondicion);
            return ResponseEntity.ok(condicion);
        }catch (RuntimeException e){
            if(e.getMessage().equals("Condicion no encontrada")){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

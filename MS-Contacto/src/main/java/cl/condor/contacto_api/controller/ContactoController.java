package cl.condor.contacto_api.controller;


import cl.condor.contacto_api.model.Contacto;
import cl.condor.contacto_api.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.Map;

import java.util.List;

@Tag(
        name = "Contacto",
        description = """
                Controller del microservicio de Contacto,
                aqui se maneja principalmente los endpoints del CRUD 
                del microservicio. 
                """
)
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/contacto")
public class ContactoController {

    @Autowired
    private ContactoService contactoService;

    @Operation(
            summary = "Listar todas los formularios de contacto",
            description = """
                    Retorna la lista completa de formaularios
                    de contactanos.
                    """
    )
    @GetMapping
    public ResponseEntity<List<Contacto>> findAll() {
        try{
            List<Contacto> contactos = contactoService.findAll();
            return ResponseEntity.ok(contactos);
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Mostrar un formulario por id",
            description = """
                    Retorna el formaulario con la id
                    que se le entregue.
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<Contacto> findById(@PathVariable Integer id) {
        try{
            Contacto contacto = contactoService.findById(id);
            return ResponseEntity.ok(contacto);
        }catch(RuntimeException e){
            if(e.getMessage().equals("Contacto no encontrado")){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Publicar el mensaje de contactanos",
            description = """
                    Guarda el formulario en la base
                    de datos para que pueda ser consultado
                    cuando sea necesario.
                    """
    )
    @PostMapping
    public ResponseEntity<Contacto> create(@RequestBody Contacto contacto) {
        try{
            // Asegurarse de que campos obligatorios a nivel de base de datos tengan valores
            if (contacto.getIdUsuario() == null) {
                // En el formulario de contacto público no se asocia usuario; usar 0 como marcador
                contacto.setIdUsuario(0);
            }
            // Establecer fecha de creación si no viene
            if (contacto.getFCreacion() == null) {
                contacto.setFCreacion(new Date());
            }

            contactoService.save(contacto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Borrar formulario de contactanos",
            description = """
                    Esta funcion borra uno de los formularios
                    por la id, esto para que en caso de que exista
                    alguno que no lo queramos el la BD pueda ser borrado.
                    """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @RequestHeader(value = "X-User-Role", required = false) String roleHeader) {
        try {
            // Verificar rol: solo moderador o admin pueden borrar
            System.out.println("[ContactoController] DELETE called. X-User-Role=" + roleHeader);
            if (!isModeratorOrAdmin(roleHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            contactoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(
            summary = "Actualizar un formulario de contacto",
            description = "Permite actualizar campos como respuesta y marcar como resuelto"
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Contacto updated,
                                    @RequestHeader(value = "X-User-Role", required = false) String roleHeader) {
        try {
            // Verificar rol
            System.out.println("[ContactoController] PUT called. X-User-Role=" + roleHeader);
            if (!isModeratorOrAdmin(roleHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Acceso denegado: se requiere rol moderador."));
            }
            Contacto existing = contactoService.findById(id);
            // Solo actualizar campos permitidos por moderador
            existing.setRespuesta(updated.getRespuesta());
            existing.setResuelto(updated.getResuelto() != null ? updated.getResuelto() : existing.getResuelto());
            contactoService.save(existing);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Contacto no encontrado")) return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isModeratorOrAdmin(String roleHeader) {
        if (roleHeader == null) return false;
        String v = roleHeader.trim().toLowerCase();
        // Accept numeric role ids or textual names
        if (v.equals("1") || v.contains("admin")) return true;
        // En la base de datos el id para Moderador es 2 (Administrador=1, Moderador=2, Usuario=3)
        if (v.equals("2") || v.contains("moder")) return true;
        return false;
    }

}

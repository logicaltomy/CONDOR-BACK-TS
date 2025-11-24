package cl.condor.contacto_api.controller;


import cl.condor.contacto_api.model.Contacto;
import cl.condor.contacto_api.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Contacto",
        description = """
                Controller del microservicio de Contacto,
                aqui se maneja principalmente los endpoints del CRUD 
                del microservicio. 
                """
)
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
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            contactoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

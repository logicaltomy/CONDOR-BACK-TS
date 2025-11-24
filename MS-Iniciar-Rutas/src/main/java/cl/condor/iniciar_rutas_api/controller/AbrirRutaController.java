package cl.condor.iniciar_rutas_api.controller;

import cl.condor.iniciar_rutas_api.model.AbrirRuta;
import cl.condor.iniciar_rutas_api.service.AbrirRutaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/abrir-ruta")
public class AbrirRutaController {

    @Autowired
    private AbrirRutaService abrirRutaService;

    @Operation(
            summary = "Funcion que trae todas las rutas que se han inicializado",
            description = """
                    Te trae todas las rutas que se hayan realizado con 
                    el usuario que la realizo.
                    """
    )
    @GetMapping
    public ResponseEntity<List<AbrirRuta>> getAllAbrirRuta() {
        List<AbrirRuta> lista = abrirRutaService.findAll();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Funcion que trae una inicializacion de ruta por su id",
            description = """
                    Esta funcion te entrega la inicializacion que le hayas
                    especificado que te traiga con su id correspondiente.
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<AbrirRuta> getAbrirRutaById(@PathVariable Integer id) {
        try {
            AbrirRuta abrirRuta = abrirRutaService.findById(id);
            return ResponseEntity.ok(abrirRuta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Esta funcion te entrega todas las rutas que haya realizado el usuario que especifiques",
            description = """
                    Esta funcion esta hecha para que le entregues la id de un
                    usuario y esta te entregara todas las rutas que este 
                    usuario haya realizado.
                    """
    )
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<AbrirRuta>> getAbrirRutaByIdUsuario(@PathVariable Integer id) {
        try {
            List<AbrirRuta> abrirRuta = abrirRutaService.findByIdUsuario(id);
            return ResponseEntity.ok(abrirRuta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Post simple que sube la ruta que realizo un usuario",
            description = """
                    Esta funcion te crea la ruta con la finalidad principal
                    que se pone automaticamente la fecha inicio de la ruta como
                    la fecha actual.
                    """
    )
    @PostMapping
    public ResponseEntity<AbrirRuta> createAbrirRuta(@RequestBody AbrirRuta abrirRuta) {
        try {
            AbrirRuta saved = abrirRutaService.save(abrirRuta);
            return ResponseEntity.status(201).body(saved);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Funcion que marca la fecha fin de la ruta realizada",
            description = """
                    Esta funcion actualiza la fecha fin de la ruta,
                    con fin de diferenciar cuando una ruta esta o no en
                    progreso.
                    """
    )
    @PatchMapping("/marcarFin/{id}")
    public ResponseEntity<AbrirRuta> marcarFin(@PathVariable Integer id) {
        try {
            AbrirRuta abrirRuta =  abrirRutaService.marcarFin(id);
            return ResponseEntity.ok(abrirRuta);
        }catch (WebClientRequestException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }catch (RuntimeException e) {
            if (e.getMessage().equals("AbrirRuta no encontrada")){
                return  ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

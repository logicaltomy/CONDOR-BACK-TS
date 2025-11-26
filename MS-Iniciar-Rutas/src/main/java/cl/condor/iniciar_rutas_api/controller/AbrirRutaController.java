package cl.condor.iniciar_rutas_api.controller;

import cl.condor.iniciar_rutas_api.model.AbrirRuta;
import cl.condor.iniciar_rutas_api.service.AbrirRutaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
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

    
}

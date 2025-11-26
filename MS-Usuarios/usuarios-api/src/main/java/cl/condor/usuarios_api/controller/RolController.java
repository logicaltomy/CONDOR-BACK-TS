package cl.condor.usuarios_api.controller;

/**
 * Catálogo de roles deshabilitado.
 *
 * Los roles se gestionan ahora de forma interna desde {@link cl.condor.usuarios_api.service.UsuarioService},
 * por lo que este controlador se mantiene vacío para evitar exponer endpoints sin uso.
 */
public class RolController {
    private RolController() {
        throw new IllegalStateException("Controlador de roles sin uso");
    }
}

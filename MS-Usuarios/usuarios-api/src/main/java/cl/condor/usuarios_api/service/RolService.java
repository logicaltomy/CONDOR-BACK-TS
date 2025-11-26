package cl.condor.usuarios_api.service;

/**
 * Servicio de roles deshabilitado.
 *
 * La asignación de roles se resuelve directamente mediante {@code RolRepository}
 * desde {@link cl.condor.usuarios_api.service.UsuarioService},
 * por lo que este bean se mantiene vacío para evitar inicializaciones innecesarias.
 */
public final class RolService {
    private RolService() {
        throw new IllegalStateException("Servicio de roles sin uso");
    }
}

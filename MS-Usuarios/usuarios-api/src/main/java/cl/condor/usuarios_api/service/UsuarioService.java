package cl.condor.usuarios_api.service;

import cl.condor.usuarios_api.dto.LoginDTO;
import cl.condor.usuarios_api.dto.PreguntasResponseDTO; 
import cl.condor.usuarios_api.dto.RecuperacionDTO;    
import cl.condor.usuarios_api.dto.UsuarioDTO;
import cl.condor.usuarios_api.model.Usuario;
import cl.condor.usuarios_api.repository.EstadoRepository;
import cl.condor.usuarios_api.repository.RegionRepository;
import cl.condor.usuarios_api.repository.RolRepository;
import cl.condor.usuarios_api.repository.UsuarioRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RolRepository rolRepository;

    // Inyección del encoder de contraseñas
    private final PasswordEncoder encoder;

    public UsuarioDTO mapToDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .fotoPerfil(usuario.getFotoPerfil())
                .rutasRecorridas(usuario.getRutasRecorridas())
                .kmRecorridos(usuario.getKmRecorridos())
                .idRol(usuario.getIdRol())
                .idRegion(usuario.getIdRegion())
                .idEstado(usuario.getIdEstado())
                .build();
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ---------------------------------------------------------
    // MÉTODO SAVE (REGISTRO) ACTUALIZADO CON DOBLE PREGUNTA
    // ---------------------------------------------------------
    @Transactional
    public Usuario save(Usuario usuario) {
        // Validaciones de claves foráneas
        if (usuario.getIdRegion() == null || !regionRepository.existsById(usuario.getIdRegion())) {
            throw new RuntimeException("La región es obligatoria, no se puede guardar el Usuario");
        }
        if (usuario.getIdRol() == null || !rolRepository.existsById(usuario.getIdRol())) {
            throw new RuntimeException("Rol no encontrado, no se puede guardar el Usuario");
        }

        // --- VALIDACIÓN DE SEGURIDAD (NUEVO) ---
        // Validamos que vengan las 2 preguntas y las 2 respuestas desde el front
        if (usuario.getPreguntaSeguridad1() == null || usuario.getPreguntaSeguridad1().trim().isEmpty() ||
            usuario.getRespuestaSeguridad1() == null || usuario.getRespuestaSeguridad1().trim().isEmpty() ||
            usuario.getPreguntaSeguridad2() == null || usuario.getPreguntaSeguridad2().trim().isEmpty() ||
            usuario.getRespuestaSeguridad2() == null || usuario.getRespuestaSeguridad2().trim().isEmpty()) {
            
            throw new RuntimeException("Debe seleccionar 2 preguntas de seguridad y responderlas.");
        }

        // Validar que las preguntas no sean iguales (regla de negocio lógica)
        if (usuario.getPreguntaSeguridad1().equals(usuario.getPreguntaSeguridad2())) {
            throw new RuntimeException("Por favor seleccione dos preguntas diferentes.");
        }

        // NORMALIZACIÓN: Guardamos respuestas en minúsculas y sin espacios extra
        usuario.setRespuestaSeguridad1(usuario.getRespuestaSeguridad1().trim().toLowerCase());
        usuario.setRespuestaSeguridad2(usuario.getRespuestaSeguridad2().trim().toLowerCase());
        // Las preguntas se guardan tal cual vienen del ComboBox
        // ----------------------------------------

        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    // ---------------------------------------------------------
    // MÉTODOS DE ACTUALIZACIÓN EXISTENTES (Sin cambios)
    // ---------------------------------------------------------

    @Transactional
    public Usuario updateNombre(Integer id, String nuevoNombre) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombre(nuevoNombre);
        return usuarioRepository.save(usuario);
    }


    @Transactional
    public Usuario updateCorreo(Integer id, String nuevoCorreo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setCorreo(nuevoCorreo);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateRegion(Integer id, Integer nuevaRegion) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setIdRegion(nuevaRegion);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario updateRutasRecorridas(Integer id, Integer nuevasRutas) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRutasRecorridas(nuevasRutas);
        return usuarioRepository.save(usuario);
    }

    public void login(LoginDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByCorreo(loginDTO.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        if (!encoder.matches(loginDTO.getPassword(), usuario.getContrasena())){
            throw new RuntimeException("Credenciales invalidas");
        }
    }
    
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }
    
    @Transactional
    public Usuario updateFoto(Integer id, String fotoBase64) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (fotoBase64 == null || fotoBase64.trim().isEmpty()) {
            throw new RuntimeException("La foto no puede estar vacía");
        }

        try {
            String cleanBase64 = fotoBase64;
            if (fotoBase64.contains(",")) {
                cleanBase64 = fotoBase64.split(",")[1]; 
            }
            cleanBase64 = cleanBase64.replaceAll("\\s", "");
            byte[] fotoBytes = java.util.Base64.getDecoder().decode(cleanBase64);
            usuario.setFotoPerfil(fotoBytes);
            return usuarioRepository.save(usuario);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El string enviado no es un Base64 válido: " + e.getMessage());
        }
    }

    // ========================================================================
    //  NUEVAS FUNCIONALIDADES DE RECUPERACIÓN DE CONTRASEÑA
    // ========================================================================

    /**
     * PASO 1: Obtener las preguntas que el usuario guardó.
     * El frontend llamará a esto cuando el usuario ponga su correo en "Recuperar Clave".
     */
    public PreguntasResponseDTO obtenerPreguntasSeguridad(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo."));

        // Verificamos si tiene las preguntas configuradas
        if (usuario.getPreguntaSeguridad1() == null || usuario.getPreguntaSeguridad2() == null) {
            throw new RuntimeException("Este usuario no configuró sus preguntas de seguridad.");
        }

        return PreguntasResponseDTO.builder()
                .correo(usuario.getCorreo())
                .pregunta1(usuario.getPreguntaSeguridad1()) // Texto de la pregunta 1
                .pregunta2(usuario.getPreguntaSeguridad2()) // Texto de la pregunta 2
                .build();
    }

    /**
     * PASO 2: Validar respuestas y cambiar contraseña.
     * Recibe el DTO con correo, respuestas del usuario y nueva pass.
     */
    @Transactional
    public void recuperarContrasena(RecuperacionDTO dto) {
        // 1. Buscar Usuario
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 2. Verificar que existan datos guardados en BD
        if (usuario.getRespuestaSeguridad1() == null || usuario.getRespuestaSeguridad2() == null) {
            throw new RuntimeException("Error de integridad: Usuario sin respuestas configuradas.");
        }

        // 3. Preparar respuestas enviadas por el usuario (limpiar y minúsculas)
        String respuestaEnviada1 = dto.getRespuestaSeguridad1() != null ? dto.getRespuestaSeguridad1().trim().toLowerCase() : "";
        String respuestaEnviada2 = dto.getRespuestaSeguridad2() != null ? dto.getRespuestaSeguridad2().trim().toLowerCase() : "";

        // 4. Recuperar respuestas reales de la BD (ya están en minúsculas desde el save)
        String respuestaReal1 = usuario.getRespuestaSeguridad1();
        String respuestaReal2 = usuario.getRespuestaSeguridad2();

        // 5. COMPARACIÓN EXACTA
        boolean respuesta1Correcta = respuestaReal1.equals(respuestaEnviada1);
        boolean respuesta2Correcta = respuestaReal2.equals(respuestaEnviada2);

        if (!respuesta1Correcta || !respuesta2Correcta) {
            throw new RuntimeException("Una o ambas respuestas de seguridad son incorrectas.");
        }

        // 6. Validar la nueva contraseña
        if (dto.getNuevaPassword() == null || dto.getNuevaPassword().length() < 4) {
             throw new RuntimeException("La nueva contraseña debe tener al menos 4 caracteres.");
        }

        // 7. Todo OK -> Hashear y guardar nueva contraseña
        usuario.setContrasena(encoder.encode(dto.getNuevaPassword()));
        usuarioRepository.save(usuario);
    }
}

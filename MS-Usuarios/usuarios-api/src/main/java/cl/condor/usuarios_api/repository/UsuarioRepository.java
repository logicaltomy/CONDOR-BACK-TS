package cl.condor.usuarios_api.repository;

import cl.condor.usuarios_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // 1.28.0 - Spring creara la query automaticamente
    Optional<Usuario> findByCorreo(String correo);
}

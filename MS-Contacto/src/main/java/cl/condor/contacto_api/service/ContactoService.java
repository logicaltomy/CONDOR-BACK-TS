package cl.condor.contacto_api.service;

import cl.condor.contacto_api.model.Contacto;
import cl.condor.contacto_api.repository.ContactoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ContactoService {

    @Autowired
    private ContactoRepository contactoRepository;

    public List<Contacto> findAll() {
        return contactoRepository.findAll();
    }

    public Contacto findById(Integer id) {
        return contactoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));
    }

    @Transactional
    public Contacto save(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    @Transactional
    public void deleteById(Integer id) {
        contactoRepository.findById(id).orElseThrow(() -> new RuntimeException("Contacto no encontrado"));
        contactoRepository.deleteById(id);
    }


}

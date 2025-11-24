package cl.condor.logros_api.service;

import cl.condor.logros_api.model.Condicion;
import cl.condor.logros_api.model.Tipo_condicion;
import cl.condor.logros_api.repository.CondicionRepository;
import cl.condor.logros_api.repository.TipoCondicionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CondicionService {

    @Autowired
    private CondicionRepository condicionRepository;

    @Autowired
    private TipoCondicionRepository tipo_condicionRepository;

    //Nos entrega una lsita de los Json de todas las condiciones
    public List<Condicion> findAll(){ return condicionRepository.findAll();}

    //Buscar una condicion especifica
    public Condicion findById(int id){
        return condicionRepository.findById(id).orElseThrow(() -> new RuntimeException("Ruta no encontrada"));
    }

    public List<Tipo_condicion> findAllTipoCondicion(){ return tipo_condicionRepository.findAll();}

    public Tipo_condicion findTipoCondicionById(int id){
        return tipo_condicionRepository.findById(id).orElseThrow(() -> new RuntimeException("Tipo condicion no encontrado"));
    }

    //Aqui haremos los Post, desde las entidades fuertes hasta las que dependen de estas
    public Tipo_condicion save(Tipo_condicion tipo_condicion){
        return tipo_condicionRepository.save(tipo_condicion);
    }

    //Entidad debil
    @Transactional
    public Condicion save(Condicion condicion){
        if (condicion.getId_tipo_condicion() == 1){
            condicion.setCondicion("Recorrer "+condicion.getRestriccion()+" km");
        }else if (condicion.getId_tipo_condicion() == 2){
            condicion.setCondicion("Terminar "+condicion.getRestriccion()+" rutas en diferentes regiones");
        }else if (condicion.getId_tipo_condicion() == 3){
            condicion.setCondicion("Termina "+condicion.getRestriccion()+" rutas");
        }
        return condicionRepository.save(condicion);
    }

    @Transactional
    public Condicion updateRestriccion(Integer id, BigDecimal restriccion){
        Condicion condicion = condicionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Condicion no encontrada"));
        condicion.setRestriccion(restriccion);
        if (condicion.getId_tipo_condicion() == 1){
            condicion.setCondicion("Recorrer "+condicion.getRestriccion()+" km");
        }else if (condicion.getId_tipo_condicion() == 2){
            condicion.setCondicion("Terminar "+condicion.getRestriccion()+" rutas en diferentes regiones");
        }else if (condicion.getId_tipo_condicion() == 3){
            condicion.setCondicion("Termina "+condicion.getRestriccion()+" rutas");
        }
        return condicionRepository.save(condicion);
    }

    @Transactional
    public Condicion updateidTipo(Integer id, Integer idTipoCondicion){
        Condicion condicion = condicionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Condicion no encontrada"));
        condicion.setId_tipo_condicion(idTipoCondicion);
        if (idTipoCondicion == 1){
            condicion.setCondicion("Recorrer "+condicion.getRestriccion()+" km");
        }else if (idTipoCondicion == 2){
            condicion.setCondicion("Terminar "+condicion.getRestriccion()+" rutas en diferentes regiones");
        }else if (idTipoCondicion == 3){
            condicion.setCondicion("Termina "+condicion.getRestriccion()+" rutas");
        }
        return condicionRepository.save(condicion);
    }

}

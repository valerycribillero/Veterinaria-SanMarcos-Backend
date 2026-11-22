package cl.veterinariasanmarcos.mensajeria.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.veterinariasanmarcos.mensajeria.dto.NuevoContacto;
import cl.veterinariasanmarcos.mensajeria.model.Contacto;
import cl.veterinariasanmarcos.mensajeria.repository.ContactoRepository;

@Service
public class ContactoService {

    private final ContactoRepository repository;

    public ContactoService(ContactoRepository repository) {
        this.repository = repository;
    }

    public Contacto crear(NuevoContacto datos) {
        Contacto contacto =
                new Contacto(
                    datos.nombreCompleto(),
                    datos.correo(),
                    datos.telefono(),
                    datos.asunto(),
                    datos.mensaje()
                );
        return repository.save(contacto);
    }

    public List<Contacto> listar() {
        return repository.findAll(Sort.by(
            Sort.Direction.DESC,
            "fechaCreacion",
            "id"
        ));
    }

    @Transactional
    public Contacto marcarAtendido(Long id) {
        Contacto contacto = buscar(id);
        contacto.marcarAtendido();
        return contacto;
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    private Contacto buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Contacto no encontrado"
                                ));
    }
}

package cl.veterinariasanmarcos.servicios.service;

import cl.veterinariasanmarcos.servicios.dto.ServicioCatalogoDTO;
import cl.veterinariasanmarcos.servicios.model.ServicioCatalogo;
import cl.veterinariasanmarcos.servicios.repository.ServicioCatalogoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioCatalogoService {

    private final ServicioCatalogoRepository repository;

    @Transactional(readOnly = true)
    public List<ServicioCatalogoDTO> listarTodos() {
        return repository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioCatalogoDTO> listarPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioCatalogoDTO> listarPorEspecie(String especie) {
        return repository.findByEspecieContainingIgnoreCaseAndActivoTrue(especie).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServicioCatalogoDTO obtenerPorCodigo(String codigo) {
        ServicioCatalogo servicio = repository.findById(codigo.toUpperCase())
                .filter(ServicioCatalogo::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con código: " + codigo));
        return toDTO(servicio);
    }

    @Transactional
    public ServicioCatalogoDTO guardarOActualizar(ServicioCatalogoDTO dto) {
        ServicioCatalogo servicio = ServicioCatalogo.builder()
                .codigo(dto.getCodigo().toUpperCase())
                .categoria(dto.getCategoria())
                .nombreServicio(dto.getNombreServicio())
                .especie(dto.getEspecie())
                .duracionAprox(dto.getDuracionAprox())
                .precioClp(dto.getPrecioClp())
                .observaciones(dto.getObservaciones())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        return toDTO(repository.save(servicio));
    }

    @Transactional
    public void desactivar(String codigo) {
        ServicioCatalogo servicio = repository.findById(codigo.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con código: " + codigo));
        servicio.setActivo(false);
        repository.save(servicio);
    }

    private ServicioCatalogoDTO toDTO(ServicioCatalogo s) {
        return ServicioCatalogoDTO.builder()
                .codigo(s.getCodigo())
                .categoria(s.getCategoria())
                .nombreServicio(s.getNombreServicio())
                .especie(s.getEspecie())
                .duracionAprox(s.getDuracionAprox())
                .precioClp(s.getPrecioClp())
                .observaciones(s.getObservaciones())
                .activo(s.getActivo())
                .build();
    }
}
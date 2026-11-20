package cl.veterinariasanmarcos.compras.service;

import cl.veterinariasanmarcos.compras.dto.ProductoServicioDTO;
import cl.veterinariasanmarcos.compras.model.ProductoServicio;
import cl.veterinariasanmarcos.compras.model.TipoItem;
import cl.veterinariasanmarcos.compras.repository.ProductoServicioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ProductoServicioRepository repository;

    @Transactional(readOnly = true)
    public List<ProductoServicioDTO> listarTodo() {
        return repository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoServicioDTO> listarPorTipo(TipoItem tipo) {
        return repository.findByTipoItemAndActivoTrue(tipo).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoServicioDTO> listarPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoServicioDTO obtenerPorCodigo(String codigo) {
        ProductoServicio item = repository.findById(codigo)
                .filter(ProductoServicio::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Ítem no encontrado en catálogo: " + codigo));
        return toDTO(item);
    }

    @Transactional
    public ProductoServicioDTO guardarOActualizar(ProductoServicioDTO dto) {
        ProductoServicio item = ProductoServicio.builder()
                .codigo(dto.getCodigo().toUpperCase())
                .tipoItem(dto.getTipoItem())
                .categoria(dto.getCategoria())
                .nombre(dto.getNombre())
                .principioActivo(dto.getPrincipioActivo())
                .presentacion(dto.getPresentacion())
                .duracionAprox(dto.getDuracionAprox())
                .especie(dto.getEspecie())
                .stockActual(dto.getTipoItem() == TipoItem.MEDICAMENTO_VACUNA ? dto.getStockActual() : null)
                .precioClp(dto.getPrecioClp())
                .observaciones(dto.getObservaciones())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        return toDTO(repository.save(item));
    }

    @Transactional
    public ProductoServicioDTO actualizarStock(String codigo, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        ProductoServicio item = repository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Ítem no encontrado: " + codigo));
        if (item.getTipoItem() != TipoItem.MEDICAMENTO_VACUNA) {
            throw new IllegalStateException("Los servicios clínicos no manejan inventario físico");
        }
        item.setStockActual(nuevoStock);
        return toDTO(repository.save(item));
    }

    private ProductoServicioDTO toDTO(ProductoServicio p) {
        return ProductoServicioDTO.builder()
                .codigo(p.getCodigo())
                .tipoItem(p.getTipoItem())
                .categoria(p.getCategoria())
                .nombre(p.getNombre())
                .principioActivo(p.getPrincipioActivo())
                .presentacion(p.getPresentacion())
                .duracionAprox(p.getDuracionAprox())
                .especie(p.getEspecie())
                .stockActual(p.getStockActual())
                .precioClp(p.getPrecioClp())
                .observaciones(p.getObservaciones())
                .activo(p.getActivo())
                .build();
    }
}
package cl.veterinariasanmarcos.compras.service;

import cl.veterinariasanmarcos.compras.dto.ProductoDTO;
import cl.veterinariasanmarcos.compras.model.Producto;
import cl.veterinariasanmarcos.compras.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return repository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(String categoria) {
        return repository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorEspecie(String especie) {
        return repository.findByEspecieContainingIgnoreCaseAndActivoTrue(especie).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarBajoStock(int umbral) {
        return repository.findByStockActualLessThanEqualAndActivoTrue(umbral).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorCodigo(String codigo) {
        Producto producto = repository.findById(codigo.toUpperCase())
                .filter(Producto::getActivo)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con código: " + codigo));
        return toDTO(producto);
    }

    @Transactional
    public ProductoDTO guardarOActualizar(ProductoDTO dto) {
        Producto producto = Producto.builder()
                .codigo(dto.getCodigo().toUpperCase())
                .categoria(dto.getCategoria())
                .nombreComercial(dto.getNombreComercial())
                .principioActivo(dto.getPrincipioActivo())
                .presentacion(dto.getPresentacion())
                .especie(dto.getEspecie())
                .stockActual(dto.getStockActual())
                .precioUnitarioClp(dto.getPrecioUnitarioClp())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        return toDTO(repository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarStock(String codigo, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        Producto producto = repository.findById(codigo.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con código: " + codigo));
        producto.setStockActual(nuevoStock);
        return toDTO(repository.save(producto));
    }

    @Transactional
    public void desactivar(String codigo) {
        Producto producto = repository.findById(codigo.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con código: " + codigo));
        producto.setActivo(false);
        repository.save(producto);
    }

    private ProductoDTO toDTO(Producto p) {
        return ProductoDTO.builder()
                .codigo(p.getCodigo())
                .categoria(p.getCategoria())
                .nombreComercial(p.getNombreComercial())
                .principioActivo(p.getPrincipioActivo())
                .presentacion(p.getPresentacion())
                .especie(p.getEspecie())
                .stockActual(p.getStockActual())
                .precioUnitarioClp(p.getPrecioUnitarioClp())
                .activo(p.getActivo())
                .build();
    }
}
package cl.veterinariasanmarcos.compras.service;

import cl.veterinariasanmarcos.compras.dto.*;
import cl.veterinariasanmarcos.compras.model.*;
import cl.veterinariasanmarcos.compras.repository.CompraRepository;
import cl.veterinariasanmarcos.compras.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public CompraResponseDTO crearCompra(CompraRequestDTO dto) {
        Compra compra = Compra.builder()
                .duenoId(dto.getDuenoId())
                .nombreDueno(dto.getNombreDueno())
                .pacienteId(dto.getPacienteId())
                .nombrePaciente(dto.getNombrePaciente())
                .observacionCliente(dto.getObservacionCliente())
                .estado(EstadoCompra.PENDIENTE)
                .totalClp(0)
                .build();

        List<DetalleCompra> detalles = new ArrayList<>();
        int totalGeneral = 0;

        for (ItemCompraRequestDTO itemReq : dto.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getCodigoProducto().toUpperCase())
                    .filter(Producto::getActivo)
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + itemReq.getCodigoProducto()));

            if (producto.getStockActual() < itemReq.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para " + producto.getNombreComercial()
                        + " (disponible: " + producto.getStockActual() + ", solicitado: " + itemReq.getCantidad() + ")");
            }

            // Descontar stock
            producto.setStockActual(producto.getStockActual() - itemReq.getCantidad());
            productoRepository.save(producto);

            int subtotal = producto.getPrecioUnitarioClp() * itemReq.getCantidad();
            totalGeneral += subtotal;

            DetalleCompra detalle = DetalleCompra.builder()
                    .compra(compra)
                    .producto(producto)
                    .cantidad(itemReq.getCantidad())
                    .precioUnitarioClp(producto.getPrecioUnitarioClp())
                    .subtotalClp(subtotal)
                    .build();

            detalles.add(detalle);
        }

        compra.setDetalles(detalles);
        compra.setTotalClp(totalGeneral);

        return toResponseDTO(compraRepository.save(compra));
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> listarTodas() {
        return compraRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> listarPorDueno(Long duenoId) {
        return compraRepository.findByDuenoIdOrderByFechaCompraDesc(duenoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompraResponseDTO obtenerPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada con ID: " + id));
        return toResponseDTO(compra);
    }

    @Transactional
    public CompraResponseDTO cambiarEstado(Long id, EstadoCompra nuevoEstado) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada con ID: " + id));

        if (compra.getEstado() == EstadoCompra.CANCELADA) {
            throw new IllegalStateException("No se puede modificar una compra que ya fue cancelada");
        }

        // Si se cancela la compra, devolver el stock al inventario
        if (nuevoEstado == EstadoCompra.CANCELADA) {
            for (DetalleCompra det : compra.getDetalles()) {
                Producto prod = det.getProducto();
                prod.setStockActual(prod.getStockActual() + det.getCantidad());
                productoRepository.save(prod);
            }
        }

        compra.setEstado(nuevoEstado);
        return toResponseDTO(compraRepository.save(compra));
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> reporteVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return compraRepository.findByFechaCompraBetweenOrderByFechaCompraDesc(inicio, fin).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private CompraResponseDTO toResponseDTO(Compra c) {
        List<DetalleCompraResponseDTO> detallesDTO = c.getDetalles().stream()
                .map(d -> DetalleCompraResponseDTO.builder()
                        .id(d.getId())
                        .codigoProducto(d.getProducto().getCodigo())
                        .nombreComercial(d.getProducto().getNombreComercial())
                        .principioActivo(d.getProducto().getPrincipioActivo())
                        .presentacion(d.getProducto().getPresentacion())
                        .cantidad(d.getCantidad())
                        .precioUnitarioClp(d.getPrecioUnitarioClp())
                        .subtotalClp(d.getSubtotalClp())
                        .build())
                .collect(Collectors.toList());

        return CompraResponseDTO.builder()
                .id(c.getId())
                .duenoId(c.getDuenoId())
                .nombreDueno(c.getNombreDueno())
                .pacienteId(c.getPacienteId())
                .nombrePaciente(c.getNombrePaciente())
                .fechaCompra(c.getFechaCompra())
                .estado(c.getEstado())
                .totalClp(c.getTotalClp())
                .observacionCliente(c.getObservacionCliente())
                .detalles(detallesDTO)
                .build();
    }
}
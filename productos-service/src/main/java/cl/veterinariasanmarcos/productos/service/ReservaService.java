package cl.veterinariasanmarcos.productos.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.productos.dto.*;
import cl.veterinariasanmarcos.productos.model.*;
import cl.veterinariasanmarcos.productos.repository.*;

@Service
public class ReservaService {
    private final ReservaRepository reservas;
    private final ProductoRepository productos;
    private final TransactionTemplate transaccion;

    public ReservaService(
        ReservaRepository reservas,
        ProductoRepository productos,
        TransactionTemplate transaccion
    ) {
        this.reservas = reservas;
        this.productos = productos;
        this.transaccion = transaccion;
    }

    // Se guarda primero el identificador. Luego se bloquea para serializar los reintentos.
    private void preparar(String id) {
        try {
            transaccion.executeWithoutResult(
                    estado -> {
                        if (!reservas.existsById(id)) {
                            reservas.saveAndFlush(new Reserva(id));
                        }
                    });
        } catch (DataIntegrityViolationException error) {
            if (!reservas.existsById(id)) {
                throw error;
            }
        }
    }

    public ReservaVista reservar(
        String id,
        DatosReserva datos
    ) {
        var lineas = new ArrayList<>(datos.productos());
        lineas.sort(Comparator.comparing(DatosReserva.Linea::codigo));
        var codigos = new HashSet<String>();
        StringBuilder firma = new StringBuilder();
        for (var linea : lineas) {
            if (!codigos.add(linea.codigo())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No repitas productos"
                );
            }
            firma.append(linea.codigo()).append(':').append(linea.cantidad()).append(';');
        }
        preparar(id);
        return transaccion.execute(
                estado -> {
                    Reserva reserva = reservas.bloquear(id).orElseThrow();
                    if (reserva.getEstado() == Reserva.Estado.LIBERADA) {
                        throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "La reserva ya fue cancelada"
                        );
                    }
                    if (reserva.getEstado() == Reserva.Estado.RESERVADA) {
                        if (!firma.toString().equals(reserva.getFirma())) {
                            throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ese identificador pertenece a otra compra"
                            );
                        }
                        return ReservaVista.de(reserva);
                    }
                    var detalles = new ArrayList<DetalleReserva>();
                    for (var linea : lineas) {
                        Producto producto =
                                productos
                                        .bloquear(linea.codigo())
                                        .filter(Producto::isActivo)
                                        .orElseThrow(
                                                () ->
                                                        new ResponseStatusException(
                                                            HttpStatus.NOT_FOUND,
                                                            "Producto no encontrado"
                                                        ));
                        if (producto.getStock() < linea.cantidad()) {
                            throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Stock insuficiente para " + linea.codigo()
                            );
                        }
                        producto.setStock(producto.getStock() - linea.cantidad());
                        detalles.add(new DetalleReserva(
                            producto,
                            linea.cantidad()
                        ));
                    }
                    reserva.confirmar(
                        firma.toString(),
                        detalles
                    );
                    return ReservaVista.de(reserva);
                });
    }

    public void liberar(String id) {
        preparar(id);
        transaccion.executeWithoutResult(
                estado -> {
                    Reserva reserva = reservas.bloquear(id).orElseThrow();
                    if (reserva.getEstado() == Reserva.Estado.LIBERADA) {
                        return;
                    }
                    for (var detalle : reserva.getProductos()) {
                        Producto producto = productos.bloquear(detalle.getCodigo()).orElseThrow();
                        producto.setStock(
                                Math.addExact(
                                    producto.getStock(),
                                    detalle.getCantidad()
                                ));
                    }
                    // También bloquea reservas tardías cuando se canceló antes de reservar.
                    reserva.liberar();
                });
    }
}

package cl.veterinariasanmarcos.productos.dto;

import java.util.List;
import cl.veterinariasanmarcos.productos.model.DetalleReserva;
import cl.veterinariasanmarcos.productos.model.Reserva;

public record ReservaVista(
        String id,

        Reserva.Estado estado,

        List<DetalleReserva> productos
) {
    public static ReservaVista de(Reserva reserva) {
        return new ReservaVista(
            reserva.getId(),
            reserva.getEstado(),
            List.copyOf(reserva.getProductos())
        );
    }
}

package cl.veterinariasanmarcos.pedidos.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReservaProducto(
        String id,

        String estado,

        List<Linea> productos
) {
    public record Linea(
        String codigo,
        String nombre,
        BigDecimal precioUnitario,
        int cantidad
    ) {
    }
}

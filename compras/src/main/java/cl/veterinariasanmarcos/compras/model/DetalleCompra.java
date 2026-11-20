package cl.veterinariasanmarcos.compras.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_compra")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_codigo", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer precioUnitarioClp;

    @Column(nullable = false)
    private Integer subtotalClp;
}
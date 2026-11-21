package cl.veterinariasanmarcos.compras.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long duenoId; // ID del dueño desde el microservicio Usuarios

    @Column(nullable = false, length = 120)
    private String nombreDueno;

    private Long pacienteId; // Opcional: mascota para la cual se compra el producto

    @Column(length = 80)
    private String nombrePaciente;

    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estado;

    @Column(nullable = false)
    private Integer totalClp;

    @Column(length = 255)
    private String observacionCliente;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleCompra> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.fechaCompra == null) {
            this.fechaCompra = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoCompra.PENDIENTE;
        }
    }
}
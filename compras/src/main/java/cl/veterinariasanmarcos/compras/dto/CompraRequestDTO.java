package cl.veterinariasanmarcos.compras.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequestDTO {

    @NotNull(message = "El ID del dueño es obligatorio")
    @Positive(message = "El ID del dueño debe ser positivo")
    private Long duenoId;

    @NotBlank(message = "El nombre del dueño es obligatorio")
    private String nombreDueno;

    @Positive(message = "El ID de la mascota debe ser positivo")
    private Long pacienteId;
    private String nombrePaciente;
    private String observacionCliente;

    @NotEmpty(message = "La compra debe contener al menos un producto o servicio")
    @Valid
    private List<ItemCompraRequestDTO> items;
}

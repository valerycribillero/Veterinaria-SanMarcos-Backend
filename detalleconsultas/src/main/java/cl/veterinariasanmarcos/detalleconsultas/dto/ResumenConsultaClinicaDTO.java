package cl.veterinariasanmarcos.detalleconsultas.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ResumenConsultaClinicaDTO {
    private Long consultaId;
    private Long pacienteId;
    private String nombrePaciente;
    private List<DetalleConsultaResponseDTO> medicamentosRecetados;
    private List<DetalleConsultaResponseDTO> vacunasAplicadas;
    private List<DetalleConsultaResponseDTO> procedimientosYExamenes;
    private List<DetalleConsultaResponseDTO> insumosAplicados;
}
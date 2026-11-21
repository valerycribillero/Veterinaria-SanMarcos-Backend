package cl.veterinariasanmarcos.catalogo.catalogo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CatalogoListadoDTO {

    private Integer id;
    private String nombre;
    private String categoria;
    private String principioActivo;
    private String presentacion;
    private String especie;
    private Integer stock;
    private Integer precio;
}

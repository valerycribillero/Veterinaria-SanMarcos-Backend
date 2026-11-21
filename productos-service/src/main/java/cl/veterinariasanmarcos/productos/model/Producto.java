package cl.veterinariasanmarcos.productos.model;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(length = 30)
    private String codigo;

    @Column(
        nullable = false,
        length = 150
    )
    private String nombre;

    @Column(
        nullable = false,
        length = 100
    )
    private String categoria;

    @Column(
        nullable = false,
        precision = 10,
        scale = 0
    )
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    @Column(
        nullable = false,
        length = 500
    )
    private String imagen;

    @Column(
        name = "principio_activo",
        nullable = false,
        length = 150
    )
    private String principioActivo;

    @Column(
        nullable = false,
        length = 100
    )
    private String presentacion;

    @Column(
        nullable = false,
        length = 100
    )
    private String especie;

    @Column(nullable = false)
    private boolean activo = true;

    protected Producto() {
    }

    public Producto(String codigo) {
        this.codigo = codigo;
    }

    public void desactivar() {
        activo = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String valor) {
        this.nombre = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String valor) {
        this.categoria = valor;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal valor) {
        this.precio = valor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int valor) {
        this.stock = valor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String valor) {
        this.imagen = valor;
    }

    public String getPrincipioActivo() {
        return principioActivo;
    }

    public void setPrincipioActivo(String valor) {
        this.principioActivo = valor;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String valor) {
        this.presentacion = valor;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String valor) {
        this.especie = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isActivo() {
        return activo;
    }
}

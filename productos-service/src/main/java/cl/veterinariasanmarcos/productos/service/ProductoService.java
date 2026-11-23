package cl.veterinariasanmarcos.productos.service;

import java.util.List;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.productos.dto.DatosProducto;
import cl.veterinariasanmarcos.productos.model.Producto;
import cl.veterinariasanmarcos.productos.repository.ProductoRepository;

@Service
@Transactional
public class ProductoService {
    private final ProductoRepository repository;
    private final EntityManager entityManager;

    public ProductoService(
        ProductoRepository repository,
        EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return repository.findByActivoTrueOrderByCodigoAsc();
    }

    @Transactional(readOnly = true)
    public Producto buscar(String codigo) {
        return repository
                .findByCodigoAndActivoTrue(codigo)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Producto no encontrado"
                                ));
    }

    public Producto crear(DatosProducto datos) {
        if (repository.existsById(datos.codigo())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El código de producto ya existe"
            );
        }
        Producto producto = new Producto(datos.codigo());
        aplicar(
            producto,
            datos
        );
        // persist garantiza INSERT; un código duplicado concurrente no actualiza el producto
        // existente.
        entityManager.persist(producto);
        entityManager.flush();
        return producto;
    }

    public Producto actualizar(
        String codigo,
        DatosProducto datos
    ) {
        if (!codigo.equals(datos.codigo())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El código de producto no se puede cambiar"
            );
        }
        Producto producto = bloquearActivo(codigo);
        aplicar(
            producto,
            datos
        );
        return producto;
    }

    public Producto actualizarStock(
        String codigo,
        int stock
    ) {
        Producto producto = bloquearActivo(codigo);
        producto.setStock(stock);
        return producto;
    }

    public void eliminar(String codigo) {
        bloquearActivo(codigo).desactivar();
    }

    public Producto bloquearActivo(String codigo) {
        return repository
                .bloquear(codigo)
                .filter(Producto::isActivo)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Producto no encontrado: " + codigo
                                ));
    }

    private void aplicar(
        Producto producto,
        DatosProducto datos
    ) {
        producto.setNombre(datos.nombre());
        producto.setCategoria(datos.categoria());
        producto.setPrecio(datos.precio());
        producto.setStock(datos.stock());
        producto.setImagen(datos.imagen());
        producto.setPrincipioActivo(datos.principioActivo());
        producto.setPresentacion(datos.presentacion());
        producto.setEspecie(datos.especie());
    }
}

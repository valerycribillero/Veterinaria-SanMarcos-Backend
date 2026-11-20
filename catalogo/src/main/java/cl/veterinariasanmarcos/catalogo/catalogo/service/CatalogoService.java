
package cl.veterinariasanmarcos.catalogo.catalogo.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import cl.veterinariasanmarcos.catalogo.catalogo.dto.CatalogoListadoDTO;
import cl.veterinariasanmarcos.catalogo.catalogo.model.Catalogo;
import cl.veterinariasanmarcos.catalogo.catalogo.repository.CatalogoRepository;

@Service
public class CatalogoService {

    private final CatalogoRepository repository;

    public CatalogoService(CatalogoRepository repository) {
        this.repository = repository;
    }

    public List<Catalogo> obtenerProducto() {
        return repository.findAll();
    }

    public Catalogo guardarProducto(Catalogo catalogo) {
        return repository.save(catalogo);
    }

    public Catalogo obtenerPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminarProducto(Integer id) {
        repository.deleteById(id);
    }

    public Catalogo actualizarProducto(Integer id, Catalogo datos) {

        Catalogo catalogo = repository.findById(id).orElse(null);

        if (catalogo == null) {
            return null;
        }

        catalogo.setCategoria(datos.getCategoria());
        catalogo.setNombre(datos.getNombre());
        catalogo.setPrincipioActivo(datos.getPrincipioActivo());
        catalogo.setPresentacion(datos.getPresentacion());
        catalogo.setEspecie(datos.getEspecie());
        catalogo.setStock(datos.getStock());
        catalogo.setPrecio(datos.getPrecio());

        return repository.save(catalogo);
    }

    public List<Catalogo> buscarPorTipo(String categoria) {
        return repository.findByCategoriaContainingIgnoreCase(categoria);
    }

    public List<CatalogoListadoDTO> listarDTO() {

        List<Catalogo> catalogos = repository.findAll();

        List<CatalogoListadoDTO> lista = new ArrayList<>();

        for (Catalogo c : catalogos) {

            CatalogoListadoDTO dto = new CatalogoListadoDTO();

            dto.setId(c.getId());
            dto.setNombre(c.getNombre());
            dto.setCategoria(c.getCategoria());
            dto.setPrincipioActivo(c.getPrincipioActivo());
            dto.setPresentacion(c.getPresentacion());
            dto.setEspecie(c.getEspecie());
            dto.setStock(c.getStock());
            dto.setPrecio(c.getPrecio());

            lista.add(dto);
        }

        return lista;
    }
}

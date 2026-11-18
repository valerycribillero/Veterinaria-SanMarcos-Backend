package cl.veterinariasanmarcos.catalogo.catalogo.service;

import java.util.List;

import javax.xml.catalog.Catalog;

import cl.veterinariasanmarcos.catalogo.catalogo.dto.CatalogoListadoDTO;
import cl.veterinariasanmarcos.catalogo.catalogo.model.Catalogo;
import cl.veterinariasanmarcos.catalogo.catalogo.repository.CatalogoRepository;

@Service
public class CatalogoService {
    private final CatalogoRepository repository;

    public CatalogoService (CatalogoRepository repository){
        this.repository = repository;
    }

    public List<Catalogo> obtenerProducto(){
        return repository.findAll();
    }

    public Catalogo guardarProducto(Catalogo catalogo){
        return repository.save(catalogo);
    }

    public Catalogo obtenerPorId(Integer id){
        return repository.findById(id).orElse(null);
    }

    public void eliminarProducto(Integer id){
        repository.deleteById(id);
    }

    public Catalogo actualizarProducto(Integer id, Catalogo datos) {
        Catalogo catalogo = repository.findById(id).orElse(null);
        if (producto == null) {
            return null;
        }
        producto.setCategoria(datos.getCategoria());
        producto.setNombre(datos.getNombre());
        producto.setPrincipioActivo(datos.getPrincipioActivo());
        producto.setPresentacion(datos.getPresentacion());
        producto.setEspecie(datos.getEspecie());
        producto.setStock(datos.getStock());
        producto.setPrecio(datos.getPrecio());
        return repository.save(catalogo);
    }

    public List<Catalogo> buscarPorTipo(String categoria){
        return repository.findByTipoCOntainingIgnoreCase(categoria);
    }

    public List<CatalogoListadoDTO> listarDTO() {

        List<Catalogo> catalogo = repository.findAll();
        List<CatalogoListadoDTO> lista = new ArrayList<>();

        for (Catalogo catalogo : c) {

            CatalogoListadoDTO dto = new CatalogoListadoDTO();

            dto.setNombre(c.getNombre());
            dto.setCategoria(c.getCategoria());
            dto.setPrincipioActivo(c.getPrincipioActivo());
            dto.setPresentacion(c.getPresentacion());
            dto.setEspecie(c.getEspecie());
            dto.setStock(c.getStock());


            lista.add(dto);
        }

        return lista;
    }
  
}
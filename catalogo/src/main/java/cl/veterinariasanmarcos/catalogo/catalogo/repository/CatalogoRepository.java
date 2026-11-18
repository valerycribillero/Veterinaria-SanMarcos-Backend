
package cl.veterinariasanmarcos.catalogo.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.veterinariasanmarcos.catalogo.catalogo.model.Catalogo;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer> {

    List<Catalogo> findByCategoriaContainingIgnoreCase(String categoria);

}

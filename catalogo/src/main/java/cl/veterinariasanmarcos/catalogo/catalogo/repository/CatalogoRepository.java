package cl.veterinariasanmarcos.catalogo.catalogo.repository;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer> {

    Optional<Catalogo> findById(Integer id);
    
}

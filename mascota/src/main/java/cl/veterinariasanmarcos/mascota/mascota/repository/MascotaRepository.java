package cl.veterinariasanmarcos.mascota.mascota.repository;

import cl.veterinariasanmarcos.mascota.mascota.model.Mascota;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    java.util.List<Mascota> findByClienteId(Long clienteId);
}

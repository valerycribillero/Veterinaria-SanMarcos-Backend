package cl.veterinariasanmarcos.mascota.mascota.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.veterinariasanmarcos.mascota.mascota.model.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
}

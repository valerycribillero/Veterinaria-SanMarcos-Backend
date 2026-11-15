package cl.veterinariasanmarcos.mensajeria.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.veterinariasanmarcos.mensajeria.model.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}

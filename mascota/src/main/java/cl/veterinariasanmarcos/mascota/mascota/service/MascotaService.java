package cl.veterinariasanmarcos.mascota.mascota.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.veterinariasanmarcos.mascota.mascota.dto.ListarMascotasDTO;
import cl.veterinariasanmarcos.mascota.mascota.model.Mascota;
import cl.veterinariasanmarcos.mascota.mascota.repository.MascotaRepository;

@Service
public class MascotaService {

    @Autowired private MascotaRepository mascotaRepository;

    public List<Mascota> obtenerMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota guardarMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public Mascota obtenerPorId(Integer id) {
        return mascotaRepository.findById(id).orElse(null);
    }

    public void eliminarMascota(Integer id) {
        mascotaRepository.deleteById(id);
    }

    public Mascota actualizarMascota(
        Integer id,
        Mascota mascota
    ) {
        mascota.setId(id);
        return mascotaRepository.save(mascota);
    }

    public List<ListarMascotasDTO> listarDTO() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        List<ListarMascotasDTO> lista = new ArrayList<>();

        for (Mascota m : mascotas) {
            ListarMascotasDTO dto = new ListarMascotasDTO();
            dto.setNombre(m.getNombre());
            dto.setEspecie(m.getEspecie());
            dto.setEdad(m.getEdad());
            dto.setActivo(m.getActivo());
            dto.setPesoKg(m.getPesoKg());
            dto.setRaza(m.getRaza());
            dto.setSexo(m.getSexo());
            dto.setCreadoEn(m.getCreadoEn());
            dto.setClienteId(m.getClienteId());

            lista.add(dto);
        }

        return lista;
    }
}

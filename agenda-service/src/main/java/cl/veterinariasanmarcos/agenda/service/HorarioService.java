package cl.veterinariasanmarcos.agenda.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.agenda.dto.NuevoHorario;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.repository.HorarioRepository;

@Service
@Transactional
public class HorarioService {
    private final HorarioRepository repository;
    private static final ZoneId ZONA = ZoneId.of("America/Santiago");

    public HorarioService(HorarioRepository repository) {
        this.repository = repository;
    }

    public Horario crear(NuevoHorario datos) {
        if (datos.hora().getSecond() != 0 || datos.hora().getNano() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora debe indicar horas y minutos");
        }
        validarFecha(datos.fecha().atTime(datos.hora()));
        if (repository.existsByFechaAndHoraAndVeterinario(datos.fecha(), datos.hora(), datos.veterinario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese veterinario ya tiene un horario registrado");
        }
        return repository.saveAndFlush(new Horario(datos.fecha(), datos.hora(), datos.veterinario()));
    }

    @Transactional(readOnly = true)
    public List<Horario> listarDisponibles(LocalDate fecha) {
        LocalDateTime ahora = LocalDateTime.now(ZONA);
        return repository.listarDisponibles(ahora.toLocalDate(), ahora.toLocalTime(), fecha);
    }

    public Horario buscarBloqueado(Long id) {
        return repository.bloquear(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));
    }

    public void validarVigente(Horario horario) {
        if (!horario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El horario está desactivado");
        }
        validarFecha(horario.getFecha().atTime(horario.getHora()));
    }

    public void validarDisponible(Horario horario) {
        validarVigente(horario);
        if (horario.isReservado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El horario ya está reservado");
        }
    }

    public void desactivar(Long id) {
        Horario horario = buscarBloqueado(id);
        if (horario.isReservado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El horario tiene una cita; cancela la cita primero");
        }
        horario.desactivar();
    }

    public Horario activar(Long id) {
        Horario horario = buscarBloqueado(id);
        validarFecha(horario.getFecha().atTime(horario.getHora()));
        horario.activar();
        return horario;
    }

    private void validarFecha(LocalDateTime fecha) {
        if (!fecha.isAfter(LocalDateTime.now(ZONA))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El horario debe ser futuro");
        }
    }
}

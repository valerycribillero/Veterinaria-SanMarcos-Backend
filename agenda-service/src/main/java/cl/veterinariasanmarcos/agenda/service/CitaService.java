package cl.veterinariasanmarcos.agenda.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import cl.veterinariasanmarcos.agenda.dto.DatosCita;
import cl.veterinariasanmarcos.agenda.model.Cita;
import cl.veterinariasanmarcos.agenda.model.Horario;
import cl.veterinariasanmarcos.agenda.repository.CitaRepository;

@Service
@Transactional
public class CitaService {
    private static final ZoneId ZONA = ZoneId.of("America/Santiago");
    private final CitaRepository repository;
    private final HorarioService horarios;

    public CitaService(
        CitaRepository repository,
        HorarioService horarios
    ) {
        this.repository = repository;
        this.horarios = horarios;
    }

    public Cita reservar(DatosCita datos) {
        // El bloqueo evita que dos personas reserven el mismo horario a la vez.
        Horario horario = horarios.buscarBloqueado(datos.horarioId());
        horarios.validarDisponible(horario);
        horario.reservar();
        Cita cita = new Cita(
            datos,
            horario,
            LocalDateTime.now(ZONA)
        );
        return repository.saveAndFlush(cita);
    }

    @Transactional(readOnly = true)
    public List<Cita> listar(LocalDate fecha) {
        return repository.listar(fecha);
    }

    @Transactional(readOnly = true)
    public Cita buscar(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Cita no encontrada"
                                ));
    }

    public Cita actualizar(
        Long id,
        DatosCita datos
    ) {
        Cita cita = buscarBloqueada(id);
        validarReservada(cita);

        // Se bloquean por ID en orden para evitar bloqueos cruzados al reprogramar.
        Long primero = Math.min(
            cita.getHorarioId(),
            datos.horarioId()
        );
        Long segundo = Math.max(
            cita.getHorarioId(),
            datos.horarioId()
        );
        Horario horarioPrimero = horarios.buscarBloqueado(primero);
        Horario horarioSegundo = horarioPrimero;
        if (!primero.equals(segundo)) {
            horarioSegundo = horarios.buscarBloqueado(segundo);
        }
        Horario anterior = horarioPrimero;
        Horario nuevo = horarioSegundo;
        if (!cita.getHorarioId().equals(primero)) {
            anterior = horarioSegundo;
            nuevo = horarioPrimero;
        }

        if (anterior.getId().equals(nuevo.getId())) {
            horarios.validarVigente(nuevo);
        } else {
            horarios.validarDisponible(nuevo);
            anterior.liberar();
            nuevo.reservar();
        }
        cita.actualizar(
            datos,
            nuevo
        );
        return cita;
    }

    public Cita cancelar(Long id) {
        Cita cita = buscarBloqueada(id);
        if (cita.getEstado() == Cita.Estado.CANCELADA) {
            return cita;
        }
        validarReservada(cita);
        horarios.buscarBloqueado(cita.getHorarioId()).liberar();
        cita.cancelar();
        return cita;
    }

    public Cita atender(Long id) {
        Cita cita = buscarBloqueada(id);
        if (cita.getEstado() == Cita.Estado.ATENDIDA) {
            return cita;
        }
        if (cita.getEstado() == Cita.Estado.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La cita está cancelada");
        }
        cita.atender();
        return cita;
    }

    public Cita confirmarLlegada(Long id) {
        Cita cita = buscarBloqueada(id);
        if (cita.getEstado() == Cita.Estado.EN_ESPERA) return cita;
        validarReservada(cita);
        if (!cita.getFecha().equals(LocalDate.now(ZONA))) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Solo puedes confirmar la llegada el día de la cita"
            );
        }
        cita.confirmarLlegada();
        return cita;
    }

    public Cita llamar(Long id) {
        Cita cita = buscarBloqueada(id);
        if (cita.getEstado() == Cita.Estado.EN_ATENCION) return cita;
        if (cita.getEstado() != Cita.Estado.EN_ESPERA) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Recepción debe confirmar la llegada antes de llamar al paciente"
            );
        }
        cita.llamar();
        return cita;
    }

    private Cita buscarBloqueada(Long id) {
        return repository
                .bloquear(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Cita no encontrada"
                                ));
    }

    private void validarReservada(Cita cita) {
        if (cita.getEstado() != Cita.Estado.RESERVADA) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La cita debe estar reservada para realizar esta operación"
            );
        }
    }
}

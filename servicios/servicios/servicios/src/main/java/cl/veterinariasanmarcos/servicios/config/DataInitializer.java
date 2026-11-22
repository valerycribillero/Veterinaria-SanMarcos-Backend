package cl.veterinariasanmarcos.servicios.config;

import cl.veterinariasanmarcos.servicios.model.ServicioCatalogo;
import cl.veterinariasanmarcos.servicios.repository.ServicioCatalogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ServicioCatalogoRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        List<ServicioCatalogo> servicios = List.of(
            srv("SV001", "Consultas", "Consulta general", "Perro / Gato", "30 min", 15000, null),
            srv("SV002", "Consultas", "Consulta urgencia", "Perro / Gato", "30 min", 25000, "Fuera de horario +$10.000"),
            srv("SV003", "Consultas", "Control postoperatorio", "Perro / Gato", "20 min", 10000, null),
            srv("SV004", "Consultas", "Consulta ave / conejo", "Ave / Conejo", "30 min", 18000, null),
            srv("SV005", "Consultas", "Segunda opinión médica", "Todas", "40 min", 20000, "Requiere ficha previa"),
            srv("VA001", "Vacunación", "Vacuna antirrábica canina", "Perro", "10 min", 12000, "Obligatoria por ley"),
            srv("VA002", "Vacunación", "Vacuna sextuple canina", "Perro", "10 min", 18000, "Refuerzo anual"),
            srv("VA003", "Vacunación", "Vacuna bivalente felina", "Gato", "10 min", 15000, "Refuerzo anual"),
            srv("VA004", "Vacunación", "Vacuna triple felina", "Gato", "10 min", 17000, "Refuerzo anual"),
            srv("VA005", "Vacunación", "Vacuna Bordetella canina", "Perro", "10 min", 14000, "Tos de las perreras"),
            srv("VA006", "Vacunación", "Vacuna antirrábica felina", "Gato", "10 min", 12000, null),
            srv("CI001", "Cirugía", "Esterilización hembra canina", "Perra", "90 min", 80000, "Incluye anestesia y hospitalización 24h"),
            srv("CI002", "Cirugía", "Esterilización macho canino", "Perro", "60 min", 60000, "Incluye anestesia"),
            srv("CI003", "Cirugía", "Esterilización hembra felina", "Gata", "60 min", 65000, "Incluye anestesia y hospitalización 12h"),
            srv("CI004", "Cirugía", "Esterilización macho felino", "Gato", "45 min", 50000, "Incluye anestesia"),
            srv("CI005", "Cirugía", "Extirpación de tumor cutáneo", "Perro / Gato", "60 min", 120000, "Precio referencial; varía según tamaño"),
            srv("CI006", "Cirugía", "Cesárea de urgencia", "Perra / Gata", "120 min", 180000, null),
            srv("DE001", "Desparasitación", "Desparasitación interna pequeños (<10 kg)", "Perro", "5 min", 8000, null),
            srv("DE002", "Desparasitación", "Desparasitación interna medianos (10-25 kg)", "Perro", "5 min", 9500, null),
            srv("DE003", "Desparasitación", "Desparasitación interna grandes (>25 kg)", "Perro", "5 min", 11000, null),
            srv("DE004", "Desparasitación", "Desparasitación interna felina", "Gato", "5 min", 8000, null),
            srv("DE005", "Desparasitación", "Antiparasitario externo (pipeta)", "Perro / Gato", "5 min", 7500, "Incluye aplicación"),
            srv("EX001", "Exámenes", "Hemograma completo", "Perro / Gato", "30 min", 22000, "Resultado en 24-48 h"),
            srv("EX002", "Exámenes", "Perfil bioquímico completo", "Perro / Gato", "30 min", 35000, "Resultado en 24-48 h"),
            srv("EX003", "Exámenes", "Radiografía (1 proyección)", "Perro / Gato", "20 min", 28000, null),
            srv("EX004", "Exámenes", "Ecografía abdominal", "Perro / Gato", "30 min", 45000, null),
            srv("EX005", "Exámenes", "Test de leishmaniasis", "Perro", "20 min", 18000, null),
            srv("OT001", "Otros", "Corte de uñas", "Perro / Gato", "15 min", 5000, null),
            srv("OT002", "Otros", "Limpieza dental", "Perro / Gato", "45 min", 55000, "Requiere anestesia"),
            srv("OT003", "Otros", "Microchip identificación", "Perro / Gato", "10 min", 15000, "Incluye registro"),
            srv("OT004", "Otros", "Hospitalización (por día)", "Perro / Gato", "24 h", 30000, "Incluye monitoreo y alimentación básica")
        );

        repository.saveAll(servicios);
    }

    private ServicioCatalogo srv(String cod, String cat, String nom, String esp, String dur, int precio, String obs) {
        return ServicioCatalogo.builder()
                .codigo(cod).categoria(cat).nombreServicio(nom)
                .especie(esp).duracionAprox(dur).precioClp(precio)
                .observaciones(obs).activo(true).build();
    }
}
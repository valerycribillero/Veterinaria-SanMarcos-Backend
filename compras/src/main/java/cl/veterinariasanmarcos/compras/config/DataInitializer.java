package cl.veterinariasanmarcos.compras.config;

import cl.veterinariasanmarcos.compras.model.Producto;
import cl.veterinariasanmarcos.compras.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        List<Producto> productos =
                List.of(
                    prod(
                        "ME001",
                        "Antibióticos",
                        "Amoxibay 250mg",
                        "Amoxicilina",
                        "Blíster 10 comp.",
                        "Perro / Gato",
                        45,
                        4200
                    ),
                    prod(
                        "ME002",
                        "Antibióticos",
                        "Enrox 50mg",
                        "Enrofloxacino",
                        "Blíster 10 comp.",
                        "Perro / Gato",
                        30,
                        6800
                    ),
                    prod(
                        "ME003",
                        "Antibióticos",
                        "Metrobay 250mg",
                        "Metronidazol",
                        "Blíster 10 comp.",
                        "Perro / Gato",
                        28,
                        3900
                    ),
                    prod(
                        "ME004",
                        "Antiparasitarios",
                        "Nexgard",
                        "Afoxolaner",
                        "Masticable 1 unid.",
                        "Perro",
                        60,
                        9500
                    ),
                    prod(
                        "ME005",
                        "Antiparasitarios",
                        "Bravecto",
                        "Fluralaner",
                        "Masticable 1 unid.",
                        "Perro",
                        40,
                        18900
                    ),
                    prod(
                        "ME006",
                        "Antiparasitarios",
                        "Revolution Plus",
                        "Selamectina+Sarolaner",
                        "Pipeta 1 unid.",
                        "Gato",
                        35,
                        14500
                    ),
                    prod(
                        "ME007",
                        "Antiparasitarios",
                        "Drontal Plus",
                        "Praziquantel+Pamoato",
                        "Comprimido 1 unid.",
                        "Perro",
                        80,
                        3200
                    ),
                    prod(
                        "ME008",
                        "Antiparasitarios",
                        "Milbemax Gato",
                        "Milbemicina+Praziq.",
                        "Comprimido 2 unid.",
                        "Gato",
                        50,
                        6800
                    ),
                    prod(
                        "ME009",
                        "Antiinflamatorios",
                        "Meloxicam 1mg",
                        "Meloxicam",
                        "Blíster 10 comp.",
                        "Perro / Gato",
                        55,
                        4500
                    ),
                    prod(
                        "ME010",
                        "Antiinflamatorios",
                        "Carprofen 50mg",
                        "Carprofeno",
                        "Blíster 10 comp.",
                        "Perro",
                        30,
                        9800
                    ),
                    prod(
                        "ME011",
                        "Dermatología",
                        "Clorhexidina shampoo",
                        "Clorhexidina 2%",
                        "Frasco 250ml",
                        "Perro / Gato",
                        25,
                        8900
                    ),
                    prod(
                        "ME012",
                        "Dermatología",
                        "Malaseb shampoo",
                        "Miconazol+Clorhex.",
                        "Frasco 250ml",
                        "Perro / Gato",
                        20,
                        12500
                    ),
                    prod(
                        "ME013",
                        "Dermatología",
                        "Apoquel 16mg",
                        "Oclacitinib",
                        "Blíster 10 comp.",
                        "Perro",
                        18,
                        22000
                    ),
                    prod(
                        "ME014",
                        "Digestivo",
                        "Probifor",
                        "Bacillus clausii",
                        "Sobre 5ml x10",
                        "Perro / Gato",
                        40,
                        5600
                    ),
                    prod(
                        "ME015",
                        "Digestivo",
                        "Omeprazol 10mg vet",
                        "Omeprazol",
                        "Blíster 10 comp.",
                        "Perro / Gato",
                        35,
                        3800
                    ),
                    prod(
                        "ME016",
                        "Cardíaco",
                        "Vetmedin 2.5mg",
                        "Pimobendan",
                        "Blíster 10 comp.",
                        "Perro",
                        15,
                        28000
                    ),
                    prod(
                        "ME017",
                        "Analgésicos",
                        "Tramadol 50mg vet",
                        "Tramadol",
                        "Blíster 10 comp.",
                        "Perro",
                        22,
                        5200
                    ),
                    prod(
                        "ME018",
                        "Vacunas",
                        "Nobivac DHPPi",
                        "Vacuna polivalente",
                        "Vial 1 dosis",
                        "Perro",
                        48,
                        8500
                    ),
                    prod(
                        "ME019",
                        "Vacunas",
                        "Nobivac Rabies",
                        "Vacuna antirrábica",
                        "Vial 1 dosis",
                        "Perro / Gato",
                        60,
                        5800
                    ),
                    prod(
                        "ME020",
                        "Vacunas",
                        "Felocell CVR",
                        "Vacuna triple felina",
                        "Vial 1 dosis",
                        "Gato",
                        36,
                        7200
                    ),
                    prod(
                        "ME021",
                        "Suplementos",
                        "Omega vet 3-6-9",
                        "Ácidos grasos omega",
                        "Frasco 100ml",
                        "Perro / Gato",
                        30,
                        9900
                    ),
                    prod(
                        "ME022",
                        "Suplementos",
                        "Condrovet forte",
                        "Condroitín+Glucos.",
                        "Blíster 30 comp.",
                        "Perro",
                        25,
                        14500
                    )
                );

        repository.saveAll(productos);
    }

    private Producto prod(
        String cod,
        String cat,
        String nom,
        String pa,
        String pres,
        String esp,
        int stock,
        int precio
    ) {
        return Producto.builder()
                .codigo(cod)
                .categoria(cat)
                .nombreComercial(nom)
                .principioActivo(pa)
                .presentacion(pres)
                .especie(esp)
                .stockActual(stock)
                .precioUnitarioClp(precio)
                .activo(true)
                .build();
    }
}

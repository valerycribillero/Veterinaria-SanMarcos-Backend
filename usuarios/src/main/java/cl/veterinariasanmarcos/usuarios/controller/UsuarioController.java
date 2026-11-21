package cl.veterinariasanmarcos.usuarios.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import cl.veterinariasanmarcos.usuarios.dto.UsuarioListadoDTO;
import cl.veterinariasanmarcos.usuarios.model.Usuario;
import cl.veterinariasanmarcos.usuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping ("/registro")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service){
        this.service = service;
    }

    @Operation(
        summary = "Listar",
        description = "Obtiee todos los usuarios registrados")

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "lista obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")})

    @GetMapping
    public List<UsuarioListadoDTO> listar(){
        return service.listarDTO();
    }


    @Operation(
        summary = "Registrar usuario",
        description = "Permite registrar un nuevo usuario a la web"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente"),
        @ApiResponse(responseCode = "201", description = "Se guardaron los datos correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos ingresados incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

})
    @PostMapping("/agregar")
    public ResponseEntity<Usuario> crearPersona(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = service.guardarUsuario(usuario);
        return ResponseEntity.status(201).body(nuevo);
    }

    @Operation(
        summary = "Buscar usuario por ID",
        description = "Permite buscar un usuario con su id"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos ingresados incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

})
    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioListadoDTO> buscarPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = service.buscarPorId(id);
        if (usuario.isEmpty()) return ResponseEntity.notFound().build();
        Usuario u = usuario.get();
        UsuarioListadoDTO dto = new UsuarioListadoDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        return ResponseEntity.ok(dto);
    }

    @Operation(
        summary = "Eliminar usuario",
        description = "Permite eliminar un usuario de la base de datos"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos ingresados incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

})
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar (@PathVariable Integer id){
        Optional<Usuario> usuario = service.buscarPorId(id);
        if (usuario.isEmpty()) return ResponseEntity.notFound().build();
        service.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Actualizar usuario",
        description = "Permite actulizar datos de un usuario ya registrado"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos actualizados correctamente"),
        @ApiResponse(responseCode = "201", description = "Se guardaron los datos correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos ingresados incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

})
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Optional<Usuario> existente = service.buscarPorId(id);
        if (existente.isEmpty()) return ResponseEntity.notFound().build();
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getEmail() == null || usuario.getEmail().isBlank()
                || usuario.getRol() == null) {
            return ResponseEntity.badRequest().build();
        }
        service.actualizarUsuario(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Listar con DTO",
        description = "Permite listar datos seleccionados sobre los usuarios registrados"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos ingresados incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")

})
    @GetMapping("/listar-dto")
    public List<UsuarioListadoDTO> listarDTO(){
        return service.listarDTO();
    }


}

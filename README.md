# Veterinaria San Marcos Backend

## Iniciar todos los microservicios con Docker

1. Abre Docker Desktop y espera a que el motor esté iniciado.
2. Inicia MySQL en Laragon. Docker usa esa base de datos y conserva sus datos.
3. Detén los microservicios que tengas ejecutándose con Run en VS Code para liberar los puertos 8081 a 8089.
4. Abre la terminal en la carpeta principal de este backend, donde está `compose.yaml`, y ejecuta:

```sh
docker compose up --build -d
```

La primera ejecución descarga las imágenes y compila los nueve microservicios. No necesitas compilar Java manualmente. Cada carpeta tiene un `Dockerfile` junto a su `pom.xml`; `compose.yaml` los inicia juntos.

Para comprobar el estado:

```sh
docker compose ps -a
```

Para ver los mensajes de un microservicio:

```sh
docker compose logs --tail=100 mensajeria-service
```

Para detenerlos todos:

```sh
docker compose down
```

Después de modificar código, ejecuta nuevamente `docker compose up --build -d`.

## Puertos y bases de datos

| Microservicio | Puerto | Base MySQL de Laragon |
|---|---|---|
| usuarios | 8081 | usuarios |
| mensajeria-service | 8082 | mensajeria |
| mascota | 8083 | mascota |
| catalogo | 8084 | producto |
| ficha | 8085 | sanmarcos_ficha_db |
| productos-service | 8086 | productos |
| pedidos-service | 8087 | pedidos |
| agenda-service | 8088 | agenda |
| compras | 8089 | sanmarcos_compras_db |

Si alguna base aún no existe, créala en MySQL de Laragon:

```sql
CREATE DATABASE IF NOT EXISTS usuarios;
CREATE DATABASE IF NOT EXISTS mensajeria;
CREATE DATABASE IF NOT EXISTS mascota;
CREATE DATABASE IF NOT EXISTS producto;
CREATE DATABASE IF NOT EXISTS sanmarcos_ficha_db;
CREATE DATABASE IF NOT EXISTS productos;
CREATE DATABASE IF NOT EXISTS pedidos;
CREATE DATABASE IF NOT EXISTS agenda;
CREATE DATABASE IF NOT EXISTS sanmarcos_compras_db;
```

Esta configuración usa el usuario `root` sin contraseña, como la configuración local del proyecto. Si tu MySQL tiene otra cuenta o contraseña, crea un archivo `.env` junto a `compose.yaml`:

```text
DB_USER=tu_usuario
DB_PASSWORD=tu_clave
```

Ese archivo se ignora en Git. Los contenedores se conectan a Laragon mediante `host.docker.internal`; Pedidos se conecta a Productos mediante `productos-service`. Las direcciones que utiliza React conservan los mismos puertos.

Si aparece `port is already allocated`, otro proceso está usando ese puerto. Si aparece `Access denied` en los logs, MySQL está rechazando la cuenta desde Docker. `docker compose ps` muestra el contenedor iniciado; revisa también sus logs para confirmar que Spring Boot terminó de arrancar.

El frontend se inicia por separado. Esta configuración corresponde a desarrollo local.

## Estructura de cada microservicio

- `controller`: recibe las peticiones HTTP.
- `service`: contiene las reglas del servicio.
- `repository`: accede a MySQL.
- `model`: define las entidades.
- `dto`: contiene los datos de entrada y salida.
- `src/main/resources`: contiene la configuración y las migraciones SQL, cuando el servicio usa Flyway.

Puedes seguir ejecutando los microservicios con Run en VS Code cuando Docker esté detenido. Para las pruebas de controladores, abre el archivo de prueba en `src/test/java` y pulsa Run Test. Usan MockMvc y servicios simulados.

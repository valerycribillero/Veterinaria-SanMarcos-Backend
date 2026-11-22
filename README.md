# Veterinaria San Marcos Backend

## Iniciar todos los microservicios con Docker

1. Abre Docker Desktop y espera a que el motor esté iniciado.
2. Inicia MySQL en Laragon. Docker usa esa base de datos y conserva sus datos.
3. Detén las aplicaciones que tengas ejecutándose con Run en VS Code para liberar los puertos 8080 a 8091 y 8761.
4. Abre la terminal en la carpeta principal de este backend, donde está `compose.yaml`, y ejecuta:

```sh
docker compose up --build -d
```

La primera ejecución descarga las imágenes y compila los once microservicios, el servidor Eureka y el API Gateway. Cada servicio se compila en Docker con el `Dockerfile` ubicado junto a su `pom.xml`. El archivo `compose.yaml` los inicia juntos.

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

### Iniciar en otro computador con MySQL en Docker

Este modo crea un MySQL independiente con las once bases vacías. En un equipo nuevo, abre Docker Desktop y ejecuta desde la raíz del backend:

```sh
docker compose -f compose.yaml -f compose.mysql.yaml up --build -d
```

Si ya tienes el backend de Laragon en ejecución en ese equipo, apágalo antes con `docker compose stop` para liberar los puertos del backend. La opción con MySQL en Docker usa un proyecto separado y crea su propio volumen. Los datos de Laragon todavía se consultan con la conexión habitual.

Para ver MySQL de Docker desde el cliente de Laragon, conéctate a `127.0.0.1:3307` con el usuario `sanmarcos` y la contraseña local `sanmarcos-local`. Puedes definir `DOCKER_MYSQL_PASSWORD` en `.env` antes del primer arranque para elegir otra contraseña.

Consulta las aplicaciones con `docker compose -f compose.yaml -f compose.mysql.yaml ps`. Para apagarlas, usa `docker compose -f compose.yaml -f compose.mysql.yaml down`. El volumen conserva los datos de MySQL entre arranques.

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
| detalleconsultas | 8090 | sanmarcos_detalle_servicios_db |
| servicios | 8091 | sanmarcos_servicios_db |

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
CREATE DATABASE IF NOT EXISTS sanmarcos_detalle_servicios_db;
CREATE DATABASE IF NOT EXISTS sanmarcos_servicios_db;
```

Esta configuración usa el usuario `root` sin contraseña, como la configuración local del proyecto. Si tu MySQL tiene otra cuenta o contraseña, crea un archivo `.env` junto a `compose.yaml`:

```text
DB_USER=tu_usuario
DB_PASSWORD=tu_clave
```

Ese archivo se ignora en Git. Los contenedores se conectan a Laragon mediante `host.docker.internal`; Pedidos se conecta a Productos mediante `productos-service`. Las direcciones que utiliza React conservan los mismos puertos.

Si aparece `port is already allocated`, otro proceso está usando ese puerto. Si aparece `Access denied` en los logs, MySQL está rechazando la cuenta desde Docker. `docker compose ps` muestra el contenedor iniciado; revisa también sus logs para confirmar que Spring Boot terminó de arrancar.

El frontend se inicia por separado. Esta configuración corresponde a desarrollo local.

## Swagger y OpenAPI

Con los microservicios en ejecución, abre `http://localhost:<puerto>/swagger-ui/index.html` con el puerto de la tabla anterior. Por ejemplo, el [Swagger de Productos](http://localhost:8086/swagger-ui/index.html) muestra los endpoints de productos y reservas de stock.

Cada microservicio también publica su especificación OpenAPI en `http://localhost:<puerto>/v3/api-docs`. Puedes importar esa URL en Postman.

## Registro de servicios con Eureka

Con Docker Compose en ejecución, abre el [panel de Eureka](http://localhost:8761/) para ver los microservicios registrados y su estado. Los clientes se registran en `http://eureka-server:8761/eureka/` dentro de la red de Docker. El panel puede tardar unos segundos en mostrar todos los servicios.

Para iniciar solo el registro, ejecuta `docker compose up --build -d eureka-server`. Si ejecutas un microservicio fuera de Docker, el registro está desactivado por defecto. Puedes activarlo con `EUREKA_CLIENT_ENABLED=true`. El cliente usará `http://localhost:8761/eureka/` si no configuras `EUREKA_URL`.

El puerto 8761 corresponde al panel y a la API del registro. El gateway también aparece en el panel como `API-GATEWAY`.

## API Gateway

React y Postman pueden llamar a las API por `http://localhost:8080`. El primer segmento de la ruta indica qué servicio debe buscar el gateway en Eureka. Por ejemplo, `GET http://localhost:8080/productos/api/productos` consulta Productos.

| Servicio | Ejemplo de ruta en el puerto 8080 |
|---|---|
| Usuarios | `POST /usuarios/registro/login` |
| Mensajería | `GET /mensajeria/api/contactos` |
| Mascotas | `GET /mascotas/api/mascotas` |
| Catálogo | `GET /catalogo/productos/listar` |
| Ficha clínica | `GET /ficha/api/fichas` |
| Productos | `GET /productos/api/productos` |
| Pedidos | `GET /pedidos/api/pedidos` |
| Agenda | `GET /agenda/api/horarios` |
| Compras | `GET /compras/api/compras` |
| Detalle de consultas | `GET /detalle-consultas/api/detalle-consultas/proximas-dosis` |
| Servicios veterinarios | `GET /servicios/api/servicios` |

Puedes abrir Swagger en el puerto de cada microservicio o consultar su OpenAPI en el gateway. Por ejemplo, `GET http://localhost:8080/productos/v3/api-docs`.

### Arrancar desde VS Code con MySQL de Laragon

1. Inicia MySQL en Laragon y abre la carpeta raíz del backend en VS Code.
2. Presiona `Ctrl+Shift+D`, selecciona **Eureka Server (8761)** y pulsa el botón verde.
3. Abre el archivo Java que contiene el `main` de un microservicio. Con ese archivo activo, selecciona **Microservicio actual con Eureka** y pulsa el botón verde. Repite con los servicios que vas a usar. Esta opción activa el registro en Eureka para esa ejecución.
4. Selecciona **API Gateway (8080)** y pulsa el botón verde. Cuando aparezca en Eureka, prueba una ruta en el puerto 8080.
5. Abre el frontend en otra ventana de VS Code e inicia React con `npm run dev`.

## Estructura de cada microservicio

- `controller`: recibe las peticiones HTTP.
- `service`: contiene las reglas del servicio.
- `repository`: accede a MySQL.
- `model`: define las entidades.
- `dto`: contiene los datos de entrada y salida.
- `src/main/resources`: contiene la configuración y las migraciones SQL, cuando el servicio usa Flyway.

Puedes seguir ejecutando los microservicios con Run en VS Code cuando Docker esté detenido. Para las pruebas de controladores, abre el archivo de prueba en `src/test/java` y pulsa Run Test. Usan MockMvc y servicios simulados.

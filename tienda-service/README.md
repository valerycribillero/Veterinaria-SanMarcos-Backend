# Microservicio de tienda

Spring Boot 4.0.9, Java 17+, Maven, Spring Data JPA, Flyway y MySQL. Sigue la
estructura del servicio de mensajería del repositorio: controller, service,
repository, model y dto. Usa una base de datos propia y el puerto 8083.

## Iniciar con Laragon y VS Code

1. Inicia MySQL en Laragon.
2. Ejecuta en MySQL:

   ```sql
   CREATE DATABASE tienda CHARACTER SET utf8mb4;
   ```

3. Abre `tienda-service` en VS Code. Necesitas un JDK 17 o superior y las
   extensiones de Java. Abre `TiendaServiceApplication.java` y ejecuta su `main`.
   También puedes usar, desde esta carpeta:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   En Linux/macOS: `./mvnw spring-boot:run`.

4. Abre `http://127.0.0.1:8083/api/productos`. Debes ver los 22 productos del
   catálogo de React, con sus códigos ME001 a ME022. Flyway crea las tablas y
   carga ese catálogo una sola vez; reiniciar el servidor conserva los cambios.

La conexión predeterminada usa MySQL en `127.0.0.1:3306`, usuario `root`, sin
contraseña. Si tu instalación usa otros datos, configura las variables de entorno
`DB_URL`, `DB_USER` y `DB_PASSWORD` antes de ejecutar Java. `PORT` cambia el puerto
HTTP. Ejemplo de URL: `jdbc:mysql://127.0.0.1:3306/tienda`.

No reutilices una base de mensajería: cada servicio tiene su propio historial de
migraciones. Las imágenes siguen siendo rutas del frontend, por ejemplo
`/img/Amoxibay.jpg`; este servicio no aloja ni sube imágenes.

## API de productos

| Método | Ruta | Función |
| --- | --- | --- |
| GET | `/api/productos` | Lista productos activos, ordenados por código. |
| GET | `/api/productos/{codigo}` | Consulta un producto activo. |
| POST | `/api/productos` | Crea un producto. Responde 201. |
| PUT | `/api/productos/{codigo}` | Sustituye los datos, incluido el stock. |
| PATCH | `/api/productos/{codigo}/stock` | Establece el stock disponible. |
| DELETE | `/api/productos/{codigo}` | Desactiva el producto. Responde 204. |

Ejemplo de POST o PUT:

```json
{
  "codigo": "ME023",
  "nombre": "Producto de prueba",
  "categoria": "Accesorios",
  "precio": 3500,
  "stock": 10,
  "imagen": "/img/Amoxibay.jpg",
  "principioActivo": "",
  "presentacion": "Unidad",
  "especie": "Perro / Gato"
}
```

`codigo` es único e inmutable, con letras mayúsculas, números, guion o guion bajo,
hasta 30 caracteres. El precio es positivo y se expresa en pesos chilenos enteros.
El stock es un entero de 0 a 1.000.000. Los campos `principioActivo`, `presentacion`
y `especie` pueden ser cadenas vacías. PATCH recibe `{"stock": 15}`: establece
15 unidades disponibles, no añade 15. Un código desactivado no se reutiliza.

## API de pedidos

| Método | Ruta | Función |
| --- | --- | --- |
| POST | `/api/pedidos` | Crea el pedido y descuenta stock. Responde 201. |
| GET | `/api/pedidos` | Lista pedidos del más reciente al más antiguo. |
| GET | `/api/pedidos/{id}` | Consulta un pedido con sus detalles. |
| PATCH | `/api/pedidos/{id}/estado` | Cambia el estado del pedido. |

Ejemplo de POST:

```json
{
  "nombreCompleto": "Cliente de prueba",
  "correo": "cliente@example.com",
  "direccion": "Av. Brasil 2140, Rancagua",
  "metodo": "Transferencia",
  "productos": [
    {
      "codigo": "ME001",
      "cantidad": 2
    },
    {
      "codigo": "ME004",
      "cantidad": 1
    }
  ]
}
```

El total de este ejemplo con el catálogo original es $17.900. Envía únicamente
código y cantidad por producto. El servidor obtiene los precios de MySQL y calcula
cada subtotal y el total. Rechaza códigos repetidos, cantidades no positivas,
productos inexistentes o desactivados, y stock insuficiente. Si falla una línea,
se revierte toda la operación y no se crea el pedido ni se descuenta stock.
Las compras simultáneas bloquean los productos para no vender una misma unidad
dos veces. Los detalles guardan el nombre y precio del momento de la compra.

`metodo` admite `Debito`, `Credito` o `Transferencia`, como tu formulario actual.
Es un dato del pedido: el servicio no procesa cobros ni confirma pagos bancarios.

Para el estado, envía por ejemplo `{"estado":"ENVIADO"}`:

- `CONFIRMADO` → `ENVIADO` → `ENTREGADO`.
- `CONFIRMADO` → `CANCELADO`: devuelve las existencias de todas sus líneas.
- Repetir el estado actual es seguro y no devuelve stock dos veces.
- No se puede cancelar un pedido enviado o entregado, ni reabrir uno cancelado.

Los errores devuelven JSON con `mensaje`: 400 por datos inválidos, 404 si no existe
el recurso y 409 por conflictos como stock insuficiente o cambios de estado
inválidos. Los errores de validación añaden una lista `errores`. Se rechazan campos
JSON desconocidos, para detectar contratos de frontend incorrectos.

## Probar con Postman

Importa `Tienda.postman_collection.json`. `baseUrl` apunta a
`http://127.0.0.1:8083`. La petición «Crear pedido» guarda automáticamente el ID
para las siguientes peticiones. Para probar el flujo de envío y entrega, crea un
pedido nuevo: un pedido cancelado no puede enviarse.

## Conectar React

El servicio ya permite CORS desde `http://localhost:5173` y
`http://127.0.0.1:5173`. Los campos de GET productos coinciden con la interfaz
`Producto` de React. El frontend todavía usa sus datos locales: arrancar este
servicio por sí solo no cambia React. Los puntos de integración son:

1. `src/paginas/Productos.tsx`: cargar `/api/productos` en lugar del arreglo fijo.
2. `src/paginas/ProductosInventario.tsx`: usar POST, PUT, PATCH y DELETE en lugar
   de guardar inventario en `localStorage`.
3. `src/paginas/Pago.tsx`: enviar POST `/api/pedidos`, usando `nombreCompleto`,
   `correo`, `direccion`, `metodo` y `productos: carrito.map(p => ({codigo:
   p.codigo, cantidad: p.cantidad}))`. Vaciar el carrito solo tras recibir 201.
   Mostrar el total devuelto por el servidor y confirmar el pedido, sin afirmar
   que se ha cobrado. Conservar los datos del formulario cuando hay un error.
4. Los listados de pedidos de administración pueden consultar `/api/pedidos`.
   El historial privado de cada cliente requiere integrar la identidad del
   servicio de usuarios antes de habilitarlo; no uses un correo enviado por el
   navegador como autorización.

Usa una variable distinta de mensajería: `VITE_TIENDA_API_URL=http://127.0.0.1:8083`
y reinicia Vite al cambiarla. Ejemplo de petición desde React:

```ts
const base = import.meta.env.VITE_TIENDA_API_URL || 'http://127.0.0.1:8083';
const respuesta = await fetch(`${base}/api/productos`);
if (!respuesta.ok) throw new Error('No se pudo cargar la tienda');
const productos = await respuesta.json();
```

Esta primera versión funciona localmente, igual que el servicio de mensajería.
El servidor escucha en 127.0.0.1 y todavía no integra autenticación ni roles;
las operaciones de administración y los datos de pedidos requieren esa integración
antes de publicar el servicio. El stock del carrito es orientativo: el backend
vuelve a comprobarlo al confirmar el pedido.

## Pruebas y compilación

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

En Linux/macOS usa `./mvnw`. Las pruebas levantan la API HTTP real con H2 en modo
MySQL y ejecutan las migraciones. Cubren catálogo, validaciones, total calculado,
rollback de stock, estados, cancelación, historial de precios, concurrencia y
CORS. No necesitan Laragon. La comprobación final con tu MySQL local se realiza
siguiendo los pasos de inicio y las peticiones de Postman.

# 🐾 Veterinary Management System — Backend

API REST para la gestión integral de una clínica veterinaria. Desarrollada como trabajo final para **Programación III (UTN)**, con Spring Boot, MySQL, Spring Security + JWT y documentación OpenAPI.

Permite administrar propietarios, mascotas, veterinarios, recepcionistas, sucursales, turnos, visitas clínicas, diagnósticos, estudios médicos, medicamentos y prescripciones. Incluye autenticación con JWT, refresh tokens y control de acceso por roles y permisos.

> Este repositorio contiene únicamente el **backend**. La configuración CORS permite peticiones desde un frontend en `http://localhost:4200`.

---

## 🛠 Stack tecnológico

| Tecnología | Versión / Notas |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Web MVC | Gestionado por Spring Boot (`spring-boot-starter-webmvc`) |
| Spring Data JPA / Hibernate | Gestionado por Spring Boot |
| Spring Security | Gestionado por Spring Boot |
| Jakarta Validation | Gestionado por Spring Boot |
| MySQL Connector/J | Gestionado por Spring Boot |
| JJWT (JWT) | 0.13.0 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) |
| Springdoc OpenAPI / Swagger UI | 3.0.2 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.38 |
| Maven | Maven Wrapper incluido (`mvnw` / `mvnw.cmd`) |
| Base de datos | MySQL 8+ |

> Las versiones de Spring Data JPA, Spring Security y Validation no son `4.0.6`: las administra el BOM de Spring Boot. Solo se fija explícitamente lo declarado en `pom.xml`.

---

## 📦 Módulos funcionales

La API está organizada por *features*. Todas las rutas requieren autenticación salvo `/api/auth/**` y las rutas de Swagger.

| Módulo | Endpoint base | Descripción |
|---|---|---|
| Autenticación | `/api/auth` | Registro, login y refresh token |
| Usuarios | `/api/users` | Consulta y baja de usuarios |
| Propietarios | `/api/owners` | CRUD y búsqueda por DNI |
| Mascotas | `/api/pets` | CRUD, mascotas del usuario autenticado e historial clínico |
| Tipos de animal | `/api/animal-types` | ABM de tipos de animal |
| Razas | `/api/breeds` | ABM de razas |
| Sucursales | `/api/branches` | CRUD con horarios de atención y dirección |
| Veterinarios | `/api/veterinarians` | CRUD con licencia, horarios y días de trabajo |
| Recepcionistas | `/api/receptionists` | CRUD de recepcionistas por sucursal |
| Turnos | `/api/appointments` | CRUD, confirmación y consulta de horarios disponibles |
| Visitas clínicas | `/api/visits` | Visitas desde turno confirmado, *walk-in* e historial |
| Diagnósticos | `/api/diagnoses` | CRUD de diagnósticos asociados a una visita |
| Estudios | `/api/studies` | ABM del catálogo de estudios |
| Estudios por visita | `/api/studies-by-visits` | Asociación estudio ↔ visita |
| Estudios por diagnóstico | `/api/diagnosis-by-studies` | Asociación estudio ↔ diagnóstico |
| Condiciones | `/api/conditions` | ABM de condiciones clínicas |
| Condiciones por mascota | `/api/conditions-by-pets` | Asociación condición ↔ mascota |
| Medicamentos | `/api/medications` | CRUD y búsqueda por nombre |
| Prescripciones | `/api/prescriptions` | Alta, consulta y baja de prescripciones |
| Propietario–Mascota | `/api/owner-by-pet` | Asociación propietario ↔ mascota |

> Los recursos se identifican en las URLs por un **`externalId` (UUID)**, no por el `id` numérico interno.

---

## 🏗 Arquitectura

- **Organización por feature**: cada módulo agrupa `controller`, `service`, `repository`, `mapper`, `model` y `dto`.
- **DTOs + MapStruct** para separar las entidades JPA de los contratos de entrada/salida.
- **Spring Security stateless** con filtro JWT (`JwtAuthenticationFilter`) y autorización por permisos con `@PreAuthorize`.
- **Manejo global de errores** con `@RestControllerAdvice` y una respuesta uniforme.
- **Auditoría de identidad pública**: cada entidad expone un `externalId` UUID generado en `@PrePersist`.

---

## ⚙️ Requisitos previos

- **Java 21+** (`java -version`)
- **MySQL 8+** en ejecución
- **Git**
- IDE compatible con Maven (IntelliJ IDEA recomendado) — opcional

No es necesario instalar Maven: el proyecto incluye el wrapper.

---

## 🚀 Puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/technofriki/Veterinary.git
cd Veterinary
```

### 2. Crear la base de datos

```sql
CREATE DATABASE veterinary
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar variables de entorno

La aplicación arranca con valores por defecto pensados para **desarrollo local**, pero se recomienda definir estas variables:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `BD_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/veterinary` |
| `BD_USER` | Usuario de MySQL | `root` |
| `BD_PASS` | Contraseña de MySQL | `password` |
| `JWT_SECRET` | Clave secreta para firmar los JWT | Clave de desarrollo incluida en `application.yaml` |
| `JWT_EXPIRATION` | Expiración del access token (ms) | `900000` (15 minutos) |
| `JWT_REFRESH_EXPIRATION` | Expiración del refresh token (ms) | `86400000` (24 horas) |
| `ADMIN_USERNAME` | Usuario administrador inicial | `admin` |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial | `admin123` |
| `ADMIN_EMAIL` | Email del administrador inicial | `admin@veterinary.com` |

Ejemplo en **PowerShell**:

```powershell
$env:BD_URL="jdbc:mysql://localhost:3306/veterinary"
$env:BD_USER="root"
$env:BD_PASS="tu_contraseña"
$env:JWT_SECRET="una_clave_larga_y_segura_de_al_menos_32_caracteres"
```

En **IntelliJ IDEA**: `Run → Edit Configurations… → Environment variables`.

> ⚠️ Los valores por defecto de `JWT_SECRET`, `ADMIN_PASSWORD` y `BD_PASS` son solo para desarrollo. No los uses en producción.

### 4. Ejecutar la aplicación

**Con el wrapper (recomendado):**

```powershell
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Git Bash / macOS / Linux
./mvnw spring-boot:run
```

**Compilando y ejecutando el JAR:**

```bash
./mvnw clean package
java -jar target/veterinary-0.0.1-SNAPSHOT.jar
```

La aplicación queda disponible en `http://localhost:8080`.

Al iniciar, `SecurityDataLoader` crea automáticamente:

- todos los **permisos** y **roles** del sistema;
- el **usuario administrador** (`ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL`) si no existe.

---

## 🔐 Autenticación

La API usa **JWT Bearer**. El access token se envía en el header:

```http
Authorization: Bearer <accessToken>
```

### 1. Registro de cliente

Crea un `User`, sus `Credentials` con rol `ROLE_CLIENT`, una dirección y un `Owner`.

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "1123456789",
  "email": "juan@email.com",
  "dni": "30123456",
  "address": {
    "street": "Av. Siempre Viva",
    "number": "742",
    "city": "Buenos Aires",
    "province": "CABA",
    "country": "Argentina"
  },
  "password": "contraseñaSegura"
}
```

**Respuesta:** `200 OK` con el texto `User registered successfully`.

> Para los clientes registrados, el **`username` de login es el email**.

### 2. Iniciar sesión

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "username": "juan@email.com",
  "password": "contraseñaSegura"
}
```

**Respuesta:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Refrescar el token

```http
POST /api/auth/refresh
Content-Type: application/json
```

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Respuesta:** un nuevo par `accessToken` / `refreshToken`. El refresh token anterior se invalida (rotación).

### 4. Usar el token

```http
GET /api/pets
Authorization: Bearer <accessToken>
```

En Swagger UI se pueden explorar los endpoints, pero el esquema de seguridad Bearer no está declarado explícitamente en la configuración de OpenAPI. Para probar endpoints protegidos, usá **Postman, cURL** o el cliente HTTP de tu IDE enviando el header `Authorization`.

---

## 👥 Roles y permisos

Los roles se crean al iniciar la aplicación:

| Rol | Uso |
|---|---|
| `ROLE_ADMIN` | Administración general (todos los permisos excepto crear registros clínicos) |
| `ROLE_VETERINARIAN` | Diagnósticos, prescripciones y registros clínicos |
| `ROLE_RECEPTIONIST` | Gestión de propietarios, mascotas, turnos y consulta clínica |
| `ROLE_CLIENT` | Mascotas, turnos y consulta de historial clínico |

Los permisos se declaran en `Permits` y se aplican con `@PreAuthorize` (`hasAuthority(...)`, `hasRole(...)` o `hasAnyRole(...)`) en los controllers.

---

## 📋 Reglas de negocio implementadas

### 📅 Turnos

- No se permiten turnos en fechas/horas pasadas.
- El turno debe estar dentro del horario de atención de la sucursal (último inicio: 30 minutos antes del cierre).
- Solo se permiten inicios en **hora exacta o y media**.
- El turno debe estar dentro del horario laboral del veterinario (último inicio: 30 minutos antes de su fin de jornada).
- No se permiten superposiciones para un mismo veterinario ni para una misma mascota, considerando turnos `PENDING` y `CONFIRMED`.
- El turno se crea en estado `PENDING` y se confirma con `PATCH /api/appointments/{externalId}/confirm`.
- `DELETE /api/appointments/{externalId}` **cancela** el turno (no lo borra físicamente). No se pueden cancelar turnos `COMPLETED` ni ya `CANCELLED`.
- No se puede pasar directamente a `CANCELLED` o `COMPLETED` por `PUT`: la finalización se hace registrando la visita clínica.
- `GET /api/appointments/available` devuelve los horarios libres de 30 minutos para un veterinario, sucursal y fecha.

### 🩺 Visitas clínicas

- Una visita desde turno solo puede crearse si el turno está `CONFIRMED`.
- Cada turno admite **una sola visita**.
- Al registrar la visita, el turno pasa a `COMPLETED`.
- La mascota de la visita se toma del turno.
- Las visitas *walk-in* (`POST /api/visits/walk-in`) no requieren turno.
- El historial se consulta con `GET /api/pets/{externalId}/history` o `GET /api/visits/medical-history/{petExternalId}`.

### 👤 Propietarios y usuarios

- El **DNI** del propietario debe ser único.
- El **email** del usuario debe ser único.
- No se permite asociar dos veces el mismo propietario a la misma mascota.

### 🏢 Sucursales

- El **nombre**, **teléfono** y **email** de la sucursal deben ser únicos.

### 🩺 Veterinarios

- La **licencia**, el **email** y el **teléfono** deben ser únicos.
- La baja de un veterinario es **lógica**: `DELETE` marca `active = false` y no permite darlo de baja dos veces.
- Debe tener horario de trabajo configurado para poder asignarle turnos.

### 🔬 Estudios, condiciones y prescripciones

- No se permite asociar dos veces el mismo estudio a una visita.
- No se permite asociar dos veces el mismo estudio a un diagnóstico.
- No se permite asociar dos veces la misma condición a una mascota.
- No se permite duplicar un medicamento con el mismo nombre, dosis y unidad (sin distinguir mayúsculas/minúsculas).

---

## 📚 Documentación de la API (Swagger)

Con la aplicación en ejecución:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## 🗂 Estructura del proyecto

```
Veterinary/
├── src/
│   ├── main/
│   │   ├── java/com/mokah/veterinary/
│   │   │   ├── VeterinaryApplication.java
│   │   │   ├── common/
│   │   │   │   ├── dto/                 # ApiErrorResponse
│   │   │   │   └── exception/           # Excepciones y GlobalExceptionHandler
│   │   │   ├── features/
│   │   │   │   ├── adresses/            # Direcciones (nombre histórico del paquete)
│   │   │   │   ├── animaltypes/         # Tipos de animal
│   │   │   │   ├── appointments/        # Turnos
│   │   │   │   ├── branches/            # Sucursales
│   │   │   │   ├── breed/               # Razas
│   │   │   │   ├── conditions/          # Condiciones clínicas
│   │   │   │   ├── conditionsbypet/     # Condiciones por mascota
│   │   │   │   ├── diagnosis/           # Diagnósticos
│   │   │   │   ├── diagnosisbystudies/  # Estudios por diagnóstico
│   │   │   │   ├── medication/          # Medicamentos
│   │   │   │   ├── owners/              # Propietarios
│   │   │   │   ├── ownersbypets/        # Propietario ↔ mascota
│   │   │   │   ├── pets/                # Mascotas
│   │   │   │   ├── prescriptions/       # Prescripciones
│   │   │   │   ├── receptionists/       # Recepcionistas
│   │   │   │   ├── studies/             # Catálogo de estudios
│   │   │   │   ├── studiesbyvisit/      # Estudios por visita
│   │   │   │   ├── users/               # Usuarios
│   │   │   │   ├── veterinarians/       # Veterinarios
│   │   │   │   └── visits/              # Visitas clínicas
│   │   │   └── security/
│   │   │       ├── config/              # Spring Security, CORS y carga inicial
│   │   │       ├── controller/          # AuthController
│   │   │       ├── dto/                 # DTOs de autenticación
│   │   │       ├── enums/               # Roles y permisos
│   │   │       ├── filter/              # JwtAuthenticationFilter
│   │   │       ├── model/               # Credentials, Role, Permit
│   │   │       ├── repository/
│   │   │       └── service/             # AuthService, JwtService, UserDetailsService
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/com/mokah/veterinary/VeterinaryApplicationTests.java
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## 🧯 Manejo de errores

Todas las excepciones se traducen a una respuesta uniforme:

```json
{
  "timestamp": "2026-01-01T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Pet already has an overlapping appointment."
}
```

| Código | Situación |
|---|---|
| `400` | Body inválido o error de validación |
| `401` | Credenciales inválidas o token inválido/expirado |
| `403` | Permisos insuficientes |
| `404` | Recurso no encontrado |
| `409` | Regla de negocio o duplicado |
| `500` | Error no controlado |

---

## ✅ Tests

Actualmente el proyecto incluye únicamente el test de contexto de Spring Boot:

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

---

## 🐛 Troubleshooting

### Error de conexión a la base de datos

- Verificá que MySQL esté ejecutándose y que la base `veterinary` exista.
- Revisá `BD_URL`, `BD_USER` y `BD_PASS`.
- Si no definís `BD_PASS`, se usa `password` por defecto.

### Error de compilación

- Confirmá que estás usando **Java 21**.
- Ejecutá `.\mvnw.cmd clean install` (Windows) o `./mvnw clean install`.

### Error de autenticación JWT

- Verificá que `JWT_SECRET` tenga al menos 32 caracteres para HMAC.
- El access token expira por defecto a los **15 minutos**; usá `/api/auth/refresh` para renovarlo.
- Si el refresh token no coincide con el último emitido, volvé a iniciar sesión.

### Error `401` al probar desde Swagger

- Swagger UI no tiene declarado el esquema Bearer en este proyecto. Probá con Postman, cURL o el cliente HTTP del IDE enviando `Authorization: Bearer <token>`.

---

## 📝 Notas

- `spring.jpa.hibernate.ddl-auto` está en `update`: Hibernate crea/actualiza las tablas automáticamente. No se usan migraciones (Flyway/Liquibase).
- `spring.jpa.show-sql` está en `true` para desarrollo.
- CORS habilitado para `http://localhost:4200`.
- El identificador público de los recursos es `externalId` (UUID); el `id` numérico no se expone en las URLs.
- No se deben commitear credenciales reales: usá siempre variables de entorno.

---

## 📄 Licencia y autoría

Proyecto desarrollado como trabajo final para la cátedra **Programación III (UTN)**.

**Autor:** technofriki  
**Repositorio:** https://github.com/technofriki/Veterinary

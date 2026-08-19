# 🐾 Veterinary Management System

Sistema de gestión veterinaria desarrollado como trabajo final para Programación III (UTN), utilizando Spring Boot, MySQL y arquitectura REST.

La aplicación permite gestionar propietarios, mascotas, veterinarios, sucursales, turnos, visitas clínicas, diagnósticos, estudios médicos, medicamentos y prescripciones. Además, incorpora autenticación JWT y control de acceso basado en roles y permisos.

## 🛠 Tecnologías Utilizadas

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | 4.0.6 |
| Spring Security | 4.0.6 |
| Hibernate ORM | - |
| MySQL Connector | 8.x |
| JWT (jjwt) | 0.13.0 |
| Swagger / OpenAPI | 3.0.2 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.38 |
| Jakarta Validation | - |
| Maven | - |

## ✨ Funcionalidades Principales

### 🐾 Gestión de Mascotas
- Registro de propietarios con validación de DNI único
- Registro de mascotas
- Asociación propietario ↔ mascota
- Gestión de tipos de animales
- Gestión de razas

### 📅 Gestión de Turnos
- Creación de turnos con validación de horarios
- Confirmación de turnos
- Cancelación de turnos
- Validación de horarios de atención de sucursales
- Prevención de superposición de turnos
- Asignación de veterinarios y sucursales

### 🏥 Gestión Clínica
- Registro de visitas veterinarias
- Historial médico de mascotas
- Registro de observaciones clínicas
- Gestión de diagnósticos

### 🔬 Estudios Diagnósticos
- Catálogo de estudios
- Asociación de estudios a visitas clínicas
- Asociación de estudios a diagnósticos
- Registro de conclusiones diagnósticas

### 🔐 Seguridad
- Autenticación mediante JWT
- Refresh Token
- Roles y permisos
- Protección de endpoints mediante Spring Security

## ⚙️ Configuración del Entorno

### Requisitos Previos

- **Java 21+** instalado
- **MySQL 8+** instalado y ejecutándose
- **IntelliJ IDEA** (recomendado) o cualquier IDE compatible con Maven
- **Git** para clonar el repositorio

### Clonar el Proyecto

```bash
git clone https://github.com/technofriki/Veterinary.git
cd Veterinary
```

O desde IntelliJ IDEA:
1. **File → New → Project from Version Control**
2. Pegar el URL: `https://github.com/technofriki/Veterinary.git`
3. Elegir directorio y clonar

### Configurar Base de Datos

1. En MySQL Workbench o terminal, crear la base de datos:

```sql
CREATE DATABASE veterinary;
```

2. Configurar las siguientes variables de entorno en tu IDE o sistema:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `BD_URL` | URL de conexión a MySQL | `jdbc:mysql://localhost:3306/veterinary` |
| `BD_USER` | Usuario de MySQL | `root` |
| `BD_PASS` | Contraseña de MySQL | `tu_contraseña` |
| `JWT_SECRET` | Clave secreta para JWT | `tu_clave_secreta_muy_larga_y_segura` |
| `JWT_EXPIRATION` | Expiración del token (ms) | `3600000` (1 hora, opcional) |
| `JWT_REFRESH_EXPIRATION` | Expiración refresh token (ms) | `86400000` (24 horas, opcional) |
| `ADMIN_USERNAME` | Usuario admin inicial | `admin` |
| `ADMIN_PASSWORD` | Contraseña admin inicial | `admin123` |
| `ADMIN_EMAIL` | Email del admin inicial | `admin@veterinary.com` |

**En IntelliJ IDEA:**
- **Run → Edit Configurations...**
- En **Environment variables**, agregar las variables mencionadas arriba

## 📋 Reglas de Negocio Implementadas

### 📅 Turnos
- No se pueden crear turnos en fechas pasadas
- No se permiten turnos fuera del horario de atención de la sucursal
- No se permiten turnos superpuestos para una misma mascota
- No se permiten turnos superpuestos para un mismo veterinario
- Una visita clínica solo puede registrarse sobre un turno confirmado

### 👤 Propietarios
- El DNI debe ser único
- No se permite asociar dos veces el mismo propietario a la misma mascota

### 🏢 Sucursales
- El nombre de la sucursal debe ser único

### 🔬 Estudios
- No se permite asociar dos veces el mismo estudio a una visita
- No se permite asociar dos veces el mismo estudio a un diagnóstico

## 🔄 Flujo de Trabajo

### Ramas

- **develop**: Rama de desarrollo principal
- **master**: Rama estable para releases

### Comandos Git

**Antes de comenzar a trabajar:**
```bash
git checkout develop
git pull origin develop
```

**Después de hacer cambios:**
```bash
git add .
git commit -m "descripción del cambio"
git push origin develop
```

Desde IntelliJ IDEA puedes usar la pestaña **Git** para realizar estas operaciones de forma visual.

## 📁 Estructura del Proyecto

```
Veterinary/
├── src/
│   ├── main/
│   │   ├── java/com/mokah/veterinary/
│   │   │   ├── features/           # Módulos funcionales
│   │   │   │   ├── adresses/       # Gestión de direcciones
│   │   │   │   ├── appointments/   # Gestión de turnos
│   │   │   │   ├── diagnoses/      # Gestión de diagnósticos
│   │   │   │   ├── medical_studies/# Gestión de estudios médicos
│   │   │   │   ├── owners/         # Gestión de propietarios
│   │   │   │   ├── pets/           # Gestión de mascotas
│   │   │   │   ├── branches/       # Gestión de sucursales
│   │   │   │   ├── users/          # Gestión de usuarios
│   │   │   │   └── vets/           # Gestión de veterinarios
│   │   │   ├── security/           # Seguridad JWT y autorización
│   │   │   │   ├── config/         # Configuración de Spring Security
│   │   │   │   ├── dto/            # DTOs de autenticación
│   │   │   │   ├── filter/         # Filtros JWT
│   │   │   │   └── service/        # Servicios de autenticación
│   │   │   ├── common/             # Excepciones y componentes compartidos
│   │   │   └── VeterinaryApplication.java
│   │   └── resources/
│   │       └── application.yaml    # Configuración de Spring Boot
│   └── test/                       # Tests unitarios e integración
├── pom.xml                         # Dependencias de Maven
└── README.md
```

## 📚 Documentación Swagger

Una vez iniciada la aplicación, accede a la documentación interactiva de la API:

```
http://localhost:8080/swagger-ui/index.html
```

Swagger/OpenAPI permite:
- Visualizar todos los endpoints disponibles
- Probar los endpoints directamente desde el navegador
- Ver los modelos de datos y esquemas
- Documentación de parámetros y respuestas

## 🚀 Ejecutar la Aplicación

### Opción 1: Desde IntelliJ IDEA

1. Configurar las variables de entorno mencionadas arriba
2. Ejecutar la clase `VeterinaryApplication.java`
3. La aplicación iniciará en `http://localhost:8080`

### Opción 2: Desde terminal con Maven

```bash
# Configurar variables de entorno en tu sistema
# Luego ejecutar:
./mvnw spring-boot:run
```

### Opción 3: Compilar y ejecutar JAR

```bash
./mvnw clean package
java -jar target/veterinary-0.0.1-SNAPSHOT.jar
```

## 🔑 Autenticación

La aplicación utiliza JWT (JSON Web Tokens) para la autenticación.

### 1. Registrar un usuario

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "password": "contraseña",
  "email": "usuario@email.com"
}
```

### 2. Iniciar sesión

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "contraseña"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 3. Usar el token

En Swagger:
1. Haz clic en el botón **Authorize** 🔒
2. Ingresa: `Bearer <tu_token>`
3. Haz clic en **Authorize**

En Postman o cURL:
```http
Authorization: Bearer <tu_token>
```

### 4. Refrescar el token

```http
POST /api/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## ⚠️ Notas Importantes

- **No modificar** `application.yaml` con credenciales personales. Usa variables de entorno.
- La aplicación genera y actualiza automáticamente las tablas mediante Hibernate (`ddl-auto: update`).
- Swagger está habilitado para facilitar las pruebas de la API.
- El acceso a los endpoints se encuentra protegido mediante JWT y permisos por rol.
- El usuario admin se crea automáticamente al iniciar la aplicación con las credenciales configuradas en las variables de entorno.

## 🐛 Troubleshooting

### Error de conexión a la base de datos
- Verifica que MySQL esté ejecutándose
- Confirma que la base de datos `veterinary` existe
- Revisa que las variables de entorno `BD_URL`, `BD_USER` y `BD_PASS` sean correctas

### Error de compilación
- Asegúrate de tener Java 21 instalado
- Ejecuta `./mvnw clean install` para limpiar y reconstruir

### Error de autenticación JWT
- Verifica que `JWT_SECRET` esté configurado como variable de entorno
- Asegúrate de que el token no haya expirado (1 hora por defecto)

## 📝 Licencia

Este proyecto fue desarrollado como trabajo final para la cátedra de Programación III (UTN).

---

**Autor:** technofriki  
**Repositorio:** https://github.com/technofriki/Veterinary

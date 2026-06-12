# Veterinary Management System

Sistema de gestión veterinaria desarrollado como trabajo final para Programación III (UTN), utilizando Spring Boot, MySQL y arquitectura REST.

La aplicación permite gestionar propietarios, mascotas, veterinarios, sucursales, turnos, visitas clínicas, diagnósticos, estudios médicos, medicamentos y prescripciones. Además, incorpora autenticación JWT y control de acceso basado en roles y permisos.

## Tecnologías Utilizadas

* Java 21
* Spring Boot 4
* Spring Data JPA
* Hibernate ORM
* MySQL 8
* Spring Security
* JWT Authentication
* Swagger / OpenAPI
* MapStruct
* Lombok
* Jakarta Validation
* Maven

## Funcionalidades Principales

### Gestión de Mascotas

* Registro de propietarios
* Registro de mascotas
* Asociación propietario ↔ mascota
* Gestión de tipos de animales
* Gestión de razas

### Gestión de Turnos

* Creación de turnos
* Confirmación de turnos
* Cancelación de turnos
* Validación de horarios
* Prevención de superposición de turnos
* Asignación de veterinarios y sucursales

### Gestión Clínica

* Registro de visitas veterinarias
* Historial médico de mascotas
* Registro de observaciones clínicas
* Gestión de diagnósticos

### Estudios Diagnósticos

* Catálogo de estudios
* Asociación de estudios a visitas clínicas
* Asociación de estudios a diagnósticos
* Registro de conclusiones diagnósticas

### Seguridad

* Autenticación mediante JWT
* Refresh Token
* Roles y permisos
* Protección de endpoints mediante Spring Security

## Configuración del Entorno

### Requisitos

* IntelliJ IDEA
* MySQL Workbench local
* Java 21+

### Clonar el Proyecto

1. Abrir IntelliJ IDEA
2. **File → New → Project from Version Control**
3. Pegar el URL: `https://github.com/technofriki/Veterinary.git`
4. Elegir directorio y clonar

### Configurar Base de Datos

1. En MySQL Workbench, crear la base de datos:

```sql
CREATE DATABASE veterinary;
```

2. Configurar variables de entorno en IntelliJ:

* **Run → Edit Configurations...**
* En **Environment variables**, agregar:

```text
BD_URL=jdbc:mysql://localhost:3306/veterinary
BD_USER=root
BD_PASS=tu_contraseña_de_mysql
```

## Reglas de Negocio Implementadas

### Turnos

* No se pueden crear turnos en fechas pasadas.
* No se permiten turnos fuera del horario de atención de la sucursal.
* No se permiten turnos superpuestos para una misma mascota.
* No se permiten turnos superpuestos para un mismo veterinario.
* Una visita clínica solo puede registrarse sobre un turno confirmado.

### Propietarios

* El DNI debe ser único.
* No se permite asociar dos veces el mismo propietario a la misma mascota.

### Sucursales

* El nombre de la sucursal debe ser único.

### Estudios

* No se permite asociar dos veces el mismo estudio a una visita.
* No se permite asociar dos veces el mismo estudio a un diagnóstico.

## Flujo de Trabajo

### Rama Principal

* **develop**: Rama de desarrollo
* **master**: Rama estable para releases

### Comandos Git (desde IntelliJ Git tab)

**Antes de trabajar:**

* Pull para descargar cambios del repositorio

**Después de hacer cambios:**

* Commit → Push para subir cambios

### O usando terminal:

```bash
git checkout develop
git pull origin develop

git add .
git commit -m "descripción del cambio"
git push origin develop
```

## Estructura del Proyecto

* `src/main/java/com/mokah/veterinary/features/`: Módulos funcionales
* `src/main/java/com/mokah/veterinary/security/`: Seguridad JWT y autorización
* `src/main/java/com/mokah/veterinary/common/`: Excepciones y componentes compartidos
* `src/main/resources/application.yaml`: Configuración de Spring Boot
* `src/test/`: Tests unitarios e integración

## Documentación Swagger

Una vez iniciada la aplicación:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger permite visualizar y probar todos los endpoints de la API.

## Ejecutar la Aplicación

1. Configurar variables de entorno
2. Ejecutar `VeterinaryApplication.java`
3. Acceder a:

```text
http://localhost:8080
```

## Autenticación

1. Realizar login:

```http
POST /api/auth/login
```

2. Obtener el token JWT.

3. Utilizar el token en Swagger o Postman:

```text
Authorization: Bearer <token>
```

## Notas Importantes

* No modificar `application.yaml` con credenciales personales.
* Utilizar variables de entorno para la conexión a la base de datos.
* Swagger está habilitado para facilitar las pruebas de la API.
* El acceso a los endpoints se encuentra protegido mediante JWT y permisos por rol.
* La aplicación genera y actualiza automáticamente las tablas mediante Hibernate.

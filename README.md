# Veterinary Management System

Sistema de gestión veterinaria desarrollado con Spring Boot y MySQL.

## Configuración del Entorno

### Requisitos
- IntelliJ IDEA
- MySQL Workbench local
- Java 21+

### Clonar el Proyecto

1. Abrir IntelliJ IDEA
2. **File → New → Project from Version Control**
3. Pegar el URL: `https://github.com/technofriki/Veterinary.git`
4. Elegir directorio y clonar

### Configurar Base de Datos

1. En MySQL Workbench, crear la base de datos:
   ```sql
   CREATE DATABASE veterinaria_db;
   ```

2. Configurar variables de entorno en IntelliJ:
   - **Run → Edit Configurations...**
   - En **Environment variables**, agregar:
     - `BD_URL` = `jdbc:mysql://localhost:3306/veterinaria_db`
     - `BD_USER` = `root` (o tu usuario de MySQL)
     - `BD_PASS` = `tu_contraseña_de_mysql`

## Flujo de Trabajo

### Rama Principal
- **develop**: Rama de desarrollo (todos los commits van aquí)
- **master**: Rama estable (solo para releases)

### Comandos Git (desde IntelliJ Git tab)

**Antes de trabajar:**
- Pull para descargar cambios del compañero

**Después de hacer cambios:**
- Commit → Push para subir cambios

### O usando terminal:
```bash
# Cambiar a rama develop
git checkout develop

# Actualizar antes de trabajar
git pull origin develop

# Subir cambios
git add .
git commit -m "descripción del cambio"
git push origin develop
```

## Estructura del Proyecto

- `src/main/java/com/Mokah/Veterinary/features/`: Entidades JPA
- `src/main/resources/application.yaml`: Configuración de Spring Boot
- `src/test/`: Tests unitarios

## Ejecutar la Aplicación

1. Configurar variables de entorno (ver sección arriba)
2. Ejecutar `VeterinaryApplication.java`
3. Acceder a: http://localhost:8080

## Notas Importantes

- No modificar `application.yaml` con credenciales personales
- Usar siempre la rama `develop` para desarrollo
- La aplicación crea automáticamente las tablas con `ddl-auto: update`

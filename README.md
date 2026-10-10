# Biblioteca Escolar — Evaluación Final Transversal

Sistema de gestión de biblioteca escolar desarrollado con **Java 17**, **Swing**, **MySQL** y **Maven**.

## Funcionalidades

- Inicio de sesión y control de acceso según rol.
- Consulta y gestión de libros.
- Registro y gestión de estudiantes.
- Registro de préstamos y devoluciones con actualización de stock.
- Reportes de títulos, ejemplares disponibles, estudiantes y préstamos.
- Operaciones de préstamos y devoluciones en segundo plano mediante `SwingWorker`.

## Tecnologías y arquitectura

- Java 17 y Swing para la interfaz gráfica.
- MySQL mediante JDBC y `mysql-connector-j`.
- Maven para compilación y empaquetado.
- Separación por paquetes: `modelo`, `vista`, `controlador`, `datos` y `main`.
- Patrón DAO para operaciones de base de datos y Singleton para administrar la configuración de conexión.

## Requisitos

1. JDK 17.
2. MySQL Server en funcionamiento.
3. Maven, o IntelliJ IDEA con soporte Maven.
4. Base de datos `biblioteca` preparada con los scripts SQL del directorio `sql/`.

## Preparación de la base de datos

Los scripts proporcionados para la evaluación están en `sql/`:

1. Ejecutar el script de creación de tablas.
2. Ejecutar el script de poblado de tablas.

**Importante:** ejecutar los scripts en una base de datos de prueba, siguiendo su contenido y orden. No ejecutarlos de nuevo sobre una base ya utilizada sin revisar su efecto, porque podrían reemplazar o duplicar datos.

La aplicación utiliza la conexión `jdbc:mysql://localhost:3306/biblioteca`, con usuario MySQL `root`. Si se utiliza otro servidor, puerto o usuario, ajustar `ConexionBD.java`.

## Configurar la contraseña de MySQL

La contraseña no se almacena en el código fuente. Configurar la variable de entorno `BIBLIOTECA_DB_PASSWORD` en el entorno de ejecución.

En IntelliJ IDEA, abrir **Run > Edit Configurations**, seleccionar `Main` y añadir `BIBLIOTECA_DB_PASSWORD` en **Environment variables**, con la contraseña de la instalación local de MySQL.

No compartir ni subir contraseñas al repositorio.

## Ejecutar desde IntelliJ

1. Abrir la carpeta del proyecto.
2. Importar o recargar el proyecto Maven.
3. Confirmar que se está utilizando JDK 17.
4. Preparar MySQL y la variable de entorno.
5. Ejecutar `cl.biblioteca.main.Main`.

## Compilar el ejecutable

Desde la raíz del proyecto:

```bash
mvn clean package
```

El ejecutable con dependencias se genera en:

```text
target/BibliotecaEscolarEFT-1.0-SNAPSHOT.jar
```

Para iniciarlo desde una terminal que tenga configurada la variable de entorno y acceso a MySQL:

```bash
java -jar target/BibliotecaEscolarEFT-1.0-SNAPSHOT.jar
```

El archivo JAR incluye las dependencias Java, **pero no incluye el servidor MySQL ni la base de datos**.

## Estructura del proyecto

```text
BibliotecaEscolarEFT/
├── pom.xml
├── sql/
├── src/main/java/cl/biblioteca/
│   ├── controlador/
│   ├── datos/
│   ├── main/
│   ├── modelo/
│   └── vista/
└── README.md
```

## Nota de entrega

El código fuente y los scripts SQL están en este repositorio. El ejecutable JAR puede incluirse por separado en el ZIP de entrega, ya que la carpeta `target/` se excluye del control de versiones.

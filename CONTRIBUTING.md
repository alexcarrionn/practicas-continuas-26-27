# Guía de contribución

Gracias por contribuir al proyecto **practicas-continuas-26-27**.

Este documento describe cómo preparar el entorno de desarrollo, trabajar con el repositorio, utilizar los hooks, ejecutar las comprobaciones, crear Pull Requests y fusionar cambios en la rama principal.

---

## Índice

- [Descripción del proyecto](#descripción-del-proyecto)
- [Requisitos](#requisitos)
- [Configuración del entorno](#configuración-del-entorno)
- [Clonar el repositorio](#clonar-el-repositorio)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Hooks de Git](#hooks-de-git)
- [Formato del código](#formato-del-código)
- [Tests](#tests)
- [Ejecutar la aplicación](#ejecutar-la-aplicación)
- [Flujo de trabajo](#flujo-de-trabajo)
- [Ramas](#ramas)
- [Commits](#commits)
- [Pull Requests](#pull-requests)
- [Revisión de código](#revisión-de-código)
- [Política de fusión](#política-de-fusión)
- [Resolución de conflictos](#resolución-de-conflictos)
- [Issues](#issues)
- [Buenas prácticas](#buenas-prácticas)
- [Archivos que no deben incluirse](#archivos-que-no-deben-incluirse)
- [Comprobación antes de fusionar](#comprobación-antes-de-fusionar)

---

## Descripción del proyecto

Este repositorio contiene una API REST de una biblioteca desarrollada con:

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **H2**
- **Maven**

La aplicación implementa operaciones CRUD sobre libros y dispone además de un endpoint de búsqueda avanzada con filtros, paginación y ordenación.

La aplicación está organizada en diferentes capas:

```text
src/
└── main/
    └── java/
        └── ...
            ├── controller/
            ├── service/
            ├── repository/
            ├── dto/
            ├── mapper/
            └── exception/
```

Las responsabilidades principales son:

- `controller`: endpoints REST.
- `service`: lógica y reglas de negocio.
- `repository`: acceso a datos mediante Spring Data JPA.
- `dto`: objetos utilizados para entrada y salida de datos.
- `mapper`: transformación entre entidades y DTOs.
- `exception`: gestión centralizada de errores.

---

## Requisitos

Para trabajar con el proyecto es necesario disponer de:

- Java 21.
- Maven 3.9 o superior.
- Git.
- Un editor o IDE compatible con Java.

Se recomienda utilizar una instalación nativa de Java y Maven en el sistema operativo utilizado para el desarrollo.

Comprueba las versiones instaladas:

```bash
java -version
mvn -version
git --version
```

Java debe ser versión 21.

Maven debe ser versión 3.9 o superior.

---

## Configuración del entorno

### Clonar el repositorio

Clona el repositorio:

```bash
git clone https://github.com/jgarciacutillas/practicas-continuas-26-27.git
```

Accede al directorio:

```bash
cd practicas-continuas-26-27
```

### Comprobar el proyecto

Comprueba que Maven puede compilar el proyecto:

```bash
mvn clean compile
```

Si la compilación termina correctamente, el entorno está preparado para comenzar a trabajar.

---

## Estructura del proyecto

La estructura principal del repositorio es:

```text
practicas-continuas-26-27/
├── .githooks/
├── src/
├── .editorconfig
├── .gitattributes
├── .gitignore
├── CHECKLIST.md
├── README.md
├── pom.xml
└── CONTRIBUTING.md
```

### Código fuente

El código de la aplicación se encuentra dentro de:

```text
src/main/
```

Los tests se encuentran dentro de:

```text
src/test/
```

No se deben modificar archivos generados dentro de `target/`.

---

## Hooks de Git

El repositorio utiliza hooks de Git almacenados en:

```text
.githooks/
```

Actualmente se utiliza un hook `pre-commit` para ejecutar el formateo del código mediante Spotless.

### Activar los hooks

Después de clonar el repositorio es necesario configurar Git para utilizar los hooks incluidos en el proyecto:

```bash
git config core.hooksPath .githooks
```

Comprueba la configuración:

```bash
git config core.hooksPath
```

El resultado esperado es:

```text
.githooks
```

---

## Ejecutar la aplicación

Para generar el paquete ejecutable:

```bash
mvn clean package
```

El proyecto utiliza `spring-boot-maven-plugin` para generar un JAR ejecutable.

El JAR generado se encuentra en:

```text
target/
```

Puede ejecutarse mediante:

```bash
java -jar target/book-library.jar
```

La aplicación utiliza H2 en memoria y crea el esquema automáticamente al arrancar.

---

## API

La API proporciona operaciones CRUD para libros:

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/books` | Crear un libro |
| `GET` | `/api/books` | Obtener todos los libros |
| `GET` | `/api/books/{id}` | Obtener un libro |
| `PUT` | `/api/books/{id}` | Actualizar un libro |
| `DELETE` | `/api/books/{id}` | Eliminar un libro |
| `GET` | `/api/books/search` | Buscar libros |

El endpoint de búsqueda permite utilizar filtros como:

- `keyword`
- `author`
- `genre`
- `minYear`
- `maxYear`
- `page`
- `size`
- `sort`

El tamaño máximo de página es de 100 resultados.

---

## Ramas

Los cambios deben realizarse en ramas independientes.

No se debe desarrollar directamente sobre `main`.

Se recomienda utilizar los siguientes prefijos:

| Prefijo | Uso |
|---|---|
| `feature/` | Nueva funcionalidad |
| `fix/` | Corrección de errores |
| `test/` | Tests |
| `refactor/` | Refactorización |
| `docs/` | Documentación |
| `chore/` | Mantenimiento |

Ejemplos:

```text
feature/book-search
fix/isbn-validation
test/book-service
refactor/book-controller
docs/update-readme
```

Los nombres de las ramas deben describir claramente el objetivo del cambio.

---

## Commits

Los commits deben ser pequeños, coherentes y describir claramente el cambio realizado.

Se recomienda utilizar el formato:

```text
tipo: descripción
```

Tipos recomendados:

- `feat`: nueva funcionalidad.
- `fix`: corrección de errores.
- `test`: tests.
- `docs`: documentación.
- `refactor`: refactorización.
- `chore`: mantenimiento.
- `style`: formato o estilo.

Ejemplos:

```text
feat: add book search endpoint
fix: validate duplicated isbn
test: add book service tests
docs: update contributing guide
refactor: simplify book mapper
```

Evita combinar cambios no relacionados en un mismo commit.

---

## Política de fusión

La estrategia de fusión establecida para este repositorio es **Merge Commit**.

Todos los Pull Requests deben fusionarse mediante:

**Create a merge commit**

No se utilizarán:

- `Squash and merge`.
- `Rebase and merge`.

### Requisitos para fusionar

Un Pull Request podrá fusionarse cuando:

1. El cambio esté terminado.
2. El Pull Request esté correctamente descrito.
3. Los tests pasen correctamente.
4. Spotless no detecte problemas de formato.
5. La aplicación compile correctamente.
6. Se hayan realizado las revisiones necesarias.
7. No existan conflictos pendientes.

La rama `main` debe mantenerse en un estado funcional.

### Historial

La utilización de Merge Commit permite conservar el historial de commits de la rama de trabajo.

Ejemplo:

```text
*   Merge pull request #10
|\
| * feat: add advanced book search
| * test: add search service tests
|/
* Previous commit on main
```

El commit de merge representa la integración de la rama de trabajo en `main`.

---

## Resolución de conflictos

Si un Pull Request presenta conflictos con `main`, deben resolverse antes de fusionarlo.

Actualiza primero `main`:

```bash
git checkout main
git pull origin main
```

Vuelve a tu rama:

```bash
git checkout nombre-de-tu-rama
```

Integra los cambios:

```bash
git merge main
```

Resuelve los conflictos indicados por Git.

Después ejecuta:

```bash
mvn test
mvn verify
```

Comprueba los cambios:

```bash
git status
git diff
```

Una vez resueltos:

```bash
git add .
git commit
git push
```

El Pull Request quedará actualizado con la resolución de conflictos.

---

## Issues

Los errores y propuestas de mejora pueden registrarse mediante Issues.

### Reportar un error

Un Issue de tipo bug debe incluir:

- Descripción del problema.
- Pasos para reproducirlo.
- Resultado esperado.
- Resultado obtenido.
- Información del entorno.
- Logs o mensajes de error relevantes.

### Solicitar una funcionalidad

Una propuesta de funcionalidad debería explicar:

- Qué funcionalidad se propone.
- Qué problema resuelve.
- Comportamiento esperado.
- Consideraciones técnicas relevantes.

---

## Buenas prácticas

Al contribuir:

- Mantén los cambios relacionados con el objetivo de la rama.
- Evita introducir dependencias innecesarias.
- Respeta la arquitectura existente.
- Mantén las diferentes capas separadas.
- Añade tests cuando introduzcas o modifiques comportamiento.
- Mantén la documentación actualizada.
- Utiliza nombres descriptivos.
- Evita código duplicado.
- No desactives los hooks para evitar comprobaciones.
- No subas información sensible.
- Revisa siempre los cambios antes de realizar un commit.

---

## Archivos que no deben incluirse

No deben subirse archivos generados o información específica del entorno local.

En particular, no debe incluirse:

```text
target/
*.class
.env
.idea/
```

Los archivos generados por Maven deben permanecer fuera del control de versiones.

Antes de realizar un commit:

```bash
git status
```

Y revisa los cambios preparados:

```bash
git diff --cached
```

Nunca deben incluirse:

- Contraseñas.
- Tokens.
- API keys.
- Claves privadas.
- Credenciales de bases de datos.
- Información confidencial.

---

## Resumen

El proceso de contribución es:

1. Clonar el repositorio.
2. Configurar los hooks.
3. Actualizar `main`.
4. Crear una rama.
5. Realizar los cambios.
6. Ejecutar Spotless.
7. Ejecutar los tests.
8. Comprobar la compilación.
9. Crear commits claros.
10. Subir la rama.
11. Crear un Pull Request.
12. Resolver las revisiones necesarias.
13. Resolver posibles conflictos.
14. Fusionar mediante **Merge Commit**.

Gracias por contribuir al proyecto.
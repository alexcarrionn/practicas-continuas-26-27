# Biblioteca de libros

API REST sencilla de una biblioteca, construida con Spring Boot 3, Java 21, Spring Data JPA y H2. El proyecto está diseñado para cumplir los requisitos de una aplicación no trivial sin introducir complejidad innecesaria.

## Requisitos

- Java 21
- Maven 3.9+

## Arquitectura

La aplicación está separada en capas:

- `controller`: endpoints REST.
- `service`: reglas de negocio.
- `repository`: persistencia con Spring Data JPA.
- `dto` y `mapper`: entrada/salida y transformación.
- `exception`: errores y manejo centralizado.

## Dominio

Cada libro tiene varios atributos: título, autor, género, ISBN, año de publicación y número de páginas.

Validaciones de entrada:

- título y autor obligatorios;
- género obligatorio;
- ISBN opcional, pero si se informa debe tener 10 o 13 dígitos;
- año entre 1450 y 2100;
- páginas entre 1 y 10000.

Reglas de negocio adicionales:

- no se permite repetir un ISBN;
- no se permite un año de publicación futuro;
- al actualizar, un ISBN que pertenece al mismo libro sigue siendo válido;
- un libro inexistente provoca `404 NOT_FOUND`.

## CRUD

- `POST /api/books`
- `GET /api/books`
- `GET /api/books/{id}`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

## Errores

`GlobalExceptionHandler` centraliza los errores. Un recurso inexistente devuelve HTTP 404; datos inválidos devuelven 400 y un ISBN duplicado devuelve 409.

## Tests unitarios

La suite contiene 15 tests unitarios en total:

- reglas de negocio del servicio: ISBN duplicado al crear, año futuro, ISBN duplicado al actualizar, mismo ISBN permitido al actualizar, recurso inexistente en lectura, actualización y borrado, y borrado correcto;
- manejo centralizado de errores: 404 para libro inexistente y 400 para una regla de negocio inválida;
- validaciones de DTO: título obligatorio, ISBN con 10/13 dígitos, páginas positivas y año mínimo.

No se utiliza `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest` ni se levanta una base de datos para los tests.

## Empaquetado ejecutable

El proyecto usa `spring-boot-maven-plugin`, que genera un JAR ejecutable con las dependencias incluidas.

```bash
mvn clean package
java -jar target/book-library.jar
```

La aplicación utiliza H2 en memoria y crea el esquema automáticamente al arrancar.

La ampliación añade 5 tests unitarios al conjunto existente.

## Búsqueda avanzada

Además del CRUD existente, la API incorpora un endpoint de búsqueda paginada:

- `GET /api/books/search`
- filtros opcionales: `keyword`, `author`, `genre`, `minYear`, `maxYear` y `available` (`true` devuelve los libros con ejemplares disponibles y `false` los que no tienen ninguno);
- paginación mediante `page` y `size` solamente 100 resultados por página (máximo);
- ordenación mediante `sort`, por ejemplo `title,asc`, `author,desc` o `publishedYear,desc`.

Los filtros de texto no distinguen entre mayúsculas y minúsculas y se pueden combinar. El servicio valida el rango de años, el tamaño de página y los campos de ordenación permitidos antes de consultar el repositorio.

La respuesta de búsqueda usa un DTO propio para mantener estable el contrato JSON y no exponer directamente la estructura interna de `PageImpl` de Spring Data.

## Hooks

Para que lance el hook de formateo automático debes ejecutar el siguiente comando:
```bash
git config core.hooksPath .githooks
```
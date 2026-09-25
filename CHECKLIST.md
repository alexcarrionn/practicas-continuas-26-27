# Checklist de requisitos

| Requisito | Implementación | Evidencia |
|---|---|---|
| Entidad de dominio con varios campos y validaciones | `Book` con id, título, autor, género, ISBN, año y páginas; Bean Validation y restricciones JPA | `src/main/java/com/library/model/Book.java` |
| CRUD completo | Crear, listar, obtener por ID, actualizar y borrar | `BookController`, `BookService`, `BookServiceImpl` |
| Separación por capas | Controller / Service / Repository + DTO/Mapper | paquetes `controller`, `service`, `repository`, `dto`, `mapper` |
| Error 404 centralizado | `BookNotFoundException` + `@RestControllerAdvice` | `GlobalExceptionHandler.handleBookNotFound` |
| ≥5 tests de reglas reales | 8 tests del servicio cubriendo duplicados, año futuro, actualización, inexistentes y borrado | `BookServiceImplTest` |
| Solo tests unitarios | Mockito/JUnit y Bean Validation directa; ningún test levanta Spring o H2 | `src/test/java`; no hay `@SpringBootTest`, `@WebMvcTest` ni `@DataJpaTest` |
| Fat JAR ejecutable | `spring-boot-maven-plugin` con `repackage` explícito y nombre `book-library` | `pom.xml` |

## Comando de entrega

```bash
mvn clean package
java -jar target/book-library.jar
```

Este repositorio no incluye tests de integración ni depende de una base de datos externa para ejecutar la aplicación: usa H2 en memoria.

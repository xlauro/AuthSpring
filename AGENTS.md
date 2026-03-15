# AGENTS.md

## Project snapshot
- This repo is currently a **Spring Boot 4.0.3 scaffold** with one app entrypoint and one context-load test.
- Core boot class: `src/main/java/com/laurosantos/authspring/AuthSpringApplication.java`.
- Runtime config is minimal (`spring.application.name` only) in `src/main/resources/application.yaml`.
- Dependencies in `pom.xml` already include Web MVC, Security, Validation, JPA, and H2, so auto-configuration is the main active behavior.

## Architecture and data flow (current)
- There are no controllers/services/repositories yet; behavior is driven by Spring Boot auto-config.
- Security starter enables default login and generated dev password at startup/tests (seen during `./mvnw test`).
- JPA + H2 starters initialize an in-memory datasource and EntityManagerFactory automatically.
- Test scope currently validates only app bootstrapping via `@SpringBootTest` in `src/test/java/com/laurosantos/authspring/AuthSpringApplicationTests.java`.

## Developer workflows
- Run tests (verified):
  ```bash
  ./mvnw -q test
  ```
- Run app locally:
  ```bash
  ./mvnw spring-boot:run
  ```
- Build jar:
  ```bash
  ./mvnw clean package
  ```
- Build OCI image (plugin is available through Spring Boot Maven plugin):
  ```bash
  ./mvnw spring-boot:build-image
  ```

## Project-specific conventions to keep
- Keep Java sources under package `com.laurosantos.authspring` (matches existing classes/tests).
- `pom.xml` uses Java `25` and Spring Boot parent `4.0.3`; align new code/tests with that baseline.
- Lombok is enabled as annotation processor and excluded from final artifact; prefer project-wide Lombok usage consistency when introducing DTOs/entities.
- This repo uses Maven wrapper (`./mvnw`) as the default command entrypoint.

## Integration points and dependencies
- Persistence boundary: Spring Data JPA + H2 (`pom.xml`) with no custom datasource properties yet.
- Web boundary: Spring Web MVC starter, but no mapped endpoints yet.
- Security boundary: Spring Security starter with default auth until explicit config is added.
- Validation boundary: Jakarta Validation starter is present; no validators/constraints implemented yet.

## AI-instruction source scan provenance
- Per requested glob search (`**/{.github/copilot-instructions.md,AGENT.md,AGENTS.md,CLAUDE.md,.cursorrules,.windsurfrules,.clinerules,.cursor/rules/**,.windsurf/rules/**,.clinerules/**,README.md}`), no existing AI-instruction or README files were found.
- Guidance above is derived from discoverable project files: `pom.xml`, `HELP.md`, `application.yaml`, and current Java source/test files.

## Requirements

- Implement new features and tests in alignment with the existing Spring Boot scaffold and project conventions.
- You will implement a api rest to register a user and login with jwt token, and a api rest to get the user info with the jwt token. You will use spring security and spring data jpa to implement the authentication and authorization. You will use h2 database to store the user info. You will use lombok to reduce the boilerplate code. You will use java 25 and spring boot 4.0.3. You will write unit tests for the new features using junit 5 and mockito. You will follow the best practices for coding, testing and documentation.
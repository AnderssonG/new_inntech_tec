# Voting API

API REST para gestionar un sistema de votacion con Spring Boot y MySQL.

## Tecnologias
- Java 21
- Spring Boot 4.0.5
- MySQL
- Spring Data JPA
- Spring Security

## Autenticacion
La API usa Basic Auth en todos los endpoints.

Credenciales configuradas actualmente:
- Usuario: `admin`
- Clave: `admin123`

## Configuracion local
Crear un archivo `.env` en la raiz del proyecto con estas variables:

```env
DB_URL=jdbc:mysql://localhost:3306/voting_system
DB_USERNAME=root
DB_PASSWORD=root123
APP_SECURITY_USERNAME=admin
APP_SECURITY_PASSWORD=admin123
```

## Ejecucion del proyecto
```powershell
.\mvnw.cmd spring-boot:run
```

Base URL:

```text
http://localhost:8080
```

## Endpoints

### Votantes
- `POST /voters`
- `GET /voters?page=0&size=10`
- `GET /voters/{id}`
- `DELETE /voters/{id}`

Ejemplo de registro:

```json
{
  "cedula": "10000001",
  "name": "Ana Gomez",
  "email": "ana.gomez@test.local"
}
```

Filtros disponibles en `GET /voters`:
- `cedula`
- `name`
- `email`
- `hasVoted`

### Candidatos
- `POST /candidates`
- `GET /candidates?page=0&size=10`
- `GET /candidates/{id}`
- `DELETE /candidates/{id}`

Ejemplo de registro:

```json
{
  "cedula": "20000001",
  "name": "Laura Torres",
  "party": "Partido Azul"
}
```

`party` es opcional.

Filtros disponibles en `GET /candidates`:
- `cedula`
- `name`
- `party`

### Votos
- `POST /votes`
- `GET /votes`
- `GET /votes/statistics`

Ejemplo de voto:

```json
{
  "voterId": "10000001",
  "candidateId": "20000001"
}
```

## Validaciones principales
- Un votante no puede ser candidato.
- Un candidato no puede ser votante.
- Un votante solo puede votar una vez.
- El `email` del votante es unico.
- `candidateId` debe existir.
- `party` es opcional.

## Codigos de respuesta
- `200` OK
- `201` Creado
- `400` Datos invalidos
- `401` No autenticado
- `404` Recurso no encontrado
- `409` Conflicto
- `500` Error interno

## Script SQL utilizado en MySQL

```sql
CREATE DATABASE voting_system;
USE voting_system;


/*
CREACION DE TABLAS
*/

CREATE TABLE voters (
    cedula VARCHAR(20) PRIMARY KEY UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    has_voted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE candidates (
    cedula VARCHAR(20) PRIMARY KEY UNIQUE,
    name VARCHAR(100) NOT NULL,
    party VARCHAR(100) NULL,
    votes INT NOT NULL DEFAULT 0
);

CREATE TABLE votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    voter_cedula VARCHAR(20) NOT NULL,
    candidate_cedula VARCHAR(20) NOT NULL,
    
    
    CONSTRAINT fk_votes_voter FOREIGN KEY (voter_cedula) REFERENCES voters(cedula)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_votes_candidate FOREIGN KEY (candidate_cedula) REFERENCES candidates(cedula)
        ON DELETE RESTRICT ON UPDATE CASCADE
    
);

-- cambio de limitador para evitar complicacion en la compilacion de creacion de los Triggers

DELIMITER $$

-- TRIGGER de limitacion para las cedulas de votantes par verificar que no sean candidatos

CREATE TRIGGER trg_voter_before_insert
BEFORE INSERT ON voters
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM candidates WHERE cedula = NEW.cedula) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cedula ya está registrada como candidato';
    END IF;
END$$

-- TRIGGER de limitacion para la cedulas de los candidatos no esten como votantes

CREATE TRIGGER trg_candidate_before_insert
BEFORE INSERT ON candidates
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM voters WHERE cedula = NEW.cedula) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cedula ya está registrada como votante';
    END IF;
END$$

-- TRIGGER de verificacion del votante y suma de voto para el candidato

CREATE TRIGGER trg_vote_after_insert
AFTER INSERT ON votes
FOR EACH ROW
BEGIN
    UPDATE voters
    SET has_voted = TRUE
    WHERE cedula = NEW.voter_cedula;

    UPDATE candidates
    SET votes = votes + 1
    WHERE cedula = NEW.candidate_cedula;
END$$

-- TRIGGER de verificacion del votante y resta de voto para el candidato

CREATE TRIGGER trg_vote_after_delete
AFTER DELETE ON votes
FOR EACH ROW
BEGIN
    UPDATE voters
    SET has_voted = FALSE
    WHERE cedula = OLD.voter_cedula;

    UPDATE candidates
    SET votes = votes - 1
    WHERE cedula = OLD.candidate_cedula;
END$$

-- Devuelta de limitador normal
DELIMITER ;
```

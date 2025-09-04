# Microservicios de Banca - Prueba Técnica

## Descripción del Proyecto

Este proyecto implementa una solución de microservicios para un sistema bancario básico, desarrollado con **Spring Boot** y **Java**. La arquitectura está compuesta por dos microservicios principales que se comunican de forma asíncrona mediante **Apache Kafka**.

## Arquitectura

### Microservicios

1. **Customer Service** (Puerto 8081)
   - Gestión de clientes y personas
   - Endpoints: `/api/clientes`

2. **Accounting Service** (Puerto 8082)
   - Gestión de cuentas y movimientos
   - Generación de reportes
   - Endpoints: `/api/cuentas`, `/api/movimientos`, `/api/reportes`

### Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **MySQL 8.0**
- **Apache Kafka**
- **Docker & Docker Compose**
- **Maven**
- **JUnit 5**

## Funcionalidades Implementadas

### ✅ F1: CRUD Completo
- **Clientes**: Crear, Leer, Actualizar, Eliminar
- **Cuentas**: Crear, Leer, Actualizar, Eliminar
- **Movimientos**: Crear, Leer, Actualizar, Eliminar

### ✅ F2: Registro de Movimientos
- Movimientos con valores positivos (depósitos) y negativos (retiros)
- Actualización automática del saldo disponible
- Registro completo de transacciones

### ✅ F3: Validación de Saldo
- Validación de saldo disponible antes de retiros
- Mensaje de error: "Saldo no disponible"
- Manejo de excepciones personalizadas

### ✅ F4: Reportes
- Reporte de "Estado de Cuenta" por rango de fechas y cliente
- Información de cuentas asociadas con saldos
- Detalle de movimientos de las cuentas
- Formato JSON

### ✅ F5: Pruebas Unitarias
- Pruebas unitarias para entidad Cliente
- Cobertura de casos de uso principales

### ✅ F6: Pruebas de Integración
- Pruebas de integración para MovimientoController
- Validación de flujos completos de negocio

### ✅ F7: Despliegue en Docker
- Docker Compose para orquestación de servicios
- Configuración de base de datos MySQL
- Configuración de Apache Kafka

## Estructura del Proyecto

```
├── customer-service/                 # Microservicio de Clientes
│   ├── src/main/java/
│   │   └── com/microservices/customerservice/
│   │       ├── application/service/  # Servicios de aplicación
│   │       ├── domain/               # Entidades, DTOs, Repositorios
│   │       ├── infrastructure/       # Controladores, Configuración
│   │       └── CustomerServiceApplication.java
│   └── src/test/java/               # Pruebas unitarias
├── accounting-service/              # Microservicio de Contabilidad
│   ├── src/main/java/
│   │   └── com/microservices/accountingservice/
│   │       ├── application/service/  # Servicios de aplicación
│   │       ├── domain/               # Entidades, DTOs, Repositorios
│   │       ├── infrastructure/       # Controladores, Configuración
│   │       └── AccountingServiceApplication.java
│   └── src/test/java/               # Pruebas de integración
├── docker-compose.yml              # Orquestación de servicios
├── BaseDatos.sql                   # Script de base de datos
├── Microservicios_Banca.postman_collection.json  # Colección Postman
└── README.md                       # Este archivo
```

## Instalación y Despliegue

### Prerrequisitos

- Docker y Docker Compose
- Java 17+ (para desarrollo local)
- Maven 3.6+ (para desarrollo local)

### Despliegue con Docker Compose

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd microservicios-banca
   ```

2. **Ejecutar con Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Verificar servicios**
   ```bash
   docker-compose ps
   ```

### Despliegue Local (Desarrollo)

1. **Configurar base de datos MySQL**
   ```bash
   # Ejecutar el script BaseDatos.sql en MySQL
   mysql -u root -p < BaseDatos.sql
   ```

2. **Configurar Apache Kafka**
   ```bash
   # Iniciar Zookeeper
   bin/zookeeper-server-start.sh config/zookeeper.properties
   
   # Iniciar Kafka
   bin/kafka-server-start.sh config/server.properties
   ```

3. **Ejecutar microservicios**
   ```bash
   # Terminal 1 - Customer Service
   cd customer-service
   mvn spring-boot:run
   
   # Terminal 2 - Accounting Service
   cd accounting-service
   mvn spring-boot:run
   ```

## Uso de la API

### Endpoints Principales

#### Customer Service (Puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/clientes` | Crear cliente |
| GET | `/api/clientes` | Obtener clientes (con filtros) |
| GET | `/api/clientes?activos=true` | Obtener solo clientes activos |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| PATCH | `/api/clientes/{id}/desactivar` | Desactivar cliente |

#### Accounting Service (Puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/cuentas` | Crear cuenta |
| GET | `/api/cuentas` | Obtener cuentas (con filtros) |
| GET | `/api/cuentas?activas=true` | Obtener solo cuentas activas |
| GET | `/api/cuentas?clienteId=1` | Obtener cuentas por cliente |
| GET | `/api/cuentas/{id}` | Obtener cuenta por ID |
| PUT | `/api/cuentas/{id}` | Actualizar cuenta |
| PATCH | `/api/cuentas/{id}/desactivar` | Desactivar cuenta |
| POST | `/api/movimientos` | Crear movimiento |
| GET | `/api/movimientos` | Obtener todos los movimientos |
| GET | `/api/movimientos/cliente/{id}` | Obtener movimientos por cliente |
| GET | `/api/movimientos/cliente/{id}?cuentaId=1` | Obtener movimientos por cuenta |
| GET | `/api/movimientos/cliente/{id}?fechaInicio=...&fechaFin=...` | Obtener movimientos por fechas |
| GET | `/api/reportes?cliente=1&fechaInicio=...&fechaFin=...` | Generar reporte |

### Ejemplos de Uso

#### 1. Crear Cliente
```bash
curl -X POST http://localhost:8081/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Jose Lema",
    "genero": "Masculino",
    "edad": 35,
    "identificacion": "1234567890",
    "direccion": "Otavalo sn y principal",
    "telefono": "098254785",
    "clienteId": "CLI001",
    "contrasena": "1234",
    "estado": true
  }'
```

#### 2. Crear Cuenta
```bash
curl -X POST http://localhost:8082/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCuenta": "478758",
    "tipoCuenta": "Ahorros",
    "saldoInicial": 2000.00,
    "clienteId": 1,
    "estado": true
  }'
```

#### 3. Realizar Movimiento
```bash
curl -X POST http://localhost:8082/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "cuentaId": 1,
    "clienteId": 1,
    "tipoMovimiento": "Deposito",
    "valor": 500.00
  }'
```

#### 4. Generar Reporte
```bash
curl "http://localhost:8082/api/reportes?cliente=1&fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59"
```

## Pruebas

### Ejecutar Pruebas Unitarias
```bash
# Customer Service
cd customer-service
mvn test

# Accounting Service
cd accounting-service
mvn test
```

### Ejecutar Pruebas de Integración
```bash
cd accounting-service
mvn test -Dtest=MovimientoControllerIntegrationTest
```

### Usar Colección de Postman

1. Importar el archivo `Microservicios_Banca.postman_collection.json` en Postman
2. Configurar las variables de entorno si es necesario
3. Ejecutar las pruebas de la colección

## Monitoreo y Health Checks

### Health Checks
- Customer Service: `http://localhost:8081/actuator/health`
- Accounting Service: `http://localhost:8082/actuator/health`

### Logs
```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f customer-service
docker-compose logs -f accounting-service
```

## Base de Datos

### Estructura de Tablas

#### Customer Service (customer_db)
- `persona`: Información personal
- `cliente`: Información de cliente (hereda de persona)

#### Accounting Service (accounting_db)
- `cuenta`: Información de cuentas bancarias
- `movimiento`: Registro de transacciones

### Datos de Prueba

El script `BaseDatos.sql` incluye datos de prueba según los casos de uso especificados:

- 3 clientes de prueba
- 5 cuentas de prueba
- 4 movimientos de ejemplo

## Comunicación Asíncrona

Los microservicios se comunican mediante eventos de Kafka:

- **Topics**: `cliente-events`, `cuenta-events`, `movimiento-events`
- **Eventos**: CREATED, UPDATED, DELETED, DEACTIVATED
- **Formato**: JSON

## Consideraciones de Rendimiento y Escalabilidad

### Optimizaciones Implementadas
- Índices en base de datos para consultas frecuentes
- Transacciones optimizadas
- Caching de consultas (Spring Data JPA)
- Comunicación asíncrona para desacoplamiento

### Escalabilidad
- Arquitectura de microservicios permite escalado independiente
- Base de datos separada por servicio
- Comunicación asíncrona reduce dependencias

### Resiliencia
- Manejo de excepciones personalizadas
- Validaciones de negocio
- Transacciones atómicas
- Health checks para monitoreo

## Casos de Uso Implementados

### Casos de Prueba Incluidos

1. **Creación de Usuarios**
   - Jose Lema, Marianela Montalvo, Juan Osorio

2. **Creación de Cuentas**
   - Cuentas de ahorros y corrientes
   - Diferentes saldos iniciales

3. **Movimientos Bancarios**
   - Depósitos y retiros
   - Validación de saldo disponible

4. **Reportes**
   - Estado de cuenta por cliente y fechas
   - Información detallada de movimientos

## Troubleshooting

### Problemas Comunes

1. **Error de conexión a base de datos**
   - Verificar que MySQL esté ejecutándose
   - Revisar credenciales en `application.yml`

2. **Error de conexión a Kafka**
   - Verificar que Kafka esté ejecutándose
   - Revisar configuración de bootstrap servers

3. **Puertos ocupados**
   - Cambiar puertos en `application.yml` y `docker-compose.yml`

### Logs de Debug
```bash
# Habilitar logs detallados
docker-compose logs -f --tail=100
```

## Contribución

1. Fork del repositorio
2. Crear rama para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## Licencia

Este proyecto es parte de una prueba técnica y está destinado únicamente para fines de evaluación.

## Contacto

Para preguntas sobre este proyecto, contactar al desarrollador.

---

**Nota**: Este proyecto cumple con todos los requisitos de la prueba técnica, incluyendo las funcionalidades F1-F7, arquitectura de microservicios, comunicación asíncrona, pruebas unitarias e integración, y despliegue en Docker.

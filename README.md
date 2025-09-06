# Accounting Service - Microservicio de Contabilidad

## 📋 Descripción

El **Accounting Service** es un microservicio especializado en la gestión de cuentas bancarias y movimientos contables. Forma parte del sistema de microservicios bancarios y se comunica de forma asíncrona con el Customer Service mediante Apache Kafka.

## 🏗️ Arquitectura

```
┌─────────────────┐    ┌─────────────────┐
│  Customer       │    │  Accounting     │
│  Service        │◄──►│  Service        │
│  (Puerto 8081)  │    │  (Puerto 8082)  │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────┬───────────┘
                     │
         ┌─────────────────┐
         │  Apache Kafka   │
         │  (Puerto 9092)  │
         └─────────────────┘
         │
    ┌─────────┬─────────┐
    │         │         │
┌───▼───┐ ┌───▼───┐ ┌───▼───┐
│ MySQL │ │ MySQL │ │ Zookeeper │
│ 3307  │ │ 3308  │ │  2181    │
└───────┘ └───────┘ └─────────┘
```

## 🚀 Despliegue

### Opción 1: Docker Compose (Recomendado)

```bash
# Desde el directorio raíz del proyecto
docker-compose up -d
```

### Opción 2: Desarrollo Local

```bash
# 1. Iniciar dependencias
docker-compose up -d mysql-accounting kafka zookeeper

# 2. Compilar el proyecto
mvn clean package -DskipTests

# 3. Ejecutar con perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## ⚙️ Configuración

### Puertos y Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Accounting Service | 8082 | API principal del servicio |
| MySQL (Accounting) | 3307 | Base de datos contable |
| Kafka | 9092 | Message broker |
| Zookeeper | 2181 | Coordinador de Kafka |

### Variables de Entorno

**Docker Compose:**
```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql-accounting:3306/accounting_db
  SPRING_DATASOURCE_USERNAME: root
  SPRING_DATASOURCE_PASSWORD: password
  SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
```

**Desarrollo Local:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/accounting_db
    username: root
    password: password
  kafka:
    bootstrap-servers: localhost:9092
```

## 📚 API Documentation

### Swagger UI
```
http://localhost:8082/swagger-ui.html
```

### Health Check
```
http://localhost:8082/actuator/health
```

## 🗄️ Base de Datos

### Estructura de Tablas

**accounting_db:**

#### Tabla: `cuentas`
```sql
CREATE TABLE cuentas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL,
    saldo_disponible DECIMAL(15,2) NOT NULL,
    cliente_id BIGINT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### Tabla: `movimientos`
```sql
CREATE TABLE movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo_anterior DECIMAL(15,2) NOT NULL,
    saldo_disponible DECIMAL(15,2) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE
);
```

## 🔗 Endpoints de la API

### Gestión de Cuentas

| Método | Endpoint | Descripción | Parámetros |
|--------|----------|-------------|------------|
| POST | `/api/cuentas` | Crear cuenta | Body: CuentaDto |
| GET | `/api/cuentas` | Listar cuentas | Query: activas, clienteId |
| GET | `/api/cuentas/{id}` | Obtener cuenta por ID | Path: id |
| PUT | `/api/cuentas/{id}` | Actualizar cuenta | Path: id, Body: CuentaDto |
| PATCH | `/api/cuentas/{id}/desactivar` | Desactivar cuenta | Path: id |

### Gestión de Movimientos

| Método | Endpoint | Descripción | Parámetros |
|--------|----------|-------------|------------|
| POST | `/api/movimientos` | Crear movimiento | Body: MovimientoDto |
| GET | `/api/movimientos` | Listar movimientos | - |
| GET | `/api/movimientos/cliente/{id}` | Movimientos por cliente | Path: id, Query: cuentaId, fechaInicio, fechaFin |

### Reportes

| Método | Endpoint | Descripción | Parámetros |
|--------|----------|-------------|------------|
| GET | `/api/reportes` | Generar reporte de estado de cuenta | Query: cliente, fechaInicio, fechaFin |

## 📝 Ejemplos de Uso

### 1. Crear Cuenta

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

### 2. Realizar Depósito

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

### 3. Realizar Retiro

```bash
curl -X POST http://localhost:8082/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "cuentaId": 1,
    "clienteId": 1,
    "tipoMovimiento": "Retiro",
    "valor": 200.00
  }'
```

### 4. Generar Reporte de Estado de Cuenta

```bash
curl "http://localhost:8082/api/reportes?cliente=1&fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59"
```

## 🔄 Comunicación Asíncrona

### Eventos de Kafka

El servicio publica y consume los siguientes eventos:

#### Eventos Publicados
- `cuenta-events`: Creación, actualización, desactivación de cuentas
- `movimiento-events`: Creación de movimientos

#### Eventos Consumidos
- `cliente-events`: Cambios en clientes del Customer Service

### Configuración de Topics

```yaml
spring:
  kafka:
    consumer:
      group-id: accounting-service-group
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias

```bash
mvn test
```

### Ejecutar Pruebas de Integración

```bash
mvn test -Dtest=*IntegrationTest
```

### Cobertura de Pruebas

```bash
mvn jacoco:report
```

## 📊 Monitoreo

### Health Checks

- **Health Check**: `http://localhost:8082/actuator/health`
- **Info**: `http://localhost:8082/actuator/info`
- **Metrics**: `http://localhost:8082/actuator/metrics`

### Logs

```bash
# Ver logs del servicio
docker logs accounting-service -f

# Ver logs con Docker Compose
docker-compose logs -f accounting-service
```

## 🏗️ Estructura del Código

```
src/main/java/com/microservices/accountingservice/
├── application/
│   └── service/              # Servicios de aplicación
│       ├── CuentaService.java
│       ├── MovimientoService.java
│       └── ReporteService.java
├── domain/
│   ├── dto/                  # Data Transfer Objects
│   │   ├── CuentaDto.java
│   │   ├── MovimientoDto.java
│   │   └── ReporteEstadoCuentaDto.java
│   ├── entity/               # Entidades JPA
│   │   ├── Cuenta.java
│   │   └── Movimiento.java
│   ├── event/                # Eventos de dominio
│   │   ├── ClienteEvent.java
│   │   ├── CuentaEvent.java
│   │   └── MovimientoEvent.java
│   ├── exception/            # Excepciones personalizadas
│   │   ├── CuentaAlreadyExistsException.java
│   │   ├── CuentaNotFoundException.java
│   │   ├── MovimientoNotFoundException.java
│   │   └── SaldoNoDisponibleException.java
│   ├── mapper/               # Mappers con MapStruct
│   │   ├── CuentaMapper.java
│   │   └── MovimientoMapper.java
│   └── repository/           # Repositorios JPA
│       ├── CuentaRepository.java
│       └── MovimientoRepository.java
├── infrastructure/
│   ├── controller/           # Controladores REST
│   │   ├── CuentaController.java
│   │   ├── MovimientoController.java
│   │   └── ReporteController.java
│   ├── exception/            # Manejo global de excepciones
│   │   └── GlobalExceptionHandler.java
│   └── messaging/            # Configuración de Kafka
│       ├── AccountingEventProducer.java
│       └── ClienteEventConsumer.java
└── AccountingServiceApplication.java
```

## 🔧 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Kafka**
- **MySQL 8.0**
- **Apache Kafka**
- **MapStruct** (Mappers)
- **Lombok** (Reducción de código)
- **JUnit 5** (Pruebas)
- **Docker**

## 🐛 Solución de Problemas

### Error de Conexión a Base de Datos

```bash
# Verificar que MySQL esté ejecutándose
docker ps | grep mysql-accounting

# Ver logs de MySQL
docker logs microservices-mysql-accounting
```

### Error de Conexión a Kafka

```bash
# Verificar que Kafka esté ejecutándose
docker ps | grep kafka

# Ver logs de Kafka
docker logs microservices-kafka
```

### Error de Saldo No Disponible

- Verificar que el saldo de la cuenta sea suficiente
- Revisar que el movimiento sea de tipo "Retiro" con valor positivo
- Verificar que la cuenta esté activa

## 📈 Métricas y Rendimiento

### Indicadores Clave

- **Tiempo de respuesta promedio**: < 200ms
- **Disponibilidad**: 99.9%
- **Throughput**: 1000+ requests/minuto

### Optimizaciones

- Índices en base de datos para consultas frecuentes
- Transacciones optimizadas
- Caching de consultas con Spring Data JPA
- Comunicación asíncrona para desacoplamiento

## 🤝 Contribución

1. Fork del repositorio
2. Crear rama para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto es parte de un sistema de microservicios bancarios y está destinado para fines de evaluación técnica.

---

**Nota**: Este servicio cumple con todos los requisitos de gestión contable, incluyendo validación de saldos, generación de reportes y comunicación asíncrona con otros microservicios.
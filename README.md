# 🛒 Inventory Sales API

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9.3-blue?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Enabled-6DB33F?style=flat-square&logo=spring-security)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-Auth-blueviolet?style=flat-square&logo=jsonwebtokens)](https://jwt.io/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-85EA2D?style=flat-square&logo=openapi-initiative)](https://www.openapis.org/)
[![Swagger UI](https://img.shields.io/badge/Swagger-UI-85EA2D?style=flat-square&logo=swagger)](https://swagger.io/tools/swagger-ui/)
[![MapStruct](https://img.shields.io/badge/MapStruct-Mapper-red?style=flat-square)](https://mapstruct.org/)
[![Hibernate](https://img.shields.io/badge/Hibernate-JPA%20Provider-59666C?style=flat-square&logo=hibernate)](https://hibernate.org/)
[![H2](https://img.shields.io/badge/DB-H2-blue?style=flat-square)](https://www.h2database.com/)
[![PostgreSQL](https://img.shields.io/badge/DB-PostgreSQL-336791?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

Una API RESTful completa para la gestión de inventario y ventas, desarrollada con Spring Boot 3 y Java 21. Incluye autenticación JWT, auditoría de datos, y operaciones CRUD para entidades de e-commerce.

## ✨ Características

- 🔐 **Autenticación JWT** con roles de usuario (ADMIN, USER)
- 📊 **Gestión completa de inventario** (Productos, Categorías, Clientes)
- 🛍️ **Sistema de ventas** con detalles y control de stock
- 📝 **Auditoría de datos** con Hibernate Envers
- 🗃️ **Múltiples bases de datos** (H2 para demo, PostgreSQL para producción)
- 📚 **Documentación OpenAPI** con Swagger UI
- 🐳 **Containerización Docker** lista para despliegue
- ✅ **Validación de datos** y manejo de errores
- 🔒 **Seguridad robusta** con Spring Security

## 🏗️ Arquitectura

### Entidades Principales
- **👤 User** - Gestión de usuarios y autenticación
- **📦 Product** - Productos con control de stock
- **🏷️ Category** - Categorías de productos
- **👥 Customer** - Clientes del sistema
- **🛒 Sale** - Ventas con detalles
- **📋 SaleDetail** - Detalles de cada venta

### Tecnologías Utilizadas
- **Backend**: Spring Boot 3.5.5, Java 21
- **Persistencia**: JPA/Hibernate (proveedor Hibernate)
- **Auditoría**: Hibernate Envers
- **Mapeo**: MapStruct para DTOs
- **Seguridad**: Spring Security + JWT
- **Documentación**: OpenAPI 3 + Swagger UI
- **Base de datos**: H2 (demo/dev/test), PostgreSQL (producción)
- **Containerización**: Docker

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 21+
- Maven 3.9+
- Docker (opcional)

### Ejecución Local

```bash
# Clonar el repositorio
git clone https://github.com/argenischacon/inventory-sales-api.git
cd inventory-sales-api

# Ejecutar la aplicación (demo por defecto)
mvn spring-boot:run
```

#### Perfiles (local, sin Docker)

```bash
# Demo (por defecto)
mvn spring-boot:run

# Dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test (ejecución de tests con perfil test)
mvn -Dspring.profiles.active=test test

# Prod (requiere variables de entorno)
SPRING_PROFILES_ACTIVE=prod \ 
DB_URL=jdbc:postgresql://localhost:5432/inventory_db \ 
DB_USER=inventory_user \ 
DB_PASSWORD=inventory_password \ 
JWT_SECRET=cambia-esto \ 
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📖 Documentación de la API

Una vez ejecutada la aplicación, accede a la documentación interactiva:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Endpoints Principales

> La autenticación es requerida para todos los endpoints excepto `/login`.

| Endpoint | Descripción | Rol Requerido |
|----------------------------------------------------------|--------------------------------|---------------|
| `POST /api/v1/auth/login` | Autenticar un usuario | *Público* |
| `GET /api/v1/categories` | Listar todas las categorías | `USER` |
| `POST /api/v1/categories` | Crear una nueva categoría | `ADMIN` |
| `GET /api/v1/products` | Listar todos los productos | `USER` |
| `POST /api/v1/products` | Crear un nuevo producto | `ADMIN` |
| `GET /api/v1/customers` | Listar todos los clientes | `USER` |
| `POST /api/v1/customers` | Crear un nuevo cliente | `ADMIN` |
| `GET /api/v1/sales` | Listar todas las ventas | `USER` |
| `POST /api/v1/sales` | Crear una nueva venta | `ADMIN` |
| `GET /api/v1/products/audit/{id}/revisions` | Ver historial de un producto | `ADMIN` |

## 🔧 Configuración

### Perfiles de Aplicación

- `demo` (por defecto): H2 en memoria. Propósito: demos y exploración rápida.
- `dev`: H2 en memoria con `ddl-auto=update` y SQL visible. Propósito: desarrollo local.
- `test`: H2 en memoria con `create-drop`. Propósito: pruebas automatizadas.
- `prod`: PostgreSQL. Propósito: producción.

### Variables de Entorno

```bash
# Base de datos (solo para producción)
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USER=inventory_user
DB_PASSWORD=inventory_password

# JWT
JWT_SECRET=your-secure-secret-key-here
JWT_EXPIRATION=3600000
```

## 🐳 Docker

A continuación se muestran comandos mínimos para construir y ejecutar. Para escenarios completos (logs, reinicio, perfiles adicionales), revisa `DOCKER.md`.

### 1) Construir imagen

```bash
docker build -t inventory-sales-api:latest .
```

### 2) Ejecutar rápidamente por perfil

```bash
# Demo (H2, sin variables)
docker run -d --name inventory-sales-api-demo -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=demo \
  inventory-sales-api:latest

# Producción (PostgreSQL externo)
docker run -d --name inventory-sales-api-prod -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/inventory_db \
  -e DB_USER=inventory_user \
  -e DB_PASSWORD=inventory_password \
  inventory-sales-api:latest
```

> Si PostgreSQL corre en tu máquina, usa `host.docker.internal` en `DB_URL`. Si corre en otro contenedor, usa el nombre del servicio/red de Docker.
> Ajusta `JWT_SECRET` y credenciales según tu entorno antes de exponer a producción.

Para más detalles, consulta [DOCKER.md](DOCKER.md)

## 🧪 Testing

```bash
# Ejecutar todas las pruebas
mvn test

```

## 📊 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/argenischacon/inventory_sales_api/
│   │   ├── audit/          # Clases para la auditoría de entidades con Envers
│   │   ├── config/         # Configuraciones de Spring (Security, OpenAPI, Beans)
│   │   ├── controller/     # Controladores REST (endpoints de la API)
│   │   ├── dto/            # Data Transfer Objects (DTOs) para las respuestas y peticiones
│   │   ├── exception/      # Manejadores de excepciones globales y clases de error
│   │   ├── mapper/         # Mapeadores de objetos (MapStruct)
│   │   ├── model/          # Entidades JPA (representación de la base de datos)
│   │   ├── repository/     # Repositorios de datos (Spring Data JPA)
│   │   ├── security/       # Clases relacionadas con JWT y la seguridad
│   │   ├── service/        # Lógica de negocio y servicios
│   │   └── InventorySalesApiApplication.java # Punto de entrada de la aplicación
│   └── resources/
│       ├── static/         
│       ├── templates/      
│       ├── application.properties          # Configuración principal
│       ├── application-demo.properties     # Perfil para demostración
│       ├── application-dev.properties      # Perfil para desarrollo
│       ├── application-prod.properties     # Perfil para producción
│       └── application-test.properties     # Perfil para pruebas
└── test/
    └── java/com/argenischacon/inventory_sales_api/
        ├── controller/     # Pruebas unitarias de controladores
        ├── service/        # Pruebas unitarias de servicios
        └── InventorySalesApiApplicationTests.java 
```

## 🔐 Autenticación

### Usuarios por Defecto

> **Nota:** Estos usuarios se crean automáticamente solo cuando la aplicación se ejecuta con los perfiles `demo` o `dev`.

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | ADMIN |
| `user` | `user123` | USER |

### Uso de la API

1. **Autenticarse**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

2. **Usar el token** en las siguientes peticiones:
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m \'Add some AmazingFeature\'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👨‍💻 Autor

**Argenis Chacón**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Argenis_Chacon-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/argenischaconb)
[![GitHub](https://img.shields.io/badge/GitHub-@argenischacon-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/argenischacon)

---

⭐ Si este proyecto te resultó útil, ¡dale una estrella!

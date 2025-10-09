# 🐳 Docker Setup - Inventory Sales API

Este documento explica cómo construir y desplegar la API de Inventario y Ventas usando Docker.

## 📋 Prerrequisitos

- Docker instalado
- Git (para clonar el repositorio)

## 🚀 Despliegue Rápido

### Modo Demo (H2 Database)
```bash
# Construir la imagen
docker build -t inventory-sales-api:latest .

# Ejecutar en modo demo
docker run -d --name inventory-sales-api-demo -p 8080:8080 -e SPRING_PROFILES_ACTIVE=demo inventory-sales-api:latest
```

### Modo Producción (PostgreSQL)
```bash
# Construir la imagen
docker build -t inventory-sales-api:latest .

# Ejecutar con perfil de producción (requiere PostgreSQL externo)
docker run -d --name inventory-sales-api-prod -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/inventory_db \
  -e DB_USER=inventory_user \
  -e DB_PASSWORD=inventory_password \
  inventory-sales-api:latest
```

## 🌐 Acceso a la Aplicación

Una vez desplegada, la aplicación estará disponible en:

- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 📊 Perfiles de Configuración

### Demo Profile
- Base de datos: H2 (en memoria)
- Configuración: `application-demo.properties`
- Ideal para: Demostraciones y desarrollo

### Dev Profile
- Base de datos: H2 (en memoria), SQL visible (`spring.jpa.show-sql=true`)
- Configuración: `application-dev.properties`
- Ideal para: Desarrollo local dentro de contenedor

```bash
# Ejecutar con perfil dev
docker run -d --name inventory-sales-api-dev -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  inventory-sales-api:latest
```

### Production Profile
- Base de datos: PostgreSQL
- Configuración: `application-prod.properties`
- Ideal para: Despliegue en producción
- **Requiere PostgreSQL externo y variables de entorno**

### Test Profile
- Base de datos: H2 (en memoria), `create-drop`
- Configuración: `application-test.properties`
- Ideal para: Pruebas automatizadas
- Nota: usualmente no se ejecuta en Docker; se activa con Maven:

```bash
mvn -Dspring.profiles.active=test test
```

## 🔧 Configuración

### Variables de Entorno para Producción
Para el perfil de producción, configura estas variables:
```bash
# Base de datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USER=inventory_user
DB_PASSWORD=inventory_password

# JWT
JWT_SECRET=your-secure-secret-key-here
JWT_EXPIRATION=3600000
```

## 📝 Comandos Útiles

### Ver logs de la aplicación
```bash
docker logs inventory-sales-api-demo
# o
docker logs inventory-sales-api-prod
```

### Detener la aplicación
```bash
# Modo demo
docker stop inventory-sales-api-demo
docker rm inventory-sales-api-demo

# Modo producción
docker stop inventory-sales-api-prod
docker rm inventory-sales-api-prod
```

### Reiniciar la aplicación
```bash
# Modo demo
docker restart inventory-sales-api-demo

# Modo producción
docker restart inventory-sales-api-prod
```

### Acceder al contenedor
```bash
docker exec -it inventory-sales-api-demo sh
```
# LogiFlow - Microservicios

Este directorio contiene el código fuente de los microservicios y la configuración necesaria para desplegar la plataforma **logiflow** en un entorno local utilizando Docker.

## 📦 Servicios y Puertos

| Servicio | Puerto Host | Descripción | Base de Datos |
|----------|-------------|-------------|---------------|
| **API Gateway** | `8000` | Puerta de enlace y enrutamiento. | - |
| **Auth Service** | `8081` | Autenticación y Usuarios. | `jwt_demo` (5432) |
| **Billing Service** | `8082` | Facturación. | `db_billing_users` (5433) |
| **Fleet Service** | `8083` | Flota y conductores. | `fleet_db` (5435) |
| **Pedido Service** | `8084` | Gestión de pedidos. | `pedidos_db` (5436) |
| **Notifications** | `8085` | Servicio de notificaciones. | `db_notification` (5434) |
| **Tracking Service** | `8090` | Rastreo de pedidos. | - |
| **GraphQL Service** | `4000` | API unificada para frontend. | - |
| **RabbitMQ** | `15672` (UI) | Broker de mensajería. | - |

## 🛠 Requisitos Previos

- **Docker Desktop** instalado y ejecutándose.
- **Java 21** (si deseas ejecutar servicios individualmente).
- **Node.js 18+** (para el servicio GraphQL).
- **Maven** (para compilar servicios Java).

## 🚀 Despliegue Local (Docker Compose)

La forma más sencilla de levantar todo el ecosistema es utilizando Docker Compose.

1.  **Configurar Variables de Entorno**
    Asegúrate de que el archivo `.env` en este directorio contenga las credenciales necesarias (especialmente email para notificaciones si se usa).
    ```env
    MAIL_USERNAME=tu_email@gmail.com
    MAIL_PASSWORD=tu_password_aplicacion
    ```

2.  **Construir y Levantar Contenedores**
    Ejecuta el siguiente comando en la terminal dentro de la carpeta `logiflow`:
    ```bash
    docker compose up -d --build
    ```
    *Esto descargará las imágenes base, compilará los proyectos Java y Node, y levantará todos los contenedores.*

3.  **Verificar Estado**
    Puedes ver los logs de un servicio específico (ej. authservice):
    ```bash
    docker compose logs -f authservice
    ```

4.  **Detener Plataforma**
    Para detener y remover los contenedores:
    ```bash
    docker compose down
    ```
    *(Añade `-v` si quieres borrar también los volúmenes de base de datos datos: `docker compose down -v`)*

## 🧪 Pruebas de API

Una vez levantado:
- **GraphQL Playground**: [http://localhost:4000/graphql](http://localhost:4000/graphql)
- **API Gateway**: Las rutas se exponen en `http://localhost:8000`.
  - Auth: `/api/auth/...`
  - Pedidos: `/api/pedido/...`
  - etc. (ver `docker-compose.yml` para mapeos completos).

## ⚠️ Solución de Problemas Comunes

- **Puertos Ocupados**: Asegúrate de que los puertos 8000, 8081-8085, 8090, 5432-5436 no estén en uso.
- **Base de Datos no lista**: Los servicios tienen `healthcheck`, pero si alguno falla al iniciar por conexión rechazada, docker compose suele reintentar. Si persiste, reinicia el servicio afectado:
  ```bash
  docker compose restart nombre-servicio
  ```

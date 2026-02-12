# EntregaExpress P2 - LogiFlow Platform

**EntregaExpress P2** es una plataforma de logística distribuida diseñada para gestionar el ciclo de vida completo de pedidos, desde la creación hasta la entrega y el seguimiento en tiempo real. Este proyecto implementa una arquitectura de microservicios moderna, escalable y resiliente.

## 📂 Estructura del Proyecto

El repositorio está organizado en dos componentes principales:

- **`/logiflow`**: Contiene el código fuente de todos los microservicios, la configuración de Docker Compose para despliegue local y los archivos de configuración del entorno.
- **`/kubernets`**: Contiene los manifiestos YAML necesarios para desplegar la plataforma completa en un clúster de Kubernetes (probado en Minikube).

## 🚀 Inicio Rápido

### Desarrollo Local (Docker Compose)
Para ejecutar el sistema en un entorno local de desarrollo utilizando Docker:
👉 **[Ver instrucciones en /logiflow](./logiflow/README.md)**

### Despliegue en Producción (Kubernetes)
Para desplegar el sistema en un clúster de Kubernetes:
👉 **[Ver instrucciones en /kubernets](./kubernets/README.md)**

## 🏗 Arquitectura del Sistema

El sistema se compone de los siguientes microservicios:

| Servicio | Descripción | Puerto (Docker) |
|----------|-------------|-----------------|
| **API Gateway** | Punto de entrada único, enrutamiento y balanceo de carga. | `8000` |
| **Auth Service** | Gestión de usuarios, autenticación JWT y roles. | `8081` |
| **Pedido Service** | Gestión de pedidos, asignación y estados. | `8082` |
| **Tracking Service** | Seguimiento de ubicación y eventos de entrega. | `8083` |
| **Fleet Service** | Gestión de conductores y vehículos. | `8084` |
| **Billing Service** | Facturación y procesamiento de pagos. | `8085` |
| **GraphQL Service** | Capa de agregación de datos para clientes frontend. | `4000` |
| **Notification Service** | Envío de notificaciones asíncronas (RabbitMQ). | n/a |

## 🛠 Tecnologías

- **Backend**: Java 21 (Spring Boot 3.x), Node.js (GraphQL)
- **Bases de Datos**: PostgreSQL
- **Mensajería**: RabbitMQ
- **Conteneurización**: Docker, Docker Compose
- **Orquestación**: Kubernetes

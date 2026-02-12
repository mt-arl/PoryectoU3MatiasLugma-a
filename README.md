# LogiFlow - Plataforma Distribuida de Gestión Logística

Plataforma de gestión logística empresarial basada en arquitectura de microservicios, diseñada para optimizar procesos de pedidos, autenticación, facturación y seguimiento de flota en tiempo real.

**Versión:** 3.0 | Java 25 LTS | Spring Boot 3.5.8 | Estado: Producción

---

## Tabla de Contenidos

1. [Descripción General](#descripción-general)
2. [Requisitos del Sistema](#requisitos-del-sistema)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Despliegue](#despliegue)
5. [Referencia de API](#referencia-de-api)
6. [Configuración](#configuración)
7. [Monitoreo y Troubleshooting](#monitoreo-y-troubleshooting)
8. [Stack Tecnológico](#stack-tecnológico)

---

## Descripción General

LogiFlow es un sistema completo de gestión logística que integra:

- **Autenticación y Seguridad**: Control de acceso basado en JWT
- **Gestión de Pedidos**: Creación, actualización y rastreo de órdenes
- **Facturación**: Cálculo de tarifas y generación de facturas
- **Gestión de Flota**: Administración de vehículos y conductores
- **Rastreo en Tiempo Real**: Localización de entregas y rutas
- **Notificaciones**: Sistema asincrónico de alertas por correo

### Arquitectura de Microservicios

```
┌──────────────────────────────────────────────┐
│          CLIENTES EXTERNOS                   │
└────────────────┬─────────────────────────────┘
                 │
                 ▼
      ┌──────────────────────┐
      │  API GATEWAY: 8000   │
      │ Spring Cloud Gateway │
      └──────────┬───────────┘
                 │
    ┌────┬──────┼──────┬────┬──────┬────┐
    │    │      │      │    │      │    │
    ▼    ▼      ▼      ▼    ▼      ▼    ▼
  AUTH BILL FLEET ORDER TRACK NOTIF GRAPH
  :8081 :8082 :8083  :8084 :8090 :8085 :5000
   │    │      │      │    │      │    │
    └────┴──────┴──────┴────┴──────┴────┘
                 │
        ┌────────┼────────┐
        │        │        │
        ▼        ▼        ▼
    PostgreSQL RabbitMQ Redis
     :5432      :5672    :6379
```

### Servicios Disponibles

| Servicio | Puerto | Tecnología | Función |
|----------|--------|-----------|---------|
| API Gateway | 8000 | Spring Cloud Gateway | Enrutamiento y reescritura de rutas |
| Autenticación | 8081 | Spring Boot + JWT | Gestión de tokens y acceso |
| Facturación | 8082 | Spring Boot + JPA | Cálculo de tarifas e invoices |
| Flota | 8083 | Spring Boot + JPA | Gestión de vehículos y conductores |
| Pedidos | 8084 | Spring Boot + RabbitMQ | Procesamiento de órdenes |
| Rastreo | 8090 | Spring Boot + AMQP | Ubicación en tiempo real |
| Notificaciones | 8085 | Spring Boot + Mail | Alertas asincrónicas por correo |
| GraphQL | 5000 | Apollo + TypeScript | API GraphQL para entregas |

---

## Requisitos del Sistema

### Para Docker Compose (Recomendado)

- Docker 24.0+
- Docker Compose 2.0+
- Mínimo 4 GB de RAM disponibles
- Puertos disponibles: 5432, 5672, 8000-8090, 15672

### Para Ejecución Local

- Java 25 LTS
- Maven 3.9+
- PostgreSQL 14+
- RabbitMQ 4.2+

### Para Kubernetes

- Minikube 1.25+ o cluster de Kubernetes 1.24+
- kubectl 1.24+
- 8 GB de RAM asignada

---

## Estructura del Proyecto

```
LogiFlowApp3/
├── README.md                      # Documentación principal (este archivo)
├── docker-compose.yml             # Orquestación desde raíz (opcional)
│
├── logiflow/
│   ├── docker-compose.yml         # Configuración principal de servicios
│   ├── DOCKER_GUIDE.md            # Guía detallada de despliegue Docker
│   ├── start-services.sh          # Script para ejecución local
│   │
│   ├── api-gateway/               # Spring Cloud Gateway
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── authservice/               # Servicio de Autenticación
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── billing-service/           # Servicio de Facturación
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── fleet-service/             # Servicio de Flota
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── pedido-service/            # Servicio de Pedidos
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── tracking-service/          # Servicio de Rastreo
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── ms-notifications/          # Servicio de Notificaciones
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/
│   │
│   ├── delivery-graphql-service/  # Servicio GraphQL
│   │   ├── package.json
│   │   ├── Dockerfile
│   │   ├── tsconfig.json
│   │   └── src/
│   │
│   └── init-scripts/              # Scripts de inicialización
│       └── 01-init-db.sql
│
├── kubernetes/                     # Manifiestos Kubernetes
│   ├── 01-namespace.yaml
│   ├── 02-databases.yaml
│   ├── 03-ingress.yaml
│   └── 04-deploy.yaml
│
└── imagenes/                      # Documentación e imágenes
```

---

## Despliegue

### Opción 1: Docker Compose (Recomendado para Desarrollo)

#### Construcción e Inicio

```bash
# Navegar al directorio
cd logiflow

# Construir todas las imágenes
docker-compose build

# Iniciar servicios en segundo plano
docker-compose up -d

# Verificar estado
docker-compose ps
```

#### Verificación de Servicios

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f api-gateway

# Verificar salud de API Gateway
curl http://localhost:8000/actuator/health
```

#### Detener Servicios

```bash
# Detener sin eliminar volúmenes (datos se conservan)
docker-compose down

# Detener y eliminar volúmenes (reset completo)
docker-compose down -v
```

### Opción 2: Kubernetes (Para Producción)

#### Inicio de Minikube

```bash
# Iniciar Minikube con memoria suficiente
minikube start --memory=8192

# Usar el docker daemon de Minikube
eval $(minikube docker-env)
```

#### Despliegue de Manifiestos

```bash
# Aplicar los manifiestos en orden
kubectl apply -f kubernetes/01-namespace.yaml
kubectl apply -f kubernetes/02-databases.yaml
kubectl apply -f kubernetes/03-ingress.yaml
kubectl apply -f kubernetes/04-deploy.yaml

# Verificar estado de los pods
kubectl get pods -n logiflow

# Ver detalles de un pod
kubectl describe pod <pod-name> -n logiflow
```

#### Port Forwarding

```bash
# API Gateway (mantener terminal abierta)
kubectl port-forward -n logiflow svc/api-gateway 8000:8000

# RabbitMQ Management (en otra terminal)
kubectl port-forward -n logiflow svc/rabbitmq 15672:15672

# PostgreSQL (en otra terminal)
kubectl port-forward -n logiflow svc/postgresql 5432:5432
```

#### Monitoreo con k9s

```bash
# Instalar k9s
brew install k9s

# Monitorear namespace logiflow
k9s -n logiflow
```

---

## Referencia de API - Flujo de Trabajo Completo

### 📍 Guía de Uso en Postman

#### Opción 1: Importar Colección (Recomendado)

1. **Descargar colección JSON:**
   - Archivo: `LogiFlow.postman_collection.json` (raíz del proyecto)

2. **Importar en Postman:**
   - Abrir Postman
   - Click en **"Import"** (arriba a la izquierda)
   - Seleccionar **"File"**
   - Cargar el archivo `LogiFlow.postman_collection.json`
   - ¡Todas las peticiones se cargarán automáticamente con el flujo correcto!

3. **Configurar Variables de Entorno:**
   - Click en el ícono de **"Environment"** (derecha)
   - Click en **"Create New Environment"**
   - Nombre: **"LogiFlow Local"**
   - Agregar estas variables:
     ```
     base_url = http://localhost:8000
     token = (se llena automáticamente)
     orderId = (se llena automáticamente)
     invoiceId = (se llena automáticamente)
     vehicleId = (se llena automáticamente)
     driverId = (se llena automáticamente)
     trackingId = (se llena automáticamente)
     ```
   - Guardar y seleccionar

#### Opción 2: Configuración Manual

Si prefieres crear las peticiones manualmente, seguir la sección "[Orden de Ejecución Recomendada]" más abajo.

#### Ejecutar Flujo Completo

1. **Seleccionar environment:** "LogiFlow Local" (arriba derecha)
2. **Ejecutar peticiones en orden:**
   - Haz click en cada petición en la orden mostrada
   - Haz click en **"Send"**
   - Las variables se guardarán automáticamente
   - Verifica que cada respuesta sea exitosa (200/201)

3. **Verificar generación de notificaciones:**
   - Después de cada acción (crear pedido, crear factura, etc.)
   - Una notificación se generará automáticamente
   - Verifica en la última petición "[NOTIFICACIONES] Obtener Todas"

---

### 📍 Orden de Ejecución Recomendada

Ejecutar las peticiones **en este orden exacto**. Las notificaciones se generarán automáticamente en los puntos marcados con ✉️:

```
1️⃣  [AUTH] Login
    └─ Obtiene: JWT Token
    └─ Guarda: token en variable

2️⃣  [BILLING] Calcular Tarifa
    └─ Verifica: cálculo de costos de envío
    └─ Resultado: $62.75

3️⃣  [PEDIDOS] Crear Pedido
    └─ Crea: nuevo pedido de cliente
    └─ Guarda: orderId en variable
    └─ ✉️ NOTIFICACIÓN AUTOMÁTICA: "Pedido Confirmado"
       └─ Enviada a: cliente@example.com
       └─ Mensaje: "Su pedido ha sido confirmado"

4️⃣  [PEDIDOS] Obtener Estado de Pedido
    └─ Verifica: detalles del pedido
    └─ Status: PENDING

5️⃣  [FACTURACIÓN] Crear Factura
    └─ Crea: factura para el pedido
    └─ Guarda: invoiceId en variable
    └─ ✉️ NOTIFICACIÓN AUTOMÁTICA: "Pedido Pagado"
       └─ Enviada a: cliente@example.com
       └─ Mensaje: "Su factura ha sido creada. Total: $100.00"

6️⃣  [FLOTA] Registrar Vehículo
    └─ Registra: vehículo de entrega
    └─ Guarda: vehicleId en variable
    └─ Placa: ABC-123

7️⃣  [FLOTA] Registrar Conductor
    └─ Registra: conductor de entrega
    └─ Guarda: driverId en variable
    └─ Nombre: Juan Pérez

8️⃣  [RASTREO] Iniciar Rastreo de Entrega
    └─ Inicia: proceso de seguimiento
    └─ Guarda: trackingId en variable
    └─ Status: IN_TRANSIT
    └─ ✉️ NOTIFICACIÓN AUTOMÁTICA: "Tu pedido está en tránsito"
       └─ Enviada a: cliente@example.com
       └─ Mensaje: "Tu pedido está siendo entregado. Conductor: Juan Pérez"

9️⃣  [RASTREO] Actualizar Ubicación
    └─ Actualiza: coordenadas GPS del vehículo
    └─ Ubicación: Lat: 19.4326, Lon: -99.1332
    └─ ✉️ NOTIFICACIÓN AUTOMÁTICA: "Tu entrega está cerca"
       └─ Enviada a: cliente@example.com
       └─ Mensaje: "El conductor está a 5 minutos de tu ubicación"

🔟 [NOTIFICACIONES] Obtener Todas las Notificaciones
    └─ Lista: todas las notificaciones generadas
    └─ Verifica: 4 notificaciones en PENDING
       └─ Pedido Confirmado
       └─ Pedido Pagado
       └─ Pedido en Tránsito
       └─ Entrega Cerca
```

---

### **[AUTH] 1. Iniciar Sesión - Login**

**POST** `http://localhost:8000/api/auth/login`

**Headers:**
- Content-Type: application/json

**Body (JSON):**
```json
{
  "username": "test",
  "password": "test123"
}
```

**Respuesta esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "test",
  "roles": ["CLIENTE"]
}
```

⚠️ **IMPORTANTE:** Copiar el token recibido y guardarlo en una variable de entorno de Postman:
1. Click en la pestaña "Tests"
2. Agregar script de prueba:
```javascript
pm.globals.set("token", pm.response.json().token);
```

---

### **[BILLING] 2. Calcular Tarifa de Envío**

**POST** `http://localhost:8000/api/billing/rates/calculate`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "distance": 25.5,
  "serviceType": "STANDARD",
  "weight": 5.0
}
```

**Respuesta esperada (200 OK):**
```json
{
  "baseRate": 50.00,
  "distanceRate": 12.75,
  "surcharge": 0.00,
  "totalRate": 62.75
}
```

---

### **[PEDIDOS] 3. Crear Pedido** ✨ *Genera notificación automáticamente*

**POST** `http://localhost:8000/api/pedidos`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "items": [
    {
      "productId": "prod-001",
      "quantity": 2,
      "price": 50.00
    }
  ],
  "deliveryAddress": {
    "street": "Calle Principal 123",
    "city": "Ciudad de México",
    "state": "CDMX",
    "zip": "06500"
  },
  "emailCliente": "cliente@example.com",
  "serviceType": "STANDARD"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": "order-550e8400",
  "customerId": "cust-123",
  "status": "PENDING",
  "totalAmount": 100.00,
  "createdAt": "2026-02-11T08:00:00.000000"
}
```

⚠️ **Guardar el ID del pedido para pasos posteriores:**
```javascript
pm.globals.set("orderId", pm.response.json().id);
```

✉️ **Notificación generada automáticamente:**
- Asunto: "Pedido Confirmado"
- Mensaje: "Su pedido ha sido confirmado y está siendo procesado"
- Destinatario: cliente@example.com
- Estado: PENDING

---

### **[PEDIDOS] 4. Obtener Estado de Pedido**

**GET** `http://localhost:8000/api/pedidos/{{orderId}}`

**Headers:**
- Authorization: Bearer {{token}}

**Respuesta esperada (200 OK):**
```json
{
  "id": "order-550e8400",
  "customerId": "cust-123",
  "status": "PENDING",
  "totalAmount": 100.00,
  "items": [
    {
      "productId": "prod-001",
      "quantity": 2,
      "price": 50.00
    }
  ],
  "deliveryAddress": {
    "street": "Calle Principal 123",
    "city": "Ciudad de México",
    "state": "CDMX",
    "zip": "06500"
  },
  "createdAt": "2026-02-11T08:00:00.000000"
}
```

---

### **[FACTURACIÓN] 5. Crear Factura** ✨ *Genera notificación automáticamente*

**POST** `http://localhost:8000/api/billing/invoices`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "orderId": "{{orderId}}",
  "amount": 100.00,
  "description": "Servicio de Entrega Standard",
  "serviceType": "STANDARD",
  "emailCliente": "cliente@example.com"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": "inv-550e8400",
  "orderId": "order-550e8400",
  "status": "PENDING",
  "amount": 100.00,
  "createdAt": "2026-02-11T08:00:00.000000"
}
```

⚠️ **Guardar el ID de la factura:**
```javascript
pm.globals.set("invoiceId", pm.response.json().id);
```

✉️ **Notificación generada automáticamente:**
- Asunto: "Pedido Pagado"
- Mensaje: "Su factura ha sido creada. Total: $100.00"
- Destinatario: cliente@example.com
- Estado: PENDING

---

### **[FLOTA] 6. Registrar Vehículo**

**POST** `http://localhost:8000/api/fleet/vehicles`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "plate": "ABC-123",
  "make": "Honda",
  "model": "Civic",
  "year": 2023,
  "capacity": 500,
  "status": "AVAILABLE"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": "veh-550e8400",
  "plate": "ABC-123",
  "make": "Honda",
  "model": "Civic",
  "status": "AVAILABLE"
}
```

⚠️ **Guardar el ID del vehículo:**
```javascript
pm.globals.set("vehicleId", pm.response.json().id);
```

---

### **[FLOTA] 7. Registrar Conductor**

**POST** `http://localhost:8000/api/fleet/drivers`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phone": "555-1234",
  "licenseNumber": "DL-123456",
  "status": "AVAILABLE",
  "zone": "zone1"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": "driver-123",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "status": "AVAILABLE",
  "zone": "zone1"
}
```

⚠️ **Guardar el ID del conductor:**
```javascript
pm.globals.set("driverId", pm.response.json().id);
```

---

### **[RASTREO] 8. Iniciar Rastreo de Entrega** ✨ *Genera notificación automáticamente*

**POST** `http://localhost:8000/api/tracking/deliveries/{{orderId}}/start`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "driverId": "{{driverId}}",
  "vehicleId": "{{vehicleId}}",
  "estimatedDelivery": "2026-02-11T14:30:00.000Z",
  "notes": "Entrega estándar"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "trackingId": "track-550e8400",
  "orderId": "order-550e8400",
  "driverId": "driver-123",
  "vehicleId": "veh-550e8400",
  "status": "IN_TRANSIT",
  "estimatedArrival": "2026-02-11T14:30:00.000000",
  "createdAt": "2026-02-11T08:10:00.000000"
}
```

⚠️ **Guardar el ID de rastreo:**
```javascript
pm.globals.set("trackingId", pm.response.json().trackingId);
```

✉️ **Notificación generada automáticamente:**
- Asunto: "Tu pedido está en tránsito"
- Mensaje: "Tu pedido está siendo entregado. Conductor: Juan Pérez"
- Destinatario: cliente@example.com
- Estado: PENDING

---

### **[RASTREO] 9. Actualizar Ubicación en Tiempo Real** ✨ *Genera notificación automáticamente*

**POST** `http://localhost:8000/api/tracking/{{trackingId}}/location`

**Headers:**
- Authorization: Bearer {{token}}
- Content-Type: application/json

**Body (JSON):**
```json
{
  "latitude": 19.4326,
  "longitude": -99.1332,
  "timestamp": "2026-02-11T12:45:00.000Z",
  "accuracy": 10.5
}
```

**Respuesta esperada (200 OK):**
```json
{
  "trackingId": "track-550e8400",
  "latitude": 19.4326,
  "longitude": -99.1332,
  "updatedAt": "2026-02-11T12:45:00.000000"
}
```

✉️ **Notificación generada automáticamente:**
- Asunto: "Tu entrega está cerca"
- Mensaje: "El conductor está a 5 minutos de tu ubicación - Coordenadas: 19.4326, -99.1332"
- Destinatario: cliente@example.com
- Estado: PENDING

> 💡 **Tip:** Actualizar la ubicación varias veces para simular un viaje en curso

---

### **[NOTIFICACIONES] 10. Obtener Todas las Notificaciones**

**GET** `http://localhost:8000/api/notifications`

**Headers:**
- Authorization: Bearer {{token}}

**Respuesta esperada (200 OK):**
```json
[
  {
    "id": "notif-001",
    "orderId": "order-550e8400",
    "recipient": "cliente@example.com",
    "subject": "Pedido Confirmado",
    "message": "Su pedido ha sido confirmado y está siendo procesado",
    "type": "EMAIL",
    "status": "PENDING",
    "createdAt": "2026-02-11T08:00:00.000000"
  },
  {
    "id": "notif-002",
    "orderId": "order-550e8400",
    "recipient": "cliente@example.com",
    "subject": "Pedido Pagado",
    "message": "Su factura ha sido creada. Total: $100.00",
    "type": "EMAIL",
    "status": "PENDING",
    "createdAt": "2026-02-11T08:05:00.000000"
  },
  {
    "id": "notif-003",
    "orderId": "order-550e8400",
    "recipient": "cliente@example.com",
    "subject": "Tu pedido está en tránsito",
    "message": "Tu pedido está siendo entregado. Conductor: Juan Pérez",
    "type": "EMAIL",
    "status": "PENDING",
    "createdAt": "2026-02-11T08:10:00.000000"
  },
  {
    "id": "notif-004",
    "orderId": "order-550e8400",
    "recipient": "cliente@example.com",
    "subject": "Tu entrega está cerca",
    "message": "El conductor está a 5 minutos de tu ubicación - Coordenadas: 19.4326, -99.1332",
    "type": "EMAIL",
    "status": "PENDING",
    "createdAt": "2026-02-11T12:45:00.000000"
  }
]
```

---

### **Resumen de Eventos y Notificaciones Automáticas**

| Evento | Disparador | Notificación | Destinatario |
|--------|-----------|--------------|--------------|
| Pedido Creado | POST `/api/pedidos` | "Pedido Confirmado" | Email del cliente |
| Factura Creada | POST `/api/billing/invoices` | "Pedido Pagado" | Email del cliente |
| Rastreo Iniciado | POST `/api/tracking/*/start` | "Tu pedido está en tránsito" | Email del cliente |
| Ubicación Actualizada | POST `/api/tracking/*/location` | "Tu entrega está cerca" | Email del cliente |

> ✈️ Las notificaciones se envían automáticamente a través de **RabbitMQ** y se ejecutan de forma asincrónica en el servicio de **Notificaciones**.

---

## Configuración

### Variables de Entorno

Las variables se inyectan automáticamente mediante Docker. Para configuración manual, crear archivo `.env`:

```env
# PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_USER=admin
DB_PASSWORD=PamelaE1
DB_NAME=logiflow

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VHOST=/

# JWT
JWT_SECRET=your-secret-key-min-32-characters-long-for-security
JWT_EXPIRATION=3600000

# Logging
LOG_LEVEL=INFO
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Acceso a Base de Datos

```bash
# Conectar a PostgreSQL
psql -h localhost -p 5432 -U admin -d logiflow

# Listar tablas
\dt

# Salir
\q
```

### RabbitMQ Management

Interfaz web disponible en:
```
URL: http://localhost:15672
Usuario: guest
Contraseña: guest
```

---

## Monitoreo y Troubleshooting

### ✅ RabbitMQ Management UI

**Acceso correcto:**
```
URL: http://localhost:15672
Usuario: guest
Contraseña: guest
```

⚠️ **SI NO FUNCIONA:**
- Verificar que el contenedor de RabbitMQ está corriendo:
  ```bash
  docker ps | grep rabbitmq
  ```
- Si está corriendo pero no responde, reiniciar:
  ```bash
  docker compose down
  docker compose up -d
  sleep 30  # Esperar a que se estabilice
  ```
- Probar con curl:
  ```bash
  curl -u guest:guest http://localhost:15672/api/overview
  ```

---

### ✅ Dashboard de Kubernetes

**Acceso correcto:**
```bash
# Opción 1: Abrir dashboard automáticamente
minikube dashboard

# Opción 2: Obtener URL y acceder manualmente
minikube dashboard --url
# Copiar la URL mostrada en el navegador

# Opción 3: Usar port-forward (si las opciones anteriores no funcionan)
kubectl proxy &
# Luego acceder a: http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
```

⚠️ **SI NO FUNCIONA:**
- Verificar que Minikube está corriendo:
  ```bash
  minikube status
  ```
- Si está detenido, iniciar:
  ```bash
  minikube start --cpus=4 --memory=8192
  ```
- Verificar que el dashboard está instalado:
  ```bash
  minikube addons list | grep dashboard
  ```
- Si no está habilitado:
  ```bash
  minikube addons enable dashboard
  ```

---

### Verificación de Salud de Servicios

```bash
# API Gateway
curl http://localhost:8000/actuator/health

# Autenticación
curl http://localhost:8081/actuator/health

# Facturación
curl http://localhost:8082/actuator/health

# Flota
curl http://localhost:8083/actuator/health

# Pedidos
curl http://localhost:8084/actuator/health

# Rastreo
curl http://localhost:8090/actuator/health

# Notificaciones
curl http://localhost:8085/actuator/health
```

### Problemas Comunes

#### Puerto Ya en Uso

```bash
# Encontrar proceso usando el puerto
lsof -i :8000

# Terminar proceso
kill -9 <PID>
```

#### RabbitMQ no Conecta

```bash
# Reiniciar servicio en Docker
docker compose restart rabbitmq

# Ver logs
docker compose logs rabbitmq
```

#### Base de Datos sin Inicializar

```bash
# Limpiar volúmenes y reiniciar
docker compose down -v
docker compose up -d

# Ejecutar scripts de inicialización
docker compose exec postgresql psql -U admin -d logiflow -f /docker-entrypoint-initdb.d/01-init-db.sql
```

#### Servicios No se Comunican

```bash
# Verificar red Docker
docker network ls

# Inspeccionar red
docker network inspect logiflow_logiflow-network

# Probar conectividad entre contenedores
docker compose exec api-gateway ping auth-service
```

---

## Stack Tecnológico

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Java | 25 LTS |
| Framework Principal | Spring Boot | 3.5.8 |
| API Gateway | Spring Cloud Gateway | 4.1.5 |
| Base de Datos | PostgreSQL | 16.11 |
| Message Broker | RabbitMQ | 4.2+ |
| Autenticación | JWT (jjwt) | 0.12.3 |
| API GraphQL | Apollo + TypeScript | 4.0+ |
| Build Tool | Maven | 3.9+ |
| Contenedores | Docker | 24.0+ |
| Orquestación | Docker Compose | 2.0+ |
| Kubernetes | Kubernetes | 1.24+ |
| ORM | Hibernate | 6.4+ |
| Logging | SLF4J + Logback | 1.4+ |

---

## Documentación Adicional

Para información más detallada sobre despliegue con Docker, consultar:
- [DOCKER_GUIDE.md](logiflow/DOCKER_GUIDE.md)

Para manifiestos de Kubernetes disponibles en:
- [kubernetes/](kubernetes/)

---

## Notas de Versión

**v3.0 - Febrero 2026**

- Sistema de 7 microservicios completamente funcional
- Despliegue exitoso en Kubernetes y Docker Compose
- Sistema de notificaciones operacional
- Autenticación JWT implementada
- API Gateway con enrutamiento dinámico y reescritura de rutas
- RabbitMQ integrado para mensajería asincrónica
- PostgreSQL centralizada para persistencia de datos
- Servicios GraphQL para consultas flexibles
- Arquitectura escalable lista para producción

---

Proyecto desarrollado como trabajo académico en el curso de Sistemas Distribuidos.

Última actualización: 11 de febrero de 2026

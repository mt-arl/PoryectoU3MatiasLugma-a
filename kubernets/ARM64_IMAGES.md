# 🐳 Imágenes Docker - Arquitectura ARM64

Este proyecto utiliza imágenes Docker construidas específicamente para la arquitectura **ARM64** (Apple Silicon, Raspberry Pi, AWS Graviton, etc.).

## 🏗️ Compilación y Arquitectura

Todas las imágenes desplegadas en el clúster de Kubernetes han sido compiladas nativamente en una máquina con procesador **Apple Silicon (M1/M2/M3)**. Esto asegura el máximo rendimiento y compatibilidad al desplegar en entornos locales basados en ARM64 (como Minikube en Mac) o en instancias cloud ARM64.

### 📦 Imágenes Utilizadas

Las siguientes imágenes se encuentran alojadas en Docker Hub y están etiquetadas para su uso en este despliegue:

| Servicio | Imagen Docker Hub | Arquitectura |
|----------|-------------------|--------------|
| **API Gateway** | `mattlugma/api-gateway:latest` | `linux/arm64` |
| **Auth Service** | `mattlugma/auth-service:latest` | `linux/arm64` |
| **Billing Service** | `mattlugma/billing-service:latest` | `linux/arm64` |
| **Fleet Service** | `mattlugma/fleet-service:latest` | `linux/arm64` |
| **Pedido Service** | `mattlugma/pedido-service:latest` | `linux/arm64` |
| **Tracking Service** | `mattlugma/tracking-service:latest` | `linux/arm64` |
| **Notifications** | `mattlugma/ms-notifications:latest` | `linux/arm64` |
| **GraphQL Service** | `mattlugma/delivery-graphql-service:latest` | `linux/arm64` |

> **Nota:** Si intentas ejecutar estas imágenes en una arquitectura `linux/amd64` (Intel/AMD tradicional), podrías necesitar utilizar emulación (QEMU) o reconstruir las imágenes localmente.

## 🔄 Reconstrucción para AMD64 (Intel/AMD)

Si necesitas desplegar en una arquitectura x86_64 (amd64), te recomendamos reconstruir las imágenes localmente usando el código fuente:

```bash
# Ejemplo para reconstruir un servicio
cd logiflow/pedido-service
docker build -t pedido-service:local .
```

O utilizar Docker Buildx para crear imágenes multi-arquitectura:

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t usuario/imagen:tag . --push
```

## ☸️ Configuración en Kubernetes

El archivo `04-deploy.yaml` está configurado para extraer estas imágenes directamente de Docker Hub. Kubernetes intentará ejecutar la imagen en la arquitectura del nodo.

```yaml
spec:
  containers:
  - name: pedido-service
    image: mattlugma/pedido-service:latest  # Imagen ARM64
    imagePullPolicy: Always
```

Si tu clúster es ARM64, el despliegue funcionará de forma nativa y eficiente.

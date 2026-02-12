# Despliegue en Kubernetes

Este directorio contiene los manifiestos necesarios para desplegar **logiwflow** en un clúster de Kubernetes.

## 📋 Requisitos

- **Minikube** (o cualquier clúster K8s).
- **kubectl** configurado.
- **Docker** (para construir imágenes si no se usan las del registro).

## 🚀 Pasos de Despliegue

Sigue este orden estricto para evitar errores de dependencias:

1.  **Crear Namespace**
    ```bash
    kubectl apply -f 01-namespace.yaml
    ```

2.  **Desplegar Bases de Datos**
    ```bash
    kubectl apply -f 02-databases.yaml
    ```
    *Espera unos instantes para que los pods de base de datos estén `Running`.*

3.  **Desplegar Microservicios y Gateway**
    ```bash
    kubectl apply -f 04-deploy.yaml
    ```

4.  **Configurar Ingress (Opcional)**
    Si tienes activado el addon de ingress en minikube (`minikube addons enable ingress`):
    ```bash
    kubectl apply -f 03-ingress.yaml
    ```

## 🌐 Acceso a la Plataforma

### Opción A: Port Forwarding (Recomendado para pruebas rápidas)
Para acceder al **API Gateway** (punto de entrada principal) sin configurar DNS:

```bash
kubectl port-forward svc/api-gateway 8000:8000 -n logiflow
```
Ahora la API está disponible en `http://localhost:8000`.

### Opción B: Ingress (Dominios Locales)
Los archivos ingress están configurados para hostnames `.local`. Para que funcionen, debes obtener la IP de minikube:
```bash
minikube ip
```
Y añadir las siguientes entradas a tu archivo `/etc/hosts` (Mac/Linux) o `C:\Windows\System32\drivers\etc\hosts` (Windows):

```text
<MINIKUBE_IP> authservice.logiflow.local
<MINIKUBE_IP> billing-service.logiflow.local
<MINIKUBE_IP> fleet-service.logiflow.local
<MINIKUBE_IP> pedido-service.logiflow.local
<MINIKUBE_IP> tracking-service.logiflow.local
<MINIKUBE_IP> ms-notifications.logiflow.local
<MINIKUBE_IP> delivery-graphql-service.logiflow.local
```

## 🔍 Comandos Útiles

- **Ver todos los recursos**:
  ```bash
  kubectl get all -n logiflow
  ```
- **Ver logs de un pod**:
  ```bash
  kubectl logs -f <nombre-pod> -n logiflow
  ```
- **Reiniciar un despliegue**:
  ```bash
  kubectl rollout restart deployment/<nombre-deployment> -n logiflow
  ```

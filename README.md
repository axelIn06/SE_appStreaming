# 📡 StreamCusco: Sistema de Streaming IoT (Android to AWS)

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)
![AWS](https://img.shields.io/badge/Cloud-AWS%20EC2-232F3E?style=flat&logo=amazon-aws&logoColor=white)
![Nginx](https://img.shields.io/badge/Server-Nginx%20RTMP-009639?style=flat&logo=nginx&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)

> **Curso:** Sistemas Embebidos | **Fecha:** Diciembre 2025
> **Universidad Nacional de San Antonio Abad del Cusco (UNSAAC)**

## 📖 Descripción del Proyecto

Este proyecto implementa una solución completa de streaming de video en tiempo real orientada a IoT. Transforma un dispositivo móvil Android en una cámara IP inteligente que transmite video y audio mediante el protocolo **RTMP** hacia un servidor en la nube (AWS EC2).

El servidor procesa la señal utilizando **Nginx**, fragmentando el video en tiempo real para su distribución escalable vía **HLS (HTTP Live Streaming)**, permitiendo la visualización simultánea en múltiples clientes web (PC, Móvil, Smart TV).

---

## 🏗️ Arquitectura del Sistema

El sistema sigue una arquitectura de **Procesamiento Distribuido**:

```mermaid
graph LR
    A[📱 Android App<br>IoT Edge] -->|RTMP Stream<br>Port 1935| B(☁️ AWS EC2<br>Nginx Server)
    B -->|Transmuxing<br>HLS .ts/.m3u8| C[📂 Storage /var/www]
    C -->|HTTP Delivery<br>Port 80| D[💻 Web Client]
    C -->|HTTP Delivery<br>Port 80| E[📱 Mobile Client]


# 1. Actualizar repositorios e instalar Nginx + RTMP
sudo apt update
sudo apt install nginx libnginx-mod-rtmp -y

# 2. Configurar RTMP en /etc/nginx/nginx.conf
# (Ver archivo 'server_config/nginx.conf' incluido en este repo)

# 3. Crear directorios para HLS
sudo mkdir -p /var/www/html/hls
sudo chown -R www-data:www-data /var/www/html/hls


StreamCuscoApp/
├── app/
│   ├── src/main/java/.../MainActivity.kt   # Lógica principal (Camera & RTMP)
│   ├── src/main/AndroidManifest.xml        # Permisos del sistema
│   └── build.gradle.kts                    # Dependencias (RootEncoder)
├── server_config/
│   └── nginx.conf                          # Configuración de respaldo del servidor
├── web_client/
│   └── index.html                          # Interfaz Web del cliente
└── README.md                               # Documentación del proyecto

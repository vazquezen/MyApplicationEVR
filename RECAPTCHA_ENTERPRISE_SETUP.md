# reCaptcha Enterprise v3 - Configuración

## 🚨 **Error 404 - Configuración Necesaria**

Para que reCaptcha Enterprise funcione, necesitas configurar estos valores en `Constants.kt`:

```kotlin
// Reemplazar estos valores con los reales de tu Google Cloud Project
const val GOOGLE_CLOUD_PROJECT_ID = "tu-proyecto-real"  
const val GOOGLE_CLOUD_API_KEY = "tu-api-key-real"
```

## 📋 **Pasos para Obtener las Credenciales**

### **1. Google Cloud Console**
1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. **Crea o selecciona** tu proyecto
3. **Anota el PROJECT_ID** (ejemplo: `mi-app-12345`)

### **2. Habilitar APIs**
```bash
# Habilitar reCaptcha Enterprise API
gcloud services enable recaptchaenterprise.googleapis.com
```

### **3. Crear API Key**
1. **Ir a**: APIs & Services → Credentials
2. **Crear**: API Key
3. **Restringir**: Solo reCaptcha Enterprise API
4. **Copiar**: La API Key generada

### **4. Crear Site Key para Enterprise**
1. **Ir a**: Security → reCaptcha Enterprise
2. **Crear Site Key**:
   - **Type**: Score-based (v3)
   - **Platform**: Android
   - **Package name**: `com.evr.tes`
   - **SHA-1**: Tu certificado de debug/release

## 🔧 **Configuración Final**

Actualizar `Constants.kt`:

```kotlin
object Keys {
    // Site key de reCaptcha Enterprise
    const val RECAPTCHA_API_SITE_KEY = "6Le_JcQr..." // Tu site key real
    
    // Google Cloud Project Configuration
    const val GOOGLE_CLOUD_PROJECT_ID = "mi-app-12345"        // Tu project ID real
    const val GOOGLE_CLOUD_API_KEY = "AIzaSyD..."             // Tu API key real
    
    const val DEMO_MODE = false
}
```

## 🎯 **URL Final Esperada**

Con la configuración correcta, el endpoint será:
```
https://recaptchaenterprise.googleapis.com/v1/projects/mi-app-12345/assessments?key=AIzaSyD...
```

## 🔄 **Fallback a reCaptcha Estándar**

Si prefieres usar reCaptcha v3 estándar (no Enterprise), podemos cambiar a:

```kotlin
// En RecaptchaModule.kt
private const val GOOGLE_RECAPTCHA_BASE_URL = "https://www.google.com/recaptcha/api/"

// Y usar el endpoint estándar en RecaptchaService
@POST("siteverify")
suspend fun verifyTokenStandard(...)
```

## ❓ **¿Qué Prefieres?**

1. **Enterprise**: Más funciones, requiere Google Cloud Project + API Key
2. **Estándar**: Más simple, solo requiere site key + secret key

**¿Tienes el PROJECT_ID y API_KEY, o prefieres cambiar a reCaptcha v3 estándar?**
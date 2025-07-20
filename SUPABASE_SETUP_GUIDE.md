# 🚀 Guía de Configuración de Supabase para PillControlApp

## 📋 Pasos para Configurar Supabase desde Cero

### 1. 🆕 Crear Nueva Instancia de Supabase

1. Ve a [supabase.com](https://supabase.com)
2. Haz clic en "Start your project"
3. Selecciona tu organización o crea una nueva
4. Elige "Start from scratch"
5. Configura:
   - **Project name**: `pillcontrolapp` (o el nombre que prefieras)
   - **Database password**: Crea una contraseña segura
   - **Region**: Elige la más cercana a ti
6. Haz clic en "Create new project"

### 2. 🔑 Obtener Credenciales

Una vez creado el proyecto:

1. Ve a **Settings** → **API**
2. Copia:
   - **Project URL** (ej: `https://abcdefghijklmnop.supabase.co`)
   - **anon public** key (empieza con `eyJ...`)

### 3. 🔧 Actualizar Configuración en la App

Reemplaza en `SupabaseClientProvider.kt`:

```kotlin
private const val SUPABASE_URL = "TU_PROJECT_URL_AQUI"
private const val SUPABASE_ANON_KEY = "TU_ANON_KEY_AQUI"
```

### 4. 🗄️ Ejecutar Scripts SQL

1. Ve a **SQL Editor** en tu dashboard de Supabase
2. Copia y pega todo el contenido del archivo `supabase_setup.sql`
3. Haz clic en "Run" para ejecutar todos los scripts

### 5. 🔐 Configurar Autenticación

1. Ve a **Authentication** → **Settings**
2. En **Site URL**, agrega: `http://localhost:3000` (para desarrollo)
3. En **Redirect URLs**, agrega:
   - `http://localhost:3000/auth/callback`
   - `http://localhost:3000/auth/reset-callback`

### 6. 📁 Verificar Storage

1. Ve a **Storage** → **Buckets**
2. Verifica que existe el bucket `profile-images`
3. Verifica que está marcado como "Public"

### 7. 🧪 Probar la Configuración

1. Ejecuta la aplicación
2. Intenta registrarte con un nuevo usuario
3. Verifica que se crea automáticamente en la tabla `users`
4. Prueba editar el perfil y subir una imagen

## 📊 Estructura de Base de Datos Creada

### Tablas Principales:

| Tabla | Propósito | RLS |
|-------|-----------|-----|
| `users` | Perfiles de usuario | ✅ |
| `medications` | Medicamentos | ✅ |
| `reminders` | Recordatorios | ✅ |
| `medication_logs` | Historial de tomas | ✅ |

### Buckets de Storage:

| Bucket | Propósito | Público |
|--------|-----------|---------|
| `profile-images` | Imágenes de perfil | ✅ |

## 🔒 Seguridad Configurada

- ✅ **Row Level Security (RLS)** habilitado en todas las tablas
- ✅ **Políticas de acceso** configuradas para cada tabla
- ✅ **Triggers automáticos** para crear perfiles
- ✅ **Validación de datos** en el backend
- ✅ **Límites de archivo** en Storage (5MB)

## 🚀 Funcionalidades Habilitadas

### ✅ Completamente Funcional:
- **Login/Logout** con Supabase Auth
- **Registro de usuarios** con validación
- **Edición de perfil** con todos los campos
- **Subida de imágenes** a Storage
- **Validación de datos** en tiempo real
- **Navegación fluida** entre pantallas

### 🔮 Preparado para Futuras Funcionalidades:
- **Gestión de medicamentos**
- **Sistema de recordatorios**
- **Historial de tomas**
- **Estadísticas del usuario**

## 🐛 Solución de Problemas

### Error: "relation does not exist"
- Verifica que ejecutaste todos los scripts SQL
- Revisa que no haya errores en la consola de Supabase

### Error: "permission denied"
- Verifica que RLS esté habilitado
- Revisa que las políticas estén creadas correctamente

### Error: "bucket not found"
- Verifica que el bucket `profile-images` existe
- Revisa que las políticas de Storage estén configuradas

### Error: "authentication failed"
- Verifica que las credenciales en `SupabaseClientProvider.kt` sean correctas
- Revisa que el proyecto esté activo en Supabase

## 📞 Soporte

Si tienes problemas:
1. Revisa los logs en **Logs** → **Database** en Supabase
2. Verifica la configuración en **Settings** → **API**
3. Prueba las consultas directamente en **SQL Editor**

¡Tu aplicación PillControlApp ahora está completamente configurada y lista para usar! 🎉 
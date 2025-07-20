-- =====================================================
-- CONFIGURACIÓN COMPLETA DE SUPABASE PARA PILLCONTROLAPP
-- =====================================================

-- =====================================================
-- 0. FUNCIONES BASE
-- =====================================================

-- Función para actualizar timestamps (debe crearse primero)
CREATE OR REPLACE FUNCTION public.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 1. TABLA DE USUARIOS (PROFILES)
-- =====================================================

-- Crear la tabla users para almacenar información del perfil
CREATE TABLE IF NOT EXISTS public.users (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    email TEXT NOT NULL,
    full_name TEXT,
    phone TEXT,
    age TEXT,
    profile_image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Habilitar Row Level Security (RLS)
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad para la tabla users
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_policy 
        WHERE polname = 'Users can view own profile' 
        AND polrelid = 'public.users'::regclass
    ) THEN
        CREATE POLICY "Users can view own profile" ON public.users
            FOR SELECT USING (auth.uid() = id);
    END IF;
    
    -- Repetir el mismo bloque para cada política...
END $$;

-- (Aplicar el mismo patrón para las otras políticas con IF NOT EXISTS)

-- =====================================================
-- 2. FUNCIÓN PARA CREAR PERFIL AUTOMÁTICAMENTE
-- =====================================================

-- Función que se ejecuta cuando se registra un nuevo usuario
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.users (id, email, full_name)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.email)
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger para crear perfil automáticamente al registrarse
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Trigger para updated_at en users
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON public.users 
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- =====================================================
-- 3. CONFIGURACIÓN DE STORAGE PARA IMÁGENES
-- =====================================================

-- Crear bucket para imágenes de perfil
INSERT INTO storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
VALUES (
    'profile-images',
    'profile-images',
    true,
    5242880, -- 5MB limit
    ARRAY['image/jpeg','image/png','image/gif','image/webp']
) ON CONFLICT (id) DO NOTHING;

-- Políticas para el bucket profile-images
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_policy 
        WHERE polname = 'Users can upload profile images'
    ) THEN
        CREATE POLICY "Users can upload profile images" ON storage.objects
        FOR INSERT WITH CHECK (
            bucket_id = 'profile-images'
            AND auth.uid()::text = (storage.foldername(name))[1]
        );
    END IF;
    
    -- Repetir para las otras políticas de storage...
END $$;

-- =====================================================
-- 4. TABLAS DE MEDICAMENTOS, RECORDATORIOS Y LOGS
-- =====================================================

-- Crear tabla para medicamentos
CREATE TABLE IF NOT EXISTS public.medications (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    dosage TEXT,
    frequency TEXT,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Crear tabla para recordatorios
CREATE TABLE IF NOT EXISTS public.reminders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    medication_id UUID REFERENCES public.medications(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    reminder_time TIME NOT NULL,
    days_of_week INTEGER[], -- [1,2,3,4,5,6,7] donde 1=Lunes, 7=Domingo
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Crear tabla para historial de tomas
CREATE TABLE IF NOT EXISTS public.medication_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    medication_id UUID REFERENCES public.medications(id) ON DELETE CASCADE,
    reminder_id UUID REFERENCES public.reminders(id) ON DELETE SET NULL,
    taken_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    status TEXT CHECK (status IN ('taken', 'skipped', 'missed')) DEFAULT 'taken',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- =====================================================
-- 5. HABILITAR RLS Y POLÍTICAS PARA TABLAS NUEVAS
-- =====================================================

-- Configuración RLS para medications
ALTER TABLE public.medications ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can manage own medications" ON public.medications
    FOR ALL USING (auth.uid() = user_id);

-- Configuración RLS para reminders
ALTER TABLE public.reminders ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can manage own reminders" ON public.reminders
    FOR ALL USING (auth.uid() = user_id);

-- Configuración RLS para medication_logs
ALTER TABLE public.medication_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can manage own medication logs" ON public.medication_logs
    FOR ALL USING (auth.uid() = user_id);

-- =====================================================
-- 6. TRIGGERS PARA ACTUALIZACIÓN AUTOMÁTICA
-- =====================================================

CREATE TRIGGER update_medications_updated_at 
    BEFORE UPDATE ON public.medications 
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_reminders_updated_at 
    BEFORE UPDATE ON public.reminders 
    FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();

-- =====================================================
-- 7. ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_users_email ON public.users(email);
CREATE INDEX IF NOT EXISTS idx_medications_user_id ON public.medications(user_id);
CREATE INDEX IF NOT EXISTS idx_reminders_user_id ON public.reminders(user_id);
CREATE INDEX IF NOT EXISTS idx_medication_logs_taken_at ON public.medication_logs(taken_at);

-- =====================================================
-- 8. FUNCIÓN DE ESTADÍSTICAS OPTIMIZADA
-- =====================================================

CREATE OR REPLACE FUNCTION get_user_stats(user_uuid UUID)
RETURNS TABLE (
    total_medications INTEGER,
    active_medications INTEGER,
    total_reminders INTEGER,
    active_reminders INTEGER,
    today_taken INTEGER,
    today_missed INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        (SELECT COUNT(*) FROM public.medications WHERE user_id = user_uuid)::INTEGER,
        (SELECT COUNT(*) FROM public.medications WHERE user_id = user_uuid AND is_active)::INTEGER,
        (SELECT COUNT(*) FROM public.reminders WHERE user_id = user_uuid)::INTEGER,
        (SELECT COUNT(*) FROM public.reminders WHERE user_id = user_uuid AND is_active)::INTEGER,
        (SELECT COUNT(*) FROM public.medication_logs 
         WHERE user_id = user_uuid 
           AND status = 'taken' 
           AND taken_at::DATE = CURRENT_DATE)::INTEGER,
        (SELECT COUNT(*) FROM public.medication_logs 
         WHERE user_id = user_uuid 
           AND status = 'missed' 
           AND taken_at::DATE = CURRENT_DATE)::INTEGER;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- =====================================================
-- 9. CONFIGURACIÓN DE AUTENTICACIÓN POR EMAIL
-- =====================================================

-- Habilitar autenticación por email
-- NOTA: Esta configuración debe realizarse en el Dashboard de Supabase
-- bajo Authentication > Settings > Auth Providers > Email

-- Configuraciones recomendadas para el Dashboard de Supabase:

-- 1. EMAIL AUTH SETTINGS:
--    - Enable Email Signup: ✅ ON
--    - Enable Email Confirmations: ✅ ON
--    - Enable Secure Email Change: ✅ ON
--    - Double Confirm Changes: ✅ ON
--    - Enable Phone Confirmations: ❌ OFF (opcional)

-- 2. EMAIL TEMPLATES:
--    - Confirm signup: Personalizar con el código de verificación
--    - Reset password: Personalizar con el código de verificación
--    - Change email address: Personalizar con el código de verificación

-- 3. EMAIL SETTINGS:
--    - Sender Name: "PillControl App"
--    - Sender Email: tu-email@dominio.com
--    - SMTP Settings: Configurar servidor SMTP si es necesario

-- 4. SECURITY SETTINGS:
--    - JWT Expiry: 3600 (1 hora)
--    - Refresh Token Rotation: ✅ ON
--    - Refresh Token Reuse Interval: 10 (segundos)

-- =====================================================
-- 10. CONFIGURACIÓN DE EMAIL TEMPLATES (OPCIONAL)
-- =====================================================

-- NOTA: Los templates de email se configuran en el Dashboard de Supabase
-- bajo Authentication > Email Templates

-- Template recomendado para "Confirm signup":
/*
Subject: Confirma tu cuenta - PillControl App

Hola,

Gracias por registrarte en PillControl App. Para completar tu registro, 
por favor ingresa el siguiente código de verificación:

{{ .Token }}

Este código expira en 1 hora.

Si no solicitaste esta cuenta, puedes ignorar este correo.

Saludos,
El equipo de PillControl App
*/

-- Template recomendado para "Reset password":
/*
Subject: Restablece tu contraseña - PillControl App

Hola,

Has solicitado restablecer tu contraseña. Para continuar, 
ingresa el siguiente código de verificación:

{{ .Token }}

Este código expira en 1 hora.

Si no solicitaste este cambio, puedes ignorar este correo.

Saludos,
El equipo de PillControl App
*/

-- =====================================================
-- 11. VERIFICACIÓN FINAL
-- =====================================================

-- Verificar que todas las tablas se crearon correctamente
SELECT 
    table_name,
    table_type
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('users', 'medications', 'reminders', 'medication_logs')
ORDER BY table_name;

-- Verificar que el bucket se creó correctamente
SELECT 
    id,
    name,
    public,
    file_size_limit
FROM storage.buckets 
WHERE id = 'profile-images';

-- Verificar que las funciones se crearon correctamente
SELECT 
    routine_name,
    routine_type
FROM information_schema.routines 
WHERE routine_schema = 'public' 
AND routine_name IN ('handle_new_user', 'update_updated_at_column', 'get_user_stats')
ORDER BY routine_name;
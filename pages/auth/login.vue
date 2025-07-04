<script setup lang="ts">
import { useToast } from 'primevue/usetoast'
import { reactive, ref } from 'vue'

const submitForm = ref(false)
const { signIn } = useAuth()
const toast = useToast()

definePageMeta({
  layout: 'empty',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/', // ✅ Redirigir a index
  }
})

// Interface para el formulario
interface FormField {
  value: string
  label: string
  type: string
  isRequired: boolean
  showRequiredLabel: boolean
  placeholder: string
  validationKeywords: string[]
  errorMessage: string[]
  haveError: boolean
  showFeedBack?: boolean
}

interface LoginForm {
  username: FormField
  password: FormField
}

const form: LoginForm = reactive({
  username: {
    value: '',
    label: 'Email',
    type: 'text',
    isRequired: true,
    showRequiredLabel: false,
    placeholder: 'Enter your email here',
    validationKeywords: ['isEmail'],
    errorMessage: [],
    haveError: false
  },
  password: {
    value: '',
    label: 'Password',
    type: 'password',
    isRequired: true,
    showRequiredLabel: false,
    showFeedBack: false,
    placeholder: 'Enter your password here',
    validationKeywords: [],
    errorMessage: [],
    haveError: false
  },
})

const loading = ref(false)
const { executeRecaptcha } = useGoogleRecaptcha()

// Interface para errores
interface AuthError {
  message?: string
  statusCode?: number
  cause?: {
    err?: {
      message?: string
    }
  }
}

function onError(errorMessage: string) {
  toast.add({
    severity: 'error',
    summary: 'Error',
    detail: `${errorMessage}`,
    life: 5000
  })
}

async function submitAndReloadForm() {
  submitForm.value = true
}

function validateForm(): boolean {
  let isValid = true

  // Usar Object.values para evitar problemas de tipado
  Object.values(form).forEach((field) => {
    if (field.haveError) {
      isValid = false
    }
  })

  return isValid
}

// Función de login corregida
async function login() {
  const username = form.username.value
  const password = form.password.value

  await submitAndReloadForm()
  const isValid = validateForm()

  if (!isValid) {
    return
  }

  loading.value = true

  try {
    // HACER reCAPTCHA OPCIONAL PARA TESTING
    let token: string | null = null
    try {
      const captchaResult = await executeRecaptcha('login')
      token = captchaResult?.token || null
      Logger.log('✅ reCAPTCHA token obtained (optional)')
    }
    catch (captchaError) {
      Logger.warn('⚠️ reCAPTCHA failed, continuing without it:', captchaError)
    }

    // En Nuxt Auth, usar redirect: false para manejar manualmente
    const result = await signIn('credentials', {
      username,
      password,
      tokenCaptcha: token,
      redirect: false
    })

    Logger.log('📊 Login result:', result) // Para debugging

    // Verificar si hubo error
    if (result?.error) {
      Logger.error('❌ Authentication error:', result.error)

      if (result.error === 'CredentialsSignin' || result.error === 'CallbackRouteError') {
        onError('Invalid username or password')
      }
      else if (result.error.includes('Password change required')) {
        await navigateTo('/auth/change-password')
        return
      }
      else {
        onError(result.error)
      }

      loading.value = false
      return
    }

    Logger.log('✅ Authentication successful')

    // VERIFICAR EL ESTADO DE AUTENTICACIÓN DESPUÉS DEL SIGNIN
    const { status, data } = useAuth()

    // Esperar un momento para que se actualice el estado
    await nextTick()

    Logger.log('📊 Auth status after signIn:', {
      status: status.value,
      hasData: !!data.value,
      userData: data.value
    })

    // Si está autenticado, hacer la redirección
    if (status.value === 'authenticated') {
      Logger.log('🎉 User is authenticated, redirecting to index...')

      // ✅ CAMBIO: Redirigir a /
      await navigateTo('/')
    }
    else {
      // Si aún no está autenticado, esperar un poco más
      Logger.log('⏳ Waiting for authentication state to update...')

      setTimeout(async () => {
        if (status.value === 'authenticated') {
          Logger.log('🎉 User authenticated after delay, redirecting to index...')
          // ✅ También aquí cambiar a /
          await navigateTo('/')
        }
        else {
          Logger.error('❌ Authentication state not updated, trying fallback redirect')
          // ✅ Fallback también a /
          window.location.href = '/'
        }
      }, 1000)
    }
  }
  catch (error: unknown) {
    Logger.error('💥 Login error:', error)

    // Type guard para manejar el error de tipo unknown
    const authError = error as AuthError

    // Manejar diferentes tipos de errores
    if (authError.statusCode === 428) {
      await navigateTo('/auth/change-password')
    }
    else if (authError.statusCode === 401) {
      onError('Invalid username or password')
    }
    else if (authError.statusCode === 429) {
      onError('Too many login attempts. Please try again later')
    }
    else if (authError.message?.includes('Failed to fetch')) {
      onError('Network error. Please check your connection')
    }
    else if (authError.cause?.err?.message) {
      // Manejar errores específicos del servidor
      const serverError = authError.cause.err.message
      if (serverError.includes('Password change required')) {
        await navigateTo('/auth/change-password')
        return
      }
      else {
        onError(serverError)
      }
    }
    else {
      onError('Authentication failed. Please try again')
    }

    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <!-- Panel izquierdo - Formulario -->
      <div class="login-panel login-form-panel">
        <div class="form-content">
          <div class="form-header">
            <h1 class="form-title">
              Welcome back!
            </h1>
            <p class="form-subtitle">
              Sign in to your account to continue
            </p>
          </div>

          <form class="login-form" @submit.prevent="login">
            <TextField
              id="username"
              :submit="submitForm"
              :properties="form.username"
              :validation-keywords="form.username.validationKeywords"
              @update:model-value="form.username.value = $event"
              @update:error-messages="form.username.errorMessage = $event"
              @invalid-field="form.username.haveError = $event"
            />
            <PasswordField
              id="password"
              :submit="submitForm"
              :properties="form.password"
              :validation-keywords="form.password.validationKeywords"
              @update:model-value="form.password.value = $event"
              @update:error-messages="form.password.errorMessage = $event"
              @invalid-field="form.password.haveError = $event"
            />
            <Button
              type="submit"
              icon="pi pi-sign-in"
              class="login-button"
              :disabled="loading"
            >
              <i v-if="loading" class="pi pi-spin pi-spinner" style="font-size: 1.2rem" />
              <span v-else class="font-semibold">Sign In</span>
            </Button>
          </form>

          <div class="form-footer">
            <NuxtLink href="/auth/forgotPassword" class="forgot-password">
              Forgot your password?
            </NuxtLink>
          </div>
        </div>
      </div>

      <!-- Panel derecho - Branding -->
      <div class="login-panel brand-panel">
        <div class="brand-content">
          <div class="brand-header">
            <div class="logo-container">
              <NuxtImg src="/images/logo.svg" class="brand-logo" />
            </div>
            <h1 class="brand-title">
              Finamer Hub
            </h1>
            <p class="brand-subtitle">
              Your centralized gateway to financial clarity and operational control.
            </p>
          </div>

          <div class="illustration-container">
            <img src="/assets/images/home.webp" alt="Financial Hub" class="brand-illustration">
          </div>

          <div class="brand-footer">
            <div class="copyright-info">
              <span class="copyright-text">© 2024 Finamer Hub. All rights reserved.</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <Toast position="top-right" :base-z-index="5" />
  </div>
</template>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(0deg, #62a1e2e6, #a4c8ed 56%, #f5f7fbe6);
  padding: 2rem;
}

.login-card {
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  background-color: #ffffff;
}

.login-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

// Panel del formulario (izquierdo)
.login-form-panel {
  padding: 0;
  background-color: #ffffff;
}

.form-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 3rem 2.5rem;
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.form-title {
  font-size: 2.25rem;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 0.75rem 0;
  line-height: 1.2;
}

.form-subtitle {
  font-size: 1rem;
  color: #718096;
  margin: 0;
  font-weight: 400;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  margin-bottom: 1.5rem;
}

// Estilos para los campos del formulario
.login-form :deep(.p-inputtext),
.login-form :deep(.p-password input) {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  padding: 0 1rem;
  transition: all 0.2s ease;
  box-sizing: border-box;

  &:focus {
    border-color: #4a90d9;
    box-shadow: 0 0 0 3px rgba(74, 144, 217, 0.15);
    outline: none;
  }
}

// Específico para el contenedor del campo de contraseña
.login-form :deep(.p-password) {
  width: 100%;
  position: relative;

  .p-password-input {
    width: 100%;
    height: 48px;
    font-size: 1rem;
    border-radius: 8px;
    border: 2px solid #e2e8f0;
    padding: 0 1rem;
    padding-right: 3rem; // Espacio para el botón de mostrar/ocultar
    box-sizing: border-box;
    transition: all 0.2s ease;

    &:focus {
      border-color: #4a90d9;
      box-shadow: 0 0 0 3px rgba(74, 144, 217, 0.15);
      outline: none;
    }
  }

  .p-password-toggle-mask {
    right: 12px;
    color: #718096;

    &:hover {
      color: #4a90d9;
    }
  }
}

// Reset para evitar estilos duplicados
.login-form :deep(.p-password .p-password-input) {
  display: block !important;
  position: relative !important;
}

// Asegurar que no haya duplicación visual
.login-form :deep(.p-inputwrapper) {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  font-weight: 600;
  background: linear-gradient(135deg, #4a90d9 0%, #6ba3e0 100%);
  border: none;
  border-radius: 8px;
  color: white;
  transition: all 0.2s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 10px 25px rgba(74, 144, 217, 0.4);
    background: linear-gradient(135deg, #3a7bc8 0%, #5b93cf 100%);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
}

.form-footer {
  text-align: center;
  margin-top: 1rem;
}

.forgot-password {
  font-size: 0.9rem;
  color: #4a90d9;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;

  &:hover {
    color: #3a7bc8;
    text-decoration: underline;
  }
}

// Panel de branding (derecho)
.brand-panel {
  background: linear-gradient(135deg, #4a90d9 0%, #6ba3e0 50%, #a4c8ed 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.brand-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 3rem 2.5rem;
  text-align: center;
  position: relative;
  z-index: 2;
}

.brand-header {
  flex-shrink: 0;
}

.logo-container {
  margin-bottom: 1.5rem;
}

.brand-logo {
  width: 64px;
  height: 64px;
  filter: brightness(0) invert(1);
}

.brand-title {
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 0 1rem 0;
  line-height: 1.1;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
  line-height: 1.5;
  max-width: 300px;
  margin: 0 auto;
}

.illustration-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
}

.brand-illustration {
  max-width: 80%;
  height: auto;
  opacity: 0.95;
  filter: drop-shadow(0 10px 25px rgba(0, 0, 0, 0.1));
  background: transparent;
  mix-blend-mode: multiply;
}

.brand-footer {
  flex-shrink: 0;
  padding-top: 2rem;
}

.copyright-info {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 1.5rem;
}

.copyright-text {
  font-size: 0.85rem;
  opacity: 0.8;
}

// Responsive design
@media (max-width: 768px) {
  .login-container {
    padding: 1rem;
    align-items: flex-start;
    padding-top: 2rem;
  }

  .login-card {
    flex-direction: column;
    max-width: 500px;
    min-height: auto;
  }

  .form-content {
    padding: 2rem 1.5rem;
  }

  .brand-content {
    padding: 2rem 1.5rem;
    min-height: 300px;
  }

  .form-title {
    font-size: 1.75rem;
  }

  .brand-title {
    font-size: 2rem;
  }

  .brand-illustration {
    max-width: 60%;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 0.5rem;
  }

  .form-content {
    padding: 1.5rem 1rem;
  }

  .brand-content {
    padding: 1.5rem 1rem;
    min-height: 250px;
  }

  .form-title {
    font-size: 1.5rem;
  }

  .brand-title {
    font-size: 1.75rem;
  }
}
</style>

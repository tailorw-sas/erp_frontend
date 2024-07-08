<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from 'primevue/usetoast'

const route = useRoute()
const submitForm = ref(false)
const toast = useToast()

definePageMeta({
  layout: 'empty',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/',
  },
})

const form = reactive(
  {
    otp: {
      value: '',
      label: '',
      isRequired: true,
      showRequiredLabel: false,
    },
    newPassword: {
      value: '',
      label: 'Nueva Contraseña',
      type: 'password',
      isRequired: true,
      showRequiredLabel: false,
      showFeedBack: false,
      placeholder: 'Escriba su contraseña',
      validationKeywords: [],
      errorMessage: [],
      haveError: false
    },
    confirmPassword: {
      value: '',
      label: 'Confirmar Contraseña',
      type: 'password',
      isRequired: true,
      showRequiredLabel: false,
      showFeedBack: false,
      placeholder: 'Escriba su contraseña',
      validationKeywords: [],
      errorMessage: [],
      haveError: false
    },
  }
)
const loading = ref(false)

const { layoutConfig } = useLayout()

const email = route.query ? route.query.email : ''

const { $api } = useNuxtApp()
const repo = repository($api)

const { executeRecaptcha } = useGoogleRecaptcha()

async function changePassword() {
  try {
    loading.value = true
    if (form.newPassword.value !== form.confirmPassword.value) {
      // TODO: Show toast error if there is an error
      toast.add({ severity: 'error', summary: 'Error', detail: 'Las contraseñas no coinciden', life: 3000 })
      return
    }

    const { token } = await executeRecaptcha('recoverPassword')
    await repo.changePasswordWithOtp({ payload: { newPassword: form.newPassword.value, otp: form.otp.value, email }, tokenCaptcha: token })
    loading.value = false
    navigateTo({ path: '/auth/login' })
  }
  catch (error) {
    // TODO: Show toast error if there is an error
    toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 })
    loading.value = false
  }
}

function goHome() {
  navigateTo({ path: '/' })
}
</script>

<template>
  <div :class="`login-body flex min-h-screen  ${layoutConfig.colorScheme.value === 'light' ? 'layout-light' : 'layout-dark'}`">
    <div class="login-image w-6 h-screen hidden md:block" style="max-width: 490px">
      <NuxtImg :src="`/demo/images/pages/verification-${layoutConfig.colorScheme.value === 'dark' ? 'ondark' : 'onlight'}.png`" alt="atlantis" class="h-screen w-full" />
    </div>
    <div class="w-full" style="background: var(--surface-ground)">
      <div
        class="p-fluid min-h-screen bg-auto md:bg-contain bg-no-repeat text-center w-full flex align-items-center md:align-items-start justify-content-center flex-column bg-auto md:bg-contain bg-no-repeat"
        style="padding: 20% 10% 20% 10%; background: var(--exception-pages-image)"
      >
        <div class="flex flex-column">
          <div class="flex justify-content-center mb-6 logo-container">
            <NuxtImg
              :src="`/layout/images/logo/logo-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.svg`"
              class="login-logo" style="width: 150px"
            />
          </div>
          <div class="form-container">
            <h4 class="mb-5">
              Código de Verificación
            </h4>
            <div class="flex mt-1 mb-2" style="width: 300px; text-align: center">
              <span class="text-600 font-medium">
                Escriba el código de seguridad enviado a su correo electrónico:</span>
            </div>
            <div class="flex mb-3 justify-content-center" style="width: 300px; text-align: center !important">
              <span class="text-900 font-bold justify-content-center">{{ email }}</span>
            </div>

            <div class="mb-5" style="text-align: start">
              <InputOtp v-model="form.otp.value" mask :length="6" style="width: 300px;" />
            </div>
            <hr>
            <div class="mb-3">
              <PasswordFieldComponent
                :properties="form.newPassword"
                :validation-keywords="form.newPassword.validationKeywords" style="width: 300px;"
                :submit="submitForm" @update:model-value="form.newPassword.value = $event"
                @update:error-messages="form.newPassword.errorMessage = $event"
                @invalid-field="form.newPassword.haveError = $event"
              />
            </div>
            <div class="mb-3">
              <PasswordFieldComponent
                :properties="form.confirmPassword"
                :validation-keywords="form.confirmPassword.validationKeywords" style="width: 300px;"
                :submit="submitForm" @update:model-value="form.confirmPassword.value = $event"
                @update:error-messages="form.confirmPassword.errorMessage = $event"
                @invalid-field="form.confirmPassword.haveError = $event"
              />
            </div>
          </div>
          <div class="button-container mt-4 text-left" style="max-width: 320px; min-width: 270px">
            <div class="buttons flex align-items-center gap-3">
              <Button class="block" severity="danger" outlined style="max-width: 320px; margin-bottom: 32px" @click="goHome">
                Cancelar
              </Button>
              <Button class="block" style="max-width: 320px; margin-bottom: 32px" :disabled="loading" @click="changePassword">
                Verificar
              </Button>
            </div>
          </div>
        </div>

        <div class="login-footer flex justify-content-center align-items-center absolute" style="bottom: 75px;">
          <div class="flex align-items-center login-footer-logo-container pr-4 mr-4 border-right-1 surface-border">
            <NuxtImg
              :src="`/layout/images/logo/logo-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.svg`"
              class="login-logo" style="width: 100px"
            />
          </div>
          <span class="text-sm text-color-secondary mr-3">Copyright 2024</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
:deep(input) {
  background-color: white !important;
}
</style>

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
      label: 'New Password',
      type: 'password',
      isRequired: true,
      showRequiredLabel: false,
      showFeedBack: false,
      placeholder: 'Enter your new password here',
      validationKeywords: [],
      errorMessage: [],
      haveError: false
    },
    confirmPassword: {
      value: '',
      label: 'Confirm Password',
      type: 'password',
      isRequired: true,
      showRequiredLabel: false,
      showFeedBack: false,
      placeholder: 'Confirm your new password here',
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
    <div id="login-image" class="login-image w-9 h-screen hidden md:block" />
    <div id="login-panel" class="login-panel w-full">
      <div
        class="p-fluid min-h-screen bg-auto md:bg-contain bg-no-repeat text-center w-full flex align-items-center md:align-items-center justify-content-center flex-column bg-auto md:bg-contain bg-no-repeat"
        style="background: var(--exception-pages-image)"
      >
        <div class="flex flex-column">
          <div class="flex justify-content-center mb-5 logo-container">
            <NuxtImg src="/images/logo.svg" class="login-logo" style="width: 150px" />
          </div>
          <div class="form-container flex flex-column align-items-center justify-content-center">
            <div class="flex flex-column align-items-center mb-4">
              <h4 class="mb-5">
                Verification Code
              </h4>
              <span class="text-600 font-medium mb-2">
                Enter the security code sent to your email:</span>
              <div class="flex mb-3 justify-content-center" style="width: 300px; text-align: center !important">
                <span class="text-900 font-bold justify-content-center">{{ email }}</span>
              </div>
            </div>
            <div class="mb-4" style="text-align: start">
              <InputOtp v-model="form.otp.value" mask :length="6" style="width: 250px;" />
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
          <div class="button-container mb-3 mt-2" style="width: 300px;">
            <Button
              type="button" icon="pi pi-ref" class="w-full flex justify-content-center"
              style="height: 35px; background-color: #101253; border: none;" :disabled="loading" @click="changePassword"
            >
              <i v-if="loading" class="pi pi-spin pi-spinner" style="font-size: 1.5rem" />
              <span v-else class="font-semibold">Change Password</span>
            </Button>
          </div>
        </div>

        <div class="login-footer flex justify-content-center align-items-center mt-1" style="bottom: 75px;">
          <div class="flex align-items-center login-footer-logo-container pr-4 mr-4 border-right-1 surface-border">
            <NuxtImg src="/images/logo.svg" class="login-logo" style="width: 57px" />
          </div>
          <span class="text-sm text-color-secondary mr-3">Copyright 2024</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
#login-image {
  background-image: url('/images/login_image.webp');
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
}

#login-panel {
  background: rgb(98, 161, 226);
  background: linear-gradient(0deg, rgba(98, 161, 226, 0.9) 0%, rgba(164, 200, 237, 1) 56%, rgba(245, 247, 251, 0.9) 100%);
}

:deep(input) {
  background-color: white !important;
}
</style>

<script setup lang="ts">
import { useToast } from 'primevue/usetoast'
import { reactive, ref } from 'vue'

const submitForm = ref(false)

const { $api } = useNuxtApp()
const repo = repository($api)
const toast = useToast()

definePageMeta({
  layout: 'empty',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/',
  }
})

const form = reactive(
  {
    otp: {
      value: '',
      label: '',
      isRequired: true,
      showRequiredLabel: false,
      col: 12
    },
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
      placeholder: 'Enter your new password here',
      validationKeywords: [],
      errorMessage: [],
      haveError: false
    },
    oldPassword: {
      value: '',
      label: 'Old Password',
      type: 'password',
      isRequired: true,
      showRequiredLabel: false,
      showFeedBack: false,
      placeholder: 'Enter your old password here',
      validationKeywords: [],
      errorMessage: [],
      haveError: false,
      col: 12
    },
  }
)
const loading = ref(false)
async function submitAndReloadForm() {
  submitForm.value = true
}

function validateForm() {
  let isValid = true
  Object.keys(form).forEach((key) => {
    const field = form[key]
    if (field.haveError) {
      isValid = false
    }
  })
  return isValid
}

async function changePass() {
  await submitAndReloadForm()
  const isValid = validateForm()
  if (!isValid) {
    return
  }
  try {
    loading.value = true
    const payload = { email: form.username.value, newPassword: form.password.value, oldPassword: form.oldPassword.value }
    await repo.changePasswordFirstTime(payload)
    toast.add({ severity: 'info', summary: 'Password Changed', detail: 'Your password has been changed successfully.', life: 3000 })
    setTimeout(() => {
      navigateTo('/auth/login')
    }, 3000)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-body flex min-h-screen layout-light">
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
            <div v-if="false" class="flex flex-column align-items-center mb-4" style="width: 400px;">
              <p>
                For security reasons, it is necessary to change your password the first time you log in. A code has been sent to your email. Please enter it below.
              </p>
              <InputOtp v-model="form.otp.value" :length="6" style="width: 250px" />
            </div>
            <div class="mb-3">
              <TextField
                id="username"
                :submit="submitForm" :properties="form.username"
                :validation-keywords="form.username.validationKeywords" style="width: 300px;"
                @update:model-value="form.username.value = $event"
                @update:error-messages="form.username.errorMessage = $event"
                @invalid-field="form.username.haveError = $event"
              />
            </div>
            <div class="mb-3">
              <PasswordField
                id="oldPassword"
                :submit="submitForm" :properties="form.oldPassword"
                :validation-keywords="form.oldPassword.validationKeywords" style="width: 300px;"
                @update:model-value="form.oldPassword.value = $event"
                @update:error-messages="form.oldPassword.errorMessage = $event"
                @invalid-field="form.oldPassword.haveError = $event"
              />
            </div>
            <div class="mb-3">
              <PasswordField
                id="password"
                :submit="submitForm" :properties="form.password"
                :validation-keywords="form.password.validationKeywords" style="width: 300px;"
                @update:model-value="form.password.value = $event"
                @update:error-messages="form.password.errorMessage = $event"
                @invalid-field="form.password.haveError = $event"
              />
            </div>
            <div class="button-container mb-3 mt-2" style="width: 300px;">
              <Button
                type="button" icon="pi pi-ref" class="w-full flex justify-content-center"
                style="height: 35px; background-color: #101253; border: none;" :disabled="loading" @click="changePass"
              >
                <i v-if="loading" class="pi pi-spin pi-spinner" style="font-size: 1.5rem" />
                <span v-else class="font-semibold">Change Password</span>
              </Button>
            </div>
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

    <Toast position="top-center" :base-z-index="5" />
  </div>
</template>

<style lang="scss" scoped>
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

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { usePrimeVue } from 'primevue/config'
import { signOut } from 'next-auth/react'

const props = defineProps({
  message: {
    type: String,
    required: false,
    default: 'Are you sure you want to delete the selected record?'
  },
  openDialog: {
    type: Boolean,
    required: true
  }
})

const { data } = useAuth()
const toast = useToast()

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})

const loading = ref(false)
const dialogVisible = ref(props.openDialog)

const form = reactive(
  {
    otp: {
      value: '',
      label: '',
      isRequired: true,
      showRequiredLabel: false,
      col: 12
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
      haveError: false,
      col: 12
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
      haveError: false,
      col: 12
    },
  }
)

const { $api } = useNuxtApp()
const repo = repository($api)

const { executeRecaptcha } = useGoogleRecaptcha()

async function openResetPassword() {
  const { token } = await executeRecaptcha('sendOtpResetPassword')
  await repo.sendCodeOtpLogged({ email: data.value!.user!.email!, tokenCaptcha: token })
}

async function resetPassword() {
  try {
    loading.value = true

    if (form.newPassword.value === '' || form.confirmPassword.value === '') {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Las contraseñas no pueden estar vacias', life: 3000 })
    }

    if (form.newPassword.value !== form.confirmPassword.value && form.confirmPassword.value !== '') {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Las contraseñas no coinciden', life: 3000 })
    }
    const { token } = await executeRecaptcha('resetPassword')
    await repo.changePasswordLogged({ payload: { newPassword: form.newPassword.value, otp: form.otp.value }, tokenCaptcha: token })
    loading.value = false
    dialogVisible.value = false
    signOut({ callbackUrl: '/auth/login' })
  }
  catch (error) {
    // TODO: Show toast error if there is an error

  }
  finally {
    loading.value = false
  }
}

async function cancelResetPassword() {
  dialogVisible.value = false
  form.otp.value = ''
  form.newPassword.value = ''
  form.confirmPassword.value = ''
  await navigateTo({ path: '/' })
}

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
})

onMounted(async () => {
  await openResetPassword()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="Cambiar contraseña"
    class="mx-3 sm:mx-0 sm:w-full md:w-3"
    content-class="border-round-bottom border-top-1 surface-border"
    @hide="dialogVisible = false"
  >
    <div class="grid p-fluid formgrid">
      <div class="col-12">
        <div class="flex justify-content-center mt-5 mb-2">
          <span class="text-600 font-medium" style="text-align: center">
            Escriba el código de seguridad enviado a su correo electrónico:
          </span>
        </div>
        <div class="flex mb-3 justify-content-center">
          <span class="text-900 font-bold justify-content-center">{{ data.user?.email }}</span>
        </div>

        <div class="flex justify-content-center col-12 mb-5">
          <InputOtp v-model="form.otp.value" mask :length="6" />
        </div>
        <hr>
        <div class="col-12">
          <div class="mb-3">
            <PasswordFieldComponent
              :properties="form.newPassword"
              :validation-keywords="form.newPassword.validationKeywords"
              :submit="submitForm" @update:model-value="form.newPassword.value = $event"
              @update:error-messages="form.newPassword.errorMessage = $event"
              @invalid-field="form.newPassword.haveError = $event"
            />
          </div>
          <div class="mb-3">
            <PasswordFieldComponent
              :properties="form.confirmPassword"
              :validation-keywords="form.confirmPassword.validationKeywords"
              :submit="submitForm" @update:model-value="form.confirmPassword.value = $event"
              @update:error-messages="form.confirmPassword.errorMessage = $event"
              @invalid-field="form.confirmPassword.haveError = $event"
            />
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <Button label="Cancelar" icon="pi pi-times" severity="secondary" text @click="cancelResetPassword" />
      <Button label="Cambiar contraseña" icon="pi pi-check" text :disabled="loading" @click="resetPassword" />
    </template>
  </Dialog>
</template>

<style scoped lang="scss">
  :deep(input) {
    background-color: white !important;
  }
</style>

<script setup>
import { useRouter } from 'vue-router'

definePageMeta({
  layout: 'empty'
})

const { layoutConfig } = useLayout()
const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<template>
  <div :class="`login-body flex min-h-screen  ${layoutConfig.colorScheme.value === 'light' ? 'layout-light' : 'layout-dark'}`">
    <div class="login-image w-6 h-screen hidden md:block" style="max-width: 490px">
      <NuxtImg :src="`/demo/images/pages/lock-${layoutConfig.colorScheme.value === 'dark' ? 'ondark' : 'onlight'}.png`" alt="atlantis" class="h-screen w-full" />
    </div>
    <div class="w-full" style="background: var(--surface-ground)">
      <div
        class="p-fluid min-h-screen bg-auto md:bg-contain bg-no-repeat text-center w-full flex align-items-center md:align-items-start justify-content-center flex-column bg-auto md:bg-contain bg-no-repeat"
        style="padding: 20% 10% 20% 10%; background: var(--exception-pages-image)"
      >
        <div class="flex flex-column">
          <div class="flex align-items-center mb-6 logo-container">
            <NuxtImg :src="`/layout/images/logo/logo-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.png`" class="login-logo" style="width: 45px" />
            <NuxtImg :src="`/layout/images/logo/appname-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.png`" class="login-appname ml-3" style="width: 100px" />
          </div>
          <div class="form-container text-left" style="max-width: 320px; min-width: 270px">
            <div class="mb-4 flex flex-column align-items-start">
              <h4 class="text-900 font-bold mb-2">
                Screen Locked
              </h4>
              <span class="text-600 font-medium mb-5">Please enter your password</span>
              <NuxtImg src="/demo/images/pages/avatar.png" class="w-3rem h-3rem mb-2" alt="Avatar" />
              <span class="font-medium text-900 font-medium">Isabella Andolini</span>
            </div>
            <span class="p-input-icon-left">
              <i class="pi pi-key" />
              <InputText type="password" autocomplete="off" placeholder="Password" class="block mb-3" style="max-width: 320px; min-width: 270px" />
            </span>
          </div>
          <div class="button-container mt-4 text-left" style="max-width: 320px; min-width: 270px">
            <div class="buttons flex align-items-center gap-3">
              <Button type="button" class="block" style="max-width: 320px; margin-bottom: 32px" @click="goHome">
                Unlock
              </Button>
            </div>
          </div>
        </div>
        <div class="login-footer flex align-items-center absolute" style="bottom: 75px">
          <div class="flex align-items-center login-footer-logo-container pr-4 mr-4 border-right-1 surface-border">
            <NuxtImg src="/layout/images/logo/logo-gray.png" class="login-footer-logo" style="width: 22px" />
            <NuxtImg src="/layout/images/logo/appname-gray.png" class="login-footer-appname ml-2" style="width: 45px" />
          </div>
          <span class="text-sm text-color-secondary mr-3">Copyright 2023</span>
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

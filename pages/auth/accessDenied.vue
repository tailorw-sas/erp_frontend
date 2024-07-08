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
  <div :class="`exception-body min-h-screen ${layoutConfig.colorScheme.value === 'light' ? 'layout-light' : 'layout-dark'}`" style="background: var(--surface-ground)">
    <div
      class="exception-container min-h-screen flex align-items-center justify-content-center flex-column bg-auto md:bg-contain bg-no-repeat"
      :style="{ background: 'var(--exception-pages-image)', backgroundRepeat: 'no-repeat', backgroundSize: 'contain', boxSizing: 'border-box' }"
    >
      <div class="exception-panel text-center flex align-items-center justify-content-center flex-column" style="margin-top: -200px; box-sizing: border-box">
        <h1 class="text-blue-500 mb-0" style="font-size: 140px; font-weight: 900; text-shadow: 0px 0px 50px rgba(#0f8bfd, 0.2)">
          ACCESS
        </h1>
        <h3 class="text-blue-700" style="font-size: 80px; font-weight: 900; margin-top: -90px; margin-bottom: 50px">
          denied
        </h3>
        <p class="text-3xl" style="max-width: 320px">
          You are not allowed to view this page.
        </p>
        <Button type="button" style="margin-top: 50px" @click="goHome">
          Go back to home
        </Button>
      </div>
      <div class="exception-footer absolute align-items-center flex" style="bottom: 60px">
        <NuxtImg :src="`/layout/images/logo/logo-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.png`" class="exception-logo" style="width: 34px" />
        <NuxtImg :src="`/layout/images/logo/appname-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.png`" class="exception-appname ml-3" style="width: 72px" />
      </div>
    </div>
  </div>
</template>

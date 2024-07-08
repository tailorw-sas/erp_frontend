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
        <h1 class="text-red-400 mb-0" style="font-size: 140px; font-weight: 900; text-shadow: 0px 0px 50px rgba(#fc6161, 0.2)">
          ERROR
        </h1>
        <h3 class="text-red-300" style="font-size: 80px; font-weight: 900; margin-top: -90px; margin-bottom: 50px">
          something's went wrong
        </h3>
        <Button style="margin-top: 50px" @click="goHome">
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

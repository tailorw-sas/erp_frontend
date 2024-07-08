<script setup>
import { useRouter } from 'vue-router'
import AppMenu from './AppMenu.vue'
import { menuItemsMegaMenu } from './data/MenuItemsList'

const { layoutState, layoutConfig } = useLayout()
const router = useRouter()

let timeout = null

function onMouseEnter() {
  if (!layoutState.anchored.value) {
    if (timeout) {
      clearTimeout(timeout)
      timeout = null
    }
    layoutState.sidebarActive.value = true
  }
}

function onMouseLeave() {
  if (!layoutState.anchored.value) {
    if (!timeout) {
      timeout = setTimeout(() => (layoutState.sidebarActive.value = false), 300)
    }
  }
}

function anchor() {
  layoutState.anchored.value = !layoutState.anchored.value
}
function navigateToHome() {
  router.push('/')
}
</script>

<template>
  <div class="layout-sidebar" @mouseenter="onMouseEnter()" @mouseleave="onMouseLeave()">
    <div class="sidebar-header mr-5">
      <a class="app-logo" style="cursor: pointer" @click="navigateToHome">
        <div class="app-logo-small h-2rem">
          <NuxtImg
            :src="`/layout/images/logo/logo-simple-${layoutConfig.colorScheme.value === 'light' ? 'dark' : 'light'}.png`"
          />
        </div>
        <div class="app-logo-normal">
          <NuxtImg
            class="h-5rem light-mode"
            src="/layout/images/logo/horizontal-logo.png"
            style="width: 200px"
          />
          <!-- /layout/images/logo/logo-${layoutConfig.colorScheme.value === 'light' ? 'light' : 'dark'}.svg` -->

        </div>
      </a>
      <Button class="layout-sidebar-anchor p-link z-2" type="button" @click="anchor()" />
    </div>

    <div class="w-full">
      <MegaMenu
        :model="menuItemsMegaMenu"
        disabled="disabled"
        :pt="{
          submenuHeader: { class: 'text-primary font-bold' },
        }"
      />
    </div>
    <!-- <div class="layout-menu-container">
      <AppMenu />
    </div> -->
  </div>
</template>

<style lang="scss">
// .p-megamenu {
//     padding: 0.857rem;
//     background: transparent;
//     color: #676B89;
//     /* border: 1px solid rgba(68, 72, 109, 0.17); */
//     /* border-radius: 6px; */
// }
</style>

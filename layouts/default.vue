<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useAuthStore } from '@/stores/authStore'

const $primevue = usePrimeVue()
const { layoutConfig, layoutState, isSidebarActive } = useLayout()

const outsideClickListener = ref(null)
const topbarRef = ref(null)

watch(isSidebarActive, (newVal) => {
  if (newVal) {
    bindOutsideClickListener()
  }
  else {
    unbindOutsideClickListener()
  }
})

onBeforeUnmount(() => {
  unbindOutsideClickListener()
})

const containerClass = computed(() => {
  return [
    {
      'layout-light': layoutConfig.colorScheme.value === 'light',
      'layout-dark': layoutConfig.colorScheme.value === 'dark',
      'layout-overlay': layoutConfig.menuMode.value === 'overlay',
      'layout-static': layoutConfig.menuMode.value === 'static',
      'layout-slim': layoutConfig.menuMode.value === 'slim',
      'layout-slim-plus': layoutConfig.menuMode.value === 'slim-plus',
      'layout-horizontal': layoutConfig.menuMode.value === 'horizontal',
      'layout-reveal': layoutConfig.menuMode.value === 'reveal',
      'layout-drawer': layoutConfig.menuMode.value === 'drawer',
      'layout-static-inactive': layoutState.staticMenuDesktopInactive.value && layoutConfig.menuMode.value === 'static',
      'layout-overlay-active': layoutState.overlayMenuActive.value,
      'layout-mobile-active': layoutState.staticMenuMobileActive.value,
      'p-ripple-disabled': $primevue.config.ripple === false,
      'layout-sidebar-active': layoutState.sidebarActive.value,
      'layout-sidebar-anchored': layoutState.anchored.value
    }
  ]
})

function bindOutsideClickListener() {
  if (!outsideClickListener.value) {
    outsideClickListener.value = (event) => {
      if (isOutsideClicked(event)) {
        layoutState.overlayMenuActive.value = false
        layoutState.overlaySubmenuActive.value = false
        layoutState.staticMenuMobileActive.value = false
        layoutState.menuHoverActive.value = false
      }
    }
    document.addEventListener('click', outsideClickListener.value)
  }
}

function unbindOutsideClickListener() {
  if (outsideClickListener.value) {
    document.removeEventListener('click', outsideClickListener)
    outsideClickListener.value = null
  }
}

function isOutsideClicked(event) {
  if (!topbarRef.value) { return }

  const sidebarEl = topbarRef?.value.$el.querySelector('.layout-sidebar')
  const topbarEl = topbarRef?.value.$el.querySelector('.topbar-start > button')

  return !(sidebarEl.isSameNode(event.target) || sidebarEl.contains(event.target) || topbarEl.isSameNode(event.target) || topbarEl.contains(event.target))
}

const businessStore = useBusinessStore()
const authStore = useAuthStore()

try {
  await useAsyncData('userMe', () => authStore.getUserMe().then(true))

  const orderedBusinesses = authStore.userData.data.businesses.sort((a, b) => {
    return a.businessId > b.businessId ? 1 : -1
  })

  const bussinessSelected = authStore.userData.data.selectedBusiness
    ? authStore.userData.data.businesses.find(item => item.businessId === authStore.userData.data.selectedBusiness)
    : orderedBusinesses[0] ?? null

  businessStore.setCurrent(bussinessSelected)
}
catch (error) {
  businessStore.$reset()
}
</script>

<template>
  <div class="layout-container bg-home" :class="containerClass">
    <div class="layout-content-wrapper">
      <AppTopbar ref="topbarRef" />

      <div class="content-breadcrumb">
        <AppBreadCrumb />
      </div>

      <div class="layout-content w-full">
        <!-- <NuxtPage /> -->
        <slot />
      </div>

      <div class="layout-mask" />
    </div>
    <AppProfileMenu />
    <AppConfig />
    <ConfirmPopup group="headless" />
    <ConfirmDialog>
      <template #container="{ message, acceptCallback, rejectCallback }">
        <div class="flex flex-column align-items-center pb-4 surface-overlay" style="width: 30rem; border-radius: 0.8rem;">
          <span class="custom-border-top p-2 block mb-2 w-full bg-primary" style="color: white; padding: 0.4rem; font-size: 1.2rem;">
            <p class="ml-2">{{ message.header }}</p>
          </span>
          <div class="inline-flex justify-content-center align-items-center h-4rem w-4rem mt-2 mb-3">
            <i class="pi pi-exclamation-triangle text-6xl" style="color: #F54108;" />
          </div>
          <p class="mb-0">
            {{ message.message }}
          </p>
          <div class="flex align-items-center gap-2 mt-4">
            <Button icon="pi pi-check" :label="message.acceptLabel || 'Accept'" class="w-8rem bg-primary" @click="acceptCallback" />
            <Button icon="pi pi-times" :label="message.rejectLabel || 'Cancel'" class="w-8rem" severity="secondary" @click="rejectCallback" />
          </div>
        </div>
      </template>
    </ConfirmDialog>
    <Toast />
  </div>
</template>

<style lang="scss">
  .bg-home {
  background-image: url('/layout/images/img-transparent.png');
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  // opacity: 0.2;
}

.custom-border-top {
  border-top-left-radius: 0.8rem;
  border-top-right-radius: 0.8rem;
}
</style>

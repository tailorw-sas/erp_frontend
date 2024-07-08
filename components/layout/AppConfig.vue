<script setup>
import Sidebar from 'primevue/sidebar'
import { usePrimeVue } from 'primevue/config'
import { computed, ref, watch } from 'vue'

defineProps({
  simple: {
    type: Boolean,
    default: false
  }
})

const $primevue = usePrimeVue()
const rippleActive = computed(() => $primevue.config.ripple)

const { setScale, layoutConfig, layoutState, onConfigSidebarToggle } = useLayout()
const themes = ref([
  { name: 'blue', color: '#0F8BFD' },
  { name: 'green', color: '#0BD18A' },
  { name: 'magenta', color: '#EC4DBC' },
  { name: 'orange', color: '#FD9214' },
  { name: 'purple', color: '#873EFE' },
  { name: 'red', color: '#FC6161' },
  { name: 'teal', color: '#00D0DE' },
  { name: 'yellow', color: '#EEE500' }
])

const scales = ref([12, 13, 14, 15, 16])

watch(layoutConfig.menuMode, (newVal) => {
  if (newVal === 'static') {
    layoutState.staticMenuDesktopInactive.value = false
  }
})

const colorScheme = ref(layoutConfig.colorScheme.value)

function changeColorScheme(colorScheme) {
  const themeLink = document.getElementById('theme-link')
  const themeLinkHref = themeLink.getAttribute('href')
  const currentColorScheme = `theme-${layoutConfig.colorScheme.value.toString()}`
  const newColorScheme = `theme-${colorScheme}`
  const newHref = themeLinkHref.replace(currentColorScheme, newColorScheme)

  replaceLink(themeLink, newHref, () => {
    layoutConfig.colorScheme.value = colorScheme
  })
}

function changeTheme(theme) {
  const themeLink = document.getElementById('theme-link')
  const themeHref = themeLink.getAttribute('href')
  const newHref = themeHref.replace(layoutConfig.theme.value, theme)

  replaceLink(themeLink, newHref, () => {
    layoutConfig.theme.value = theme
  })
}

function replaceLink(linkElement, href, onComplete) {
  if (!linkElement || !href) {
    return
  }

  const id = linkElement.getAttribute('id')
  const cloneLinkElement = linkElement.cloneNode(true)

  cloneLinkElement.setAttribute('href', href)
  cloneLinkElement.setAttribute('id', `${id}-clone`)

  linkElement.parentNode.insertBefore(cloneLinkElement, linkElement.nextSibling)

  cloneLinkElement.addEventListener('load', () => {
    linkElement.remove()

    const element = document.getElementById(id)
    element && element.remove()

    cloneLinkElement.setAttribute('id', id)
    onComplete && onComplete()
  })
}
function decrementScale() {
  setScale(layoutConfig.scale.value - 1)
  applyScale()
}
function incrementScale() {
  setScale(layoutConfig.scale.value + 1)
  applyScale()
}

function applyScale() {
  document.documentElement.style.fontSize = `${layoutConfig.scale.value}px`
}

function onRippleChange(value) {
  $primevue.config.ripple = value
}

onMounted(() => {
  applyScale()
})
</script>

<template>
  <Button v-if="false" class="layout-config-button p-link" style="cursor: pointer" @click="onConfigSidebarToggle()">
    <i class="pi pi-cog" />
  </Button>

  <Sidebar v-model:visible="layoutState.configSidebarVisible.value" position="right" class="layout-config-sidebar w-18rem">
    <h5>Themes</h5>
    <div class="flex flex-wrap row-gap-3">
      <div v-for="(theme, i) in themes" :key="i" class="w-3">
        <Button
          :auto-focus="layoutConfig.theme === theme.name"
          class="cursor-pointer p-link w-2rem h-2rem border-circle flex-shrink-0 flex align-items-center justify-content-center"
          :style="{ 'background-color': theme.color }"
          @click="() => changeTheme(theme.name)"
        >
          <i v-if="theme.name === layoutConfig.theme.value" class="pi pi-check text-white" />
        </Button>
      </div>
    </div>
    <h5>Scale</h5>
    {{ layoutConfig.scale.value }}
    <div class="flex align-items-center">
      <Button icon="pi pi-minus" type="button" class="w-2rem h-2rem mr-2" text rounded :disabled="layoutConfig.scale.value === scales[0]" @click="decrementScale()" />
      <div class="flex gap-2 align-items-center">
        <i v-for="s in scales" :key="s" class="pi pi-circle-fill text-300" :class="{ 'text-primary-500': s === layoutConfig.scale.value }" />
      </div>
      <Button icon="pi pi-plus" type="button" class="w-2rem h-2rem ml-2" text rounded :disabled="layoutConfig.scale.value === scales[scales.length - 1]" @click="incrementScale()" />
    </div>

    <template v-if="!simple">
      <h5>Menu Type</h5>
      <div class="flex flex-wrap row-gap-3">
        <div class="flex align-items-center gap-2 w-6">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="static" input-id="mode1" />
          <label for="mode1">Static</label>
        </div>

        <div class="flex align-items-center gap-2 w-6 pl-2">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="overlay" input-id="mode2" />
          <label for="mode2">Overlay</label>
        </div>
        <div class="flex align-items-center gap-2 w-6">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="slim" input-id="mode3" />
          <label for="mode3">Slim</label>
        </div>
        <div class="flex align-items-center gap-2 w-6 pl-2">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="slim-plus" input-id="mode4" />
          <label for="mode4">Slim+</label>
        </div>
        <div class="flex align-items-center gap-2 w-6">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="horizontal" input-id="mode5" />
          <label for="mode5">Horizontal</label>
        </div>
        <div class="flex align-items-center gap-2 w-6 pl-2">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="reveal" input-id="mode6" />
          <label for="mode6">Reveal</label>
        </div>
        <div class="flex align-items-center gap-2 w-6">
          <RadioButton v-model="layoutConfig.menuMode.value" name="menuMode" value="drawer" input-id="mode7" />
          <label for="mode7">Drawer</label>
        </div>
      </div>
      <h5>Color Scheme</h5>

      <div class="field-radiobutton">
        <RadioButton id="theme3" v-model="colorScheme" name="colorScheme" value="light" @change="changeColorScheme('light')" />
        <label for="theme3">Light</label>
      </div>
      <div class="field-radiobutton">
        <RadioButton id="theme1" v-model="colorScheme" name="colorScheme" value="dark" @change="changeColorScheme('dark')" />
        <label for="theme1">Dark</label>
      </div>
    </template>

    <template v-if="!simple">
      <h5>Ripple Effect</h5>
      <InputSwitch :model-value="rippleActive" @update:model-value="onRippleChange" />
    </template>
  </Sidebar>
</template>

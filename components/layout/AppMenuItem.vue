<script setup>
import { DomHandler } from 'primevue/utils'
import { nextTick, onBeforeMount, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  item: {
    type: Object,
    default: () => ({})
  },
  index: {
    type: Number,
    default: 0
  },
  root: {
    type: Boolean,
    default: true
  },
  parentItemKey: {
    type: String,
    default: null
  },
  rootIndex: {
    type: Number,
    default: 0
  }
})

const route = useRoute()

const { layoutConfig, layoutState, setActiveMenuItem, onMenuToggle, isHorizontal, isSlim, isSlimPlus, isDesktop, isOverlay, isStatic, isDrawer, isReveal } = useLayout()

const isActiveMenu = ref(false)
const itemKey = ref(null)
const subMenuRef = ref(null)
const menuItemRef = ref(null)

onBeforeMount(() => {
  itemKey.value = props.parentItemKey ? `${props.parentItemKey}-${props.index}` : String(props.index)

  const activeItem = layoutState.activeMenuItem.value

  isActiveMenu.value = activeItem === itemKey.value || activeItem ? activeItem.startsWith(`${itemKey.value}-`) : false
  handleRouteChange(route.path)
})

watch(
  () => isActiveMenu.value,
  () => {
    if ((isSlim.value || isSlimPlus.value || isHorizontal.value) && isDesktop) {
      nextTick(() => {
        calculatePosition(subMenuRef.value, subMenuRef.value.parentElement)
      })
    }
  }
)

watch(
  () => layoutState.activeMenuItem.value,
  (newVal) => {
    isActiveMenu.value = newVal === itemKey.value || newVal.startsWith(`${itemKey.value}-`)
  }
)

watch(
  () => layoutConfig.menuMode.value,
  () => {
    isActiveMenu.value = false
  }
)

watch(
  () => layoutState.overlaySubmenuActive.value,
  (newValue) => {
    if (!newValue) {
      isActiveMenu.value = false
    }
  }
)

watch(
  () => route.path,
  (newPath) => {
    if (!(isSlim.value || isSlimPlus.value || isHorizontal.value) && props.item.to && props.item.to === newPath) {
      setActiveMenuItem(itemKey)
    }
    else if (isSlim.value || isSlimPlus.value || isHorizontal.value) {
      isActiveMenu.value = false
    }
  }
)

watch(() => route.path, handleRouteChange)

function handleRouteChange(newPath) {
  if (!(isSlim.value || isSlimPlus.value || isHorizontal.value) && props.item.to && props.item.to === newPath) {
    setActiveMenuItem(itemKey)
  }
  else if (isSlim.value || isSlimPlus.value || isHorizontal.value) {
    isActiveMenu.value = false
  }
}
async function itemClick(event, item) {
  if (item.disabled) {
    event.preventDefault()
    return
  }

  const { overlayMenuActive, staticMenuMobileActive } = layoutState

  if ((item.to || item.url) && (staticMenuMobileActive.value || overlayMenuActive.value)) {
    onMenuToggle()
  }

  if (item.command) {
    item.command({ originalEvent: event, item })
  }

  if (item.items) {
    if (props.root && isActiveMenu.value && (isSlim.value || isSlimPlus.value || isHorizontal.value)) {
      layoutState.overlaySubmenuActive.value = false
      layoutState.menuHoverActive.value = false

      return
    }

    setActiveMenuItem(isActiveMenu.value ? props.parentItemKey : itemKey)

    if (props.root && !isActiveMenu.value && (isSlim.value || isSlimPlus.value || isHorizontal.value)) {
      layoutState.overlaySubmenuActive.value = true
      layoutState.menuHoverActive.value = true
      isActiveMenu.value = true

      removeAllTooltips()
    }
  }
  else {
    if (!isDesktop) {
      layoutState.staticMenuMobileActive.value = !layoutState.staticMenuMobileActive.value
    }

    if (isSlim.value || isSlimPlus.value || isHorizontal.value) {
      layoutState.overlaySubmenuActive.value = false
      layoutState.menuHoverActive.value = false

      return
    }

    setActiveMenuItem(itemKey)
  }
}

function onMouseEnter() {
  if (props.root && (isSlim.value || isSlimPlus.value || isHorizontal.value) && isDesktop) {
    if (!isActiveMenu.value && layoutState.menuHoverActive.value) {
      setActiveMenuItem(itemKey)
    }
  }
}
function removeAllTooltips() {
  const tooltips = document.querySelectorAll('.p-tooltip')
  tooltips.forEach((tooltip) => {
    tooltip.remove()
  })
}

function calculatePosition(overlay, target) {
  if (overlay) {
    const { left, top } = target.getBoundingClientRect()
    const [vWidth, vHeight] = [window.innerWidth, window.innerHeight]
    const [oWidth, oHeight] = [overlay.offsetWidth, overlay.offsetHeight]
    const scrollbarWidth = DomHandler.calculateScrollbarWidth()

    // reset
    overlay.style.top = overlay.style.left = ''

    if (isHorizontal.value) {
      const width = left + oWidth + scrollbarWidth
      overlay.style.left = vWidth < width ? `${left - (width - vWidth)}px` : `${left}px`
    }
    else if (isSlim.value || isSlimPlus.value) {
      const height = top + oHeight
      overlay.style.top = vHeight < height ? `${top - (height - vHeight)}px` : `${top}px`
    }
  }
}

function checkActiveRoute(item) {
  return route.path === item.to
}
</script>

<template>
  <li ref="menuItemRef" :class="{ 'layout-root-menuitem': root, 'active-menuitem': isStatic || isOverlay || isDrawer || isReveal ? !root && isActiveMenu : isActiveMenu }">
    <div v-if="root && item.visible !== false" class="layout-menuitem-root-text">
      {{ item.label }}
    </div>
    <a
      v-if="(!item.to || item.items) && item.visible !== false"
      v-tooltip.hover="isSlim && root && !isActiveMenu ? item.label : null"
      :href="item.url"
      :class="item.class"
      :target="item.target"
      tabindex="0"
      @click="itemClick($event, item, index)"
      @mouseenter="onMouseEnter"
    >
      <i :class="item.icon" class="layout-menuitem-icon" />
      <span class="layout-menuitem-text">{{ item.label }}</span>
      <i v-if="item.items" class="pi pi-fw pi-angle-down layout-submenu-toggler" />
    </a>
    <router-link
      v-if="item.to && !item.items && item.visible !== false"
      v-tooltip.hover="(isSlim || isSlimPlus) && root && !isActiveMenu ? item.label : null"
      :class="[item.class, { 'active-route': checkActiveRoute(item) }]"
      tabindex="0"
      :to="item.to"
      @click="itemClick($event, item, index)"
      @mouseenter="onMouseEnter"
    >
      <i :class="item.icon" class="layout-menuitem-icon" />
      <span class="layout-menuitem-text">{{ item.label }}</span>
      <i v-if="item.items" class="pi pi-fw pi-angle-down layout-submenu-toggler" />
    </router-link>

    <ul v-if="item.items && item.visible !== false" ref="subMenuRef" :class="{ 'layout-root-submenulist': root }">
      <app-menu-item v-for="(child, i) in item.items" :key="child" :index="i" :item="child" :parent-item-key="itemKey" :root="false" :root-index="props.index" />
    </ul>
  </li>
</template>

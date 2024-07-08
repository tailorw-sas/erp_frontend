import { computed, reactive, toRefs } from 'vue'

const layoutConfig = reactive({
  menuMode: 'horizontal',
  colorScheme: 'light',
  theme: 'blue',
  scale: 13
})

const layoutState = reactive({
  staticMenuDesktopInactive: false,
  overlayMenuActive: false,
  rightMenuActive: false,
  configSidebarVisible: false,
  staticMenuMobileActive: false,
  menuHoverActive: false,
  sidebarActive: false,
  anchored: false,
  activeMenuItem: null,
  overlaySubmenuActive: false,
  rightMenuVisible: false,
  searchBarActive: false
})

export function useLayout() {
  const showConfigSidebar = () => {
    layoutState.configSidebarVisible = true
  }
  const showSidebar = () => {
    layoutState.rightMenuVisible = true
  }

  const changeColorScheme = (colorScheme) => {
    const themeLink = document.getElementById('theme-link')
    const themeLinkHref = themeLink.getAttribute('href')
    const currentColorScheme = `theme-${layoutConfig.colorScheme.toString()}`
    const newColorScheme = `theme-${colorScheme}`
    const newHref = themeLinkHref.replace(currentColorScheme, newColorScheme)

    replaceLink(themeLink, newHref, () => {
      layoutConfig.colorScheme = colorScheme
      if (layoutConfig.colorScheme === 'dark') {
        layoutConfig.menuTheme = 'dark'
      }
      else {
        layoutConfig.menuTheme = 'light'
      }
    })
  }
  const replaceLink = (linkElement, href, onComplete) => {
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

      const element = document.getElementById(id) // re-check
      element && element.remove()

      cloneLinkElement.setAttribute('id', id)
      onComplete && onComplete()
    })
  }
  const setScale = (scale) => {
    layoutConfig.scale = scale
  }

  const setActiveMenuItem = (item) => {
    layoutState.activeMenuItem = item.value || item
  }

  const onMenuToggle = () => {
    if (layoutConfig.menuMode === 'overlay') {
      layoutState.overlayMenuActive = !layoutState.overlayMenuActive
    }

    if (window.innerWidth > 991) {
      layoutState.staticMenuDesktopInactive = !layoutState.staticMenuDesktopInactive
    }
    else {
      layoutState.staticMenuMobileActive = !layoutState.staticMenuMobileActive
    }
  }

  const onConfigSidebarToggle = () => {
    layoutState.configSidebarVisible = !layoutState.configSidebarVisible
  }

  const isSidebarActive = computed(() => layoutState.overlayMenuActive || layoutState.staticMenuMobileActive || layoutState.overlaySubmenuActive)

  const isDesktop = computed(() => window.innerWidth > 991)

  const isSlim = computed(() => layoutConfig.menuMode === 'slim')
  const isSlimPlus = computed(() => layoutConfig.menuMode === 'slim-plus')
  const isStatic = computed(() => layoutConfig.menuMode === 'static')
  const isHorizontal = computed(() => layoutConfig.menuMode === 'horizontal')

  const isOverlay = computed(() => layoutConfig.menuMode === 'overlay')

  const isDrawer = computed(() => layoutConfig.menuMode === 'drawer')

  const isReveal = computed(() => layoutConfig.menuMode === 'reveal')

  return {
    layoutConfig: toRefs(layoutConfig),
    layoutState: toRefs(layoutState),
    setScale,
    onMenuToggle,
    isSidebarActive,
    setActiveMenuItem,
    onConfigSidebarToggle,
    isSlim,
    isSlimPlus,
    isStatic,
    isDrawer,
    isReveal,
    isHorizontal,
    isDesktop,
    changeColorScheme,
    replaceLink,
    isOverlay,
    showConfigSidebar,
    showSidebar
  }
}

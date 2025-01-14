<script setup lang="ts">
import { useRouter } from 'vue-router'
import AppMenu from './AppMenu.vue'
import { type MenuItem, menuItemsMegaMenu } from './data/MenuItemsList'
import { GenericService } from '~/services/generic-services'

const { layoutState, layoutConfig } = useLayout()
const router = useRouter()

const timeout = ref(null)

function onMouseEnter() {
  if (!layoutState.anchored.value) {
    if (timeout.value) {
      clearTimeout(timeout.value)
      timeout.value = null
    }
    layoutState.sidebarActive.value = true
  }
}

function onMouseLeave() {
  if (!layoutState.anchored.value) {
    if (!timeout.value) {
      timeout.value = setTimeout(() => (layoutState.sidebarActive.value = false), 300)
    }
  }
}

function anchor() {
  layoutState.anchored.value = !layoutState.anchored.value
}
function navigateToHome() {
  router.push('/')
}

function transformToTreeNode(data: Record<string, any[]>): MenuItem {
  return {
    label: 'Reports',
    icon: 'pi pi-folder',
    items: Object.keys(data).map(moduleKey => [
      {
        label: moduleKey,
        icon: 'pi pi-fw pi-file',
        items: data[moduleKey].map(report => ({
          label: report.name,
          icon: 'pi pi-fw pi-file',
          command: () => navigateTo(`/report-config/view?reportId=${report.id}&reportCode=${report.code}`),
          items: [],
        })),
      },
    ]),
  }
}

async function getMenuItems() {
  try {
    const response = await GenericService.get('report', 'report-menu/grouped')
    if (response) {
      const treeNodes: MenuItem = transformToTreeNode(response)
      return treeNodes
    }
    return []
  }
  catch (error) {
    console.error('Error loading menu items:', error)
    return []
  }
}

onMounted(async () => {
  const listItemMenuTree: MenuItem = await getMenuItems()
  menuItemsMegaMenu.value = [...menuItemsMegaMenu.value, { ...listItemMenuTree }]
})
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

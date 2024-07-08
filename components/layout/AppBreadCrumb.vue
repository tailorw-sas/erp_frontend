<script setup>
import { useRoute } from 'vue-router'
import { ref, watch } from 'vue'

const route = useRoute()
const breadcrumbRoutes = ref([])
const { showSidebar } = useLayout()
const searchInput = ref(null)
const searchActive = ref(false)

function setBreadcrumbRoutes() {
  if (route.meta.breadcrumb) {
    breadcrumbRoutes.value = route.meta.breadcrumb

    return
  }

  breadcrumbRoutes.value = route.fullPath
    .split('/')
    .filter(item => item !== '' && !isUUID(item))
    .filter(item => Number.isNaN(Number(item)))
    .map((item) => {
      return item
        .split('-')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ')
    })
}

function activateSearch() {
  searchActive.value = true
  setTimeout(() => {
    searchInput.value.$el.focus()
  }, 100)
}
function deactivateSearch() {
  searchActive.value = false
}

function onSidebarButtonClick() {
  showSidebar()
}

function isUUID(str) {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i
  return uuidRegex.test(str)
}

watch(
  route,
  () => {
    setBreadcrumbRoutes()
  },
  { immediate: true }
)
</script>

<template>
  <div class="layout-breadcrumb flex align-items-center relative h-3rem">
    <nav>
      <ol class="relative z-2">
        <template v-for="(breadcrumbRoute, i) in breadcrumbRoutes" :key="breadcrumbRoute">
          <li>{{ breadcrumbRoute }}</li>
          <li v-if="i !== breadcrumbRoutes.length - 1" class="layout-breadcrumb-chevron">
            /
          </li>
        </template>
      </ol>
    </nav>
  </div>
</template>

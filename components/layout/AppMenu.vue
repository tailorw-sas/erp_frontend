<script setup>
import AppMenuItem from './AppMenuItem.vue'
import { model } from './data/MenuItemsList'
</script>

<template>
  <ul class="layout-menu">
    <template v-for="(item, i) in model" :key="item">
      <AppMenuItem v-if="!item.separator" :item="item" root :index="i" />

      <li v-else class="menu-separator" />
    </template>
  </ul>
</template>

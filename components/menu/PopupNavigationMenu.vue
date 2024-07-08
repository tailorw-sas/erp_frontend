<script setup lang="ts">
import { ref } from 'vue'
import type { MenuItem } from './MenuItems'

const props = defineProps({
  items: { required: true, type: Array as () => Array<MenuItem> },
  icon: { required: true, type: String },
  label: String,
  menuId: { type: String, default: 'overlay_menu' } // custom id for css customization
})

const menu = ref()

function toggle(event) {
  menu.value.toggle(event)
}
</script>

<template>
  <Menu :id="props.menuId" ref="menu" :model="props.items" :popup="true">
    <template #item="itemprops">
      <slot name="item" :props="itemprops">
        <span>{{ itemprops.label }}</span>
      </slot>
    </template>
  </Menu>
  <Button type="button" :icon="icon" :label="label" aria-haspopup="true" aria-controls="overlay_menu" @click="toggle" />
</template>

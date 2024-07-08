<script setup lang="ts">
defineProps({
  legend: { type: Array as () => Array<any>, required: true },
})
</script>

<template>
  <div class="flex justify-content-end flex-column md:flex-row">
    <div class="grid grid-nogutter">
      <div v-for="(item, index) in legend" :key="index" class="col-12 md:col-1 w-auto">
        <div class="flex align-items-center gap-2 py-1" :class="item.colClass">
          <span :style="`background-color: ${item.color}`" style="width: 20px; min-width: 20px; height: 20px; min-height: 20px; border-radius: 6px;" />
          <span class="font-bold">{{ item.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  legend: { type: Array as () => Array<any>, required: true },
  with: { type: String, default: '15px', required: false }
})
</script>

<template>
  <div class="flex justify-content-end flex-column md:flex-row p-0 m-0">
    <div class="grid grid-nogutter p-0 m-0">
      <div v-for="(item, index) in legend" :key="index" class="col-12 md:col-1 w-auto p-0 m-0">
        <div class="flex align-items-center gap-2 p-0 m-0" :class="item.colClass">
          <span
            :style="{
              backgroundColor: item.color,
              width: props.with,
              minWidth: props.with,
              height: props.with,
              minHeight: props.with,
              borderRadius: '6px',
            }"
          />
          <span class="font-bold">{{ item.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

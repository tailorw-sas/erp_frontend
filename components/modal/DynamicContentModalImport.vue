<script setup lang="ts">
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  component: {
    type: Object,
    required: true
  },
  header: {
    type: String,
    default: ''
  },
  componentProps: {
    type: Object,
    default: () => ({})
  },
  width: {
    type: String,
    default: '99vw'
  },
  height: {
    type: String,
    default: '95vh'
  },
  autoClose: {
    type: Boolean,
    default: false // Agregamos una prop para habilitar/deshabilitar el cierre automático
  }
})

const emit = defineEmits(['close'])

const dialogVisible = ref(props.visible)

watch(() => props.visible, (newValue) => {
  dialogVisible.value = newValue
})

function onHide() {
  emit('close')
}
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    :header="header"
    :modal="true"
    :style="{ 'width': '80vw', height, 'max-height': '98vh' }"
    class="flex items-center justify-center"
    @hide="onHide"
  >
    <component :is="props.component" v-bind="props.componentProps" />
    <!-- <div class="w-full h-full">
      <component :is="props.component" v-bind="props.componentProps" />
    </div> -->
  </Dialog>
</template>

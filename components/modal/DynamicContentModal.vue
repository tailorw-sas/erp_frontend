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
    required: false,
    default: ''
  },
  componentProps: {
    type: Object,
    default: () => ({})
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
  <Dialog v-model:visible="dialogVisible" :header="header" :modal="true" class="w-10 lg:w-8" @hide="onHide">
    <component :is="props.component" v-bind="props.componentProps" />
  </Dialog>
</template>

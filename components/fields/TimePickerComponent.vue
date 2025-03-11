<script setup lang="ts">
const props = defineProps({
  model: {
    type: String,
    require: true
  }
})

const emit = defineEmits(['change'])

const hours = ref([...Array(24).keys()].map(h => h.toString().padStart(2, '0')))
const minutes = ref([...Array(60).keys()].map(m => m.toString().padStart(2, '0')))

const selectedHour = ref('')
const selectedMinute = ref('')

function emitTime() {
  if (selectedHour.value !== null && selectedMinute.value !== null) {
    emit('change', `${selectedHour.value}:${selectedMinute.value}`)
  }
}

watch(() => props.model, (newValue) => {
  if (newValue) {
    const [hour, minute] = newValue.split(':')
    selectedHour.value = hour
    selectedMinute.value = minute
  }
}, { immediate: true })
</script>

<template>
  <div class="flex gap-1 items-center">
    <Dropdown
      v-model="selectedHour"
      :options="hours"
      placeholder="hh"
      class="w-16"
      @change="emitTime"
    />
    <span class="mt-2">:</span>
    <Dropdown
      v-model="selectedMinute"
      :options="minutes"
      placeholder="mm"
      class="w-16"
      @change="emitTime"
    />
  </div>
</template>

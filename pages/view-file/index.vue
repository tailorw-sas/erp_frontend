<script setup lang="ts">
import { ref } from 'vue'

const route = useRoute()

const pdfUrl = ref('')

definePageMeta({
  layout: 'empty',
})

onMounted(() => {
  pdfUrl.value = route.query.url as string
})
</script>

<template>
  <div class="card p-0" style="max-height: 100vh; height: 100vh">
    <object :data="pdfUrl" type="application/pdf" width="100%" height="100%">
      <p>Your browser does not support PDF. <a :href="pdfUrl">Download PDF</a>.</p>
    </object>
  </div>
</template>

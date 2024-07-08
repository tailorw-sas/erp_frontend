<script setup lang="ts">
import type { Card } from './CardInterface'

const props = defineProps({
  card: {
    type: Object as PropType<Card>,
    required: true
  }
})
</script>

<template>
  <Card @click="()=>{
    if(props.card.url){
      navigateTo(props.card.url)
    }
  }" class="custom-card p-0 m-0 flex align-items-center justify-content-center" :style="`background-color: ${props.card.color}`" style="width: 100px; height: 100px; position: relative; overflow: hidden;">
    <template #content>
      <div>
        <span v-if="props.card.status" class="ribbon flex align-items-center justify-content-center">{{ props.card.status }}</span>
        <img :src="props.card.icon" alt="" srcset="">
      </div>
    </template>
  </card>
  <div class="flex align-items-center justify-content-center text-center mt-2 font-bold text-sm" style="width: 100px;">
    {{ props.card.title }}
  </div>
</template>

<style lang="scss" scoped>
.custom-card {
  transition: transform 0.3s ease-in-out;
}

.custom-card:hover {
  transform: scale(1.1);
}

.ribbon {
  position: absolute;
  top: 8px;
  right: -35px;
  width: 100px;
  height: 20px;
  background-color: #0F8BFD;
  color: white;
  text-align: center;
  line-height: 30px;
  font-size: xx-small;
  transform: rotate(45deg);
  z-index: 1;
}
</style>

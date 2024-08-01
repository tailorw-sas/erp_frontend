<script lang="ts" setup>
defineProps<{
  perms: string[]
}>()

const { status, data } = useAuth()
const authStore = useAuthStore()
const businessStore = useBusinessStore()

const isAdmin = (data.value?.user as any)?.isAdmin === true
</script>

<template>
  <slot
    v-if="status === 'authenticated' && (isAdmin || businessStore.can(perms) || authStore.can(perms))" class="wrapper"
    :data="data"
  />
  <slot v-else name="accessDenied" />
</template>

<style lang="scss" scoped>
.wrapper {
  display: inline-block;
}
</style>

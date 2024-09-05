<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from "dayjs";

const isLoading = ref(true) // Nuevo estado de carga
const isSuccess = ref<boolean | null>(null)
const orderNumber = ref<string>('')
const amount = ref<string>('')
const itbis = ref<string | null>('')
const authorizationCode = ref<string | null>('')
const dateTime = ref<string | null>('')
const responseMessage = ref<string | null>('')
const isoCode = ref<string | null>('')
const rrn = ref<string | null>('')
const cardNumber = ref<string | null>('')

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const status = route.query.status || 'error'

  if (status === 'success') {
    isSuccess.value = true
    orderNumber.value = String(route.query.OrderNumber) || ''
    amount.value = (Number.parseFloat(String(route.query.Amount)) / 100).toFixed(2) || '0.00'
    itbis.value = String(route.query.Itbis) || ''
    authorizationCode.value = String(route.query.AuthorizationCode) || ''
    dateTime.value = dayjs(String(route.query.DateTime), 'YYYYMMDDHHmmss').format('YYYY/MM/DD HH:mm') || ''
    responseMessage.value = String(route.query.ResponseMessage) || ''
    isoCode.value = String(route.query.IsoCode) || ''
    rrn.value = String(route.query.RRN) || ''
    cardNumber.value = String(route.query.CardNumber) || ''
  }
  else {
    isSuccess.value = false
  }
  isLoading.value = false // Los datos han sido cargados
})

function goHome() {
  router.push('/vcc-management')
}
</script>

<template>
  <div class="transaction-result">
    <Card v-if="isLoading">
      <template #content>
        <p>Cargando...</p>
      </template>
    </Card>
    <Card v-else>
      <template #title>
        {{ isSuccess ? '¡Transacción Exitosa!' : 'Error en la Transacción' }}
      </template>
      <template #content>
        <p v-if="isSuccess">
          Gracias por tu compra. Hemos procesado tu pago correctamente.
        </p>
        <p v-else>
          Hubo un problema al procesar tu pago. Por favor, intenta nuevamente.
        </p>

        <div v-if="isSuccess" class="transaction-details">
          <h3>Detalles de la transacción:</h3>
          <p><strong>Número de Orden:</strong> {{ orderNumber }}</p>
          <p><strong>Monto:</strong> {{ amount }}</p>
          <p><strong>ITBIS:</strong> {{ itbis }}</p>
          <p><strong>Código de Autorización:</strong> {{ authorizationCode }}</p>
          <p><strong>Fecha y Hora:</strong> {{ dateTime }}</p>
          <p><strong>Respuesta del Merchant:</strong> {{ responseMessage }} ({{ isoCode }})</p>
          <p><strong>Número de Referencia:</strong> {{ rrn }}</p>
          <p><strong>Número de Tarjeta:</strong> {{ cardNumber }}</p>
        </div>
        <div class="flex align-items-center justify-content-center mt-4">
          <Button v-if="isSuccess" label="Volver al inicio" @click="goHome" />
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped lang="scss">
.transaction-result {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>

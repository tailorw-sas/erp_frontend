<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'

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
const showDetails = ref(false)

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

function toggleDetails() {
  showDetails.value = !showDetails.value
}
</script>

<template>
  <div class="transaction-result">
    <Card v-if="isLoading">
      <template #content>
        <p>Cargando...</p>
      </template>
    </Card>
    <Card v-else class="card">
      <template #content>
        <div class="flex flex-column align-items-center">
          <div class="flex flex-column align-items-center mb-4">
            <i class="pi pi-check-circle" style="font-size: 4rem; color: #0F8BFD;" />
            <h2> {{ isSuccess ? '¡Transacción Exitosa!' : 'Error en la Transacción' }}</h2>
            <p v-if="isSuccess">
              Gracias por tu compra. Hemos procesado tu pago correctamente.
            </p>
            <p v-else>
              Hubo un problema al procesar tu pago. Por favor, intenta nuevamente.
            </p>
          </div>
          <ButtonGroup v-if="isSuccess">
            <Button label="Detalles" icon="pi pi-chevron-down" @click="toggleDetails" />
            <Button label="Ir al inicio" @click="goHome" />
          </ButtonGroup>
        </div>
        <div v-if="showDetails" class="details-card">
          <Card class="">
            <template #content>
              <h4 class="text-center">Detalles de la Transacción:</h4>
              <Divider></Divider>
              <p><strong>Número de Orden:</strong> {{ route.query.OrderNumber }}</p>
              <p><strong>Monto:</strong> {{ amount }}</p>
              <p><strong>ITBIS:</strong> {{ route.query.ITBIS }}</p>
              <p><strong>Código de Autorización:</strong> {{ route.query.AuthorizationCode }}</p>
              <p><strong>Fecha y Hora:</strong> {{ dateTime }}</p>
              <p><strong>Respuesta del Merchant:</strong> {{ route.query.ResponseMessage }} ({{ route.query.IsoCode }})</p>
              <p><strong>Número de Referencia:</strong> {{ route.query.RRN }}</p>
              <p><strong>Número de Tarjeta:</strong> {{ route.query.CardNumber }}</p>
            </template>
          </Card>
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
  //height: 100vh;
}
.transaction-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: var(--primary-color)
}

.transaction-card {
  width: 500px;
  text-align: center;
}

.header h2 {
  margin: 10px 0;
}

.buttons {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.details-card {
  text-align: left;
  margin-top: 20px;
}

.details-card p {
  margin: 5px 0;
}
</style>

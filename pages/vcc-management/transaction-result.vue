<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'

const isLoading = ref(true) // Nuevo estado de carga
const transactionStatus = ref<string>('')
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

function disableBackNavigation() {
  history.pushState(null, document.title, location.href)
  history.back()
  history.forward()
  window.onpopstate = function () {
    history.go(1)
  }
}

onMounted(() => {
  disableBackNavigation()

  const status = route.query.status || 'error'

  transactionStatus.value = String(status) // Asignar el estado recibido a transactionStatus
  orderNumber.value = String(route.query.OrderNumber) || ''
  amount.value = (Number.parseFloat(String(route.query.Amount)) / 100).toFixed(2) || '0.00'
  itbis.value = (Number.parseFloat(String(route.query.Itbis)) / 100).toFixed(2) || '0.00'
  authorizationCode.value = String(route.query.AuthorizationCode) || ''
  dateTime.value = dayjs(String(route.query.DateTime), 'YYYYMMDDHHmmss').format('YYYY/MM/DD HH:mm') || ''
  responseMessage.value = String(route.query.ResponseMessage) || ''
  isoCode.value = String(route.query.IsoCode) || ''
  rrn.value = String(route.query.RRN) || ''
  cardNumber.value = String(route.query.CardNumber) || ''

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
    <Card v-if="isLoading" class="loading-card card-bg-color">
      <template #content>
        <div class="loading-container">
          <ProgressSpinner style="width: 50px; height: 50px;" stroke-width="4" animation-duration=".5s" />
          <p class="loading-text">
            Processing your transaction, please wait...
          </p>
        </div>
      </template>
    </Card>
    <Card v-else class="card card-bg-color">
      <template #content>
        <div class="flex flex-column align-items-center">
          <div class="flex flex-column align-items-center mb-4">
            <i v-if="transactionStatus === 'success'" class="pi pi-check-circle" style="font-size: 4rem; color: #0F8BFD;" />
            <i v-if="transactionStatus === 'declined'" class="pi pi-times-circle" style="font-size: 4rem; color: red;" />
            <i v-if="transactionStatus === 'cancelled'" class="pi pi-exclamation-circle" style="font-size: 4rem; color: orange;" />
            <h2>
              {{ transactionStatus === 'success' ? 'Transaction Successful!'
                : transactionStatus === 'declined' ? 'Transaction Declined'
                  : 'Transaction Cancelled' }}
            </h2>
            <p v-if="transactionStatus === 'success'">
              We have successfully processed your payment.
            </p>
            <p v-else-if="transactionStatus === 'declined'">
              Your payment was declined. Please try again.
            </p>
            <p v-else-if="transactionStatus === 'cancelled'">
              The transaction was cancelled.
            </p>
          </div>
          <ButtonGroup v-if="transactionStatus === 'success'">
            <Button label="Details" icon="pi pi-chevron-down" style="margin-right: 2px;" @click="toggleDetails" />
            <Button label="Go to Home" @click="goHome" />
          </ButtonGroup>
        </div>
        <div v-if="showDetails" class="details-card">
          <Card class="" style="background-color: #fff">
            <template #content>
              <h4 class="text-center">
                Transaction Details:
              </h4>
              <Divider />
              <p><strong>Order Number:</strong> {{ route.query.OrderNumber }}</p>
              <p><strong>Amount:</strong> {{ amount }}</p>
              <p><strong>ITBIS:</strong> {{ itbis }}</p>
              <p><strong>Authorization Code:</strong> {{ route.query.AuthorizationCode }}</p>
              <p><strong>Date and Time:</strong> {{ dateTime }}</p>
              <p><strong>Merchant Response:</strong> {{ route.query.ResponseMessage }} ({{ route.query.IsoCode }})</p>
              <p><strong>Reference Number:</strong> {{ route.query.RRN }}</p>
              <p><strong>Card Number:</strong> {{ route.query.CardNumber }}</p>
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

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-text {
  margin-top: 20px;
  font-size: 1.2rem;
  color: #555;
}

.card-bg-color {
  background-color: #ececf9;
}
</style>

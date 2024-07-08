<script setup lang="ts">
import { ref, watch } from 'vue'
import type { IData, IPatient, IPerson } from '../interfaces/IModelData'

const props = defineProps({
  data: {
    type: Object as () => IData | undefined,
    required: true,
  },
  message: {
    type: String,
    required: false,
    default: '¿Estás seguro que desea eliminar el registro seleccionado?'
  },
  openDialog: {
    type: Boolean,
    required: true
  }
})

const emits = defineEmits<{
  (e: 'onDeleteConfirmed', id: string): void
  (e: 'onCloseDialog', value: boolean): void
}>()

const dialogVisible = ref(props.openDialog)

const confirmMessage = ref('')
const message = ref('')

function processMessage(data: any, message?: string): string {
  if (!data) { return '' }
  if (!message) { return '' } // Si no se proporciona un mensaje, retornar cadena vacía

  // Expresión regular para encontrar las variables entre {{ y }}
  const regex = /{{\s*(.*?)\s*}}/g

  // Reemplazar las variables en el mensaje con sus valores correspondientes
  const processedMessage = message.replace(regex, (match, key) => {
    // Verificar si la propiedad existe en data
    if (key in data) {
      return String(data[key as keyof (IPatient | IPerson)])
    }
    else {
      // Si la propiedad no existe, retornar la misma variable sin cambios
      return match
    }
  })

  return processedMessage
}

function showConfirmationDialog(message: string, openDialog: boolean) {
  confirmMessage.value = message
  dialogVisible.value = openDialog
}

function closeDialog() {
  message.value = ''
  emits('onCloseDialog', false)
}

function confirmDelete(id: string) {
  emits('onDeleteConfirmed', id)
}

watch(() => props.openDialog, (newValue) => {
  if (props.data) {
    message.value = processMessage(props.data, props.message)
  }
  showConfirmationDialog(props.message, newValue)
})
</script>

<template>
  <Dialog v-model:visible="dialogVisible" :style="{ width: '450px' }" header="Eliminar registro" :modal="true" :closable="false">
    <div class="flex align-items-center justify-content-center my-3">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
      <span>{{ message }}</span>
    </div>
    <template #footer>
      <Button label="No" icon="pi pi-times" text @click="closeDialog" />
      <Button label="Sí" icon="pi pi-check" text severity="danger" @click="confirmDelete(props.data?.id)" />
    </template>
  </Dialog>
</template>

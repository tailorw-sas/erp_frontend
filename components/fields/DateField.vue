<!-- DateField.vue - ENHANCED VERSION WITH AUTONOMOUS STYLES -->
<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import Calendar from 'primevue/calendar'
import type { FormFieldProps, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

interface CalendarRef {
  $el?: HTMLElement
  show?: () => void
  hide?: () => void
  navigateToDate?: (date: Date) => void
}

interface DateFieldProps extends Omit<FormFieldProps<Date | string | null>, 'value' | 'onUpdate'> {
  value?: Date | string | null
  readonly?: boolean
  required?: boolean
  name?: string
  id?: string
  placeholder?: string
  helpText?: string
  label?: string
  description?: string
  size?: string
  variant?: string
  class?: string
  config?: any
  onUpdate?: (value: Date | string | null) => void
  dateFormat?: string
  timeOnly?: boolean
  showTime?: boolean
  showSeconds?: boolean
  hourFormat?: '12' | '24'
  showIcon?: boolean
  iconDisplay?: 'input' | 'button'
  icon?: string
  showButtonBar?: boolean
  showWeek?: boolean
  showOtherMonths?: boolean
  selectOtherMonths?: boolean
  inline?: boolean
  numberOfMonths?: number
  responsiveOptions?: Array<{
    breakpoint: string
    numMonths: number
  }>
  view?: 'date' | 'month' | 'year'
  touchUI?: boolean
  appendTo?: string | HTMLElement
  manualInput?: boolean
  minDate?: Date
  maxDate?: Date
  disabledDates?: Date[]
  disabledDays?: number[]
  yearRange?: string
  yearNavigator?: boolean
  monthNavigator?: boolean
  hideOnDateTimeSelect?: boolean
  keepInvalid?: boolean
  mask?: string
  slotChar?: string
  autoFocus?: boolean
  clearable?: boolean
  showToday?: boolean
  highlightWeekends?: boolean
}

const props = withDefaults(defineProps<DateFieldProps>(), {
  readonly: false,
  required: false,
  clearable: true,
  autoFocus: false,
  showToday: false,
  highlightWeekends: false,
  dateFormat: 'yy-mm-dd',
  hourFormat: '24',
  showIcon: true,
  iconDisplay: 'button',
  icon: 'pi pi-calendar',
  showButtonBar: false,
  showWeek: false,
  showOtherMonths: true,
  selectOtherMonths: false,
  inline: false,
  numberOfMonths: 1,
  view: 'date',
  touchUI: false,
  manualInput: true,
  yearNavigator: false,
  monthNavigator: false,
  hideOnDateTimeSelect: true,
  keepInvalid: false,
  slotChar: '_'
})

const emit = defineEmits<{
  'update:modelValue': [value: Date | string | null]
  'update:value': [value: Date | string | null]
  'blur': []
  'focus': []
  'clear': []
  'dateSelect': [value: Date]
  'todayClick': [value: Date]
  'clearClick': []
  'monthChange': [event: { month: number, year: number }]
  'yearChange': [event: { month: number, year: number }]
  'show': []
  'hide': []
}>()

const calendarRef = ref<CalendarRef | null>(null)
const isMounted = ref(false)
const internalValue = ref<Date | null>(null)

const isDateTime = computed(() =>
  props.field?.type === 'datetime' || props.showTime
)

const isTimeOnly = computed(() =>
  props.field?.type === 'time' || props.timeOnly
)

watch(() => props.value, (newValue) => {
  if (!newValue) {
    internalValue.value = null
    return
  }

  if (typeof newValue === 'string') {
    const date = new Date(newValue)
    internalValue.value = Number.isNaN(date.getTime()) ? null : date
  }
  else if (newValue instanceof Date) {
    internalValue.value = newValue
  }
  else {
    internalValue.value = null
  }
}, { immediate: true })

const calendarValue = computed({
  get: () => internalValue.value,
  set: (newValue: Date | null) => {
    internalValue.value = newValue
    handleValueUpdate(newValue)
  }
})

const isMobile = computed(() => {
  if (typeof window === 'undefined') { return false }
  return window.innerWidth <= 768
})

const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly || props.readonly
)

const showClearButton = computed(() => {
  if (!props.clearable || isDisabled.value) { return false }

  return internalValue.value !== null
    && internalValue.value !== undefined
    && !isDisabled.value
})

// Enhanced classes with error handling
const calendarClasses = computed(() => {
  const classes = ['date-field__calendar']

  // 🎯 MANEJO MÁS ESTRICTO DE ERRORES - SOLO MARCAR COMO INVÁLIDO SI HAY ERRORES REALES
  try {
    let hasRealError = false

    if (props.error) {
      if (Array.isArray(props.error)) {
        // Solo si hay errores en el array y no está vacío
        hasRealError = props.error.length > 0 && props.error.some(error =>
          error && (typeof error === 'string' ? (error as string).trim() : true)
        )
      }
      else if (typeof props.error === 'string') {
        // Solo si el string no está vacío
        hasRealError = (props.error as string).trim().length > 0
      }
      else if (props.error && typeof props.error === 'object') {
        // Solo si es un objeto de error válido
        hasRealError = true
      }
    }

    if (hasRealError) {
      classes.push('p-invalid')
    }
  }
  catch (error) {
    console.warn('🔍 [CALENDAR CLASSES] Error handling props.error:', error)
    // No añadir clase de error si hay problemas procesando
  }

  if (props.inline) {
    classes.push('date-field__calendar--inline')
  }

  if (isMobile.value) {
    classes.push('date-field__calendar--mobile')
  }

  if (props.field?.ui?.className) {
    try {
      const uiClasses = Array.isArray(props.field.ui.className)
        ? props.field.ui.className
        : [props.field.ui.className]
      classes.push(...uiClasses)
    }
    catch (error) {
      console.warn('🔍 [CALENDAR CLASSES] Error handling ui.className:', error)
    }
  }

  return classes
})

const wrapperClasses = computed(() => {
  const classes = ['date-field__wrapper']

  // Same enhanced error handling for wrapper
  try {
    let hasRealError = false

    if (props.error) {
      if (Array.isArray(props.error)) {
        hasRealError = props.error.length > 0 && props.error.some(error =>
          error && (typeof error === 'string' ? (error as string).trim() : true)
        )
      }
      else if (typeof props.error === 'string') {
        hasRealError = (props.error as string).trim().length > 0
      }
      else if (props.error && typeof props.error === 'object') {
        hasRealError = true
      }
    }

    if (hasRealError) {
      classes.push('date-field__wrapper--error', 'p-invalid')
    }
  }
  catch (error) {
    console.warn('🔍 [WRAPPER CLASSES] Error handling props.error:', error)
  }

  if (props.loading) {
    classes.push('date-field__wrapper--loading')
  }

  return classes
})

const placeholder = computed(() => {
  if (props.field?.ui?.placeholder) {
    return props.field.ui.placeholder
  }

  if (isTimeOnly.value) { return 'Select time' }
  if (isDateTime.value) { return 'Select date and time' }
  return 'Select date'
})

const computedDateFormat = computed(() => {
  if (isTimeOnly.value) {
    return props.hourFormat === '12' ? 'h:mm:ss TT' : 'HH:mm:ss'
  }

  if (isDateTime.value) {
    const timeFormat = props.showSeconds
      ? (props.hourFormat === '12' ? 'h:mm:ss TT' : 'HH:mm:ss')
      : (props.hourFormat === '12' ? 'h:mm TT' : 'HH:mm')
    return `${props.dateFormat} ${timeFormat}`
  }

  return props.dateFormat
})

const fieldStyle = computed(() => props.field?.ui?.style || {})

// Computed para propiedades de accesibilidad
const accessibilityProps = computed(() => {
  // 🎯 MANEJO MÁS ESTRICTO DE ERRORES PARA ARIA
  let hasRealError = false
  try {
    if (props.error) {
      if (Array.isArray(props.error)) {
        hasRealError = props.error.length > 0 && props.error.some(error =>
          error && (typeof error === 'string' ? (error as string).trim() : true)
        )
      }
      else if (typeof props.error === 'string') {
        hasRealError = (props.error as string).trim().length > 0
      }
      else if (props.error && typeof props.error === 'object') {
        hasRealError = true
      }
    }
  }
  catch (error) {
    console.warn('🔍 [ACCESSIBILITY PROPS] Error handling props.error:', error)
    hasRealError = false
  }

  return {
    'aria-label': `Calendar for ${props.field?.label || props.label || 'date selection'}`,
    'aria-describedby': props.field?.helpText ? `${props.id}-help` : undefined,
    'aria-invalid': hasRealError,
    'aria-required': props.required
  }
})

const responsiveCalendarOptions = computed(() => {
  if (props.responsiveOptions) { return props.responsiveOptions }

  return [
    { breakpoint: '768px', numMonths: 1 },
    { breakpoint: '480px', numMonths: 1 }
  ]
})

const baseFieldProps = computed(() => ({
  field: props.field,
  value: props.value ?? null,
  error: props.error,
  loading: props.loading,
  disabled: props.disabled,
  readonly: props.readonly,
  required: props.required,
  name: props.name,
  id: props.id,
  placeholder: props.placeholder,
  helpText: props.helpText,
  label: props.label,
  description: props.description,
  size: props.size,
  variant: props.variant,
  class: props.class,
  config: props.config,
  style: fieldStyle.value,
  onUpdate: props.onUpdate || handleValueUpdate
}))

function handleValueUpdate(value: Date | null) {
  let processedValue: Date | string | null = null

  if (value instanceof Date) {
    if (props.field?.type === 'date' && !isDateTime.value) {
      processedValue = value.toISOString().split('T')[0]
    }
    else {
      processedValue = value
    }
  }
  else {
    processedValue = null
  }

  if (props.onUpdate) {
    props.onUpdate(processedValue)
  }

  if (processedValue !== props.value) {
    emit('update:modelValue', processedValue)
    emit('update:value', processedValue)
  }
}

function _handleBlur() {
  emit('blur')
}

function _handleFocus() {
  emit('focus')
}

function handleClear() {
  const oldValue = internalValue.value
  internalValue.value = null

  if (oldValue !== null) {
    emit('update:modelValue', null)
    if (props.onUpdate) {
      props.onUpdate(null)
    }
  }

  emit('clear')
  emit('clearClick')
}

function handleDateSelect(value: Date) {
  emit('dateSelect', value)
}

function handleTodayClick() {
  const today = new Date()
  internalValue.value = today
  handleValueUpdate(today)
  emit('todayClick', today)
}

function handleMonthChange(event: { month: number, year: number }) {
  emit('monthChange', event)
}

function handleYearChange(event: { month: number, year: number }) {
  emit('yearChange', event)
}

function handleShow() {
  emit('show')
}

function handleHide() {
  emit('hide')
}

function formatDisplayValue(date: Date | null): string {
  if (!date) { return '' }

  try {
    if (isTimeOnly.value) {
      return date.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
        second: props.showSeconds ? '2-digit' : undefined,
        hour12: props.hourFormat === '12'
      })
    }

    if (isDateTime.value) {
      return date.toLocaleString([], {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: props.showSeconds ? '2-digit' : undefined,
        hour12: props.hourFormat === '12'
      })
    }

    return date.toLocaleDateString()
  }
  catch (error) {
    console.warn('Date formatting error:', error)
    return date.toString()
  }
}

async function focus() {
  await nextTick()
  if (calendarRef.value?.$el) {
    const input = calendarRef.value.$el.querySelector('input')
    input?.focus()
  }
}

function blur() {
  if (calendarRef.value?.$el) {
    const input = calendarRef.value.$el.querySelector('input')
    input?.blur()
  }
}

function show() {
  calendarRef.value?.show?.()
}

function hide() {
  calendarRef.value?.hide?.()
}

function getToday(): Date {
  return new Date()
}

function navigateToToday() {
  const today = getToday()
  calendarRef.value?.navigateToDate?.(today)
}

watch(() => props.autoFocus, (shouldFocus) => {
  if (shouldFocus && isMounted.value) {
    nextTick(() => focus())
  }
}, { immediate: true })

watch(() => true, () => {
  isMounted.value = true
}, { immediate: true })

defineExpose({
  focus,
  blur,
  show,
  hide,
  getToday,
  navigateToToday,
  calendarRef,
  formatDisplayValue
})
</script>

<template>
  <BaseField v-bind="baseFieldProps">
    <template #input="{ fieldId }">
      <div :class="wrapperClasses">
        <Calendar
          :id="fieldId"
          ref="calendarRef"
          v-model="calendarValue"
          :class="calendarClasses"
          :style="fieldStyle"
          :date-format="computedDateFormat"
          :time-only="isTimeOnly"
          :show-time="isDateTime"
          :show-seconds="showSeconds"
          :hour-format="hourFormat"
          :show-icon="showIcon"
          :icon-display="iconDisplay"
          :icon="icon"
          :show-button-bar="showButtonBar"
          :show-week="showWeek"
          :show-other-months="showOtherMonths"
          :select-other-months="selectOtherMonths"
          :inline="inline"
          :number-of-months="isMobile ? 1 : numberOfMonths"
          :responsive-options="responsiveCalendarOptions"
          :view="view"
          :touch-ui="isMobile || touchUI"
          :append-to="appendTo"
          :manual-input="manualInput"
          :min-date="minDate"
          :max-date="maxDate"
          :disabled-dates="disabledDates"
          :disabled-days="disabledDays"
          :year-range="yearRange"
          :year-navigator="yearNavigator"
          :month-navigator="monthNavigator"
          :hide-on-date-time-select="hideOnDateTimeSelect"
          :keep-invalid="keepInvalid"
          :mask="mask"
          :slot-char="slotChar"
          :placeholder="placeholder"
          :disabled="isDisabled"
          :show-clear="showClearButton"
          v-bind="accessibilityProps"
          @blur="_handleBlur"
          @focus="_handleFocus"
          @date-select="handleDateSelect"
          @today-click="handleTodayClick"
          @clear="handleClear"
          @month-change="handleMonthChange"
          @year-change="handleYearChange"
          @show="handleShow"
          @hide="handleHide"
        >
          <template v-if="showToday && !inline" #header>
            <div class="date-field__header">
              <button
                type="button"
                class="date-field__today-btn"
                @click="handleTodayClick"
              >
                <i class="pi pi-calendar" />
                <span>Today</span>
              </button>
            </div>
          </template>

          <template #footer>
            <slot name="footer" :value="calendarValue" :formatted-value="formatDisplayValue(calendarValue)">
              <div v-if="calendarValue" class="date-field__footer">
                <small class="date-field__selected-info">
                  <i class="pi pi-check-circle" />
                  Selected: {{ formatDisplayValue(calendarValue) }}
                </small>
              </div>
            </slot>
          </template>

          <template #date="{ date }">
            <slot name="date" :date="date">
              <span
                class="date-field__date-cell"
                :class="{
                  'date-field__date-cell--weekend': highlightWeekends && (date.selectable && [0, 6].includes(new Date(date.year, date.month, date.day).getDay())),
                }"
              >
                {{ date.day }}
              </span>
            </slot>
          </template>
        </Calendar>
      </div>
    </template>

    <template #label="slotProps">
      <slot name="label" v-bind="slotProps" />
    </template>

    <template #prefix="slotProps">
      <slot name="prefix" v-bind="slotProps" />
    </template>

    <template #suffix="slotProps">
      <slot name="suffix" v-bind="slotProps" />
    </template>

    <template #icon="slotProps">
      <slot name="icon" v-bind="slotProps" />
    </template>

    <template #help="slotProps">
      <slot name="help" v-bind="slotProps" />
    </template>

    <template #error="slotProps">
      <slot name="error" v-bind="slotProps" />
    </template>

    <template #loading>
      <slot name="loading" />
    </template>

    <template #clear-icon>
      <slot name="clear-icon" />
    </template>
  </BaseField>
</template>

<style scoped>
/* ===============================================================================
   🎯 ENHANCED DATE FIELD STYLES - PROFESSIONAL UI/UX
   =============================================================================== */

/* 🎨 CSS VARIABLES - CONSISTENT WITH GLOBAL DESIGN SYSTEM */
:root {
  --enhanced-form-field-height: 2.75rem;
  --enhanced-form-field-height-mobile: 3rem;
  --enhanced-form-border-width: 2px;
  --enhanced-form-border-radius: 0.5rem;
  --enhanced-form-border-radius-small: 0.375rem;
  --enhanced-form-transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --enhanced-form-transition-fast: all 0.15s ease;
  --enhanced-form-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --enhanced-form-shadow-lg: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --enhanced-form-focus-border: #3b82f6;
  --enhanced-form-focus-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  --enhanced-form-error-border: #ef4444;
  --enhanced-form-error-shadow: 0 0 0 1px #ef4444;
  --enhanced-form-error-shadow-hover: 0 0 0 3px rgba(239, 68, 68, 0.1);
  --enhanced-form-font-size: 1rem;
  --enhanced-form-dropdown-z: 1000;
}

/* Color Variables */
:root {
  --primary-color: #3b82f6;
  --primary-hover: #2563eb;
  --primary-light: #dbeafe;
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --warning-light: #fef3c7;
  --error-color: #ef4444;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;
}

/* ===============================
   🎯 ENHANCED CALENDAR/DATE FIELD STYLING - ENTERPRISE GRADE
   ===============================*/

/* UNIVERSAL CLEAR BUTTON */
:deep(.p-calendar .p-calendar-clear-icon) {
  position: absolute !important;
  top: 50% !important;
  right: calc(var(--enhanced-form-field-height) + 8px) !important;
  transform: translateY(-50%) !important;
  width: 1.25rem !important;
  height: 1.25rem !important;
  color: var(--gray-500) !important;
  cursor: pointer !important;
  border-radius: 50% !important;
  transition: var(--enhanced-form-transition) !important;
  z-index: 2 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
  margin: 0 !important;
  box-shadow: none !important;

  &:hover {
    color: var(--error-color) !important;
    background: rgba(239, 68, 68, 0.1) !important;
    transform: translateY(-50%) scale(1.1) !important;
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2) !important;
  }

  &:focus {
    outline: 2px solid rgba(239, 68, 68, 0.3) !important;
    outline-offset: 1px !important;
  }

  &:active {
    transform: translateY(-50%) scale(0.95) !important;
  }

  .p-icon,
  .pi {
    font-size: 1rem !important;
    color: inherit !important;
    margin: 0 !important;
    padding: 0 !important;
    line-height: 1 !important;
    font-weight: 400 !important;
    font-family: 'primeicons' !important;
    vertical-align: baseline !important;
    text-rendering: auto !important;
    -webkit-font-smoothing: antialiased !important;
  }
}

/* CALENDAR COMPONENT STYLING */
:deep(.p-calendar) {
  position: relative !important;
  border: var(--enhanced-form-border-width) solid var(--gray-200) !important;
  border-radius: var(--enhanced-form-border-radius) !important;
  transition: var(--enhanced-form-transition) !important;
  background: white !important;
  display: flex !important;
  align-items: center !important;
  overflow: hidden !important;
  box-shadow: var(--enhanced-form-shadow) !important;
  width: 100% !important;
  height: var(--enhanced-form-field-height) !important;
  min-height: var(--enhanced-form-field-height) !important;
  max-width: none !important;
  min-width: 0 !important;
  box-sizing: border-box !important;

  .p-component {
    height: 100% !important;
  }

  .p-calendar-w-btn {
    width: 100% !important;
    max-width: none !important;
    display: flex !important;
    align-items: center !important;
  }

  .p-inputtext,
  .p-datepicker-trigger {
    border: none !important;
    box-shadow: none !important;
    outline: none !important;

    &:focus,
    &:focus-visible,
    &:focus-within {
      border: none !important;
      box-shadow: none !important;
      outline: none !important;
    }
  }

  .p-inputtext {
    width: 100% !important;
    height: 100% !important;
    font-size: var(--enhanced-form-font-size) !important;
    background: transparent !important;
    color: var(--gray-700) !important;
    padding: 0 1rem !important;
    flex: 1 !important;
    line-height: normal !important;

    &::placeholder {
      color: var(--gray-400) !important;
      font-style: italic !important;
    }
  }

  .p-datepicker-trigger {
    height: 100% !important;
    width: var(--enhanced-form-field-height) !important;
    background: transparent !important;
    border-left: 1px solid var(--gray-200) !important;
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    color: var(--primary-color) !important;
    cursor: pointer !important;
    flex-shrink: 0 !important;
    transition: var(--enhanced-form-transition) !important;

    &:hover {
      background: rgba(59, 130, 246, 0.05) !important;
      color: var(--primary-hover) !important;
    }

    .p-icon {
      color: inherit !important;
      font-size: 1.1rem !important;
    }
  }

  &:hover:not(.p-calendar-disabled):not(.p-calendar-opened):not(.p-focus) {
    border-color: var(--gray-300) !important;
    transform: translateY(-1px) !important;
    box-shadow: var(--enhanced-form-shadow-lg) !important;
  }

  &:focus-within:not(.p-calendar-opened) {
    border-color: var(--enhanced-form-focus-border) !important;
    box-shadow: var(--enhanced-form-focus-shadow) !important;
    transform: translateY(-1px) !important;
  }

  &.p-calendar-opened {
    border-bottom-left-radius: 0 !important;
    border-bottom-right-radius: 0 !important;
    border-bottom-color: transparent !important;
    border-color: var(--enhanced-form-focus-border) !important;
    box-shadow: var(--enhanced-form-focus-shadow) !important;
    transform: translateY(-1px) !important;
  }

  &.p-invalid {
    border-color: var(--enhanced-form-error-border) !important;
    box-shadow: var(--enhanced-form-error-shadow) !important;
    animation: fieldErrorShake 0.5s ease-in-out !important;

    &:focus-within {
      border-color: var(--enhanced-form-error-border) !important;
      box-shadow: var(--enhanced-form-error-shadow-hover) !important;
    }
  }
}

/* ===============================
   DATE PICKER PANEL - ENTERPRISE GRADE
   ===============================*/

:deep(.p-datepicker) {
  border-radius: 0 0 var(--enhanced-form-border-radius) var(--enhanced-form-border-radius) !important;
  border: var(--enhanced-form-border-width) solid var(--enhanced-form-focus-border) !important;
  border-top: none !important;
  margin-top: 0 !important;
  background: white !important;
  overflow: hidden !important;
  animation: slideDown 0.2s ease-out !important;
  z-index: calc(var(--enhanced-form-dropdown-z) + 1) !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1), 0 4px 10px rgba(0, 0, 0, 0.05) !important;

  &::before {
    content: '';
    position: absolute;
    top: -1px;
    left: 0;
    right: 0;
    height: 1px;
    background: var(--enhanced-form-focus-border);
    z-index: 1;
  }

  .p-datepicker-header {
    background: var(--gray-50) !important;
    border-bottom: 1px solid var(--gray-200) !important;
    padding: 1rem !important;

    .p-datepicker-title {
      color: var(--gray-800) !important;
      font-weight: 600 !important;
      font-size: 1.1rem !important;
    }

    .p-datepicker-prev,
    .p-datepicker-next {
      color: var(--gray-600) !important;
      width: 32px !important;
      height: 32px !important;
      border-radius: var(--enhanced-form-border-radius-small) !important;
      transition: var(--enhanced-form-transition) !important;

      &:hover {
        background: var(--gray-100) !important;
        color: var(--gray-800) !important;
        transform: scale(1.1) !important;
      }

      &:focus {
        outline: 2px solid rgba(59, 130, 246, 0.3) !important;
        outline-offset: 1px !important;
      }
    }
  }

  .p-datepicker-calendar {
    .p-datepicker-weekheader {
      background: var(--gray-50) !important;

      th {
        color: var(--gray-600) !important;
        font-weight: 600 !important;
        padding: 0.75rem 0.5rem !important;
        font-size: 0.9rem !important;
        text-transform: uppercase !important;
        letter-spacing: 0.5px !important;
      }
    }

    td {
      padding: 2px !important;

      .p-datepicker-day {
        width: 36px !important;
        height: 36px !important;
        border-radius: var(--enhanced-form-border-radius-small) !important;
        color: var(--gray-700) !important;
        font-weight: 500 !important;
        transition: var(--enhanced-form-transition) !important;

        &:hover {
          background: var(--primary-light) !important;
          color: var(--primary-color) !important;
          transform: scale(1.05) !important;
          box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2) !important;
        }

        &:focus {
          outline: 2px solid rgba(59, 130, 246, 0.3) !important;
          outline-offset: 1px !important;
        }

        &.p-datepicker-day-selected {
          background: var(--primary-color) !important;
          color: white !important;
          font-weight: 700 !important;
          box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3) !important;
          transform: scale(1.05) !important;
        }

        &.p-datepicker-day-today {
          background: var(--warning-light) !important;
          color: var(--warning-color) !important;
          font-weight: 700 !important;
          border: 2px solid var(--warning-color) !important;
        }

        &.p-datepicker-day-other-month {
          opacity: 0.5 !important;
        }
      }
    }
  }
}

/* ===============================
   DATE FIELD COMPONENT STYLES
   ===============================*/

.date-field__wrapper {
  width: 100% !important;
  position: relative !important;
  display: flex !important;
  align-items: stretch !important;
  box-sizing: border-box !important;
  animation: fadeInUp 0.3s ease-out;
  margin: 0 !important;
  padding: 0 !important;

  &--error {
    .date-field__calendar {
      border-color: var(--error-color) !important;
      box-shadow: 0 0 0 1px var(--error-color) !important;
      animation: fieldErrorShake 0.5s ease-in-out !important;
    }
  }

  &--loading {
    opacity: 0.7 !important;
    pointer-events: none !important;

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(255, 255, 255, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 10;
    }
  }
}

.date-field__calendar {
  width: 100% !important;

  &--inline {
    border: none !important;
    box-shadow: none !important;
    border-radius: var(--enhanced-form-border-radius) !important;
    overflow: hidden;
  }

  &--mobile {
    font-size: 1.1rem !important;

    :deep(.p-calendar-w-btn .p-inputtext) {
      font-size: 1rem !important;
    }
  }
}

.date-field__header {
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  padding: 0.75rem 1rem !important;
  background: var(--gray-50) !important;
  border-bottom: 1px solid var(--gray-200) !important;
}

.date-field__today-btn {
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  padding: 0.5rem 0.75rem !important;
  background: var(--primary-color) !important;
  color: white !important;
  border: none !important;
  border-radius: var(--enhanced-form-border-radius-small) !important;
  cursor: pointer !important;
  font-size: 0.875rem !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3) !important;

  &:hover {
    background: var(--primary-hover) !important;
    transform: translateY(-1px) !important;
    box-shadow: 0 4px 8px rgba(59, 130, 246, 0.4) !important;
  }

  &:focus {
    outline: 2px solid rgba(59, 130, 246, 0.3) !important;
    outline-offset: 1px !important;
  }

  &:active {
    transform: translateY(0) !important;
  }
}

.date-field__footer {
  padding: 0.75rem 1rem !important;
  background: var(--gray-50) !important;
  border-top: 1px solid var(--gray-200) !important;
}

.date-field__selected-info {
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  color: var(--success-color) !important;
  font-weight: 500 !important;
  font-size: 0.875rem !important;

  .pi {
    color: inherit !important;
    animation: pulse 2s infinite !important;
  }
}

.date-field__date-cell {
  &--weekend {
    color: var(--error-color) !important;
    font-weight: 600 !important;
    background: rgba(239, 68, 68, 0.1) !important;
    border-radius: 50% !important;
  }
}

/* ===============================
   🎯 ACCESSIBILITY IMPROVEMENTS
   ===============================*/

:deep(.p-calendar[aria-expanded="true"]) {
  border-color: var(--enhanced-form-focus-border) !important;
  box-shadow: var(--enhanced-form-focus-shadow) !important;
}

/* FOCUS VISIBLE STATES */
:deep(.p-calendar:focus-visible) {
  outline: 2px solid var(--enhanced-form-focus-border) !important;
  outline-offset: 2px !important;
}

/* DISABLED STATE */
:deep(.p-calendar.p-disabled) {
  opacity: 0.6 !important;
  cursor: not-allowed !important;
  background: var(--gray-50) !important;
}

/* LOADING STATE ENHANCEMENTS */
:deep(.p-calendar.p-loading) {
  pointer-events: none !important;
}

:deep(.p-calendar.p-loading .p-datepicker-trigger) {
  opacity: 0.6 !important;
}

/* ===============================
   🎬 ANIMATIONS
   ===============================*/

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDown {
  0% {
    opacity: 0;
    transform: translateY(-5px) scaleY(0.95);
    transform-origin: top;
  }
  100% {
    opacity: 1;
    transform: translateY(0) scaleY(1);
    transform-origin: top;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@keyframes fieldErrorShake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-2px); }
  20%, 40%, 60%, 80% { transform: translateX(2px); }
}

/* ===============================
   📱 RESPONSIVE DESIGN
   ===============================*/

@media (max-width: 768px) {
  :root {
    --enhanced-form-field-height: var(--enhanced-form-field-height-mobile);
  }

  :deep(.p-calendar) {
    height: var(--enhanced-form-field-height-mobile) !important;
    min-height: var(--enhanced-form-field-height-mobile) !important;
  }

  :deep(.p-calendar .p-inputtext) {
    height: calc(var(--enhanced-form-field-height-mobile) - 4px) !important;
    padding: 0 0.75rem !important;
  }

  :deep(.p-calendar .p-datepicker-trigger) {
    height: calc(var(--enhanced-form-field-height-mobile) - 4px) !important;
    width: var(--enhanced-form-field-height-mobile) !important;
  }

  :deep(.p-calendar-clear-icon) {
    width: 1.5rem !important;
    height: 1.5rem !important;
    right: calc(var(--enhanced-form-field-height-mobile) + 10px) !important;

    .p-icon,
    .pi {
      font-size: 0.9rem !important;
    }
  }

  .date-field__today-btn {
    padding: 0.625rem 1rem !important;
    font-size: 0.9rem !important;

    .pi {
      font-size: 1rem !important;
    }
  }

  .date-field__selected-info {
    font-size: 0.9rem !important;
  }
}

@media (max-width: 480px) {
  :root {
    --enhanced-form-field-height: 3.5rem;
  }

  :deep(.p-calendar) {
    height: 3.5rem !important;
    min-height: 3.5rem !important;
  }

  :deep(.p-calendar-clear-icon) {
    width: 1.75rem !important;
    height: 1.75rem !important;
    right: calc(3.5rem + 12px) !important;
  }
}

/* ===============================
   🎯 HIGH CONTRAST MODE SUPPORT
   ===============================*/

@media (prefers-contrast: high) {
  :deep(.p-calendar) {
    border-width: 3px !important;
    border-color: #000 !important;
  }
}

/* ===============================
   🎯 REDUCED MOTION SUPPORT
   ===============================*/

@media (prefers-reduced-motion: reduce) {
  :deep(.p-calendar) {
    transition: none !important;
    animation: none !important;
  }

  .date-field__today-btn,
  .date-field__selected-info .pi {
    transition: none !important;
    animation: none !important;
  }
}

/* ===============================
   🎯 PRINT STYLES
   ===============================*/

@media print {
  :deep(.p-datepicker) {
    display: none !important;
  }
}

/* ===============================
   🎯 DARK MODE SUPPORT (if needed)
   ===============================*/

@media (prefers-color-scheme: dark) {
  :root {
    --enhanced-form-focus-border: #60a5fa;
    --enhanced-form-focus-shadow: 0 0 0 3px rgba(96, 165, 250, 0.1);
  }
}
</style>

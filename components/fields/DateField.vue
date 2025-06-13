<!-- components/fields/DateField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import Calendar from 'primevue/calendar'
import type { FormFieldProps, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

// Tipo para referencias de PrimeVue Calendar
interface CalendarRef {
  $el?: HTMLElement
  show?: () => void
  hide?: () => void
  navigateToDate?: (date: Date) => void
}

// Props
interface DateFieldProps extends Omit<FormFieldProps<Date | string | null>, 'onUpdate'> {
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
}

const props = withDefaults(defineProps<DateFieldProps>(), {
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

// Emits con nombres camelCase
const emit = defineEmits<{
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

// Template ref
const calendarRef = ref<CalendarRef | null>(null)

// Computed properties
const isDateTime = computed(() =>
  props.field.type === 'datetime' || props.showTime
)

const isTimeOnly = computed(() =>
  props.field.type === 'time' || props.timeOnly
)

const calendarValue = computed({
  get: () => {
    if (!props.value) { return null }

    // Handle string dates
    if (typeof props.value === 'string') {
      const date = new Date(props.value)
      return Number.isNaN(date.getTime()) ? null : date
    }

    // Handle Date objects
    if (props.value instanceof Date) {
      return props.value
    }

    return null
  },
  set: (newValue: Date | string | null) => {
    handleUpdate(newValue)
  }
})

const calendarClasses = computed(() => {
  const classes = ['date-field__calendar']

  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  if (props.inline) {
    classes.push('date-field__calendar--inline')
  }

  // Aplicar clases del tema/configuración
  if (props.field.ui?.className) {
    if (Array.isArray(props.field.ui.className)) {
      classes.push(...props.field.ui.className)
    }
    else {
      classes.push(props.field.ui.className)
    }
  }

  return classes
})

const isDisabled = computed(() =>
  props.disabled || props.field.ui?.readonly
)

const placeholder = computed(() => {
  // Usar el placeholder de la configuración UI
  if (props.field.ui?.placeholder) {
    return props.field.ui.placeholder
  }

  if (isTimeOnly.value) {
    return 'Select time'
  }

  if (isDateTime.value) {
    return 'Select date and time'
  }

  return 'Select date'
})

const computedDateFormat = computed(() => {
  if (isTimeOnly.value) {
    return props.hourFormat === '12' ? 'h:mm:ss TT' : 'HH:mm:ss'
  }

  if (isDateTime.value) {
    const timeFormat = props.hourFormat === '12' ? 'h:mm TT' : 'HH:mm'
    const showSecondsFormat = props.showSeconds
      ? (props.hourFormat === '12' ? 'h:mm:ss TT' : 'HH:mm:ss')
      : timeFormat
    return `${props.dateFormat} ${showSecondsFormat}`
  }

  return props.dateFormat
})

const hasValue = computed(() =>
  calendarValue.value !== null
)

const showClearButton = computed(() =>
  (props.field.ui?.clearable ?? true) && hasValue.value
)

// Configuración de estilo desde el field
const fieldStyle = computed(() => {
  return props.field.ui?.style || {}
})

// Methods
function handleUpdate(value: Date | string | null) {
  let processedValue: Date | string | null = value

  // Convert to string if needed based on field configuration
  if (value instanceof Date && props.field.type === 'date' && !isDateTime.value) {
    // For date-only fields, format as YYYY-MM-DD string
    processedValue = value.toISOString().split('T')[0]
  }

  emit('update:value', processedValue)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

function handleClear() {
  emit('update:value', null)
  emit('clear')
  emit('clearClick')
}

function handleDateSelect(value: Date) {
  emit('dateSelect', value)
}

function handleTodayClick(value: Date) {
  emit('todayClick', value)
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

// Helper methods
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
    if (process.env.NODE_ENV === 'development') {
      console.warn('Date formatting error:', error)
    }
    return date.toString()
  }
}

// Función para formatear errores de validación (prefijo _ para indicar que puede no usarse)
function _getErrorMessages(): string[] {
  if (!props.error || props.error.length === 0) { return [] }
  return props.error.map((err: ValidationError) => err.message)
}

// Expose methods for parent components
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
  <BaseField
    v-bind="$props"
    :style="fieldStyle"
    @update:value="handleUpdate"
    @blur="handleBlur"
    @focus="handleFocus"
    @clear="handleClear"
  >
    <template #input="{ fieldId, onBlur, onFocus }">
      <Calendar
        :id="fieldId"
        ref="calendarRef"
        v-model="calendarValue"
        :class="calendarClasses"
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
        :number-of-months="numberOfMonths"
        :responsive-options="responsiveOptions"
        :view="view"
        :touch-ui="touchUI"
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
        @blur="onBlur"
        @focus="onFocus"
        @date-select="handleDateSelect"
        @today-click="handleTodayClick"
        @clear-click="handleClear"
        @month-change="handleMonthChange"
        @year-change="handleYearChange"
        @show="handleShow"
        @hide="handleHide"
      >
        <!-- Header template for custom navigation -->
        <template #header>
          <slot name="header" :calendar-ref="calendarRef">
            <div v-if="showButtonBar" class="date-field__header">
              <button
                type="button"
                class="date-field__today-button"
                @click="navigateToToday"
              >
                <i class="pi pi-calendar" />
                Today
              </button>
              <button
                v-if="showClearButton"
                type="button"
                class="date-field__clear-button-header"
                @click="handleClear"
              >
                <i class="pi pi-times" />
                Clear
              </button>
            </div>
          </slot>
        </template>

        <!-- Footer template -->
        <template #footer>
          <slot name="footer" :value="calendarValue" :formatted-value="formatDisplayValue(calendarValue)">
            <div v-if="calendarValue" class="date-field__footer">
              <small class="date-field__selected-info">
                Selected: {{ formatDisplayValue(calendarValue) }}
              </small>
            </div>
          </slot>
        </template>

        <!-- Date template for custom date rendering -->
        <template #date="{ date }">
          <slot name="date" :date="date">
            {{ date.day }}
          </slot>
        </template>
      </Calendar>
    </template>

    <!-- Custom slots passthrough -->
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
/* Estilos usando tokens del tema */
.date-field__calendar {
  width: 100%;
}

.date-field__calendar--inline {
  display: block;
}

.date-field__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--field-spacing-sm, 0.5rem);
  padding: var(--field-spacing-sm, 0.5rem);
  border-bottom: 1px solid var(--field-border-color, rgb(229 231 235));
  background: var(--field-header-background, rgb(249 250 251));
}

.date-field__today-button,
.date-field__clear-button-header {
  display: flex;
  align-items: center;
  gap: var(--field-spacing-xs, 0.25rem);
  padding: var(--field-spacing-xs, 0.25rem) var(--field-spacing-sm, 0.5rem);
  background: var(--field-background, white);
  border: 1px solid var(--field-border-color, rgb(209 213 219));
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  color: var(--field-text-color, rgb(55 65 81));
  font-size: var(--field-font-size-sm, 0.75rem);
  cursor: pointer;
  transition: var(--field-transition, all 0.2s ease-in-out);
}

.date-field__today-button:hover {
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  border-color: var(--field-primary-color, rgb(59 130 246));
}

.date-field__clear-button-header {
  color: var(--field-error-color, rgb(239 68 68));
}

.date-field__clear-button-header:hover {
  background: var(--field-error-color, rgb(239 68 68));
  color: white;
  border-color: var(--field-error-color, rgb(239 68 68));
}

.date-field__footer {
  padding: var(--field-spacing-sm, 0.5rem);
  border-top: 1px solid var(--field-border-color, rgb(229 231 235));
  background: var(--field-footer-background, rgb(249 250 251));
  text-align: center;
}

.date-field__selected-info {
  color: var(--field-text-secondary, rgb(107 114 128));
  font-size: var(--field-font-size-sm, 0.75rem);
}

/* Calendar control overrides */
:deep(.p-calendar) {
  width: 100%;
}

:deep(.p-calendar .p-inputtext) {
  width: 100%;
  height: var(--field-height, 2.5rem);
  padding: var(--field-padding, 0.5rem 0.75rem);
  font-size: var(--field-font-size, 0.875rem);
  border: var(--field-border-width, 1px) solid var(--field-border-color, rgb(209 213 219));
  border-radius: var(--field-border-radius, 0.375rem);
  background: var(--field-background, white);
  color: var(--field-text-color, rgb(17 24 39));
  transition: var(--field-transition, all 0.2s ease-in-out);
}

:deep(.p-calendar .p-inputtext::placeholder) {
  color: var(--field-placeholder-color, rgb(156 163 175));
}

:deep(.p-calendar .p-inputtext:hover:not(:disabled)) {
  border-color: var(--field-border-color-hover, rgb(156 163 175));
}

:deep(.p-calendar .p-inputtext:focus) {
  border-color: var(--field-border-color-focus, rgb(59 130 246));
  box-shadow: var(--field-focus-shadow, 0 0 0 2px rgb(59 130 246 / 0.1));
}

:deep(.p-calendar .p-inputtext:disabled) {
  background: var(--field-disabled-background, rgb(249 250 251));
  color: var(--field-disabled-color, rgb(156 163 175));
  cursor: not-allowed;
}

:deep(.p-calendar.p-invalid .p-inputtext) {
  border-color: var(--field-error-color, rgb(239 68 68));
  box-shadow: var(--field-error-shadow, 0 0 0 2px rgb(239 68 68 / 0.1));
}

:deep(.p-calendar.p-invalid .p-inputtext:focus) {
  border-color: var(--field-error-color, rgb(239 68 68));
  box-shadow: var(--field-error-focus-shadow, 0 0 0 2px rgb(239 68 68 / 0.2));
}

/* Calendar button styles */
:deep(.p-calendar .p-button) {
  height: var(--field-height, 2.5rem);
  border-color: var(--field-border-color, rgb(209 213 219));
  background: var(--field-button-background, rgb(249 250 251));
  color: var(--field-text-secondary, rgb(107 114 128));
}

:deep(.p-calendar .p-button:hover) {
  background: var(--field-button-hover-background, rgb(243 244 246));
  border-color: var(--field-border-color-hover, rgb(156 163 175));
}

:deep(.p-calendar .p-button:focus) {
  box-shadow: var(--field-focus-shadow, 0 0 0 2px rgb(59 130 246 / 0.1));
  border-color: var(--field-border-color-focus, rgb(59 130 246));
}

:deep(.p-calendar .p-button.p-button-icon-only) {
  width: var(--field-height, 2.5rem);
  border-radius: 0 var(--field-border-radius, 0.375rem) var(--field-border-radius, 0.375rem) 0;
}

/* Calendar panel styles */
:deep(.p-datepicker) {
  border: 1px solid var(--field-border-color, rgb(229 231 235));
  border-radius: var(--field-border-radius, 0.375rem);
  box-shadow: var(--field-shadow-lg, 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1));
  background: var(--field-background, white);
  font-family: inherit;
}

:deep(.p-datepicker-header) {
  background: var(--field-header-background, rgb(249 250 251));
  border-bottom: 1px solid var(--field-border-color, rgb(229 231 235));
  border-radius: var(--field-border-radius, 0.375rem) var(--field-border-radius, 0.375rem) 0 0;
  padding: var(--field-spacing-sm, 0.5rem);
}

:deep(.p-datepicker-title) {
  color: var(--field-text-color, rgb(17 24 39));
  font-weight: var(--field-font-weight-semibold, 600);
  font-size: var(--field-font-size, 0.875rem);
}

:deep(.p-datepicker-prev),
:deep(.p-datepicker-next) {
  width: 2rem;
  height: 2rem;
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  background: transparent;
  color: var(--field-text-secondary, rgb(107 114 128));
  border: 1px solid transparent;
  transition: var(--field-transition, all 0.2s ease-in-out);
}

:deep(.p-datepicker-prev:hover),
:deep(.p-datepicker-next:hover) {
  background: var(--field-background, white);
  color: var(--field-primary-color, rgb(59 130 246));
  border-color: var(--field-border-color, rgb(209 213 219));
}

/* Calendar table styles */
:deep(.p-datepicker-calendar) {
  margin: var(--field-spacing-sm, 0.5rem);
}

:deep(.p-datepicker-calendar td) {
  padding: 0.125rem;
}

:deep(.p-datepicker-calendar .p-datepicker-date) {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  border: 1px solid transparent;
  background: transparent;
  color: var(--field-text-color, rgb(55 65 81));
  font-size: var(--field-font-size-sm, 0.75rem);
  transition: var(--field-transition, all 0.2s ease-in-out);
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.p-datepicker-calendar .p-datepicker-date:hover) {
  background: var(--field-hover-color, rgb(239 246 255));
  color: var(--field-primary-color, rgb(59 130 246));
  border-color: var(--field-primary-light, rgb(191 219 254));
}

:deep(.p-datepicker-calendar .p-datepicker-date.p-highlight) {
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  border-color: var(--field-primary-color, rgb(59 130 246));
}

:deep(.p-datepicker-calendar .p-datepicker-date.p-datepicker-today) {
  background: var(--field-warning-light, rgb(254 243 199));
  color: var(--field-warning-dark, rgb(146 64 14));
  border-color: var(--field-warning-color, rgb(251 191 36));
  font-weight: var(--field-font-weight-semibold, 600);
}

:deep(.p-datepicker-calendar .p-datepicker-date.p-datepicker-today.p-highlight) {
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  border-color: var(--field-primary-color, rgb(59 130 246));
}

:deep(.p-datepicker-calendar .p-datepicker-date.p-disabled) {
  opacity: 0.4;
  cursor: not-allowed;
}

:deep(.p-datepicker-calendar .p-datepicker-date.p-disabled:hover) {
  background: transparent;
  color: var(--field-text-color, rgb(55 65 81));
  border-color: transparent;
}

/* Time picker styles */
:deep(.p-datepicker-time-picker) {
  border-top: 1px solid var(--field-border-color, rgb(229 231 235));
  padding: var(--field-spacing-md, 0.75rem);
  background: var(--field-footer-background, rgb(249 250 251));
}

:deep(.p-datepicker-hour-picker),
:deep(.p-datepicker-minute-picker),
:deep(.p-datepicker-second-picker),
:deep(.p-datepicker-ampm-picker) {
  margin: 0 var(--field-spacing-xs, 0.25rem);
}

:deep(.p-datepicker-time-picker button) {
  width: 2rem;
  height: 2rem;
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
  background: var(--field-background, white);
  border: 1px solid var(--field-border-color, rgb(209 213 219));
  color: var(--field-text-color, rgb(55 65 81));
  transition: var(--field-transition, all 0.2s ease-in-out);
}

:deep(.p-datepicker-time-picker button:hover) {
  background: var(--field-primary-color, rgb(59 130 246));
  color: white;
  border-color: var(--field-primary-color, rgb(59 130 246));
}

:deep(.p-datepicker-time-picker span) {
  font-size: var(--field-font-size, 0.875rem);
  color: var(--field-text-color, rgb(17 24 39));
  font-weight: var(--field-font-weight-medium, 500);
  min-width: 2rem;
  text-align: center;
}

/* Button bar styles */
:deep(.p-datepicker-buttonbar) {
  border-top: 1px solid var(--field-border-color, rgb(229 231 235));
  padding: var(--field-spacing-sm, 0.5rem);
  display: flex;
  justify-content: space-between;
  background: var(--field-footer-background, rgb(249 250 251));
  border-radius: 0 0 var(--field-border-radius, 0.375rem) var(--field-border-radius, 0.375rem);
}

:deep(.p-datepicker-buttonbar .p-button) {
  padding: var(--field-spacing-xs, 0.25rem) var(--field-spacing-sm, 0.5rem);
  font-size: var(--field-font-size-sm, 0.75rem);
  border-radius: calc(var(--field-border-radius, 0.375rem) - 2px);
}

/* Inline calendar styles */
.date-field__calendar--inline :deep(.p-datepicker) {
  position: relative;
  display: inline-block;
  box-shadow: none;
  border: 1px solid var(--field-border-color, rgb(229 231 235));
}

/* Responsive design */
@media (max-width: 640px) {
  :deep(.p-datepicker) {
    width: 100vw;
    max-width: calc(100vw - 2rem);
  }

  :deep(.p-datepicker-calendar .p-datepicker-date) {
    width: 2rem;
    height: 2rem;
    font-size: 0.75rem;
  }

  .date-field__header {
    flex-direction: column;
    gap: var(--field-spacing-xs, 0.25rem);
  }

  .date-field__today-button,
  .date-field__clear-button-header {
    width: 100%;
    justify-content: center;
  }
}

/* Accessibility */
:deep(.p-calendar:focus-visible) {
  outline: 2px solid var(--field-primary-color, rgb(59 130 246));
  outline-offset: 2px;
}

:deep(.p-datepicker-date:focus-visible) {
  outline: 2px solid var(--field-primary-color, rgb(59 130 246));
  outline-offset: 1px;
}

@media (prefers-reduced-motion: reduce) {
  :deep(.p-calendar .p-inputtext),
  :deep(.p-datepicker-prev),
  :deep(.p-datepicker-next),
  :deep(.p-datepicker-date),
  :deep(.p-datepicker-time-picker button) {
    transition: none;
  }
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  :deep(.p-datepicker) {
    background: var(--field-background-dark, rgb(31 41 55));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
  }

  :deep(.p-datepicker-header),
  :deep(.p-datepicker-time-picker),
  :deep(.p-datepicker-buttonbar) {
    background: var(--field-header-background-dark, rgb(17 24 39));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
  }

  :deep(.p-datepicker-title),
  :deep(.p-datepicker-time-picker span) {
    color: var(--field-text-color-dark, rgb(243 244 246));
  }

  :deep(.p-datepicker-calendar .p-datepicker-date) {
    color: var(--field-text-color-dark, rgb(209 213 219));
  }

  .date-field__header {
    background: var(--field-header-background-dark, rgb(17 24 39));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
  }

  .date-field__footer {
    background: var(--field-footer-background-dark, rgb(17 24 39));
    border-color: var(--field-border-color-dark, rgb(75 85 99));
  }

  .date-field__selected-info {
    color: var(--field-text-secondary-dark, rgb(156 163 175));
  }
}
</style>

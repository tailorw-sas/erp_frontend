<!-- DateField.vue - CLEAN VERSION SIN ESTILOS -->
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

interface DateFieldProps extends Omit<FormFieldProps<Date | string | null>, 'value'> {
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

const calendarClasses = computed(() => {
  const classes = ['date-field__calendar']

  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  if (props.inline) {
    classes.push('date-field__calendar--inline')
  }

  if (isMobile.value) {
    classes.push('date-field__calendar--mobile')
  }

  if (props.field?.ui?.className) {
    const uiClasses = Array.isArray(props.field.ui.className)
      ? props.field.ui.className
      : [props.field.ui.className]
    classes.push(...uiClasses)
  }

  return classes
})

const wrapperClasses = computed(() => {
  const classes = ['date-field__wrapper']

  if (props.error && props.error.length > 0) {
    classes.push('date-field__wrapper--error', 'p-invalid')
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
  }
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
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
    <template #input="{ fieldId, onUpdate, onBlur, onFocus }">
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
          @blur="onBlur"
          @focus="onFocus"
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

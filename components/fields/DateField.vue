<!-- components/fields/DateField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import Calendar from 'primevue/calendar'
import type { FormFieldProps, ValidationError } from '../../types/form'
import BaseField from './BaseField.vue'

// ========== INTERFACES ==========
interface CalendarRef {
  $el?: HTMLElement
  show?: () => void
  hide?: () => void
  navigateToDate?: (date: Date) => void
}

interface DateFieldProps extends Omit<FormFieldProps<Date | string | null>, 'value'> {
  // ✅ FIXED: Proper prop extension
  value?: Date | string | null
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
  // ✅ NEW: Enhanced UX props
  autoFocus?: boolean
  clearable?: boolean
  showToday?: boolean
  highlightWeekends?: boolean
}

// ========== PROPS ==========
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
  slotChar: '_',
  autoFocus: false,
  clearable: true,
  showToday: false,
  highlightWeekends: false
})

// ========== EMITS ==========
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

// ========== REFS ==========
const calendarRef = ref<CalendarRef | null>(null)
const isMounted = ref(false)

// ========== COMPUTED PROPERTIES ==========
const isDateTime = computed(() =>
  props.field?.type === 'datetime' || props.showTime
)

const isTimeOnly = computed(() =>
  props.field?.type === 'time' || props.timeOnly
)

// ✅ IMPROVED: Simplified calendar value handling
const calendarValue = computed({
  get: () => {
    if (!props.value) { return null }

    // Handle different input formats
    if (typeof props.value === 'string') {
      const date = new Date(props.value)
      return isNaN(date.getTime()) ? null : date
    }

    return props.value instanceof Date ? props.value : null
  },
  set: (newValue: Date | null) => {
    handleValueUpdate(newValue)
  }
})

// ✅ IMPROVED: Better responsive handling
const isMobile = computed(() => {
  if (typeof window === 'undefined') { return false }
  return window.innerWidth <= 768
})

const calendarClasses = computed(() => {
  const classes = ['date-field__calendar']

  // Error state
  if (props.error && props.error.length > 0) {
    classes.push('p-invalid')
  }

  // Layout variants
  if (props.inline) {
    classes.push('date-field__calendar--inline')
  }

  if (isMobile.value) {
    classes.push('date-field__calendar--mobile')
  }

  // Field UI classes
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

  // Error states
  if (props.error && props.error.length > 0) {
    classes.push('date-field__wrapper--error', 'p-invalid')
  }

  // Loading state
  if (props.loading) {
    classes.push('date-field__wrapper--loading')
  }

  return classes
})

const isDisabled = computed(() =>
  props.disabled || props.field?.ui?.readonly
)

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

const hasValue = computed(() => calendarValue.value !== null)

const showClearButton = computed(() =>
  props.clearable && hasValue.value && !isDisabled.value
)

const fieldStyle = computed(() => props.field?.ui?.style || {})

// ✅ NEW: Enhanced responsive options
const responsiveCalendarOptions = computed(() => {
  if (props.responsiveOptions) { return props.responsiveOptions }

  return [
    { breakpoint: '768px', numMonths: 1 },
    { breakpoint: '480px', numMonths: 1 }
  ]
})

// ========== METHODS ==========
function handleValueUpdate(value: Date | null) {
  let processedValue: Date | string | null = value

  // Convert to string format if needed for form compatibility
  if (value instanceof Date && props.field?.type === 'date' && !isDateTime.value) {
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

function handleTodayClick() {
  const today = new Date()
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

// ========== UTILITY METHODS ==========
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

function getErrorMessages(): string[] {
  if (!props.error || props.error.length === 0) { return [] }
  return props.error.map((err: ValidationError) => err.message)
}

// ========== EXPOSED METHODS ==========
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

// ========== WATCHERS ==========
watch(() => props.autoFocus, (shouldFocus) => {
  if (shouldFocus && isMounted.value) {
    nextTick(() => focus())
  }
}, { immediate: true })

// ========== LIFECYCLE ==========
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
  formatDisplayValue,
  getErrorMessages
})
</script>

<template>
  <BaseField
    v-bind="$props"
    :style="fieldStyle"
    @update:value="handleValueUpdate"
    @blur="handleBlur"
    @focus="handleFocus"
    @clear="handleClear"
  >
    <template #input="{ fieldId, onBlur, onFocus }">
      <div :class="wrapperClasses">
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
          <!-- ✅ IMPROVED: Better header with conditional rendering -->
          <template #header>
            <slot name="header" :calendar-ref="calendarRef">
              <div v-if="showButtonBar || showToday" class="date-field__header">
                <button
                  v-if="showToday"
                  type="button"
                  class="date-field__today-btn"
                  @click="handleTodayClick"
                >
                  <i class="pi pi-calendar" />
                  <span>Today</span>
                </button>
                <button
                  v-if="showClearButton"
                  type="button"
                  class="date-field__clear-btn"
                  @click="handleClear"
                >
                  <i class="pi pi-times" />
                  <span>Clear</span>
                </button>
              </div>
            </slot>
          </template>

          <!-- ✅ IMPROVED: Enhanced footer -->
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

          <!-- Custom date cell rendering -->
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

        <!-- ✅ NEW: Quick actions for better UX -->
        <div v-if="showClearButton && !inline" class="date-field__quick-actions">
          <button
            type="button"
            class="date-field__clear-quick"
            :disabled="isDisabled"
            @click="handleClear"
          >
            <i class="pi pi-times" />
          </button>
        </div>
      </div>
    </template>

    <!-- Pass through all slots -->
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

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, reactive, readonly, ref, watch } from 'vue'
import { z } from 'zod'
import dayjs from 'dayjs'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import ProgressBar from 'primevue/progressbar'
import Dropdown from 'primevue/dropdown'
import Tag from 'primevue/tag'
import Divider from 'primevue/divider'
import Skeleton from 'primevue/skeleton'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'
import type { LocationQueryValue } from 'vue-router'
import Logger from '~/utils/Logger'
import { GenericService } from '~/services/generic-services'
import { FormFieldBuilder as FieldBuilder } from '~/utils/formFieldBuilder'
import CustomAutoCompleteComponent from '~/components/fields/CustomAutoCompleteComponent.vue'
import CustomMultiSelectComponent from '~/components/fields/CustomMultiSelectComponent.vue'
import EnhancedFormComponent from '~/components/form/EnhancedFormComponent.vue'

// ========== TYPES & INTERFACES ==========
interface ReportFormField {
  name: string // Requerido por FormField
  type: string // Requerido por FormField
  field: string // Tu propiedad existente
  label: string
  class?: string
  placeholder?: string
  helpText?: string
  validation?: any
  dataType?: string
  options?: any[]
  objApi?: {
    moduleApi: string
    uriApi: string
  }
  kwArgs?: any
  multiple?: boolean
  filterable?: boolean
  maxSelectedLabels?: number
  selectionLimit?: number
  showClear?: boolean
  disabled?: boolean
  hidden?: boolean
  required?: boolean
  defaultValue?: any
}

interface FieldValues {
  [key: string]: any
}

interface ReportConfig {
  moduleApi: string
  uriApiReportGenerate: string
  timeout?: number
  retryAttempts?: number
  retryDelay?: number
}

interface ReportProgress {
  status: 'idle' | 'generating' | 'success' | 'error' | 'timeout'
  message?: string
  progress?: number
}

interface ReportResponse {
  base64Report: string
}

interface ReportFormat {
  id: 'PDF' | 'XLS' | 'CSV'
  name: string
  icon: string
  description: string
}

// ========== BACKEND INTERFACES - NEW ==========
interface BackendReportParameter {
  id: string
  paramName: string
  type: string // java.sql.Date, java.lang.String, etc.
  module: string
  service: string
  label: string
  componentType: 'text' | 'select' | 'multiselect' | 'localselect' | 'date' | 'number'
  jasperReportTemplate: any // Nested object we don't need
  reportClass: string
  reportValidation: string
  parameterPosition: number
  dependentField: string
  filterKeyValue: string
  dataValueStatic: string
}

interface BackendReportInfo {
  id: string
  code: string
  name: string
  description?: string
  parameters?: BackendReportParameter[]
}

// ========== FRONTEND INTERFACES - UPDATED ==========
interface ReportParameter {
  paramName: string
  label: string
  componentType: 'text' | 'select' | 'multiselect' | 'localselect' | 'date' | 'number'
  module?: string
  service?: string
  reportClass?: string
  dataValueStatic?: string
  filtersBase?: any[]
  dependentField?: string
  debounceTimeMs?: number
  maxSelectedLabels?: number
  required?: boolean
  defaultValue?: any
  parameterPosition?: number
}

interface ReportInfo {
  id: string
  code: string
  name: string
  description?: string
  category?: string
  parameters?: ReportParameter[]
}

// ========== CONSTANTS ==========
const REPORT_FORMATS: ReportFormat[] = [
  {
    id: 'PDF',
    name: 'PDF Document',
    icon: 'pi pi-file-pdf',
    description: 'Portable Document Format - Best for viewing and printing'
  },
  {
    id: 'XLS',
    name: 'Excel Spreadsheet',
    icon: 'pi pi-file-excel',
    description: 'Excel format - Best for data analysis'
  },
  {
    id: 'CSV',
    name: 'CSV Data',
    icon: 'pi pi-table',
    description: 'Comma-separated values - Best for importing data'
  },
]

const FORM_VALIDATION_RULES = {
  reportCode: z.string().min(1, 'Report code is required'),
  formatType: z.object({
    id: z.string(),
    name: z.string()
  })
}

// ========== REACTIVE STATE ==========
const route = useRoute()
const toast = useToast()

// Form state
const fields = ref<ReportFormField[]>([])
const item = ref<FieldValues>({
  jasperReportCode: '',
  reportFormatType: REPORT_FORMATS[0]
})
const formReload = ref(0)
const showForm = ref(false)
const formValidationErrors = ref<Record<string, string[]>>({})

// Report state
const currentReport = ref<ReportInfo | null>(null)
const loadingReport = ref(false)
const suggestionsData = ref<any[]>([])

// Report generator config
const reportConfig = reactive<ReportConfig>({
  moduleApi: 'reports',
  uriApiReportGenerate: '/generate',
  timeout: 600000,
  retryAttempts: 2,
  retryDelay: 3000
})

// ========== COMPOSABLES ==========
function useReportGenerator(config: ReportConfig) {
  const pdfUrl = ref('')
  const reportProgress = reactive<ReportProgress>({
    status: 'idle',
    progress: 0
  })

  const defaultConfig = {
    timeout: 600000, // 10 minutes
    retryAttempts: 2,
    retryDelay: 3000,
    ...config
  }

  const isGenerating = computed(() => reportProgress.status === 'generating')
  const canRetry = computed(() => reportProgress.status === 'error' || reportProgress.status === 'timeout')

  let progressInterval: NodeJS.Timeout | null = null

  async function generateReport(payload: any): Promise<string | null> {
    let attempt = 0

    while (attempt <= defaultConfig.retryAttempts) {
      try {
        reportProgress.status = 'generating'
        reportProgress.progress = 0
        reportProgress.message = attempt > 0
          ? `Retry attempt ${attempt}/${defaultConfig.retryAttempts}...`
          : 'Initializing report generation...'

        // Simulate progress for better UX
        progressInterval = setInterval(() => {
          if ((reportProgress.progress || 0) < 90) {
            reportProgress.progress = (reportProgress.progress || 0) + Math.random() * 10
          }
        }, 1000)

        const response = await GenericService.createReport<ReportResponse>(
          defaultConfig.moduleApi,
          defaultConfig.uriApiReportGenerate,
          payload,
          defaultConfig.timeout
        )

        if (progressInterval) {
          clearInterval(progressInterval)
          progressInterval = null
        }
        reportProgress.progress = 100

        if (response?.base64Report) {
          reportProgress.status = 'success'
          reportProgress.message = 'Report generated successfully'
          return response.base64Report
        }

        throw new Error('Invalid server response - no report data received')
      }
      catch (error: any) {
        attempt++

        if (progressInterval) {
          clearInterval(progressInterval)
          progressInterval = null
        }

        Logger.error(`Generation attempt ${attempt} failed:`, error)

        if (error.message?.includes('cancelled') || error.message?.includes('timed out')) {
          reportProgress.status = 'timeout'
          reportProgress.message = 'Request timed out. Please try again with different parameters or contact support.'
          break
        }

        if (attempt <= defaultConfig.retryAttempts) {
          reportProgress.message = `Attempt ${attempt} failed. Retrying in ${defaultConfig.retryDelay / 1000} seconds...`
          await new Promise(resolve => setTimeout(resolve, defaultConfig.retryDelay))
        }
        else {
          reportProgress.status = 'error'
          reportProgress.message = `Failed to generate report after ${defaultConfig.retryAttempts} attempts. ${error.message}`
        }
      }
    }

    return null
  }

  function processReportFile(base64Report: string, formatType: string): string | null {
    try {
      // Cleanup previous file
      if (pdfUrl.value) {
        URL.revokeObjectURL(pdfUrl.value)
        pdfUrl.value = ''
      }

      const byteCharacters = atob(base64Report)
      const byteArray = new Uint8Array(byteCharacters.length)

      // Process in chunks for better performance
      const chunkSize = 1024 * 1024
      for (let i = 0; i < byteCharacters.length; i += chunkSize) {
        const chunk = byteCharacters.slice(i, i + chunkSize)
        for (let j = 0; j < chunk.length; j++) {
          byteArray[i + j] = chunk.charCodeAt(j)
        }
      }

      const timestamp = dayjs().format('YYYY-MM-DD_HH-mm-ss')
      const { mimeType, extension } = getFileTypeInfo(formatType)

      const blob = new Blob([byteArray], { type: mimeType })
      const blobUrl = URL.createObjectURL(blob)

      if (formatType === 'PDF') {
        pdfUrl.value = blobUrl
      }
      else {
        downloadFile(blobUrl, `report-${timestamp}.${extension}`)
      }

      return blobUrl
    }
    catch (error) {
      Logger.error('Error processing file:', error)
      reportProgress.status = 'error'
      reportProgress.message = 'Error processing the generated file'
      return null
    }
  }

  function downloadFile(url: string, filename: string) {
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    // Cleanup after a delay
    setTimeout(() => URL.revokeObjectURL(url), 1000)
  }

  function getFileTypeInfo(formatType: string) {
    const formats = {
      XLS: {
        mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        extension: 'xlsx'
      },
      CSV: {
        mimeType: 'text/csv',
        extension: 'csv'
      },
      PDF: {
        mimeType: 'application/pdf',
        extension: 'pdf'
      },
    }

    return formats[formatType as keyof typeof formats] || formats.PDF
  }

  function cleanup() {
    if (pdfUrl.value) {
      URL.revokeObjectURL(pdfUrl.value)
      pdfUrl.value = ''
    }
    if (progressInterval) {
      clearInterval(progressInterval)
      progressInterval = null
    }
    reportProgress.status = 'idle'
    reportProgress.message = undefined
    reportProgress.progress = 0
  }

  function resetProgress() {
    if (progressInterval) {
      clearInterval(progressInterval)
      progressInterval = null
    }
    reportProgress.status = 'idle'
    reportProgress.message = undefined
    reportProgress.progress = 0
  }

  return {
    pdfUrl: readonly(pdfUrl),
    reportProgress: readonly(reportProgress),
    isGenerating,
    canRetry,
    generateReport,
    processReportFile,
    cleanup,
    resetProgress
  }
}

function useReportParameters() {
  function validateParameter(param: ReportParameter, value: any): boolean {
    if (param.required && (!value || (Array.isArray(value) && value.length === 0))) {
      toast.add({
        severity: 'warn',
        summary: 'Validation Error',
        detail: `${param.label} is required`,
        life: 3000
      })
      return false
    }
    return true
  }

  function validateAllParameters(parameters: ReportParameter[], formData: any): boolean {
    const requiredParams = parameters.filter(p => p.required)

    for (const param of requiredParams) {
      if (!validateParameter(param, formData[param.paramName])) {
        return false
      }
    }
    return true
  }

  const isValidDate = (value: string): boolean => !Number.isNaN(Date.parse(value))

  function processParameterValue(value: any): any {
    if (value === null || value === undefined) { return null }

    if (isValidDate(value) && typeof value !== 'number') {
      return dayjs(value).format('YYYY-MM-DD')
    }
    else if (Array.isArray(value) && value.every(v => v && typeof v === 'object' && 'id' in v)) {
      return value.map(v => v.id)
    }
    else if (value && typeof value === 'object' && 'id' in value) {
      return value.id
    }
    return value
  }

  function buildPayload(formData: any, reportCode: string, formatType: any) {
    const payload = {
      parameters: {} as Record<string, any>,
      reportFormatType: typeof formatType === 'object' ? formatType.id : formatType,
      jasperReportCode: reportCode,
      requestId: Date.now().toString(),
      metadata: {
        timestamp: dayjs().toISOString(),
        userAgent: navigator.userAgent
      }
    }

    const excludeFields = ['reportFormatType', 'jasperReportCode', 'event']

    payload.parameters = Object.keys(formData)
      .filter(key => !excludeFields.includes(key))
      .reduce((acc, key) => {
        const value = processParameterValue(formData[key])
        if (value !== null && value !== undefined) {
          acc[key] = value
        }
        return acc
      }, {} as Record<string, any>)

    return payload
  }

  return {
    buildPayload,
    validateAllParameters,
    validateParameter
  }
}

function useFieldBuilder() {
  // ✅ IMPROVED: Better option normalization with support for defaultValue
  function normalizeOptions(options: any[], defaultValue?: any): Array<{ label: string, value: any, default?: boolean }> {
    if (!options || !Array.isArray(options) || options.length === 0) {
      return []
    }

    // Handle backend localselect format: {id, name, slug, defaultValue}
    if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'id' in opt)) {
      return options.map(opt => ({
        label: opt.name,
        value: opt.id,
        default: opt.defaultValue === true
      }))
    }

    // Si ya están normalizadas con 'label'
    if (options.every(opt => opt && typeof opt === 'object' && 'label' in opt && 'value' in opt)) {
      return options
    }

    // Si tienen 'name' en lugar de 'label'
    if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'value' in opt)) {
      return options.map(opt => ({ label: opt.name, value: opt.value }))
    }

    // Si son strings
    if (options.every(opt => typeof opt === 'string')) {
      return options.map(opt => ({ label: opt, value: opt }))
    }

    // Si son objetos mixtos
    if (options.every(opt => opt && typeof opt === 'object')) {
      return options.map((opt, index) => ({
        label: opt.label || opt.name || opt.text || opt.value || `Option ${index + 1}`,
        value: opt.value !== undefined ? opt.value : opt.id !== undefined ? opt.id : opt
      }))
    }

    // Fallback
    return options.map(opt => ({ label: String(opt), value: opt }))
  }

  // ✅ NEW: Map backend parameter to frontend parameter
  function mapBackendParameter(backendParam: BackendReportParameter): ReportParameter {
    const mappedParam: ReportParameter = {
      paramName: backendParam.paramName,
      label: backendParam.label,
      componentType: backendParam.componentType,
      required: false, // Default as requested
      debounceTimeMs: 300, // Default as requested
      maxSelectedLabels: 3, // Default as requested
      parameterPosition: backendParam.parameterPosition || 0, // ✅ NEW: Preserve parameter order
    }

    // Add module/service for dynamic components
    if (backendParam.module && backendParam.module.trim() !== '') {
      mappedParam.module = backendParam.module.trim()
    }

    if (backendParam.service && backendParam.service.trim() !== '') {
      mappedParam.service = backendParam.service.trim()
    }

    // Handle dependent field relationships
    if (backendParam.dependentField && backendParam.dependentField.trim() !== '') {
      mappedParam.dependentField = backendParam.dependentField.trim()
    }

    // Map filterKeyValue to filtersBase structure
    if (backendParam.filterKeyValue && backendParam.filterKeyValue.trim() !== '') {
      mappedParam.filtersBase = [{
        key: backendParam.filterKeyValue.trim(),
        value: null // Will be populated dynamically based on dependent field
      }]
    }
    else {
      mappedParam.filtersBase = []
    }

    // Handle static data for localselect
    if (backendParam.componentType === 'localselect' && backendParam.dataValueStatic) {
      mappedParam.dataValueStatic = backendParam.dataValueStatic
    }

    return mappedParam
  }

  function createFieldFromParameter(param: ReportParameter): any {
    const baseProps = {
      name: param.paramName,
      type: param.componentType,
      field: param.paramName,
      label: param.label,
      class: 'col-12 md:col-6', // Default layout
      placeholder: `Enter ${param.label}`,
      helpText: param.required ? 'This field is required' : `Optional - ${param.label}`,
      validation: param.required
        ? z.string().min(1, `${param.label} is required`)
        : z.string().optional(),
      required: param.required || false
    }

    if (param.required) {
      baseProps.class = `${baseProps.class} required`
    }

    const fieldConfigs = {
      multiselect: () => ({
        ...FieldBuilder.multiselect(param.paramName, param.label, []),
        ...baseProps,
        dataType: 'multiselect',
        multiple: true,
        filterable: true,
        maxSelectedLabels: param.maxSelectedLabels || 3,
        selectionLimit: 50,
        showClear: true
      }),

      select: () => ({
        ...FieldBuilder.select(param.paramName, param.label, []),
        ...baseProps,
        dataType: 'select',
        filterable: true,
        showClear: true
      }),

      localselect: () => {
        let options: Array<{ label: string, value: any, default?: boolean }> = []
        let defaultValue: any = null

        try {
          if (param.dataValueStatic) {
            const rawData = JSON.parse(param.dataValueStatic.replace(/\n/g, ''))
            options = normalizeOptions(rawData)

            // Find default value
            const defaultOption = options.find(opt => opt.default === true)
            if (defaultOption) {
              defaultValue = defaultOption.value
            }
          }
        }
        catch (e) {
          Logger.error('Invalid JSON in dataValueStatic for param', param.paramName, e)
          options = []
        }

        return {
          ...FieldBuilder.select(param.paramName, param.label, options),
          ...baseProps,
          type: 'localselect',
          dataType: 'localselect',
          options,
          defaultValue, // ✅ NEW: Support for default value
          filterable: options.length > 10,
          showClear: true
        }
      },

      date: () => ({
        ...FieldBuilder.date(param.paramName, param.label),
        ...baseProps,
        dataType: 'date'
      }),

      number: () => ({
        ...FieldBuilder.number(param.paramName, param.label),
        ...baseProps,
        dataType: 'number'
      }),

      text: () => ({
        ...FieldBuilder.text(param.paramName, param.label),
        ...baseProps,
        dataType: 'text'
      })
    }

    return fieldConfigs[param.componentType]?.() || fieldConfigs.text()
  }

  return {
    createFieldFromParameter,
    normalizeOptions,
    mapBackendParameter // ✅ NEW: Export the mapping function
  }
}

// ========== INITIALIZE COMPOSABLES ==========
const {
  pdfUrl,
  reportProgress,
  isGenerating,
  canRetry,
  generateReport,
  processReportFile,
  cleanup,
  resetProgress
} = useReportGenerator(reportConfig)

const {
  buildPayload,
  validateAllParameters
} = useReportParameters()

const {
  createFieldFromParameter,
  normalizeOptions,
  mapBackendParameter
} = useFieldBuilder()

// ========== COMPUTED PROPERTIES ==========
const reportTitle = computed(() => {
  return currentReport.value?.name || 'No Report Selected'
})

const reportDescription = computed(() => {
  return currentReport.value?.description || 'Select a report to begin'
})

const hasParameters = computed(() => {
  return currentReport.value?.parameters && currentReport.value.parameters.length > 0
})

const canGenerate = computed(() => {
  return !isGenerating.value
    && item.value.jasperReportCode
    && item.value.reportFormatType
})

const progressPercentage = computed(() => {
  return Math.round(reportProgress.progress || 0)
})

// ========== METHODS ==========
async function loadReport(reportId: string) {
  if (!reportId) { return }

  try {
    loadingReport.value = true

    // ✅ FIXED: Proper type casting for backend response
    const response = await GenericService.getById<BackendReportInfo>(
      'report',
      'jasper-report-template/template-with-params/',
      reportId
    )

    // ✅ NEW: Map backend response to frontend format with proper sorting
    const mappedReport: ReportInfo = {
      id: response.id,
      code: response.code,
      name: response.name,
      description: response.description,
      parameters: response.parameters
        ? response.parameters
            .map(mapBackendParameter)
            .sort((a, b) => (a.parameterPosition || 0) - (b.parameterPosition || 0)) // ✅ Sort by parameterPosition
        : []
    }

    currentReport.value = mappedReport
    await loadReportParameters(mappedReport.id, mappedReport.code, mappedReport)
  }
  catch (error) {
    Logger.error('Error loading report:', error)
    toast.add({
      severity: 'error',
      summary: 'Load Failed',
      detail: 'Failed to load report information',
      life: 5000
    })
  }
  finally {
    loadingReport.value = false
  }
}

async function loadReportParameters(id: string, code: string, reportData?: ReportInfo) {
  if (!id) { return }

  try {
    showForm.value = false

    // Reset fields to base
    // fields.value = [
    //   {
    //     name: 'jasperReportCode',
    //     type: 'text',
    //     field: 'jasperReportCode',
    //     label: 'Report Code',
    //     disabled: true,
    //     hidden: true,
    //     class: 'col-12',
    //     validation: FORM_VALIDATION_RULES.reportCode
    //   }
    // ]

    // ✅ IMPROVED: Better parameter processing with default values and proper ordering
    if (reportData?.parameters && reportData.parameters.length > 0) {
      // Parameters are already sorted by parameterPosition in loadReport method
      reportData.parameters.forEach((param: ReportParameter, index: number) => {
        // Create field definition
        const fieldDef = createFieldFromParameter(param)

        // ✅ NEW: Set default value if available
        let initialValue = param.componentType === 'multiselect' ? [] : ''
        if (fieldDef.defaultValue !== undefined && fieldDef.defaultValue !== null) {
          initialValue = fieldDef.defaultValue
        }

        // Initialize field value
        item.value[param.paramName] = initialValue

        // ✅ IMPROVED: Better API configuration for dynamic fields
        if (['select', 'multiselect'].includes(param.componentType) && param.module && param.service) {
          fieldDef.objApi = {
            moduleApi: param.module,
            uriApi: param.service
          }
          fieldDef.kwArgs = {
            ...param,
            filtersBase: param.filtersBase || [],
            dependentField: param.dependentField,
            debounceTimeMs: param.debounceTimeMs || 300
          }
        }

        fields.value.push(fieldDef)

        Logger.log(`✅ Parameter ${index + 1} loaded:`, {
          paramName: param.paramName,
          position: param.parameterPosition,
          componentType: param.componentType,
          hasDefault: fieldDef.defaultValue !== undefined,
          initialValue
        })
      })
    }

    item.value.jasperReportCode = code || ''
    currentReport.value = reportData || null // ✅ FIXED: Update current report
    formReload.value++

    await nextTick()
    showForm.value = true

    Logger.log('✅ Parameters loaded successfully:', {
      reportCode: code,
      parametersCount: reportData?.parameters?.length || 0,
      fieldsGenerated: fields.value.length,
      parameterOrder: reportData?.parameters?.map(p => ({ name: p.paramName, position: p.parameterPosition })) || [],
      currentFormValues: item.value
    })
  }
  catch (error) {
    Logger.error('Error loading parameters:', error)
    toast.add({
      severity: 'error',
      summary: 'Parameter Load Failed',
      detail: 'Failed to load report parameters',
      life: 5000
    })
  }
}

async function executeReport() {
  if (!canGenerate.value) {
    toast.add({
      severity: 'warn',
      summary: 'Cannot Generate',
      detail: 'Please ensure all required fields are filled',
      life: 3000
    })
    return
  }

  resetProgress()

  try {
    // Validate parameters if report has them
    if (currentReport.value?.parameters) {
      const isValid = validateAllParameters(currentReport.value.parameters, item.value)
      if (!isValid) { return }
    }

    const payload = buildPayload(
      item.value,
      item.value.jasperReportCode,
      item.value.reportFormatType
    )

    Logger.log('Executing report with payload:', payload)

    const base64Report = await generateReport(payload)

    if (base64Report) {
      const processedUrl = processReportFile(base64Report, item.value.reportFormatType.id)

      if (processedUrl) {
        toast.add({
          severity: 'success',
          summary: 'Report Generated',
          detail: `Your ${item.value.reportFormatType.name} report is ready`,
          life: 5000
        })
      }
    }
  }
  catch (error) {
    Logger.error('Error executing report:', error)
    toast.add({
      severity: 'error',
      summary: 'Generation Failed',
      detail: 'Failed to generate report. Please try again.',
      life: 5000
    })
  }
}

async function retryGeneration() {
  if (canRetry.value) {
    await executeReport()
  }
}

function clearForm() {
  // Reset form values but keep report code and format
  const reportCode = item.value.jasperReportCode
  const reportFormat = item.value.reportFormatType

  item.value = {
    jasperReportCode: reportCode,
    reportFormatType: reportFormat
  }

  // Reinitialize parameter values
  if (currentReport.value?.parameters) {
    currentReport.value.parameters.forEach((param: ReportParameter) => {
      item.value[param.paramName] = param.componentType === 'multiselect' ? [] : ''
    })
  }

  formValidationErrors.value = {}
  formReload.value++
}

// ========== EVENT HANDLERS ==========
function handleFieldUpdate(event: any) {
  // Adaptar diferentes formatos de evento
  const name = event.name || event.fieldName || event.field
  const value = event.value

  item.value[name] = value

  // Clear validation error for this field
  if (formValidationErrors.value[name]) {
    delete formValidationErrors.value[name]
  }
}

function handleReactiveUpdate(values: FieldValues) {
  item.value = { ...values }
}

function handleCancel() {
  clearForm()
}

function handleValidationErrors(event: any) {
  // Handle FormValidationEvent properly
  if (event && typeof event === 'object' && 'errors' in event) {
    // Convert ValidationError[] to Record<string, string[]>
    const errorRecord: Record<string, string[]> = {}
    if (Array.isArray(event.errors)) {
      event.errors.forEach((error: any) => {
        if (error.path) {
          if (!errorRecord[error.path]) {
            errorRecord[error.path] = []
          }
          errorRecord[error.path].push(error.message)
        }
      })
    }
    formValidationErrors.value = errorRecord
  }
  else {
    // Fallback for legacy format
    formValidationErrors.value = event || {}
  }
}

// ========== UTILITY FUNCTIONS ==========
function getFormatIcon(formatId: string): string {
  const format = REPORT_FORMATS.find(f => f.id === formatId)
  return format?.icon || 'pi pi-file'
}

function downloadCurrentReport() {
  if (pdfUrl.value) {
    const timestamp = dayjs().format('YYYY-MM-DD_HH-mm-ss')
    const link = document.createElement('a')
    link.href = pdfUrl.value
    link.download = `report-${timestamp}.pdf`
    link.click()
  }
}

function openInNewTab() {
  if (pdfUrl.value) {
    window.open(pdfUrl.value, '_blank', 'noopener,noreferrer')
  }
}

function shareReport() {
  if (typeof navigator !== 'undefined' && (navigator as any).share && pdfUrl.value) {
    (navigator as any).share({
      title: `Report: ${currentReport.value?.name}`,
      text: 'Generated report',
      url: pdfUrl.value
    }).catch((err: any) => Logger.error('Error sharing:', err))
  }
}

// PDF Viewer functions (placeholders for actual implementation)
function zoomIn() {
  // Implement zoom in functionality
  Logger.log('Zoom in')
}

function zoomOut() {
  // Implement zoom out functionality
  Logger.log('Zoom out')
}

// ========== WATCHERS ==========
watch(() => route.query.reportId, (newReportId) => {
  if (newReportId && typeof newReportId === 'string') {
    loadReport(newReportId)
  }
}, { immediate: true })

// ========== LIFECYCLE ==========
onMounted(() => {
  Logger.log('ReportViewer mounted with query params:', route.query)
})

onUnmounted(() => {
  cleanup()
})

// ========== COMPUTED PROPERTIES - Additional ==========
const isShareSupported = computed(() => {
  return typeof navigator !== 'undefined' && (navigator as any).share
})
</script>

<template>
  <div class="report-viewer">
    <!-- Enhanced Header Section -->
    <div class="report-viewer__header">
      <div class="report-viewer__header-content">
        <div class="report-viewer__header-info">
          <div class="report-viewer__header-icon">
            <i class="pi pi-chart-bar" />
          </div>
          <div>
            <h1 class="report-viewer__title">
              Report Viewer
            </h1>
            <p class="report-viewer__subtitle">
              Generate and preview reports with customizable parameters
            </p>
          </div>
        </div>
        <div class="report-viewer__header-badge">
          <i :class="currentReport ? 'pi pi-check-circle' : 'pi pi-info-circle'" />
          <div class="report-viewer__header-badge-content">
            <span class="report-viewer__header-badge-title">{{ reportTitle }}</span>
            <small class="report-viewer__header-badge-subtitle">{{ reportDescription }}</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Enhanced Progress/Status Messages -->
    <div v-if="reportProgress.status !== 'idle'" class="report-viewer__status">
      <!-- Generation Progress -->
      <div v-if="isGenerating" class="report-viewer__status-card report-viewer__status-card--loading">
        <div class="report-viewer__status-content">
          <div class="report-viewer__status-progress">
            <ProgressSpinner style="width: 24px; height: 24px" />
            <div class="report-viewer__progress-details">
              <ProgressBar
                :value="progressPercentage"
                class="report-viewer__progress-bar"
                :show-value="false"
              />
              <span class="report-viewer__progress-text">{{ progressPercentage }}%</span>
            </div>
          </div>
          <div>
            <h4 class="report-viewer__status-title">
              Generating Report
            </h4>
            <p class="report-viewer__status-message">
              {{ reportProgress.message }}
            </p>
          </div>
        </div>
      </div>

      <!-- Success Message -->
      <div v-if="reportProgress.status === 'success'" class="report-viewer__status-card report-viewer__status-card--success">
        <div class="report-viewer__status-content">
          <i class="pi pi-check-circle" />
          <div>
            <h4 class="report-viewer__status-title">
              Report Generated Successfully
            </h4>
            <p class="report-viewer__status-message">
              {{ reportProgress.message }}
            </p>
          </div>
        </div>
      </div>

      <!-- Timeout Warning with Retry -->
      <div v-if="reportProgress.status === 'timeout'" class="report-viewer__status-card report-viewer__status-card--warning">
        <div class="report-viewer__status-content">
          <i class="pi pi-clock" />
          <div class="flex-1">
            <h4 class="report-viewer__status-title">
              Request Timeout
            </h4>
            <p class="report-viewer__status-message">
              {{ reportProgress.message }}
            </p>
          </div>
          <Button
            label="Retry"
            icon="pi pi-refresh"
            class="p-button-warning p-button-outlined"
            size="small"
            @click="retryGeneration"
          />
        </div>
      </div>

      <!-- Error Message with Retry -->
      <div v-if="reportProgress.status === 'error'" class="report-viewer__status-card report-viewer__status-card--error">
        <div class="report-viewer__status-content">
          <i class="pi pi-exclamation-triangle" />
          <div class="flex-1">
            <h4 class="report-viewer__status-title">
              Generation Failed
            </h4>
            <p class="report-viewer__status-message">
              {{ reportProgress.message }}
            </p>
          </div>
          <Button
            label="Retry"
            icon="pi pi-refresh"
            class="p-button-danger p-button-outlined"
            size="small"
            @click="retryGeneration"
          />
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="report-viewer__main">
      <!-- Left Panel: Enhanced Parameters -->
      <div class="report-viewer__parameters">
        <div class="report-viewer__parameters-container">
          <!-- Panel Header -->
          <div class="report-viewer__parameters-header">
            <div class="report-viewer__parameters-header-content">
              <div class="report-viewer__parameters-icon">
                <i class="pi pi-sliders-h" />
              </div>
              <div>
                <h3 class="report-viewer__parameters-title">
                  Report Parameters
                </h3>
                <div class="report-viewer__parameters-subtitle">
                  {{ hasParameters ? 'Configure your report settings' : 'No parameters required' }}
                </div>
              </div>
            </div>
            <div v-if="currentReport" class="report-viewer__parameters-actions">
              <Button
                v-tooltip.top="'Reset Parameters'"
                icon="pi pi-refresh"
                class="p-button-text p-button-sm"
                :disabled="isGenerating"
                @click="clearForm"
              />
            </div>
          </div>

          <!-- Panel Content -->
          <div v-if="hasParameters" class="report-viewer__parameters-content">
            <div v-if="loadingReport" class="report-viewer__loading">
              <div class="report-viewer__loading-content">
                <ProgressSpinner style="width: 32px; height: 32px" />
                <p>Loading report parameters...</p>
              </div>
            </div>

            <EnhancedFormComponent
              v-else-if="showForm"
              :key="formReload"
              :fields="fields as any"
              :initial-values="item"
              :show-actions="false"
              :loading="isGenerating"
              :validate-on-change="true"
              data-test-id="report-form"
              @field-change="handleFieldUpdate"
              @update:values="handleReactiveUpdate"
              @validation-change="handleValidationErrors"
              @cancel="handleCancel"
            >
              <template
                v-for="fieldItem in fields.filter((f) => f.field !== 'reportFormatType' && f.dataType && ['select', 'multiselect', 'localselect'].includes(f.dataType))"
                :key="fieldItem.field"
                #[`field-${fieldItem.field}`]="{ item: data, onUpdate, errors }"
              >
                <!-- Local Select (static options) -->
                <div v-if="fieldItem.dataType === 'localselect'" class="w-full">
                  <Dropdown
                    :id="`local-select-${fieldItem.field}`"
                    :model-value="data[fieldItem.field]"
                    :options="normalizeOptions(fieldItem.options || [])"
                    option-label="label"
                    option-value="value"
                    placeholder="Select an option"
                    class="w-full"
                    :class="{ 'p-invalid': errors && errors.length > 0 }"
                    :filter="(fieldItem.options || []).length > 10"
                    show-clear
                    :disabled="isGenerating"
                    @update:model-value="onUpdate(fieldItem.field, $event)"
                  />
                </div>

                <!-- Dynamic Select (API-based) -->
                <div
                  v-else-if="fieldItem.dataType === 'select' && !isGenerating"
                  class="w-full"
                >
                  <CustomAutoCompleteComponent
                    :id="`autocomplete-${fieldItem.field}`"
                    field="name"
                    item-value="id"
                    :model="data[fieldItem.field]"
                    :suggestions="suggestionsData"
                    :filters-base="fieldItem.kwArgs?.filtersBase || []"
                    :api-config="fieldItem.objApi"
                    :dependent-field="fieldItem.kwArgs?.dependentField"
                    :debounce-time-ms="fieldItem.kwArgs?.debounceTimeMs || 300"
                    :class="{ 'p-invalid': errors && errors.length > 0 }"
                    class="w-full"
                    @change="onUpdate(fieldItem.field, $event)"
                  />
                </div>

                <!-- Dynamic MultiSelect (API-based) -->
                <div
                  v-else-if="fieldItem.dataType === 'multiselect' && !isGenerating"
                  class="w-full"
                >
                  <CustomMultiSelectComponent
                    :id="`multiselect-${fieldItem.field}`"
                    field="name"
                    item-value="id"
                    class="w-full"
                    :class="{ 'p-invalid': errors && errors.length > 0 }"
                    :model-value="data[fieldItem.field] || []"
                    :api-config="fieldItem.objApi"
                    :filters-base="fieldItem.kwArgs?.filtersBase || []"
                    :dependent-field="fieldItem.kwArgs?.dependentField"
                    :debounce-time-ms="fieldItem.kwArgs?.debounceTimeMs || 300"
                    :max-selected-labels="fieldItem.kwArgs?.maxSelectedLabels || 3"
                    :load-on-open="true"
                    :min-query-length="0"
                    @update:model-value="onUpdate(fieldItem.field, $event)"
                  />
                </div>

                <!-- Loading skeleton for dynamic fields -->
                <Skeleton v-else class="w-full h-3rem" />
              </template>

              <!-- Custom header for specific fields -->
              <template
                v-for="fieldItem in fields.filter((f) => f.required)"
                :key="`header-${fieldItem.field}`"
                #[`header-${fieldItem.field}`]="{ field }"
              >
                <div class="flex align-items-center gap-2">
                  <span>{{ field.label }}</span>
                  <Tag severity="danger" value="Required" class="text-xs" />
                </div>
              </template>
            </EnhancedFormComponent>

            <!-- Enhanced Action Section -->
            <div class="report-viewer__actions">
              <!-- Export Format Selection -->
              <div class="report-viewer__format-section">
                <label class="report-viewer__format-label">
                  <i class="pi pi-download mr-2" />
                  Export Format
                </label>
                <div class="report-viewer__format-grid">
                  <div
                    v-for="format in REPORT_FORMATS"
                    :key="format.id"
                    class="report-viewer__format-option"
                    :class="{ 'report-viewer__format-option--selected': item.reportFormatType?.id === format.id }"
                    @click="item.reportFormatType = format"
                  >
                    <div class="report-viewer__format-option-header">
                      <i :class="format.icon" />
                      <span class="report-viewer__format-option-name">{{ format.name }}</span>
                    </div>
                    <p class="report-viewer__format-option-description">
                      {{ format.description }}
                    </p>
                  </div>
                </div>
              </div>

              <!-- Generation Controls -->
              <div class="report-viewer__generation-controls">
                <Button
                  :label="isGenerating ? 'Generating...' : 'Generate Report'"
                  :icon="isGenerating ? 'pi pi-spin pi-spinner' : 'pi pi-play'"
                  class="report-viewer__generate-button report-viewer__generate-btn"
                  :loading="isGenerating"
                  :disabled="!canGenerate"
                  @click="executeReport"
                />

                <div class="report-viewer__generation-info">
                  <div class="flex align-items-center gap-2 text-sm text-gray-600">
                    <i class="pi pi-info-circle" />
                    <span v-if="item.reportFormatType?.id === 'PDF'">
                      PDF will be displayed in the preview panel
                    </span>
                    <span v-else>
                      {{ item.reportFormatType?.name }} file will be automatically downloaded
                    </span>
                  </div>
                </div>
              </div>

              <!-- Quick Actions -->
              <div v-if="pdfUrl" class="report-viewer__quick-actions">
                <h4 class="report-viewer__quick-actions-title">
                  Quick Actions
                </h4>
                <div class="report-viewer__quick-actions-buttons">
                  <Button
                    icon="pi pi-download"
                    label="Download"
                    class="p-button-outlined p-button-sm"
                    @click="downloadCurrentReport"
                  />
                  <Button
                    icon="pi pi-external-link"
                    label="New Tab"
                    class="p-button-outlined p-button-sm"
                    @click="openInNewTab"
                  />
                  <Button
                    v-if="isShareSupported"
                    icon="pi pi-share-alt"
                    label="Share"
                    class="p-button-outlined p-button-sm"
                    @click="shareReport"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- No Parameters State -->
          <div v-else-if="currentReport && !hasParameters" class="report-viewer__no-parameters">
            <div class="report-viewer__no-parameters-content">
              <div class="report-viewer__no-parameters-icon">
                <i class="pi pi-check-circle" />
              </div>
              <h4 class="report-viewer__no-parameters-title">
                Ready to Generate
              </h4>
              <p class="report-viewer__no-parameters-text">
                This report doesn't require any parameters. You can generate it directly.
              </p>

              <!-- Format selection for no-parameter reports -->
              <div class="report-viewer__format-section mt-4">
                <label class="report-viewer__format-label">
                  <i class="pi pi-download mr-2" />
                  Export Format
                </label>
                <Dropdown
                  v-model="item.reportFormatType"
                  :options="REPORT_FORMATS"
                  option-label="name"
                  placeholder="Select Format"
                  class="w-full"
                >
                  <template #value="slotProps">
                    <div v-if="slotProps.value" class="flex align-items-center gap-2">
                      <i :class="slotProps.value.icon" />
                      <span>{{ slotProps.value.name }}</span>
                    </div>
                  </template>
                  <template #option="slotProps">
                    <div class="flex align-items-center gap-2">
                      <i :class="slotProps.option.icon" />
                      <div>
                        <div>{{ slotProps.option.name }}</div>
                        <small class="text-gray-500">{{ slotProps.option.description }}</small>
                      </div>
                    </div>
                  </template>
                </Dropdown>
              </div>

              <Button
                :label="isGenerating ? 'Generating...' : 'Generate Report'"
                :icon="isGenerating ? 'pi pi-spin pi-spinner' : 'pi pi-play'"
                class="w-full mt-3 report-viewer__generate-btn"
                :loading="isGenerating"
                :disabled="!canGenerate"
                @click="executeReport"
              />
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="report-viewer__empty">
            <div class="report-viewer__empty-content">
              <div class="report-viewer__empty-icon">
                <i class="pi pi-file-o" />
              </div>
              <h4 class="report-viewer__empty-title">
                No Report Selected
              </h4>
              <p class="report-viewer__empty-text">
                Please select a report from the URL parameter or navigation to view its parameters and generate reports.
              </p>
              <div class="report-viewer__empty-help">
                <div class="report-viewer__help-content">
                  <i class="pi pi-lightbulb" />
                  <div>
                    <p class="report-viewer__help-title">
                      How to use:
                    </p>
                    <p class="report-viewer__help-text">
                      Add <code>?reportId=YOUR_REPORT_ID</code> to the URL to load a specific report.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel: Enhanced PDF Preview -->
      <div class="report-viewer__preview">
        <div class="report-viewer__preview-container">
          <!-- Enhanced Viewer Header -->
          <div class="report-viewer__preview-header">
            <div class="report-viewer__preview-header-content">
              <div class="report-viewer__preview-icon">
                <i class="pi pi-eye" />
              </div>
              <div>
                <h3 class="report-viewer__preview-title">
                  Report Preview
                </h3>
                <p class="report-viewer__preview-subtitle">
                  {{ pdfUrl ? 'Your generated report' : 'Generated reports will appear here' }}
                </p>
              </div>
            </div>
            <div v-if="pdfUrl" class="report-viewer__preview-actions">
              <Button
                v-tooltip.top="'Zoom In'"
                icon="pi pi-search-plus"
                class="p-button-text p-button-sm"
                @click="zoomIn"
              />
              <Button
                v-tooltip.top="'Zoom Out'"
                icon="pi pi-search-minus"
                class="p-button-text p-button-sm"
                @click="zoomOut"
              />
              <Divider layout="vertical" />
              <Button
                icon="pi pi-download"
                label="Download"
                class="p-button-outlined p-button-sm"
                @click="downloadCurrentReport"
              />
              <Button
                v-tooltip.top="'Open in New Tab'"
                icon="pi pi-external-link"
                class="p-button-text p-button-sm"
                @click="openInNewTab"
              />
            </div>
          </div>

          <!-- Enhanced Viewer Content -->
          <div class="report-viewer__preview-content">
            <!-- PDF Display with enhanced features -->
            <div v-if="item?.reportFormatType?.id === 'PDF' && pdfUrl" class="report-viewer__pdf-container">
              <div class="report-viewer__pdf-toolbar">
                <div class="report-viewer__pdf-info">
                  <Tag severity="success" icon="pi pi-check">
                    Report Generated
                  </Tag>
                  <span class="text-sm text-gray-600">
                    {{ dayjs().format('MMM DD, YYYY - HH:mm') }}
                  </span>
                </div>
              </div>

              <object
                :data="pdfUrl"
                type="application/pdf"
                class="report-viewer__pdf-object"
              >
                <div class="report-viewer__pdf-fallback">
                  <div class="report-viewer__pdf-fallback-content">
                    <i class="pi pi-file-pdf" />
                    <h4>PDF Viewer Not Supported</h4>
                    <p>Your browser doesn't support inline PDF viewing</p>
                    <Button
                      label="Download PDF"
                      icon="pi pi-download"
                      @click="downloadCurrentReport"
                    />
                  </div>
                </div>
              </object>
            </div>

            <!-- Enhanced Empty State for Preview -->
            <div v-else class="report-viewer__preview-empty">
              <div class="report-viewer__preview-empty-content">
                <div class="report-viewer__preview-empty-icon">
                  <i :class="getFormatIcon(item?.reportFormatType?.id || 'PDF')" />
                </div>
                <h4 class="report-viewer__preview-empty-title">
                  {{ item?.reportFormatType?.id === 'PDF' ? 'PDF Preview' : `${item?.reportFormatType?.name || 'File'} Download` }}
                </h4>
                <p class="report-viewer__preview-empty-text">
                  {{ item?.reportFormatType?.id === 'PDF'
                    ? 'PDF reports will be displayed here after generation'
                    : `${item?.reportFormatType?.name || 'Files'} will be automatically downloaded when generated`
                  }}
                </p>

                <!-- Preview features info -->
                <div v-if="item?.reportFormatType?.id === 'PDF'" class="report-viewer__preview-features">
                  <h5 class="report-viewer__preview-features-title">
                    Preview Features:
                  </h5>
                  <ul class="report-viewer__preview-features-list">
                    <li><i class="pi pi-eye" /> Inline viewing</li>
                    <li><i class="pi pi-search-plus" /> Zoom controls</li>
                    <li><i class="pi pi-download" /> Download option</li>
                    <li><i class="pi pi-external-link" /> Open in new tab</li>
                  </ul>
                </div>

                <div v-if="!item.jasperReportCode" class="report-viewer__preview-empty-info">
                  <i class="pi pi-info-circle" />
                  <span>Select a report and configure parameters to generate preview</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Enhanced Toast and Popup components -->
    <Toast position="top-center" :base-z-index="5000" group="tc" />
    <ConfirmPopup group="headless" />
  </div>
</template>

<style scoped>
@import '@/assets/styles/pages/report-viewer.scss';
</style>

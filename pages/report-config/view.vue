<script setup lang="ts">
import { computed, nextTick, onUnmounted, reactive, readonly, ref, watch, watchEffect } from 'vue'
import { z } from 'zod'
import dayjs from 'dayjs'
import { useRoute } from 'vue-router'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import ProgressBar from 'primevue/progressbar'
import Dropdown from 'primevue/dropdown'
import Tag from 'primevue/tag'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'
import Timeline from 'primevue/timeline'
import Steps from 'primevue/steps'
import Logger from '~/utils/Logger'
import { GenericService } from '~/services/generic-services'
import EnhancedFormComponent from '~/components/form/EnhancedFormComponent.vue'

// ✅ FIXED: Import createDynamicField as value, not type
import type {
  DynamicApiConfig,
  DynamicFieldArgs,
  FormField,
  ReportFormField,
} from '~/types/form'
import { createDynamicField } from '~/types/form'

// ========== PHASE 1: UPDATED TYPES & INTERFACES ==========

interface FieldValues {
  [key: string]: any
}

// ✅ NEW: Updated interfaces to match backend ApiResponse<T>
interface ApiResponse<T> {
  success: boolean
  message?: string
  data?: T
  error?: ErrorResponse
}

interface ErrorResponse {
  error: string
  details: string
  code?: string
  timestamp?: string
  validationErrors?: string[]
}

interface ReportSubmissionResponse {
  requestId: string
  clientRequestId?: string
  message: string
  status: string
  timestamp: number
}

interface ReportStatusResponse {
  requestId: string
  clientRequestId?: string
  jasperReportCode: string
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  errorMessage?: string
  createdAt: string
  updatedAt: string
  reportFormatType: string
  fileSizeBytes?: number
}

interface ReportDownloadResponse {
  base64Report: string
  fileSizeBytes: number
  fileName: string
  contentType: string
}

// ✅ NEW: Specific state types for better UX
type ReportWorkflowState =
  | { type: 'idle' }
  | { type: 'submitting', clientRequestId: string }
  | { type: 'polling', serverRequestId: string, clientRequestId: string, attempt: number, maxAttempts: number }
  | { type: 'completed', serverRequestId: string, report: ReportDownloadResponse }
  | { type: 'failed', error: string, canRetry: boolean, serverRequestId?: string }
  | { type: 'cancelled' }

interface ReportProgress {
  percentage: number
  message: string
  currentStep: number
  totalSteps: number
  elapsedTime: number
  estimatedTimeRemaining?: number
}

interface ReportConfig {
  readonly moduleApi: string
  readonly uriApiReportGenerate: string
  readonly uriApiReportStatus: string
  readonly uriApiReportDownload: string
  readonly uriApiReportCleanup: string
  readonly pollingInterval: number
  readonly maxPollingAttempts: number
  readonly timeout: number
}

interface ReportFormat {
  readonly id: 'PDF' | 'XLS' | 'CSV'
  readonly name: string
  readonly icon: string
  readonly description: string
}

// ========== BACKEND INTERFACES (unchanged) ==========
interface BackendReportParameter {
  readonly id: string
  readonly paramName: string
  readonly type: string
  readonly module: string
  readonly service: string
  readonly label: string
  readonly componentType: 'text' | 'select' | 'multiselect' | 'localselect' | 'date' | 'number'
  readonly jasperReportTemplate: any
  readonly parameterPosition: number
  readonly dependentField: string
  readonly filterKeyValue: string
  readonly dataValueStatic: string
  readonly parameterCategory: string
}

interface BackendReportInfo {
  readonly id: string
  readonly code: string
  readonly name: string
  readonly description?: string
  readonly parameters?: BackendReportParameter[]
}

// ========== FRONTEND INTERFACES (unchanged) ==========
interface ReportParameter {
  readonly id?: string
  readonly paramName: string
  readonly label: string
  readonly componentType: 'text' | 'select' | 'multiselect' | 'localselect' | 'date' | 'number'
  readonly module?: string
  readonly service?: string
  readonly dataValueStatic?: string
  readonly filtersBase?: any[]
  readonly dependentField?: string
  readonly filterKeyValue?: string
  readonly debounceTimeMs?: number
  readonly maxSelectedLabels?: number
  readonly required?: boolean
  readonly defaultValue?: any
  readonly parameterPosition?: number
  readonly parameterCategory?: string
}

interface ReportInfo {
  readonly id: string
  readonly code: string
  readonly name: string
  readonly description?: string
  readonly category?: string
  readonly parameters?: ReportParameter[]
}

interface FieldConfigurationMap {
  readonly multiselect: () => ReportFormField
  readonly select: () => ReportFormField
  readonly localselect: () => ReportFormField
  readonly date: () => ReportFormField
  readonly number: () => ReportFormField
  readonly text: () => ReportFormField
}

interface NormalizedOption {
  readonly name: string
  readonly label: string
  readonly value: any
  readonly id: string
  readonly description?: string
  readonly defaultValue?: boolean
}

interface MutableReportFormField extends Omit<ReportFormField, 'objApi' | 'kwArgs'> {
  objApi?: DynamicApiConfig
  kwArgs?: DynamicFieldArgs
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

// Report generator config
const reportConfig = reactive<ReportConfig>({
  moduleApi: 'report',
  uriApiReportGenerate: 'reports/generate-async',
  uriApiReportStatus: 'reports/status',
  uriApiReportDownload: 'reports/download',
  uriApiReportCleanup: 'reports/cleanup',
  pollingInterval: 3000, // 3 seconds
  maxPollingAttempts: 200, // 10 minutes total (200 * 3s)
  timeout: 60000 // 30 seconds for initial submission
})

// ========== PHASE 2: SPECIALIZED COMPOSABLES ==========

// ✅ NEW: Report Submission Composable
function useReportSubmission(config: ReportConfig) {
  const isSubmitting = ref(false)
  const submissionError = ref<string | null>(null)

  async function submitReport(payload: any): Promise<{ serverRequestId: string, clientRequestId: string } | null> {
    isSubmitting.value = true
    submissionError.value = null

    try {
      Logger.info('🚀 [SUBMISSION] Starting report submission:', payload)

      const response = await GenericService.create<ApiResponse<ReportSubmissionResponse>>(
        config.moduleApi,
        config.uriApiReportGenerate,
        payload,
        { timeout: config.timeout }
      )

      if (!response.success || !response.data) {
        throw new Error(response.error?.error || 'Failed to submit report')
      }

      const submissionData = response.data
      Logger.info('✅ [SUBMISSION] Report submitted successfully:', submissionData)

      return {
        serverRequestId: submissionData.requestId,
        clientRequestId: submissionData.clientRequestId || payload.requestId
      }
    }
    catch (error: any) {
      const errorMessage = error.message || 'Failed to submit report'
      submissionError.value = errorMessage
      Logger.error('❌ [SUBMISSION] Failed:', error)
      return null
    }
    finally {
      isSubmitting.value = false
    }
  }

  function resetSubmission() {
    isSubmitting.value = false
    submissionError.value = null
  }

  return {
    isSubmitting: readonly(isSubmitting),
    submissionError: readonly(submissionError),
    submitReport,
    resetSubmission
  }
}

// ✅ NEW: Report Polling Composable
function useReportPolling(config: ReportConfig) {
  const isPolling = ref(false)
  const pollingAttempt = ref(0)
  const pollingError = ref<string | null>(null)
  const currentStatus = ref<ReportStatusResponse | null>(null)

  let pollingInterval: NodeJS.Timeout | null = null
  let startTime: number = 0

  async function checkReportStatus(serverRequestId: string): Promise<ReportStatusResponse | null> {
    try {
      Logger.info(`🔍 [POLLING] Checking status for: ${serverRequestId} (attempt ${pollingAttempt.value + 1})`)

      const response = await GenericService.getById<ApiResponse<ReportStatusResponse>>(
        config.moduleApi,
        config.uriApiReportStatus,
        serverRequestId,
        undefined,
        undefined,
        { timeout: 10000 }
      )

      if (!response.success || !response.data) {
        throw new Error(response.error?.error || 'Failed to get report status')
      }

      currentStatus.value = response.data
      Logger.info(`📊 [POLLING] Status: ${response.data.status}`)

      return response.data
    }
    catch (error: any) {
      Logger.error('❌ [POLLING] Status check failed:', error)
      throw error
    }
  }

  function startPolling(serverRequestId: string, onStatusUpdate: (status: ReportStatusResponse) => void, onComplete: (status: ReportStatusResponse) => void, onError: (error: string) => void) {
    if (isPolling.value) {
      stopPolling()
    }

    isPolling.value = true
    pollingAttempt.value = 0
    pollingError.value = null
    startTime = Date.now()

    Logger.info(`🔄 [POLLING] Starting polling for: ${serverRequestId}`)

    pollingInterval = setInterval(async () => {
      pollingAttempt.value++

      try {
        const status = await checkReportStatus(serverRequestId)

        if (!status) {
          throw new Error('No status received')
        }

        onStatusUpdate(status)

        // Check completion states
        if (status.status === 'COMPLETED') {
          Logger.info('✅ [POLLING] Report completed successfully')
          stopPolling()
          onComplete(status)
          return
        }

        if (status.status === 'FAILED') {
          Logger.error('❌ [POLLING] Report failed:', status.errorMessage)
          stopPolling()
          onError(status.errorMessage || 'Report generation failed')
          return
        }

        // Check timeout
        if (pollingAttempt.value >= config.maxPollingAttempts) {
          Logger.warn('⏰ [POLLING] Max attempts reached')
          stopPolling()
          onError(`Report generation timeout after ${config.maxPollingAttempts} attempts`)
        }
      }
      catch (error: any) {
        const errorMessage = error.message || 'Polling error'
        Logger.error('❌ [POLLING] Error:', error)

        // Stop polling on persistent errors
        if (pollingAttempt.value >= 5) {
          stopPolling()
          onError(`Polling failed: ${errorMessage}`)
        }
      }
    }, config.pollingInterval)
  }

  function stopPolling() {
    if (pollingInterval) {
      clearInterval(pollingInterval)
      pollingInterval = null
    }
    isPolling.value = false
    Logger.info('🛑 [POLLING] Stopped')
  }

  function getElapsedTime(): number {
    return startTime > 0 ? Date.now() - startTime : 0
  }

  return {
    isPolling: readonly(isPolling),
    pollingAttempt: readonly(pollingAttempt),
    pollingError: readonly(pollingError),
    currentStatus: readonly(currentStatus),
    startPolling,
    stopPolling,
    checkReportStatus,
    getElapsedTime
  }
}

// ✅ NEW: Report Download Composable
function useReportDownload(config: ReportConfig) {
  async function downloadReport(serverRequestId: string): Promise<ReportDownloadResponse | null> {
    try {
      Logger.info(`📥 [DOWNLOAD] Starting download for: ${serverRequestId}`)

      const response = await GenericService.getById<ApiResponse<ReportDownloadResponse>>(
        config.moduleApi,
        config.uriApiReportDownload,
        serverRequestId
      )

      if (!response.success || !response.data) {
        throw new Error(response.error?.error || 'Failed to download report')
      }

      Logger.info('✅ [DOWNLOAD] Report downloaded successfully')
      return response.data
    }
    catch (error: any) {
      Logger.error('❌ [DOWNLOAD] Failed:', error)
      throw error
    }
  }

  async function cleanupReport(serverRequestId: string): Promise<boolean> {
    try {
      Logger.info(`🧹 [CLEANUP] Cleaning up: ${serverRequestId}`)

      const response = await GenericService.delete<ApiResponse<any>>(
        config.moduleApi,
        config.uriApiReportCleanup,
        serverRequestId
      )

      if (response.success) {
        Logger.info('✅ [CLEANUP] Report cleaned up successfully')
        return true
      }
      else {
        Logger.warn('⚠️ [CLEANUP] Cleanup failed:', response.error?.error)
        return false
      }
    }
    catch (error: any) {
      Logger.warn('⚠️ [CLEANUP] Error:', error)
      return false
    }
  }

  return {
    downloadReport,
    cleanupReport
  }
}

// ✅ NEW: Main Async Report Generation Orchestrator
function useAsyncReportGeneration(config: ReportConfig) {
  // State management
  const workflowState = ref<ReportWorkflowState>({ type: 'idle' })
  const progress = reactive<ReportProgress>({
    percentage: 0,
    message: 'Ready to generate report',
    currentStep: 0,
    totalSteps: 3,
    elapsedTime: 0
  })
  const pdfUrl = ref('')

  // Use specialized composables
  const { isSubmitting, submissionError, submitReport, resetSubmission } = useReportSubmission(config)
  const { isPolling, pollingAttempt, currentStatus, startPolling, stopPolling, getElapsedTime } = useReportPolling(config)
  const { downloadReport, cleanupReport } = useReportDownload(config)

  // Progress tracking
  let progressInterval: NodeJS.Timeout | null = null

  // ✅ NEW: Reactive progress calculation
  const isActive = computed(() =>
    workflowState.value.type === 'submitting'
    || workflowState.value.type === 'polling'
  )

  const canRetry = computed(() =>
    workflowState.value.type === 'failed' && workflowState.value.canRetry
  )

  const canCancel = computed(() =>
    workflowState.value.type === 'polling'
  )

  // ✅ NEW: Real-time progress updates
  watchEffect(() => {
    if (isActive.value) {
      if (!progressInterval) {
        startProgressTracking()
      }
    }
    else {
      stopProgressTracking()
    }
  })

  function startProgressTracking() {
    const startTime = Date.now()

    progressInterval = setInterval(() => {
      progress.elapsedTime = Date.now() - startTime

      if (workflowState.value.type === 'submitting') {
        progress.percentage = Math.min(15, progress.elapsedTime / 1000 * 5)
        progress.message = 'Submitting report request...'
        progress.currentStep = 1
      }
      else if (workflowState.value.type === 'polling') {
        const state = workflowState.value
        const progressFromAttempts = (state.attempt / state.maxAttempts) * 70
        progress.percentage = 15 + progressFromAttempts
        progress.message = `Processing report... (${state.attempt}/${state.maxAttempts})`
        progress.currentStep = 2

        // Estimate remaining time
        if (state.attempt > 5) {
          const avgTimePerAttempt = progress.elapsedTime / state.attempt
          progress.estimatedTimeRemaining = (state.maxAttempts - state.attempt) * avgTimePerAttempt
        }
      }
    }, 1000)
  }

  function stopProgressTracking() {
    if (progressInterval) {
      clearInterval(progressInterval)
      progressInterval = null
    }
  }

  // ✅ NEW: Main generation workflow
  async function generateReportAsync(payload: any): Promise<void> {
    try {
      // Reset state
      cleanup()
      workflowState.value = {
        type: 'submitting',
        clientRequestId: payload.requestId
      }
      progress.percentage = 0
      progress.currentStep = 1

      // Phase 1: Submit report
      const submissionResult = await submitReport(payload)

      if (!submissionResult) {
        workflowState.value = {
          type: 'failed',
          error: submissionError.value || 'Submission failed',
          canRetry: true
        }
        return
      }

      // Phase 2: Start polling
      workflowState.value = {
        type: 'polling',
        serverRequestId: submissionResult.serverRequestId,
        clientRequestId: submissionResult.clientRequestId,
        attempt: 0,
        maxAttempts: config.maxPollingAttempts
      }

      progress.currentStep = 2
      progress.message = 'Starting report processing...'

      // Start polling with callbacks
      startPolling(
        submissionResult.serverRequestId,
        // onStatusUpdate
        (status: ReportStatusResponse) => {
          if (workflowState.value.type === 'polling') {
            workflowState.value.attempt = pollingAttempt.value
          }
        },
        // onComplete
        async (status: ReportStatusResponse) => {
          await handleReportCompletion(submissionResult.serverRequestId)
        },
        // onError
        (error: string) => {
          workflowState.value = {
            type: 'failed',
            error,
            canRetry: true,
            serverRequestId: submissionResult.serverRequestId
          }
          progress.currentStep = 2
          progress.message = error
        }
      )
    }
    catch (error: any) {
      Logger.error('❌ [GENERATION] Unexpected error:', error)
      workflowState.value = {
        type: 'failed',
        error: error.message || 'Unexpected error occurred',
        canRetry: true
      }
    }
  }

  async function handleReportCompletion(serverRequestId: string) {
    try {
      progress.currentStep = 3
      progress.message = 'Downloading report...'
      progress.percentage = 90

      // Download report
      const reportData = await downloadReport(serverRequestId)

      if (!reportData) {
        throw new Error('Failed to download report data')
      }

      // Process file
      const fileUrl = processReportFile(reportData)

      if (fileUrl) {
        workflowState.value = {
          type: 'completed',
          serverRequestId,
          report: reportData
        }
        progress.percentage = 100
        progress.currentStep = 3
        progress.message = 'Report generated successfully!'

        // Auto cleanup after successful completion
        setTimeout(() => {
          cleanupReport(serverRequestId)
        }, 60000) // Cleanup after 1 minute
      }
    }
    catch (error: any) {
      Logger.error('❌ [COMPLETION] Error:', error)
      workflowState.value = {
        type: 'failed',
        error: error.message || 'Failed to process completed report',
        canRetry: false,
        serverRequestId
      }
    }
  }

  function processReportFile(reportData: ReportDownloadResponse): string | null {
    try {
      // Cleanup previous file
      if (pdfUrl.value) {
        URL.revokeObjectURL(pdfUrl.value)
        pdfUrl.value = ''
      }

      const byteCharacters = atob(reportData.base64Report)
      const byteArray = new Uint8Array(byteCharacters.length)

      // Process in chunks for better performance
      const chunkSize = 1024 * 1024
      for (let i = 0; i < byteCharacters.length; i += chunkSize) {
        const chunk = byteCharacters.slice(i, i + chunkSize)
        for (let j = 0; j < chunk.length; j++) {
          byteArray[i + j] = chunk.charCodeAt(j)
        }
      }

      const blob = new Blob([byteArray], { type: reportData.contentType })
      const blobUrl = URL.createObjectURL(blob)

      // Handle based on content type
      if (reportData.contentType === 'application/pdf') {
        pdfUrl.value = blobUrl
      }
      else {
        downloadFile(blobUrl, reportData.fileName)
      }

      return blobUrl
    }
    catch (error) {
      Logger.error('❌ [FILE PROCESSING] Error:', error)
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

  async function retryGeneration(payload: any): Promise<void> {
    if (canRetry.value) {
      await generateReportAsync(payload)
    }
  }

  function cancelGeneration(): void {
    if (canCancel.value) {
      stopPolling()
      workflowState.value = { type: 'cancelled' }
      progress.message = 'Generation cancelled by user'

      // Cleanup server resources if we have a server request ID
      if (workflowState.value.type === 'polling') {
        cleanupReport(workflowState.value.serverRequestId)
      }
    }
  }

  function cleanup() {
    stopPolling()
    stopProgressTracking()
    resetSubmission()

    if (pdfUrl.value) {
      URL.revokeObjectURL(pdfUrl.value)
      pdfUrl.value = ''
    }

    workflowState.value = { type: 'idle' }
    progress.percentage = 0
    progress.message = 'Ready to generate report'
    progress.currentStep = 0
    progress.elapsedTime = 0
    progress.estimatedTimeRemaining = undefined
  }

  function reset() {
    cleanup()
  }

  return {
    // State
    workflowState: readonly(workflowState),
    progress: readonly(progress),
    pdfUrl: readonly(pdfUrl),

    // Computed
    isActive,
    canRetry,
    canCancel,

    // Actions
    generateReportAsync,
    retryGeneration,
    cancelGeneration,
    cleanup,
    reset,

    // File operations
    downloadFile,

    // Cleanup
    cleanupReport
  }
}

// ========== UTILITY FUNCTIONS (unchanged) ==========
function normalizeOptions(options: any[]): NormalizedOption[] {
  if (!options || !Array.isArray(options) || options.length === 0) {
    return []
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'id' in opt)) {
    return options.map(opt => ({
      name: opt.name,
      label: opt.name,
      value: opt.id,
      id: opt.id,
      description: opt.description || opt.slug,
      defaultValue: opt.defaultValue === true
    }))
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'value' in opt)) {
    return options.map(opt => ({
      name: opt.name,
      label: opt.name,
      value: opt.value,
      id: opt.id || opt.value,
      description: opt.description,
      defaultValue: opt.defaultValue === true
    }))
  }

  if (options.every(opt => opt && typeof opt === 'object' && 'label' in opt && 'value' in opt)) {
    return options.map(opt => ({
      name: opt.label,
      label: opt.label,
      value: opt.value,
      id: opt.id || opt.value,
      description: opt.description,
      defaultValue: opt.defaultValue === true
    }))
  }

  if (options.every(opt => typeof opt === 'string')) {
    return options.map(opt => ({
      name: opt,
      label: opt,
      value: opt,
      id: opt,
      defaultValue: false
    }))
  }

  if (options.every(opt => opt && typeof opt === 'object')) {
    return options.map((opt, index) => {
      const displayName = opt.label || opt.name || opt.text || opt.value || `Option ${index + 1}`
      return {
        name: displayName,
        label: displayName,
        value: opt.value !== undefined ? opt.value : opt.id !== undefined ? opt.id : opt,
        id: (opt.id || opt.value || index).toString(),
        description: opt.description || opt.slug,
        defaultValue: opt.defaultValue === true
      }
    })
  }

  return options.map((opt, index) => ({
    name: String(opt),
    label: String(opt),
    value: opt,
    id: String(index),
    defaultValue: false
  }))
}

function mapBackendParameter(backendParam: BackendReportParameter): ReportParameter {
  const mappedParam: ReportParameter = {
    id: backendParam.id,
    paramName: backendParam.paramName,
    label: backendParam.label,
    componentType: backendParam.componentType,
    required: false,
    debounceTimeMs: 300,
    maxSelectedLabels: 2,
    parameterPosition: backendParam.parameterPosition || 0,
    parameterCategory: backendParam.parameterCategory || 'REPORT',
  }

  if (backendParam.module?.trim()) {
    Object.assign(mappedParam, { module: backendParam.module.trim() })
  }

  if (backendParam.service?.trim()) {
    Object.assign(mappedParam, { service: backendParam.service.trim() })
  }

  if (backendParam.dependentField?.trim()) {
    Object.assign(mappedParam, { dependentField: backendParam.dependentField.trim() })
  }

  if (backendParam.filterKeyValue?.trim()) {
    Object.assign(mappedParam, { filterKeyValue: backendParam.filterKeyValue.trim() })
  }

  if (backendParam.filterKeyValue?.trim()) {
    Object.assign(mappedParam, {
      filtersBase: [{
        key: backendParam.filterKeyValue.trim(),
        value: null
      }]
    })
  }
  else {
    Object.assign(mappedParam, { filtersBase: [] })
  }

  if (backendParam.componentType === 'localselect' && backendParam.dataValueStatic) {
    Object.assign(mappedParam, { dataValueStatic: backendParam.dataValueStatic })
  }

  return mappedParam
}

function createFieldFromParameter(param: ReportParameter): ReportFormField {
  const baseProps = {
    id: param.id,
    name: param.paramName,
    type: param.componentType,
    field: param.paramName,
    label: param.label,
    class: 'col-12 md:col-6',
    placeholder: `Select ${param.label}`,
    helpText: param.required ? 'This field is required' : `Optional - ${param.label}`,
    validation: param.required
      ? z.string().min(1, `${param.label} is required`)
      : z.string().optional(),
    required: param.required || false,
    showClear: true,
    filterable: true
  }

  if (param.required) {
    baseProps.class = `${baseProps.class} required`
  }

  const apiConfig: DynamicApiConfig | undefined = param.module && param.service
    ? {
        moduleApi: param.module,
        uriApi: param.service
      }
    : undefined

  const kwArgs: DynamicFieldArgs = {
    filtersBase: param.filtersBase || [],
    dependentField: param.dependentField,
    debounceTimeMs: param.debounceTimeMs || 300,
    maxSelectedLabels: param.maxSelectedLabels || 3,
    loadOnOpen: true,
    minQueryLength: 0,
    maxItems: 50
  }

  const fieldConfigs: FieldConfigurationMap = {
    multiselect: (): ReportFormField => {
      return createDynamicField(
        param.paramName,
        'multiselect',
        param.label,
        apiConfig,
        {
          ...baseProps,
          dataType: 'multiselect',
          multiple: true,
          maxSelectedLabels: param.maxSelectedLabels || 3,
          kwArgs,
          filtersBase: param.filtersBase || []
        }
      )
    },

    select: (): ReportFormField => {
      return createDynamicField(
        param.paramName,
        'select',
        param.label,
        apiConfig,
        {
          ...baseProps,
          dataType: 'select',
          multiple: false,
          kwArgs,
          filtersBase: param.filtersBase || []
        }
      )
    },

    localselect: (): ReportFormField => {
      let options: NormalizedOption[] = []
      let defaultValue: any = null

      try {
        if (param.dataValueStatic) {
          const rawData = JSON.parse(param.dataValueStatic.replace(/\n/g, ''))
          options = normalizeOptions(rawData)

          const defaultOption = options.find(opt => opt.defaultValue === true)
          if (defaultOption) {
            defaultValue = defaultOption.value
          }
        }
      }
      catch (e) {
        Logger.error('Invalid JSON in dataValueStatic for param', param.paramName, e)
        options = []
      }

      return createDynamicField(
        param.paramName,
        'localselect',
        param.label,
        undefined,
        {
          ...baseProps,
          type: 'localselect',
          dataType: 'localselect',
          options: options.map(opt => ({ ...opt, label: opt.name })),
          defaultValue,
          filterable: options.length > 5,
          config: {
            showDebugInfo: false
          }
        }
      )
    },

    date: (): ReportFormField => {
      return createDynamicField(
        param.paramName,
        'date',
        param.label,
        undefined,
        {
          ...baseProps,
          dataType: 'date',
          defaultValue: null,
        }
      )
    },

    number: (): ReportFormField => {
      return createDynamicField(
        param.paramName,
        'number',
        param.label,
        undefined,
        {
          ...baseProps,
          dataType: 'number'
        }
      )
    },

    text: (): ReportFormField => {
      return createDynamicField(
        param.paramName,
        'text',
        param.label,
        undefined,
        {
          ...baseProps,
          dataType: 'text'
        }
      )
    }
  }

  const generatedField = fieldConfigs[param.componentType]?.() || fieldConfigs.text()

  return generatedField
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

  const isValidDate = (value: any): boolean => {
    if (!value) { return false }

    const dateValue = new Date(value)
    const isValid = !Number.isNaN(dateValue.getTime())

    return isValid && dayjs(value).isValid()
  }

  function processParameterValue(value: any): any {
    if (value === null || value === undefined || value === '') {
      return null
    }

    if (isValidDate(value) && typeof value !== 'number') {
      const processedDate = dayjs(value).format('YYYY-MM-DD')
      return processedDate
    }
    else if (Array.isArray(value) && value.every(v => v && typeof v === 'object' && 'id' in v)) {
      const processedArray = value.map(v => v.id)
      return processedArray
    }
    else if (value && typeof value === 'object' && 'id' in value) {
      return value.id
    }

    return value
  }

  function buildPayload(formData: any, reportCode: string, formatType: any) {
    // ✅ NEW: Generate UUID for client request ID
    const clientRequestId = crypto.randomUUID ? crypto.randomUUID() : `client-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`

    const payload = {
      parameters: {} as Record<string, any>,
      reportFormatType: typeof formatType === 'object' ? formatType.id : formatType,
      jasperReportCode: reportCode,
      requestId: clientRequestId, // Client-generated UUID
      metadata: {
        timestamp: dayjs().toISOString(),
        userAgent: navigator.userAgent
      }
    }

    const excludeFields = ['reportFormatType', 'jasperReportCode', 'event']

    payload.parameters = Object.keys(formData)
      .filter((key) => {
        if (excludeFields.includes(key)) {
          return false
        }

        const paramDef = currentReport.value?.parameters?.find(p => p.paramName === key)
        return paramDef?.parameterCategory === 'REPORT'
      })
      .reduce((acc, key) => {
        const rawValue = formData[key]
        const isDateField = key.toLowerCase().includes('date')

        if (isDateField) {
          if (rawValue && rawValue !== '' && rawValue !== null) {
            const processedValue = processParameterValue(rawValue)
            if (processedValue && processedValue !== null) {
              acc[key] = processedValue
            }
          }
        }
        else {
          const processedValue = processParameterValue(rawValue)
          if (processedValue !== null && processedValue !== undefined && processedValue !== '') {
            acc[key] = processedValue
          }
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
  return {
    createFieldFromParameter,
    normalizeOptions,
    mapBackendParameter
  }
}

// ========== INITIALIZE COMPOSABLES ==========
const asyncReportGeneration = useAsyncReportGeneration(reportConfig)
const { buildPayload, validateAllParameters } = useReportParameters()
const { createFieldFromParameter: createField, mapBackendParameter: mapParameter } = useFieldBuilder()

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
  return !asyncReportGeneration.isActive.value
    && item.value.jasperReportCode
    && item.value.reportFormatType
})

// ✅ NEW: Enhanced progress display
const progressPercentage = computed(() => {
  return Math.round(asyncReportGeneration.progress.percentage || 0)
})

const formattedElapsedTime = computed(() => {
  const seconds = Math.floor(asyncReportGeneration.progress.elapsedTime / 1000)
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
})

const formattedEstimatedTime = computed(() => {
  if (!asyncReportGeneration.progress.estimatedTimeRemaining) { return null }

  const seconds = Math.floor(asyncReportGeneration.progress.estimatedTimeRemaining / 1000)
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `~${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
})

// ✅ NEW: Timeline data for better UX
const timelineEvents = computed(() => {
  const events = []
  const state = asyncReportGeneration.workflowState.value

  // Submit step
  events.push({
    status: state.type === 'idle' ? 'outlined' : 'filled',
    date: state.type !== 'idle' ? 'Submitted' : 'Pending',
    icon: 'pi pi-send',
    color: state.type === 'idle' ? '#9C27B0' : '#673AB7'
  })

  // Processing step
  if (state.type === 'polling' || state.type === 'completed' || (state.type === 'failed' && state.serverRequestId)) {
    events.push({
      status: state.type === 'polling' ? 'filled' : state.type === 'completed' ? 'filled' : 'outlined',
      date: state.type === 'polling' ? 'Processing...' : state.type === 'completed' ? 'Processed' : 'Failed',
      icon: 'pi pi-cog',
      color: state.type === 'polling' ? '#FF9800' : state.type === 'completed' ? '#4CAF50' : '#F44336'
    })
  }

  // Complete step
  if (state.type === 'completed') {
    events.push({
      status: 'filled',
      date: 'Completed',
      icon: 'pi pi-check',
      color: '#4CAF50'
    })
  }

  return events
})

// ========== METHODS ==========
async function loadReport(reportId: string) {
  if (!reportId) {
    return
  }

  try {
    loadingReport.value = true

    const response = await GenericService.getById<BackendReportInfo>(
      'report',
      'jasper-report-template/template-with-params/',
      reportId
    )

    const mappedReport: ReportInfo = {
      id: response.id,
      code: response.code,
      name: response.name,
      description: response.description,
      parameters: response.parameters
        ? response.parameters
            .map(mapParameter)
            .sort((a, b) => (a.parameterPosition || 0) - (b.parameterPosition || 0))
        : []
    }

    currentReport.value = mappedReport
    await loadReportParameters(mappedReport.id, mappedReport.code, mappedReport)
  }
  catch (error) {
    Logger.error('🧪 [ERROR] loadReport failed:', error)
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
  if (!id) {
    return
  }

  try {
    showForm.value = false
    fields.value = []

    if (reportData?.parameters && reportData.parameters.length > 0) {
      reportData.parameters.forEach((param: ReportParameter) => {
        const fieldDef = createField(param) as MutableReportFormField

        let initialValue: any
        switch (param.componentType) {
          case 'multiselect':
            initialValue = []
            break
          case 'date':
            initialValue = null
            break
          default:
            initialValue = ''
        }

        if (param.componentType === 'localselect' && fieldDef.defaultValue !== undefined && fieldDef.defaultValue !== null) {
          initialValue = fieldDef.defaultValue
        }
        else if (fieldDef.defaultValue !== undefined && fieldDef.defaultValue !== null) {
          initialValue = fieldDef.defaultValue
        }

        item.value[param.paramName] = initialValue

        if (['select', 'multiselect'].includes(param.componentType) && param.module && param.service) {
          fieldDef.objApi = {
            moduleApi: param.module,
            uriApi: param.service
          }
          fieldDef.kwArgs = {
            ...param,
            filtersBase: param.filtersBase || [],
            dependentField: param.dependentField,
            filterKeyValue: param.filterKeyValue,
            debounceTimeMs: param.debounceTimeMs || 300,
            getParentValues: () => item.value
          }
        }

        fields.value.push(fieldDef as ReportFormField)
      })
    }

    item.value.jasperReportCode = code || ''
    currentReport.value = reportData || null
    formReload.value++

    await nextTick()
    showForm.value = true
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

// ✅ NEW: Enhanced report execution with new async workflow
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

    Logger.info('🚀 [EXECUTE] Starting async report generation:', payload)

    // Use the new async generation workflow
    await asyncReportGeneration.generateReportAsync(payload)

    // Show success notification when completed
    if (asyncReportGeneration.workflowState.value.type === 'completed') {
      toast.add({
        severity: 'success',
        summary: 'Report Generated',
        detail: `Your ${item.value.reportFormatType.name} report is ready`,
        life: 5000
      })
    }
  }
  catch (error) {
    Logger.error('❌ [EXECUTE] Error:', error)
    toast.add({
      severity: 'error',
      summary: 'Generation Failed',
      detail: 'Failed to generate report. Please try again.',
      life: 5000
    })
  }
}

// ✅ NEW: Enhanced retry functionality
async function retryGeneration() {
  if (asyncReportGeneration.canRetry.value) {
    const payload = buildPayload(
      item.value,
      item.value.jasperReportCode,
      item.value.reportFormatType
    )
    await asyncReportGeneration.retryGeneration(payload)
  }
}

// ✅ NEW: Cancel functionality
function cancelGeneration() {
  if (asyncReportGeneration.canCancel.value) {
    asyncReportGeneration.cancelGeneration()
    toast.add({
      severity: 'info',
      summary: 'Generation Cancelled',
      detail: 'Report generation has been cancelled',
      life: 3000
    })
  }
}

function clearForm() {
  const reportCode = item.value.jasperReportCode
  const reportFormat = item.value.reportFormatType

  item.value = {
    jasperReportCode: reportCode,
    reportFormatType: reportFormat
  }

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
  const name = event.name || event.fieldName || event.field
  const value = event.value

  const oldValue = item.value[name]
  item.value[name] = value

  if (formValidationErrors.value[name]) {
    delete formValidationErrors.value[name]
  }

  if (JSON.stringify(oldValue) !== JSON.stringify(value)) {
    handleDependencyCascade(name, value, oldValue)
  }
}

function handleDependencyCascade(changedFieldName: string, newValue: any, oldValue: any) {
  if (JSON.stringify(newValue) === JSON.stringify(oldValue)) {
    return
  }

  const dependentFields = fields.value.filter((field) => {
    const dependentField = field.kwArgs?.dependentField
    return dependentField === changedFieldName
  })

  if (dependentFields.length > 0) {
    const updates: Record<string, any> = {}

    dependentFields.forEach((depField) => {
      const resetValue = depField.multiple ? [] : null
      updates[depField.name] = resetValue
    })

    Object.assign(item.value, updates)

    dependentFields.forEach((depField) => {
      if (depField.multiple) {
        item.value[depField.name] = []
      }
      else {
        item.value[depField.name] = null
      }

      nextTick(() => {
        // formReload.value++
      })
    })
  }
}

function handleReactiveUpdate(values: FieldValues) {
  const CRITICAL_FIELDS = ['jasperReportCode', 'reportFormatType']

  const preservedValues = CRITICAL_FIELDS.reduce((acc, field) => {
    if (item.value[field] !== undefined && item.value[field] !== null) {
      acc[field] = item.value[field]
    }
    return acc
  }, {} as Record<string, any>)

  item.value = {
    ...values,
    ...preservedValues
  }
}

function handleCancel() {
  clearForm()
}

function handleValidationErrors(event: any) {
  try {
    if (event && typeof event === 'object' && 'errors' in event) {
      const errorRecord: Record<string, string[]> = {}

      if (Array.isArray(event.errors)) {
        event.errors.forEach((error: any) => {
          try {
            if (error && typeof error === 'object' && error.path) {
              if (!errorRecord[error.path]) {
                errorRecord[error.path] = []
              }

              const message = error.message || error.msg || 'Validation error'
              errorRecord[error.path].push(String(message))
            }
          }
          catch (errorProcessingError) {
            Logger.error('🔴 [VALIDATION ERRORS] Error processing individual error:', errorProcessingError)
          }
        })
      }

      formValidationErrors.value = errorRecord
    }
    else if (event && typeof event === 'object') {
      const errorRecord: Record<string, string[]> = {}

      Object.keys(event).forEach((key) => {
        try {
          const value = event[key]
          if (Array.isArray(value)) {
            errorRecord[key] = value.map(v => String(v))
          }
          else if (value) {
            errorRecord[key] = [String(value)]
          }
        }
        catch (keyProcessingError) {
          Logger.error('🔴 [VALIDATION ERRORS] Error processing key:', key, keyProcessingError)
        }
      })

      formValidationErrors.value = errorRecord
    }
    else {
      formValidationErrors.value = {}
    }
  }
  catch (handlerError) {
    Logger.error('🔴 [VALIDATION ERRORS] Critical error in validation handler:', handlerError)
    formValidationErrors.value = {}
  }
}

// ========== UTILITY FUNCTIONS ==========
function getFormatIcon(formatId: string): string {
  const format = REPORT_FORMATS.find(f => f.id === formatId)
  return format?.icon || 'pi pi-file'
}

function downloadCurrentReport() {
  if (asyncReportGeneration.pdfUrl.value) {
    const timestamp = dayjs().format('YYYY-MM-DD_HH-mm-ss')
    const link = document.createElement('a')
    link.href = asyncReportGeneration.pdfUrl.value
    link.download = `report-${timestamp}.pdf`
    link.click()
  }
}

function openInNewTab() {
  if (asyncReportGeneration.pdfUrl.value) {
    window.open(asyncReportGeneration.pdfUrl.value, '_blank', 'noopener,noreferrer')
  }
}

function shareReport() {
  if (typeof navigator !== 'undefined' && (navigator as any).share && asyncReportGeneration.pdfUrl.value) {
    (navigator as any).share({
      title: `Report: ${currentReport.value?.name}`,
      text: 'Generated report',
      url: asyncReportGeneration.pdfUrl.value
    }).catch((err: any) => Logger.error('Error sharing:', err))
  }
}

// ========== WATCHERS ==========
watch(() => route.query.reportId, (newReportId) => {
  if (newReportId && typeof newReportId === 'string') {
    loadReport(newReportId)
  }
}, { immediate: true })

// ========== LIFECYCLE ==========
onUnmounted(() => {
  asyncReportGeneration.cleanup()
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
                :disabled="asyncReportGeneration.isActive.value"
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
              v-if="showForm"
              :key="formReload"
              :fields="fields as FormField[]"
              :initial-values="item"
              :show-actions="false"
              :loading="asyncReportGeneration.isActive.value"
              :validate-on-change="true"
              data-test-id="report-form"
              @field-change="handleFieldUpdate"
              @update:values="handleReactiveUpdate"
              @validation-change="handleValidationErrors"
              @cancel="handleCancel"
            >
              <template
                v-for="fieldItem in fields.filter((f: FormField) => f.required)"
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

              <!-- ✅ NEW: Enhanced Generation Controls with Steps -->
              <div class="report-viewer__generation-controls">
                <!-- Main Generation Button -->
                <Button
                  :label="asyncReportGeneration.isActive.value ? 'Generating...' : 'Generate Report'"
                  :icon="asyncReportGeneration.isActive.value ? 'pi pi-spin pi-spinner' : 'pi pi-play'"
                  class="report-viewer__generate-button report-viewer__generate-btn"
                  :loading="asyncReportGeneration.isActive.value"
                  :disabled="!canGenerate"
                  @click="executeReport"
                />

                <!-- Generation Info -->
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

                  <!-- ✅ NEW: Real-time progress info -->
                  <div v-if="asyncReportGeneration.isActive.value" class="mt-2">
                    <div class="flex align-items-center gap-2 text-xs text-gray-500">
                      <i class="pi pi-clock" />
                      <span>{{ asyncReportGeneration.progress.message }}</span>
                    </div>
                    <div v-if="asyncReportGeneration.workflowState.value.type === 'polling'" class="flex align-items-center gap-2 text-xs text-gray-500 mt-1">
                      <i class="pi pi-refresh" />
                      <span>
                        Attempt {{ asyncReportGeneration.workflowState.value.attempt }} of {{ asyncReportGeneration.workflowState.value.maxAttempts }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Quick Actions -->
              <div v-if="asyncReportGeneration.pdfUrl.value" class="report-viewer__quick-actions">
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

              <!-- ✅ NEW: Enhanced generation button for no-parameters -->
              <div class="mt-4">
                <Button
                  :label="asyncReportGeneration.isActive.value ? 'Generating...' : 'Generate Report'"
                  :icon="asyncReportGeneration.isActive.value ? 'pi pi-spin pi-spinner' : 'pi pi-play'"
                  class="w-full report-viewer__generate-btn"
                  :loading="asyncReportGeneration.isActive.value"
                  :disabled="!canGenerate"
                  @click="executeReport"
                />

                <!-- Progress info for no-parameters -->
                <div v-if="asyncReportGeneration.isActive.value" class="mt-2 text-center">
                  <div class="text-xs text-gray-500">
                    {{ asyncReportGeneration.progress.message }}
                  </div>
                  <div v-if="formattedElapsedTime" class="text-xs text-gray-400 mt-1">
                    Elapsed: {{ formattedElapsedTime }}
                  </div>
                </div>
              </div>
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
              <div class="flex flex-column">
                <div class="flex align-items-center gap-2">
                  <h3 class="report-viewer__preview-title">
                    Report Preview
                  </h3>
                </div>
                <p class="report-viewer__preview-subtitle">
                  {{ asyncReportGeneration.pdfUrl.value ? 'Your generated report' : 'Generated reports will appear here' }}
                </p>
              </div>
              <!-- ✅ NEW: Enhanced status indicator -->
              <div v-if="asyncReportGeneration.workflowState.value.type === 'completed'" class="report-viewer__pdf-info" style="margin-left: auto; text-align: right;">
                <Tag severity="success" icon="pi pi-check">
                  Report Generated
                </Tag>
                <span class="text-sm text-gray-600">
                  {{ dayjs().format('MMM DD, YYYY - HH:mm') }}
                </span>
              </div>
              <div v-else-if="asyncReportGeneration.isActive.value" class="report-viewer__pdf-info" style="margin-left: auto; text-align: right;">
                <Tag severity="info" icon="pi pi-spin pi-spinner">
                  Processing
                </Tag>
                <span class="text-sm text-gray-600">
                  {{ formattedElapsedTime }}
                </span>
              </div>
            </div>
          </div>

          <!-- Enhanced Viewer Content -->
          <div class="report-viewer__preview-content">
            <!-- PDF Display with enhanced features -->
            <div v-if="item?.reportFormatType?.id === 'PDF' && asyncReportGeneration.pdfUrl.value" class="report-viewer__pdf-container">
              <object
                :data="asyncReportGeneration.pdfUrl.value"
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

            <!-- ✅ NEW: Loading state in preview -->
            <div v-else-if="asyncReportGeneration.isActive.value && item?.reportFormatType?.id === 'PDF'" class="report-viewer__preview-loading">
              <div class="report-viewer__preview-loading-content">
                <ProgressSpinner style="width: 48px; height: 48px" />
                <h4 class="report-viewer__preview-loading-title">
                  Generating PDF Preview
                </h4>
                <p class="report-viewer__preview-loading-text">
                  {{ asyncReportGeneration.progress.message }}
                </p>
                <div class="report-viewer__preview-progress">
                  <ProgressBar
                    :value="progressPercentage"
                    class="w-full"
                    :show-value="true"
                  />
                </div>
                <div class="flex gap-4 text-sm text-gray-600 mt-2">
                  <span>Elapsed: {{ formattedElapsedTime }}</span>
                  <span v-if="formattedEstimatedTime">ETA: {{ formattedEstimatedTime }}</span>
                </div>
              </div>
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

                <!-- ✅ NEW: Enhanced empty state info -->
                <div v-if="!item.jasperReportCode" class="report-viewer__preview-empty-info">
                  <i class="pi pi-info-circle" />
                  <span>Select a report and configure parameters to generate preview</span>
                </div>
                <div v-else-if="asyncReportGeneration.workflowState.value.type === 'failed'" class="report-viewer__preview-empty-info">
                  <i class="pi pi-exclamation-triangle text-red-500" />
                  <span>Report generation failed. Please check the status panel and try again.</span>
                </div>
                <div v-else-if="asyncReportGeneration.workflowState.value.type === 'cancelled'" class="report-viewer__preview-empty-info">
                  <i class="pi pi-ban text-orange-500" />
                  <span>Report generation was cancelled.</span>
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
@import '@/assets/styles/pages/report-viewer-layout.scss';
@import '@/assets/styles/pages/report-viewer-components.scss';
</style>

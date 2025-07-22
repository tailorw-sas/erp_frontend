<script setup lang="ts">
import { computed, ref } from 'vue'
import Button from 'primevue/button'
import ProgressBar from 'primevue/progressbar'
import dayjs from 'dayjs'
import type { ErrorBoundaryError } from '../composables/useErrorBoundary'

interface Props {
  error: ErrorBoundaryError | null
  severity?: 'error' | 'warn' | 'info'
  showClose?: boolean
  showDetails?: boolean
  showActions?: boolean
  showReportButton?: boolean
  showStack?: boolean
  isRetrying?: boolean
}

interface Emits {
  (e: 'retry'): void
  (e: 'dismiss'): void
  (e: 'report'): void
}

const props = withDefaults(defineProps<Props>(), {
  severity: 'error',
  showClose: true,
  showDetails: true,
  showActions: true,
  showReportButton: false,
  showStack: false,
  isRetrying: false
})

defineEmits<Emits>()

const showExpandedDetails = ref(false)

function getErrorIcon(severity: string): string {
  const icons = {
    error: 'pi pi-exclamation-triangle',
    warn: 'pi pi-exclamation-circle',
    info: 'pi pi-info-circle'
  }
  return icons[severity as keyof typeof icons] || icons.error
}

function getErrorTitle(): string {
  if (!props.error) { return '' }

  const titles = {
    generation: 'Report Generation Error',
    submission: 'Report Submission Error',
    download: 'Download Error',
    form: 'Form Validation Error',
    default: 'Operation Error'
  }

  return titles[props.error.context as keyof typeof titles] || titles.default
}

function getErrorMessage(): string {
  if (!props.error) { return '' }

  // Use friendly message logic from composable
  const message = props.error.error.message

  if (message.includes('Failed to fetch')) {
    return 'Network connection problem. Please check your internet connection and try again.'
  }

  if (message.includes('timeout')) {
    return 'The operation took too long to complete. This might be due to server load.'
  }

  if (message.includes('rate limit')) {
    return 'Too many requests. Please wait a moment before trying again.'
  }

  if (message.includes('unauthorized') || message.includes('permission')) {
    return 'You don\'t have permission to perform this action. Please contact support.'
  }

  if (message.includes('validation')) {
    return 'Please check your input and try again.'
  }

  return message || 'An unexpected error occurred'
}

function formatTimestamp(timestamp: string): string {
  return dayjs(timestamp).format('MMM DD, YYYY - HH:mm:ss')
}
</script>

<template>
  <Transition
    name="error-alert"
    appear
  >
    <div
      v-if="error"
      class="report-viewer__error-alert"
      :class="`report-viewer__error-alert--${severity}`"
      role="alert"
      :aria-label="`${severity} alert`"
    >
      <!-- Error Icon & Header -->
      <div class="report-viewer__error-alert-header">
        <div class="report-viewer__error-alert-icon">
          <i
            :class="getErrorIcon(severity)"
            :aria-hidden="true"
          />
        </div>

        <div class="report-viewer__error-alert-content">
          <h4 class="report-viewer__error-alert-title">
            {{ getErrorTitle() }}
          </h4>
          <p class="report-viewer__error-alert-message">
            {{ getErrorMessage() }}
          </p>
        </div>

        <button
          v-if="showClose"
          class="report-viewer__error-alert-close"
          type="button"
          aria-label="Close error alert"
          @click="$emit('dismiss')"
        >
          <i class="pi pi-times" />
        </button>
      </div>

      <!-- Error Details (Expandable) -->
      <div
        v-if="showDetails"
        class="report-viewer__error-alert-details"
      >
        <button
          class="report-viewer__error-alert-toggle"
          type="button"
          :aria-expanded="showExpandedDetails"
          @click="showExpandedDetails = !showExpandedDetails"
        >
          <i
            class="pi"
            :class="showExpandedDetails ? 'pi-chevron-up' : 'pi-chevron-down'"
          />
          {{ showExpandedDetails ? 'Hide Details' : 'Show Details' }}
        </button>

        <Transition name="error-details">
          <div
            v-if="showExpandedDetails"
            class="report-viewer__error-alert-expanded"
          >
            <div class="report-viewer__error-alert-technical">
              <div class="report-viewer__error-detail-item">
                <span class="report-viewer__error-detail-label">Error ID:</span>
                <code class="report-viewer__error-detail-value">{{ error.id }}</code>
              </div>

              <div class="report-viewer__error-detail-item">
                <span class="report-viewer__error-detail-label">Context:</span>
                <code class="report-viewer__error-detail-value">{{ error.context }}</code>
              </div>

              <div class="report-viewer__error-detail-item">
                <span class="report-viewer__error-detail-label">Timestamp:</span>
                <code class="report-viewer__error-detail-value">{{ formatTimestamp(error.timestamp) }}</code>
              </div>

              <div
                v-if="error.retryCount > 0"
                class="report-viewer__error-detail-item"
              >
                <span class="report-viewer__error-detail-label">Retry Attempts:</span>
                <code class="report-viewer__error-detail-value">{{ error.retryCount }} / {{ error.maxRetries }}</code>
              </div>

              <div
                v-if="error.error.stack && showStack"
                class="report-viewer__error-detail-item report-viewer__error-detail-item--stack"
              >
                <span class="report-viewer__error-detail-label">Stack Trace:</span>
                <pre class="report-viewer__error-detail-stack">{{ error.error.stack }}</pre>
              </div>
            </div>
          </div>
        </Transition>
      </div>

      <!-- Action Buttons -->
      <div
        v-if="showActions"
        class="report-viewer__error-alert-actions"
      >
        <Button
          v-if="error.canRetry && error.retryCount < error.maxRetries"
          :label="isRetrying ? 'Retrying...' : `Retry (${error.maxRetries - error.retryCount} left)`"
          :icon="isRetrying ? 'pi pi-spin pi-spinner' : 'pi pi-refresh'"
          class="p-button-sm report-viewer__error-alert-retry"
          :loading="isRetrying"
          :disabled="isRetrying"
          @click="$emit('retry')"
        />

        <Button
          v-if="showReportButton"
          label="Report Issue"
          icon="pi pi-flag"
          class="p-button-outlined p-button-sm"
          @click="$emit('report')"
        />

        <Button
          label="Dismiss"
          icon="pi pi-times"
          class="p-button-text p-button-sm"
          @click="$emit('dismiss')"
        />
      </div>

      <!-- Progress Bar for Retry -->
      <div
        v-if="isRetrying"
        class="report-viewer__error-alert-progress"
      >
        <ProgressBar
          mode="indeterminate"
          class="report-viewer__error-alert-progress-bar"
        />
      </div>
    </div>
  </Transition>
</template>

<style scoped>
/* Error Alert Animations */
.error-alert-enter-active,
.error-alert-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.error-alert-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

.error-alert-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.98);
}

.error-details-enter-active,
.error-details-leave-active {
  transition: all 0.3s ease;
}

.error-details-enter-from,
.error-details-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  max-height: 0;
}

.error-details-enter-to {
  opacity: 1;
  transform: translateY(0);
  max-height: 500px;
}

/* Error Alert Styles - Integrates with existing SCSS */
.report-viewer__error-alert {
  margin: 1rem;
  border-radius: 12px;
  background: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #e2e8f0;
  overflow: hidden;
  animation: slideInUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: var(--error-color);
  }

  &--error::before {
    background: linear-gradient(90deg, #ef4444, #dc2626);
  }

  &--warn::before {
    background: linear-gradient(90deg, #f59e0b, #d97706);
  }

  &--info::before {
    background: linear-gradient(90deg, #3b82f6, #2563eb);
  }

  &-header {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
    padding: 1.5rem 1.5rem 1rem;
  }

  &-icon {
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    color: white;
    font-size: 1.1rem;
  }

  &--error &-icon {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
  }

  &--warn &-icon {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  }

  &--info &-icon {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  }

  &-content {
    flex: 1;
    min-width: 0;
  }

  &-title {
    margin: 0 0 0.5rem 0;
    font-size: 1.1rem;
    font-weight: 600;
    color: #1f2937;
    line-height: 1.3;
  }

  &-message {
    margin: 0;
    color: #6b7280;
    line-height: 1.5;
    font-size: 0.95rem;
  }

  &-close {
    background: none;
    border: none;
    color: #9ca3af;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 6px;
    transition: all 0.2s ease;
    flex-shrink: 0;

    &:hover {
      background: #f3f4f6;
      color: #6b7280;
    }
  }

  &-details {
    border-top: 1px solid #f3f4f6;
    background: #f9fafb;
  }

  &-toggle {
    width: 100%;
    background: none;
    border: none;
    padding: 0.75rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    color: #6b7280;
    font-size: 0.875rem;
    font-weight: 500;
    transition: all 0.2s ease;

    &:hover {
      background: #f3f4f6;
      color: #374151;
    }

    .pi {
      font-size: 0.75rem;
      transition: transform 0.2s ease;
    }
  }

  &-expanded {
    padding: 0 1.5rem 1rem;
    overflow: hidden;
  }

  &-technical {
    background: white;
    border-radius: 8px;
    padding: 1rem;
    border: 1px solid #e5e7eb;
  }

  &-detail-item {
    display: flex;
    gap: 0.75rem;
    margin-bottom: 0.75rem;
    align-items: flex-start;

    &:last-child {
      margin-bottom: 0;
    }

    &--stack {
      flex-direction: column;
      gap: 0.5rem;
      align-items: stretch;
    }
  }

  &-detail-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
    min-width: 100px;
    flex-shrink: 0;
  }

  &-detail-value {
    background: #f3f4f6;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 0.875rem;
    color: #1f2937;
    border: 1px solid #e5e7eb;
    word-break: break-all;
  }

  &-detail-stack {
    background: #1f2937;
    color: #f9fafb;
    padding: 1rem;
    border-radius: 6px;
    font-family: 'Monaco', 'Menlo', monospace;
    font-size: 0.75rem;
    line-height: 1.4;
    overflow-x: auto;
    max-height: 200px;
    overflow-y: auto;
    border: 1px solid #374151;
  }

  &-actions {
    padding: 1rem 1.5rem;
    background: #f9fafb;
    border-top: 1px solid #f3f4f6;
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;
    align-items: center;
  }

  &-retry {
    background: linear-gradient(135deg, #3b82f6, #2563eb) !important;
    border: none !important;
    color: white !important;

    &:hover:not(:disabled) {
      background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
    }
  }

  &-progress {
    padding: 0 1.5rem 1rem;
    background: #f9fafb;
  }

  &-progress-bar {
    :deep(.p-progressbar) {
      height: 4px !important;
      border-radius: 2px;
      background: #e5e7eb;

      .p-progressbar-value {
        background: linear-gradient(90deg, #3b82f6, #2563eb) !important;
        border-radius: 2px;
      }
    }
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

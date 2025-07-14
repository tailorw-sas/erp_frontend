<script setup lang="ts">
import { computed } from 'vue'
import Button from 'primevue/button'
import Tag from 'primevue/tag'

interface Props {
  state: any
  pdfUrl?: string
  format: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  retry: []
  reset: []
}>()

const isCompleted = computed(() => props.state.type === 'completed')
const isFailed = computed(() => props.state.type === 'failed')
const isCancelled = computed(() => props.state.type === 'cancelled')

const showPdfViewer = computed(() =>
  isCompleted.value && props.format?.id === 'PDF' && props.pdfUrl
)

const showSuccessState = computed(() =>
  isCompleted.value && props.format?.id !== 'PDF'
)

function downloadPdf() {
  if (props.pdfUrl) {
    const link = document.createElement('a')
    link.href = props.pdfUrl
    link.download = `report-${new Date().toISOString().split('T')[0]}.pdf`
    link.click()
  }
}

function openInNewTab() {
  if (props.pdfUrl) {
    window.open(props.pdfUrl, '_blank', 'noopener,noreferrer')
  }
}

function getErrorMessage(): string {
  if (isFailed.value) {
    return props.state.error || 'An error occurred while generating the report'
  }
  return ''
}
</script>

<template>
  <div class="report-preview">
    <!-- PDF Viewer -->
    <div v-if="showPdfViewer" class="report-preview__pdf">
      <div class="report-preview__pdf-toolbar">
        <div class="pdf-info">
          <Tag severity="success" icon="pi pi-check">
            PDF Generated
          </Tag>
        </div>
        <div class="pdf-actions">
          <Button
            v-tooltip.top="'Download PDF'"
            icon="pi pi-download"
            size="small"
            outlined
            @click="downloadPdf"
          />
          <Button
            v-tooltip.top="'Open in new tab'"
            icon="pi pi-external-link"
            size="small"
            outlined
            @click="openInNewTab"
          />
        </div>
      </div>

      <object
        :data="pdfUrl"
        type="application/pdf"
        class="report-preview__pdf-viewer"
      >
        <div class="report-preview__pdf-fallback">
          <div class="fallback-content">
            <i class="pi pi-file-pdf" />
            <h4>PDF Viewer Not Available</h4>
            <p>Your browser doesn't support inline PDF viewing</p>
            <Button
              label="Download PDF"
              icon="pi pi-download"
              @click="downloadPdf"
            />
          </div>
        </div>
      </object>
    </div>

    <!-- Success State (non-PDF) -->
    <div v-else-if="showSuccessState" class="report-preview__success">
      <div class="success-content">
        <div class="success-icon">
          <i class="pi pi-check-circle" />
        </div>

        <h3>Report Generated Successfully!</h3>
        <p>Your {{ format?.name }} report has been downloaded automatically</p>

        <div v-if="state.report" class="file-info">
          <div class="file-details">
            <div class="file-name">
              <i :class="format?.icon" :style="{ color: format?.color }" />
              <span>{{ state.report.fileName }}</span>
            </div>
            <div class="file-size">
              {{ Math.round((state.report.fileSizeBytes || 0) / 1024) }} KB
            </div>
          </div>
        </div>

        <div class="success-actions">
          <Button
            label="Generate Another"
            icon="pi pi-plus"
            @click="emit('reset')"
          />
          <Button
            label="Download Again"
            icon="pi pi-download"
            outlined
            @click="() => {
              if (state.report) {
                const blob = new Blob([atob(state.report.base64Report)], { type: state.report.contentType });
                const url = URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = state.report.fileName;
                link.click();
                URL.revokeObjectURL(url);
              }
            }"
          />
        </div>
      </div>
    </div>

    <!-- Failed State -->
    <div v-else-if="isFailed" class="report-preview__error">
      <div class="error-content">
        <div class="error-icon">
          <i class="pi pi-exclamation-triangle" />
        </div>

        <h3>Generation Failed</h3>
        <p class="error-message">
          {{ getErrorMessage() }}
        </p>

        <div v-if="state.serverRequestId" class="error-details">
          <span class="detail-label">Request ID:</span>
          <code class="detail-value">{{ state.serverRequestId.substring(0, 8) }}...</code>
        </div>

        <div class="error-actions">
          <Button
            label="Try Again"
            icon="pi pi-refresh"
            @click="emit('retry')"
          />
          <Button
            label="Reset Form"
            icon="pi pi-undo"
            outlined
            @click="emit('reset')"
          />
        </div>
      </div>
    </div>

    <!-- Cancelled State -->
    <div v-else-if="isCancelled" class="report-preview__cancelled">
      <div class="cancelled-content">
        <div class="cancelled-icon">
          <i class="pi pi-ban" />
        </div>

        <h3>Generation Cancelled</h3>
        <p>Report generation was cancelled by user</p>

        <div class="cancelled-actions">
          <Button
            label="Generate Again"
            icon="pi pi-play"
            @click="emit('retry')"
          />
          <Button
            label="Reset"
            icon="pi pi-undo"
            outlined
            @click="emit('reset')"
          />
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else class="report-preview__empty">
      <div class="empty-content">
        <div class="empty-icon">
          <i :class="format?.icon || 'pi pi-file'" :style="{ color: format?.color || '#9ca3af' }" />
        </div>

        <h3>{{ format?.id === 'PDF' ? 'PDF Preview' : `${format?.name || 'File'} Download` }}</h3>
        <p v-if="format?.id === 'PDF'">
          PDF reports will be displayed here after generation
        </p>
        <p v-else>
          {{ format?.name || 'Files' }} will be automatically downloaded when generated
        </p>

        <!-- Preview features for PDF -->
        <div v-if="format?.id === 'PDF'" class="preview-features">
          <h4>Preview Features:</h4>
          <ul>
            <li><i class="pi pi-eye" /> Inline viewing</li>
            <li><i class="pi pi-search-plus" /> Zoom controls</li>
            <li><i class="pi pi-download" /> Download option</li>
            <li><i class="pi pi-external-link" /> Open in new tab</li>
          </ul>
        </div>

        <div class="empty-hint">
          <i class="pi pi-info-circle" />
          <span>Configure parameters and click "Generate Report" to begin</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.report-preview {
  height: 100%;
  min-height: 500px;

  &__pdf {
    height: 100%;
    display: flex;
    flex-direction: column;

    &-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 1.5rem;
      background: #f8fafc;
      border-bottom: 1px solid #e2e8f0;

      .pdf-actions {
        display: flex;
        gap: 0.5rem;
      }
    }

    &-viewer {
      flex: 1;
      width: 100%;
      border: none;
      background: white;
    }

    &-fallback {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      min-height: 400px;

      .fallback-content {
        text-align: center;
        padding: 2rem;

        i {
          font-size: 4rem;
          color: #dc2626;
          margin-bottom: 1.5rem;
        }

        h4 {
          margin: 0 0 0.5rem 0;
          color: #1f2937;
        }

        p {
          margin: 0 0 1.5rem 0;
          color: #6b7280;
        }
      }
    }
  }

  &__success,
  &__error,
  &__cancelled,
  &__empty {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    min-height: 500px;
    padding: 2rem;
  }

  &__success {
    .success-content {
      text-align: center;
      max-width: 400px;

      .success-icon {
        width: 4rem;
        height: 4rem;
        border-radius: 50%;
        background: linear-gradient(135deg, #10b981, #059669);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        font-size: 2rem;
        box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
      }

      h3 {
        margin: 0 0 0.75rem 0;
        color: #1f2937;
        font-size: 1.5rem;
        font-weight: 600;
      }

      p {
        margin: 0 0 1.5rem 0;
        color: #6b7280;
        line-height: 1.5;
      }

      .file-info {
        background: #f8fafc;
        border-radius: 0.75rem;
        padding: 1rem;
        margin-bottom: 1.5rem;
        border: 1px solid #e2e8f0;

        .file-details {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .file-name {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-weight: 500;
            color: #374151;

            i {
              font-size: 1.25rem;
            }
          }

          .file-size {
            font-size: 0.875rem;
            color: #6b7280;
          }
        }
      }

      .success-actions {
        display: flex;
        gap: 0.75rem;
        justify-content: center;
      }
    }
  }

  &__error {
    .error-content {
      text-align: center;
      max-width: 400px;

      .error-icon {
        width: 4rem;
        height: 4rem;
        border-radius: 50%;
        background: linear-gradient(135deg, #ef4444, #dc2626);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        font-size: 2rem;
        box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
      }

      h3 {
        margin: 0 0 0.75rem 0;
        color: #1f2937;
        font-size: 1.5rem;
        font-weight: 600;
      }

      .error-message {
        margin: 0 0 1rem 0;
        color: #6b7280;
        line-height: 1.5;
      }

      .error-details {
        background: #fef2f2;
        border-radius: 0.5rem;
        padding: 0.75rem;
        margin-bottom: 1.5rem;
        border: 1px solid #fecaca;

        .detail-label {
          font-size: 0.875rem;
          color: #7f1d1d;
          font-weight: 500;
        }

        .detail-value {
          background: #fee2e2;
          padding: 0.25rem 0.5rem;
          border-radius: 0.25rem;
          font-family: 'Monaco', monospace;
          font-size: 0.875rem;
          color: #991b1b;
          margin-left: 0.5rem;
        }
      }

      .error-actions {
        display: flex;
        gap: 0.75rem;
        justify-content: center;
      }
    }
  }

  &__cancelled {
    .cancelled-content {
      text-align: center;
      max-width: 400px;

      .cancelled-icon {
        width: 4rem;
        height: 4rem;
        border-radius: 50%;
        background: linear-gradient(135deg, #f59e0b, #d97706);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        font-size: 2rem;
        box-shadow: 0 4px 15px rgba(245, 158, 11, 0.3);
      }

      h3 {
        margin: 0 0 0.75rem 0;
        color: #1f2937;
        font-size: 1.5rem;
        font-weight: 600;
      }

      p {
        margin: 0 0 1.5rem 0;
        color: #6b7280;
      }

      .cancelled-actions {
        display: flex;
        gap: 0.75rem;
        justify-content: center;
      }
    }
  }

  &__empty {
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);

    .empty-content {
      text-align: center;
      max-width: 450px;

      .empty-icon {
        width: 5rem;
        height: 5rem;
        border-radius: 50%;
        background: #f3f4f6;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 1.5rem;
        font-size: 2.5rem;
      }

      h3 {
        margin: 0 0 0.75rem 0;
        color: #1f2937;
        font-size: 1.5rem;
        font-weight: 600;
      }

      p {
        margin: 0 0 1.5rem 0;
        color: #6b7280;
        line-height: 1.5;
      }

      .preview-features {
        background: white;
        border-radius: 0.75rem;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        border: 1px solid #e2e8f0;
        text-align: left;

        h4 {
          margin: 0 0 1rem 0;
          font-size: 1rem;
          font-weight: 600;
          color: #374151;
        }

        ul {
          list-style: none;
          padding: 0;
          margin: 0;
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 0.75rem;

          li {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.875rem;
            color: #6b7280;

            i {
              color: #3b82f6;
              width: 16px;
              font-size: 0.875rem;
            }
          }
        }
      }

      .empty-hint {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        padding: 1rem;
        background: #eff6ff;
        border-radius: 0.5rem;
        border: 1px solid #bfdbfe;
        font-size: 0.875rem;
        color: #1d4ed8;

        i {
          color: #3b82f6;
        }
      }
    }
  }

  // Responsive
  @media (max-width: 768px) {
    &__success,
    &__error,
    &__cancelled,
    &__empty {
      padding: 1.5rem;
    }

    &__success .success-actions,
    &__error .error-actions,
    &__cancelled .cancelled-actions {
      flex-direction: column;
    }

    &__empty .empty-content .preview-features ul {
      grid-template-columns: 1fr;
    }

    &__pdf-toolbar {
      padding: 0.75rem 1rem;

      .pdf-actions {
        gap: 0.25rem;
      }
    }
  }
}
</style>

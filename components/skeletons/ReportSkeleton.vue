<!-- components/skeletons/ReportSkeleton.vue -->
<script setup lang="ts">
interface Props {
  fieldCount?: number
  variant?: 'form' | 'progress' | 'preview'
}

const props = withDefaults(defineProps<Props>(), {
  fieldCount: 6,
  variant: 'form'
})

const fieldTypes = ['text', 'select', 'multiselect', 'date', 'number']

function getFieldType(index: number): string {
  return fieldTypes[index % fieldTypes.length]
}
</script>

<template>
  <div class="report-viewer">
    <!-- Skeleton Header -->
    <div class="report-viewer__header">
      <div class="report-viewer__header-content">
        <div class="report-viewer__header-info">
          <div class="report-viewer__skeleton-circle report-viewer__skeleton-icon" />
          <div class="report-viewer__skeleton-content">
            <div class="report-viewer__skeleton-line report-viewer__skeleton-line--75" />
            <div class="report-viewer__skeleton-line report-viewer__skeleton-line--50" />
          </div>
        </div>
        <div class="report-viewer__skeleton-badge">
          <div class="report-viewer__skeleton-circle report-viewer__skeleton-badge-icon" />
          <div class="report-viewer__skeleton-badge-content">
            <div class="report-viewer__skeleton-line report-viewer__skeleton-line--60" />
            <div class="report-viewer__skeleton-line report-viewer__skeleton-line--40" />
          </div>
        </div>
      </div>
    </div>

    <div class="report-viewer__main">
      <!-- Parameters Panel Skeleton -->
      <div class="report-viewer__parameters">
        <div class="report-viewer__parameters-container">
          <!-- Panel Header Skeleton -->
          <div class="report-viewer__parameters-header">
            <div class="report-viewer__parameters-header-content">
              <div class="report-viewer__skeleton-circle report-viewer__skeleton-panel-icon" />
              <div class="report-viewer__skeleton-header-content">
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--70" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--45" />
              </div>
            </div>
            <div class="report-viewer__skeleton-circle report-viewer__skeleton-action" />
          </div>

          <!-- Panel Content Skeleton -->
          <div class="report-viewer__parameters-content">
            <!-- Form Fields Skeleton -->
            <div class="report-viewer__skeleton-fields">
              <div
                v-for="n in fieldCount"
                :key="n"
                class="report-viewer__skeleton-field"
                :class="`report-viewer__skeleton-field--${getFieldType(n)}`"
              >
                <div class="report-viewer__skeleton-field-label">
                  <div class="report-viewer__skeleton-line report-viewer__skeleton-line--25" />
                  <div class="report-viewer__skeleton-required-dot" />
                </div>
                <div class="report-viewer__skeleton-field-input">
                  <div v-if="getFieldType(n) === 'multiselect'" class="report-viewer__skeleton-multiselect">
                    <div
                      v-for="tag in Math.floor(Math.random() * 3) + 1"
                      :key="tag"
                      class="report-viewer__skeleton-tag"
                    />
                  </div>
                  <div v-else class="report-viewer__skeleton-input" />
                </div>
              </div>
            </div>

            <!-- Format Selection Skeleton -->
            <div class="report-viewer__skeleton-format-section">
              <div class="report-viewer__skeleton-format-label">
                <div class="report-viewer__skeleton-icon" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--30" />
              </div>

              <div class="report-viewer__skeleton-format-grid">
                <div
                  v-for="n in 3"
                  :key="n"
                  class="report-viewer__skeleton-format-option"
                  :class="{ 'report-viewer__skeleton-format-option--selected': n === 1 }"
                >
                  <div class="report-viewer__skeleton-format-header">
                    <div class="report-viewer__skeleton-icon" />
                    <div class="report-viewer__skeleton-line report-viewer__skeleton-line--60" />
                  </div>
                  <div class="report-viewer__skeleton-line report-viewer__skeleton-line--80" />
                  <div class="report-viewer__skeleton-line report-viewer__skeleton-line--40" />
                </div>
              </div>
            </div>

            <!-- Generate Button Skeleton -->
            <div class="report-viewer__skeleton-generate-section">
              <div class="report-viewer__skeleton-generate-button" />
              <div class="report-viewer__skeleton-generate-info">
                <div class="report-viewer__skeleton-icon" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--70" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Preview Panel Skeleton -->
      <div class="report-viewer__preview">
        <div class="report-viewer__preview-container">
          <!-- Preview Header Skeleton -->
          <div class="report-viewer__preview-header">
            <div class="report-viewer__preview-header-content">
              <div class="report-viewer__skeleton-circle report-viewer__skeleton-panel-icon" />
              <div class="report-viewer__skeleton-header-content">
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--65" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--50" />
              </div>
            </div>
          </div>

          <!-- Preview Content Skeleton -->
          <div class="report-viewer__preview-content">
            <div class="report-viewer__skeleton-preview-empty">
              <div class="report-viewer__skeleton-preview-icon" />
              <div class="report-viewer__skeleton-preview-content">
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--50" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--70" />
                <div class="report-viewer__skeleton-line report-viewer__skeleton-line--40" />

                <!-- Feature List Skeleton -->
                <div class="report-viewer__skeleton-features">
                  <div class="report-viewer__skeleton-line report-viewer__skeleton-line--35" />
                  <div class="report-viewer__skeleton-feature-list">
                    <div
                      v-for="n in 4"
                      :key="n"
                      class="report-viewer__skeleton-feature-item"
                    >
                      <div class="report-viewer__skeleton-icon" />
                      <div class="report-viewer__skeleton-line report-viewer__skeleton-line--45" />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Base Skeleton Styles - Integrates with existing SCSS variables */
.report-viewer__skeleton-line,
.report-viewer__skeleton-circle,
.report-viewer__skeleton-input,
.report-viewer__skeleton-icon,
.report-viewer__skeleton-tag,
.report-viewer__skeleton-generate-button {
  background: linear-gradient(
    90deg,
    #f0f0f0 25%,
    #e0e0e0 50%,
    #f0f0f0 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* Line Variations */
.report-viewer__skeleton-line {
  height: 12px;
  border-radius: 6px;
  margin: 4px 0;

  &--25 { width: 25%; }
  &--30 { width: 30%; }
  &--35 { width: 35%; }
  &--40 { width: 40%; }
  &--45 { width: 45%; }
  &--50 { width: 50%; }
  &--60 { width: 60%; }
  &--65 { width: 65%; }
  &--70 { width: 70%; }
  &--75 { width: 75%; }
  &--80 { width: 80%; }
}

/* Header Skeleton */
.report-viewer__skeleton-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
}

.report-viewer__skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.report-viewer__skeleton-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.report-viewer__skeleton-badge-icon {
  width: 20px !important;
  height: 20px !important;
}

.report-viewer__skeleton-badge-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* Panel Header Skeleton */
.report-viewer__skeleton-panel-icon {
  width: 48px !important;
  height: 48px !important;
  border-radius: 12px !important;
}

.report-viewer__skeleton-header-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.report-viewer__skeleton-action {
  width: 32px !important;
  height: 32px !important;
}

/* Form Fields Skeleton */
.report-viewer__skeleton-fields {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.report-viewer__skeleton-field {
  animation: fadeInUp 0.4s ease-out;
  animation-fill-mode: both;

  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }
  &:nth-child(4) { animation-delay: 0.4s; }
  &:nth-child(5) { animation-delay: 0.5s; }
  &:nth-child(6) { animation-delay: 0.6s; }
}

.report-viewer__skeleton-field-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.report-viewer__skeleton-required-dot {
  width: 8px;
  height: 8px;
  background: #ef4444;
  border-radius: 50%;
  animation: pulse-required 2s infinite;
}

.report-viewer__skeleton-field-input {
  height: 44px;
  position: relative;
}

.report-viewer__skeleton-input {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
}

/* Multiselect Skeleton */
.report-viewer__skeleton-multiselect {
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 8px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  min-height: 44px;
  flex-wrap: wrap;
}

.report-viewer__skeleton-tag {
  width: 60px;
  height: 24px;
  border-radius: 6px;

  &:nth-child(1) { width: 80px; }
  &:nth-child(2) { width: 100px; }
  &:nth-child(3) { width: 70px; }
}

/* Format Section Skeleton */
.report-viewer__skeleton-format-section {
  margin-bottom: 32px;
  animation: fadeInUp 0.5s ease-out;
}

.report-viewer__skeleton-format-label {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.report-viewer__skeleton-format-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

.report-viewer__skeleton-format-option {
  padding: 20px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;

  &--selected {
    border-color: #3b82f6;
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(255,255,255,0.95) 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(59, 130, 246, 0.2);
  }
}

.report-viewer__skeleton-format-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

/* Generate Section Skeleton */
.report-viewer__skeleton-generate-section {
  animation: fadeInUp 0.6s ease-out;
}

.report-viewer__skeleton-generate-button {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.report-viewer__skeleton-generate-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Preview Skeleton */
.report-viewer__skeleton-preview-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 500px;
  padding: 48px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.report-viewer__skeleton-preview-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 32px;
}

.report-viewer__skeleton-preview-content {
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.report-viewer__skeleton-features {
  margin-top: 32px;
}

.report-viewer__skeleton-feature-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-viewer__skeleton-feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
}

/* Responsive Skeleton */
@media (max-width: 768px) {
  .report-viewer__skeleton-fields {
    gap: 20px;
  }

  .report-viewer__skeleton-field-input {
    height: 48px;
  }

  .report-viewer__skeleton-multiselect {
    min-height: 48px;
  }

  .report-viewer__skeleton-generate-button {
    height: 48px;
  }

  .report-viewer__skeleton-preview-empty {
    min-height: 400px;
    padding: 32px 16px;
  }

  .report-viewer__skeleton-preview-icon {
    width: 64px;
    height: 64px;
  }
}

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

@keyframes pulse-required {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

/* Accessibility */
@media (prefers-reduced-motion: reduce) {
  .report-viewer__skeleton-line,
  .report-viewer__skeleton-circle,
  .report-viewer__skeleton-input,
  .report-viewer__skeleton-icon,
  .report-viewer__skeleton-tag,
  .report-viewer__skeleton-generate-button {
    animation: none;
    background: #f0f0f0;
  }

  .report-viewer__skeleton-field {
    animation: none;
  }

  .report-viewer__skeleton-required-dot {
    animation: none;
  }
}
</style>

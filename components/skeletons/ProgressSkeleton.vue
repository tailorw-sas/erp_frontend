<!-- components/skeletons/ProgressSkeleton.vue -->
<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'

interface Props {
  currentStep?: number
  showPollingDetails?: boolean
  animated?: boolean
  variant?: 'submitting' | 'processing' | 'downloading'
}

const props = withDefaults(defineProps<Props>(), {
  currentStep: 1,
  showPollingDetails: false,
  animated: true,
  variant: 'processing'
})

const progress = ref(0)
let progressInterval: NodeJS.Timeout | null = null

const progressConfig = computed(() => {
  const configs = {
    submitting: { target: 15, speed: 2 },
    processing: { target: 75, speed: 1 },
    downloading: { target: 95, speed: 3 }
  }
  return configs[props.variant] || configs.processing
})

onMounted(() => {
  if (props.animated) {
    startProgressAnimation()
  }
})

onUnmounted(() => {
  if (progressInterval) {
    clearInterval(progressInterval)
  }
})

function startProgressAnimation() {
  const config = progressConfig.value

  progressInterval = setInterval(() => {
    if (progress.value < config.target) {
      progress.value += Math.random() * config.speed + 0.5
      if (progress.value > config.target) {
        progress.value = config.target
      }
    }
  }, 200)
}
</script>

<template>
  <div class="report-viewer__progress-section">
    <div class="report-viewer__progress-container">
      <!-- Steps Skeleton -->
      <div class="report-viewer__steps-container">
        <div class="report-viewer__skeleton-steps">
          <div
            v-for="n in 3"
            :key="n"
            class="report-viewer__skeleton-step"
            :class="{ 'report-viewer__skeleton-step--active': n <= currentStep }"
          >
            <div class="report-viewer__skeleton-step-number" />
            <div class="report-viewer__skeleton-step-line" />
            <div class="report-viewer__skeleton-step-label" />
          </div>
        </div>
      </div>

      <!-- Progress Status Skeleton -->
      <div class="report-viewer__progress-status">
        <!-- Title and Percentage -->
        <div class="report-viewer__skeleton-progress-header">
          <div class="report-viewer__skeleton-progress-title">
            <div class="report-viewer__skeleton-circle report-viewer__skeleton-status-icon" />
            <div class="report-viewer__skeleton-line report-viewer__skeleton-line--60" />
          </div>
          <div class="report-viewer__skeleton-percentage" />
        </div>

        <!-- Large Progress Bar -->
        <div class="report-viewer__progress-bar-container">
          <div class="report-viewer__skeleton-progress-bar">
            <div
              class="report-viewer__skeleton-progress-fill"
              :style="{ width: `${progress}%` }"
            />
          </div>
        </div>

        <!-- Progress Message -->
        <div class="report-viewer__progress-message">
          <div class="report-viewer__skeleton-line report-viewer__skeleton-line--70" />
        </div>

        <!-- Time Information -->
        <div class="report-viewer__time-info">
          <div class="report-viewer__skeleton-time-row">
            <div class="report-viewer__skeleton-time-item">
              <div class="report-viewer__skeleton-icon" />
              <div class="report-viewer__skeleton-line report-viewer__skeleton-line--40" />
            </div>
            <div class="report-viewer__skeleton-time-item">
              <div class="report-viewer__skeleton-icon" />
              <div class="report-viewer__skeleton-line report-viewer__skeleton-line--35" />
            </div>
          </div>
        </div>

        <!-- Polling Details (if applicable) -->
        <div
          v-if="showPollingDetails"
          class="report-viewer__polling-details"
        >
          <div class="report-viewer__skeleton-polling-row">
            <div class="report-viewer__skeleton-polling-item">
              <div class="report-viewer__skeleton-icon" />
              <div class="report-viewer__skeleton-line report-viewer__skeleton-line--50" />
            </div>
            <div class="report-viewer__skeleton-polling-item">
              <div class="report-viewer__skeleton-icon" />
              <div class="report-viewer__skeleton-line report-viewer__skeleton-line--45" />
            </div>
          </div>
        </div>

        <!-- Action Button -->
        <div class="report-viewer__progress-actions">
          <div class="report-viewer__skeleton-action-button" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Progress Skeleton Styles */
.report-viewer__skeleton-steps {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 60px;
  margin-bottom: 32px;

  @media (max-width: 768px) {
    gap: 40px;
    flex-wrap: wrap;
  }
}

.report-viewer__skeleton-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  position: relative;

  &:not(:last-child)::after {
    content: '';
    position: absolute;
    top: 22px;
    left: 100%;
    width: 60px;
    height: 2px;
    background: linear-gradient(90deg, #e5e7eb 0%, #d1d5db 50%, #e5e7eb 100%);

    @media (max-width: 768px) {
      display: none;
    }
  }

  &--active {
    .report-viewer__skeleton-step-number {
      background: linear-gradient(135deg, #3b82f6, #2563eb);
      animation: pulse-active 2s infinite;
    }

    .report-viewer__skeleton-step-label {
      background: linear-gradient(90deg, #3b82f6 25%, #2563eb 50%, #3b82f6 75%);
      background-size: 200% 100%;
      animation: shimmer-active 1.5s infinite;
    }

    &::after {
      background: linear-gradient(90deg, #3b82f6 0%, #2563eb 50%, #3b82f6 100%);
      animation: flow 2s infinite;
    }
  }
}

.report-viewer__skeleton-step-number {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border: 2px solid #e5e7eb;
}

.report-viewer__skeleton-step-label {
  width: 80px;
  height: 12px;
  border-radius: 6px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

/* Progress Header Skeleton */
.report-viewer__skeleton-progress-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.report-viewer__skeleton-progress-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-viewer__skeleton-status-icon {
  width: 24px !important;
  height: 24px !important;
  animation: spin 2s linear infinite;
}

.report-viewer__skeleton-percentage {
  width: 60px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

/* Progress Bar Skeleton */
.report-viewer__skeleton-progress-bar {
  height: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border: 1px solid #cbd5e1;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.report-viewer__skeleton-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #2563eb 50%, #1d4ed8 100%);
  border-radius: 8px;
  transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 50%;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), transparent);
    border-radius: 8px 8px 0 0;
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 30px;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3));
    animation: progress-shine 1.5s ease-in-out infinite;
  }
}

/* Time Info Skeleton */
.report-viewer__skeleton-time-row,
.report-viewer__skeleton-polling-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.report-viewer__skeleton-time-item,
.report-viewer__skeleton-polling-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Action Button Skeleton */
.report-viewer__skeleton-action-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  animation: pulse-button 2s infinite;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
    animation: button-shine 2s infinite;
  }
}

/* Enhanced Animations */
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes shimmer-active {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes pulse-active {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  }
}

@keyframes flow {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes progress-shine {
  0% { transform: translateX(-30px); }
  100% { transform: translateX(100px); }
}

@keyframes pulse-button {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.9;
    transform: scale(1.02);
  }
}

@keyframes button-shine {
  0% { left: -100%; }
  50% { left: 100%; }
  100% { left: 100%; }
}

/* Responsive */
@media (max-width: 768px) {
  .report-viewer__skeleton-progress-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .report-viewer__skeleton-time-row,
  .report-viewer__skeleton-polling-row {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .report-viewer__skeleton-action-button {
    height: 52px;
  }
}

/* Accessibility */
@media (prefers-reduced-motion: reduce) {
  .report-viewer__skeleton-step-number,
  .report-viewer__skeleton-step-label,
  .report-viewer__skeleton-percentage,
  .report-viewer__skeleton-status-icon,
  .report-viewer__skeleton-action-button {
    animation: none;
  }

  .report-viewer__skeleton-progress-fill::after {
    display: none;
  }

  .report-viewer__skeleton-action-button::before {
    display: none;
  }
}
</style>

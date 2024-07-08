<script setup lang="ts">
import { ref, watch } from 'vue'
import type { PropType } from 'vue'
import type { IOptionField } from './interfaces/IFieldInterfaces'
import type { ITreeNode } from '~/interfaces/IInterfaces'

interface CheckedStatus {
  checked: boolean
  partialChecked: boolean
  isHighRisk?: boolean
}

interface MainStructure {
  [key: string]: CheckedStatus
}

interface TreeNode {
  key: string
  label: string
  data: string
  icon: string
  status?: string
  isHighRisk?: boolean
  fullSelected?: boolean
  children?: TreeNode[]
}

const props = defineProps({
  properties: {
    type: Object as PropType<IOptionField>,
    required: false,
  },
  items: {
    type: Array as PropType<ITreeNode[]>,
    required: true,
    default: () => [],
  },
  selectedKey: {
    type: Object as PropType<MainStructure>,
    required: false,
  },
})

const emits = defineEmits([
  'update:listItems',
  'update:selectedKey',
  'update:modelValue',
  'onSelectedNodeHighrisk',
  'onSelectInactiveNode',
])

const expandedKeys = ref<Record<string, boolean>>({})
const itemsListLocal = ref<ITreeNode[]>([])
const selectedKeyLocal = ref<MainStructure>({})

watch(
  () => props.items,
  (newItems) => {
    itemsListLocal.value = newItems
  },
  { immediate: true, deep: true }
)

watch(
  () => props.selectedKey,
  (newSelectedKey) => {
    if (newSelectedKey) {
      selectedKeyLocal.value = newSelectedKey
      // updateParentSelection()
    }
  },
  { immediate: true, deep: true }
)

const collapseAndExpandAll = ref(false)
const checkOrUncheckAll = ref(false)

function toggleExpandAll() {
  if (collapseAndExpandAll.value) {
    expandAll()
  }
  else {
    collapseAll()
  }
}

function expandAll() {
  for (const node of itemsListLocal.value) {
    expandNode(node)
  }
  expandedKeys.value = { ...expandedKeys.value }
}

function collapseAll() {
  expandedKeys.value = {}
}

function expandNode(node: TreeNode) {
  if (node.children && node.children.length) {
    expandedKeys.value[node.key] = true
    for (const child of node.children) {
      expandNode(child)
    }
  }
}

function transformToCheckedObject(data: TreeNode[]): MainStructure {
  const result: MainStructure = {}
  function processItem(item: TreeNode) {
    if (item.status && item.status === 'ACTIVE') {
      result[item.key] = {
        checked: true,
        partialChecked: false,
        isHighRisk: !!(item.status && item.status === 'ACTIVE' && item.isHighRisk)
      }
    }
    if (item.children && item.children.length > 0) {
      item.fullSelected = true
      item.children.forEach(child => processItem(child))
    }
  }
  data.forEach(item => processItem(item))
  return result
}

// Quitarle el fullSelected a los padres
function transformToUnCheckedObject(data: TreeNode[]) {
  function processItem(item: TreeNode) {
    if (item.children && item.children.length > 0) {
      item.fullSelected = false
    }
  }
  data.forEach(item => processItem(item))
}

function findHighRiskNodes(node: TreeNode, highRiskNodes: TreeNode[] = []): TreeNode[] {
  if (node.isHighRisk && node.status === 'ACTIVE') {
    highRiskNodes.push(node)
  }

  if (node.children && node.children.length > 0) {
    node.children.forEach(child => findHighRiskNodes(child, highRiskNodes))
  }

  return highRiskNodes
}

function checkOrUncheckAllFn() {
  if (checkOrUncheckAll.value) {
    selectedKeyLocal.value = transformToCheckedObject(itemsListLocal.value)
    const highRiskNodes: TreeNode[] = []
    itemsListLocal.value.forEach(node => findHighRiskNodes(node, highRiskNodes))
    if (highRiskNodes.length > 0) {
      emits('onSelectedNodeHighrisk', highRiskNodes)
    }
    else {
      updateParentSelection()
    }
  }
  else {
    selectedKeyLocal.value = {}
    transformToUnCheckedObject(itemsListLocal.value)
  }
}

function getHighRiskKeys(node: any) {
  if (node.children && node.children.length > 0) {
    let result = node.children.filter((child: any) => child.isHighRisk && child.status === 'ACTIVE')
    result = [...result]
    return result
  }
  return []
}

function getInactiveKeys(node: any): string[] {
  let result: string[] = []

  if (node.status === 'INACTIVE') {
    result.push(node.key)
  }

  if (node.children && node.children.length > 0) {
    node.children.forEach((child: any) => {
      const childResult = getInactiveKeys(child)
      result = result.concat(childResult)
    })
  }

  return result
}

function uncheckNodeAndChildren(node: any) {
  // Desmarca el nodo actual
  if (selectedKeyLocal.value[node.key]) {
    selectedKeyLocal.value[node.key].checked = false
    selectedKeyLocal.value[node.key].partialChecked = false
  }
  node.fullSelected = false
  // Si el nodo tiene hijos, desmárcalos también
  if (node.children && node.children.length > 0) {
    node.children.forEach((child: any) => {
      uncheckNodeAndChildren(child)
    })
  }
}

function onNodeSelect(node: any) {
  const hasHighRisk = getHighRiskKeys(node)
  const inactiveElements = getInactiveKeys(node)
  // console.log(node, hasHighRisk, inactiveElements)

  if (node.children && node.children.length > 0) { // Is parent
    if (node.fullSelected) {
      uncheckNodeAndChildren(node)
      updateParentSelection()
    }
    else {
      if (hasHighRisk.length > 0) {
        emits('onSelectedNodeHighrisk', hasHighRisk)
      }
      if (inactiveElements.length > 0) {
        node.fullSelected = true // marco el padre como visitado para si se le vuelve a dar clic se deseleccionen los elementos
        emits('onSelectInactiveNode', inactiveElements)
      }
    }
  }
  else {
    if (node.status === 'INACTIVE') {
      emits('onSelectInactiveNode', node.key)
    }
    else {
      if (node.isHighRisk) {
        emits('onSelectedNodeHighrisk', [node])
      }
    }
  }
  // toast.add({ severity: 'success', summary: 'Node Selected', detail: node.label, life: 3000 })
}

function updateParentSelection() {
  const updateParent = (node: TreeNode) => {
    if (!node.children || node.children.length === 0) {
      return
    }

    let allChecked = true
    let anyChecked = false

    for (const child of node.children) {
      updateParent(child)
      const childStatus = selectedKeyLocal.value[child.key]
      if (childStatus) {
        if (childStatus.checked) {
          anyChecked = true
        }
        else if (childStatus.partialChecked) {
          anyChecked = true
          allChecked = false
        }
        else {
          allChecked = false
        }
      }
      else {
        allChecked = false
      }
    }

    const nodeStatus = selectedKeyLocal.value[node.key] || { checked: false, partialChecked: false }
    if (allChecked) {
      nodeStatus.checked = true
      nodeStatus.partialChecked = false
    }
    else if (anyChecked) {
      nodeStatus.checked = false
      nodeStatus.partialChecked = true
    }
    else {
      nodeStatus.checked = false
      nodeStatus.partialChecked = false
    }

    selectedKeyLocal.value[node.key] = nodeStatus
  }

  for (const node of itemsListLocal.value) {
    updateParent(node)
  }
}

watch(itemsListLocal, () => {
  emits('update:listItems', itemsListLocal.value)
}, { deep: true })
watch(selectedKeyLocal, () => {
  emits('update:selectedKey', selectedKeyLocal.value)
}, { deep: true })

defineExpose({ updateParentSelection })
</script>

<template>
  <div>
    <div class="flex align-items-center mb-2">
      <div class="mr-5 flex align-items-center">
        <Checkbox v-model="collapseAndExpandAll" input-id="ingredient1" name="collapseAndExpandAll" :binary="true" @change="toggleExpandAll" />
        <label for="ingredient1" class="ml-2 font-bold"> Expand/Collapse All </label>
      </div>
      <div class="flex align-items-center">
        <Checkbox v-model="checkOrUncheckAll" input-id="ingredient2" name="checkOrUncheckAll" :binary="true" @change="checkOrUncheckAllFn" />
        <label for="ingredient2" class="ml-2 font-bold"> Check/Uncheck All </label>
      </div>
    </div>

    <Tree
      v-model:selectionKeys="selectedKeyLocal"
      v-model:expandedKeys="expandedKeys"
      :value="itemsListLocal"
      :filter="true"
      filter-mode="lenient"
      class="w-full"
      selection-mode="checkbox"
      @node-select="onNodeSelect"
    >
      <template #default="slotProps">
        <div class="flex align-items-center" style="width: 100%;">
          <div class="flex align-items-center">
            <i
              v-if="slotProps.node.children && slotProps.node.children.length"
              :class="expandedKeys[slotProps.node.key] ? 'pi pi-folder-open' : 'pi pi-folder'"
              class="mr-2"
            />
            <i v-else class="pi pi-file mr-2" />
            <span>{{ slotProps.node.label }}</span>
            <span
              v-if="slotProps.node.isHighRisk"
              v-tooltip="'HIGH RISK'"
              class="ml-4 p-tag p-tag-rounded p-tag-danger font-bold text-sm"
              style="padding-top: 2px; padding-bottom: 2px;"
            >
              High Risk
            </span>
          </div>
          <div class="flex align-items-center" :class="slotProps.node.isHighRisk ? 'ml-1' : 'ml-4'">
            <span
              v-if="slotProps.node.status"
              v-tooltip="slotProps.node.status === 'ACTIVE' ? 'ACTIVE' : 'INACTIVE'"
              class="p-tag p-tag-rounded font-bold text-sm"
              :class="slotProps.node.status === 'ACTIVE' ? 'p-tag-success' : 'p-tag-danger'"
              style="padding-top: 2px; padding-bottom: 2px;"
            >
              {{ slotProps.node.status ? slotProps.node.status.toLowerCase()[0].toUpperCase() + slotProps.node.status.slice(1).toLowerCase() : '' }}
            </span>
          </div>
        </div>
      </template>
    </Tree>
  </div>
</template>

<style scoped>
</style>

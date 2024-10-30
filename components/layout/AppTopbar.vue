<script setup>
import { ref } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useAuthStore } from '@/stores/authStore'

const { signOut, data } = useAuth()

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})

const { onMenuToggle, showConfigSidebar } = useLayout()
const loading = ref(false)
const visible = ref(false)

const dialogConfirmChangePassword = ref(false)
const changePassVisible = ref(false)
const bussinessInput = ref(null)
const bussinessSelected = ref({})
const currentBussiness = ref({})

const confirm = useConfirm()
const dialogConfirm = ref(false)
const dialogConfirmSingOut = ref(false)
const messageDialog = ref('')

const authStore = useAuthStore()
const businessStore = useBusinessStore()

currentBussiness.value = businessStore.current

const { $api } = useNuxtApp()
const repo = repository($api)

const searchActive = ref(true)

function onMenuButtonClick() {
  onMenuToggle()
}

function onConfigButtonClick() {
  showConfigSidebar()
}

async function openDialogConfirm() {
  if (bussinessSelected.value != null) {
    messageDialog.value = `You want to change companies ${currentBussiness.value.name} to ${bussinessSelected.value.name}`
    dialogConfirm.value = true
  }
}

async function openDialogSignOut() {
  confirm.require({
    message: 'Are you sure you want to sign out?',
    header: 'Sign Out',
    icon: 'pi pi-exclamation-triangle',
    rejectClass: 'p-button-danger p-button-outlined',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      onConfirmSignOut()
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected the action', life: 3000 })
    }
  })
}

async function handleChange() {
  try {
    loading.value = true
    await repo.changeUserBusiness({ businessId: bussinessSelected.value.businessId })
    loading.value = false
    window.location.reload()
  }
  catch (error) {
    // TODO: Show toast error if there is an error
    // console.log(error)
  }
  finally {
    dialogConfirm.value = false
    visible.value = false
  }
}

async function openDialogConfirmChangePassword() {
  messageDialog.value = `Are you sure you want to change your password?`
  // dialogConfirmChangePassword.value = true
  // dialogConfirm.value = false
  confirm.require({
    message: messageDialog.value,
    header: 'Change Password',
    icon: 'pi pi-exclamation-triangle',
    rejectClass: 'p-button-danger p-button-outlined',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      openResetPassword()
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected the action', life: 3000 })
    }
  })
}

async function openResetPassword() {
  dialogConfirmChangePassword.value = false
  changePassVisible.value = true
}

async function onConfirmSignOut() {
  try {
    loading.value = true
    await signOut({ callbackUrl: '/auth/login' })
    loading.value = false
  }
  catch (error) {
    // TODO: Show toast error if there is an error
    // console.log(error)
  }
  finally {
    dialogConfirmSingOut.value = false
  }
}
</script>

<template>
  <div class="layout-topbar">
    <div class="topbar-start">
      <Button type="button" class="topbar-menubutton p-link p-trigger" @click="onMenuButtonClick">
        <i class="pi pi-bars" />
      </Button>

      <div class="topbar-breadcrumb">
        <AppBreadCrumb />
      </div>
    </div>
    <div class="layout-topbar-menu-section">
      <AppSidebar />
    </div>
    <div class="topbar-end mr-5" style="width: 30%; max-width: 30%;">
      <ul class="topbar-menu">
        <li
          class="profile-item topbar-item"
          style="justify-content: end !important; width: 100%; max-width: 100%; display: flex;"
        >
          <div class="topbar-search " :class="{ 'topbar-search-active': searchActive }">
            <div
              v-if="false"
              class="search-input-wrapper  " style="width: 100%;"
              @click="authStore.userData.data.businesses.length > 1 ? visible = !visible : null"
            >
              <IconField v-if="currentBussiness !== null" class=" bg-secondary">
                <InputIcon v-if="authStore.userData.data.businesses.length > 1" class="pi pi-search cursor-pointer" />
                <InputText
                  ref="bussinessInput" v-model="currentBussiness.name" type="text" placeholder="Search"
                  :style="{ width: `${currentBussiness.name.length + 10}ch` }" class="cursor-pointer"
                />
              </IconField>
            </div>
          </div>
        </li>

        <IfCan :perms="['CHANGE_STYLE']">
          <li class="ml-3">
            <Button
              type="button" icon="pi pi-cog" class="text-color-secondary flex-shrink-0" severity="secondary" text
              rounded @click="onConfigButtonClick()"
            />
          </li>
        </IfCan>

        <li class="profile-item topbar-item">
          <a
            v-ripple
            v-styleclass="{ selector: '@next', enterClass: 'hidden', enterActiveClass: 'px-scalein', leaveToClass: 'hidden', leaveActiveClass: 'px-fadeout', hideOnOutsideClick: true }"
            class="p-ripple cursor-pointer"
          >
            <Avatar v-if="data?.user?.image" :image="data?.user?.image" shape="circle" />
            <Avatar v-else icon="pi pi-user" style="background-color: #dee9fc; color: #1a2551" shape="circle" />
          </a>

          <ul class="topbar-menu active-topbar-menu p-4 w-15rem z-5 hidden border-round">
            <li role="menuitem" class="m-0 mb-3">
              <a
                v-styleclass="{ selector: '@grandparent', enterClass: 'hidden', enterActiveClass: 'px-scalein', leaveToClass: 'hidden', leaveActiveClass: 'px-fadeout', hideOnOutsideClick: true }"
                href="/user/profile" class="flex align-items-center hover:text-primary-500 transition-duration-200"
              >
                <i class="pi pi-fw pi-user mr-2" />
                <span>My Profile</span>
              </a>
            </li>

            <li role="menuitem" class="m-0 mb-3">
              <a
                v-if="currentBussiness !== null"
                v-styleclass="{ selector: '@grandparent', enterClass: 'hidden', enterActiveClass: 'px-scalein', leaveToClass: 'hidden', leaveActiveClass: 'px-fadeout', hideOnOutsideClick: true }"
                :href="`/business/profile/${currentBussiness.businessId}`"
                class="flex align-items-center hover:text-primary-500 transition-duration-200"
              >
                <i class="pi pi-fw pi-folder mr-2" />
                <span>My Business</span>
              </a>
            </li>

            <li role="menuitem" class="m-0 mb-3" @click="openDialogConfirmChangePassword">
              <a
                v-styleclass="{ selector: '@grandparent', enterClass: 'hidden', enterActiveClass: 'px-scalein', leaveToClass: 'hidden', leaveActiveClass: 'px-fadeout', hideOnOutsideClick: true }"
                href="#" class="flex align-items-center hover:text-primary-500 transition-duration-200"
              >
                <i class="pi pi-fw pi-key mr-2" />
                <span>Change Password</span>
              </a>
            </li>
            <li role="menuitem" class="m-0" @click="openDialogSignOut">
              <a
                v-styleclass="{ selector: '@grandparent', enterClass: 'hidden', enterActiveClass: 'px-scalein', leaveToClass: 'hidden', leaveActiveClass: 'px-fadeout', hideOnOutsideClick: true }"
                href="#" class="flex align-items-center hover:text-primary-500 transition-duration-200"
              >
                <i class="pi pi-fw pi-sign-out mr-2" />
                <span>Sign Out</span>
              </a>
            </li>
          </ul>
        </li>
      </ul>

      <div class="12">
        <Dialog
          v-model:visible="visible" modal header="Business" class="mx-3 sm:mx-0 sm:w-full md:w-4"
          content-class="border-round-bottom border-top-1 surface-border" @hide="visible = false"
        >
          <div class="grid p-2">
            <div class="col-12 field mt-1">
              <Listbox
                v-model="bussinessSelected" :options="authStore.availableCompanies" option-label="name"
                empty-message="There are no items in the list." list-style="max-height:200px;height:200px"
              />
            </div>
          </div>
          <template #footer>
            <Button label="Cancel" icon="pi pi-times" severity="secondary" @click="visible = !visible" />
            <Button label="Select" icon="pi pi-check" @click="openDialogConfirm" />
          </template>
        </Dialog>
      </div>

      <div v-if="changePassVisible" class="col-12">
        <ResetPasswordDialog :open-dialog="changePassVisible" />
      </div>
    </div>
  </div>

  <Dialog v-model:visible="dialogConfirm" :style="{ width: '450px' }" :modal="true" :closable="false">
    <div class="flex align-items-center justify-content-center my-3">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
      <span>{{ messageDialog }}</span>
    </div>
    <template #footer>
      <Button label="No" icon="pi pi-times" text @click="dialogConfirm = false" />
      <Button label="Yes" icon="pi pi-check" text severity="danger" :disabled="loading" @click="handleChange" />
    </template>
  </Dialog>

  <Dialog v-model:visible="dialogConfirmChangePassword" header="Change Password" :style="{ width: '450px' }" :modal="true" :closable="false">
    <div class="flex align-items-center justify-content-center my-3">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
      <span>{{ messageDialog }}</span>
    </div>
    <template #footer>
      <Button label="No" icon="pi pi-times" text @click="dialogConfirmChangePassword = false" />
      <Button label="Yes" icon="pi pi-check" text severity="danger" :disabled="loading" @click="openResetPassword" />
    </template>
  </Dialog>

  <Dialog v-model:visible="dialogConfirmSingOut" header="Sign Out" :style="{ width: '450px' }" :modal="true" :closable="false">
    <div class="flex align-items-center justify-content-center my-3">
      <i class="pi pi-exclamation-triangle mr-3" style="font-size: 2rem" />
      <span>{{ messageDialog }}</span>
    </div>
    <template #footer>
      <Button label="No" icon="pi pi-times" @click="dialogConfirmSingOut = false" />
      <Button label="Yes" icon="pi pi-check" severity="danger" :disabled="loading" @click="onConfirmSignOut" />
    </template>
  </Dialog>
</template>

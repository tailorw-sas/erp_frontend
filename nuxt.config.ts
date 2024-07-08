// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  experimental: { typedPages: true },
  modules: [
    '@sidebase/nuxt-auth',
    'nuxt-primevue',
    '@nuxt/image',
    '@pinia/nuxt',
    '@vueuse/nuxt'
  ],
  typescript: {
    shim: false
  },
  auth: {
    globalAppMiddleware: {
      isEnabled: true
    }
  },
  plugins: [
    { src: '~/plugins/recaptcha.ts' },
  ],
  runtimeConfig: {
    recaptcha: {
      secretKey: ''
    },
    public: {
      recaptcha: {
        siteKey: ''
      },
      loadTableData: true,
      showSaveConfirm: false,
      showDeleteConfirm: false
    }
  },
  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
})

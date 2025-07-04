// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true
  },

  experimental: {
    typedPages: true,
    payloadExtraction: false // 🔧 Fix para problemas de hidratación
  },

  modules: [
    '@sidebase/nuxt-auth',
    'nuxt-primevue',
    '@nuxt/image',
    '@pinia/nuxt',
    '@vueuse/nuxt',
  ],

  typescript: {
    strict: true,
    typeCheck: false,
    shim: false
  },

  auth: {
    origin: process.env.NUXT_AUTH_URL,
    globalAppMiddleware: {
      isEnabled: true
    }
  },

  plugins: [
    { src: '~/plugins/recaptcha.ts', mode: 'client' }, // 🔧 Solo cliente
    // { src: '~/plugins/primevue.client.ts', mode: 'client' } // 🔧 Plugin específico
  ],

  runtimeConfig: {
    recaptcha: {
      secretKey: process.env.NUXT_RECAPTCHA_SECRET_KEY || ''
    },
    public: {
      recaptcha: {
        siteKey: process.env.NUXT_PUBLIC_RECAPTCHA_SITE_KEY || ''
      },
      loadTableData: true,
      showSaveConfirm: false,
      showDeleteConfirm: false,
      useIndexedDbCache: true
    }
  },

  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],

  // 🔧 Configuración específica para PrimeVue y SSR
  build: {
    transpile: ['primevue']
  },

  // 🔧 Configuración adicional para debugging
  debug: process.env.NODE_ENV === 'development',

  // 🔧 Configuración de head por defecto
  app: {
    head: {
      meta: [
        { name: 'viewport', content: 'width=device-width, initial-scale=1' }
      ]
    }
  }
})

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
      secretKey: process.env.RECAPTCHA_SECRET_KEY || ''
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

  // 🔧 Configuración de Vite optimizada
  vite: {
    clearScreen: false, // esto desactiva el overlay
    server: {
      hmr: {
        overlay: false
      }
    },
    define: {
      'process.client': process.env.NODE_ENV !== 'production' ? true : 'typeof window !== "undefined"',
      'process.server': process.env.NODE_ENV !== 'production' ? false : 'typeof window === "undefined"'
    },
    optimizeDeps: {
      include: ['primevue/button', 'primevue/inputtext'] // Añade los componentes que uses
    }
  },

  // 🔧 Configuración SSR más robusta
  ssr: true, // Mantener SSR pero con configuración mejorada

  // 🔧 Configuración de Nitro para mejor manejo de errores
  nitro: {
    routeRules: {
      '/dashboard/**': {
        ssr: true,
        experimentalNoScripts: false
      }
    },
    esbuild: {
      options: {
        target: 'esnext'
      }
    }
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

export default defineNuxtConfig({
  compatibilityDate: '2025-07-04',
  devtools: { enabled: true },
  experimental: { typedPages: true },
  modules: [
    '@sidebase/nuxt-auth',
    'nuxt-primevue',
    '@nuxt/image',
    '@pinia/nuxt',
    '@vueuse/nuxt',
  ],
  typescript: {
    shim: false
  },

  // ✅ CONFIGURACIÓN PARA SOLUCIONAR PROBLEMAS DE SSR
  vite: {
    optimizeDeps: {
      include: ['uuid', 'picomatch']
    },
    ssr: {
      noExternal: ['uuid']
    }
  },

  auth: {
    globalAppMiddleware: {
      isEnabled: true
    },
    baseURL: process.env.AUTH_ORIGIN || 'http://localhost:3000',
    // Removido 'originEnvKey' que no existe en las opciones del módulo
    // Si necesitas configurar el origin, hazlo en runtimeConfig.auth.baseURL
  },

  runtimeConfig: {
    auth: {
      secret: process.env.NUXT_AUTH_SECRET || 'dev-secret',
      baseURL: process.env.NUXT_AUTH_URL || 'http://localhost:3000/api/auth'
    },
    recaptcha: {
      secretKey: process.env.NUXT_RECAPTCHA_SECRET_KEY || '',
    },
    public: {
      recaptcha: {
        siteKey: process.env.NUXT_PUBLIC_RECAPTCHA_SITE_KEY || ''
      },
      loadTableData: process.env.NUXT_PUBLIC_LOAD_TABLE_DATA === 'true',
      showSaveConfirm: false,
      showDeleteConfirm: false
    }
  },

  // Agregar configuración de appConfig para reCAPTCHA
  appConfig: {
    recaptcha: {
      siteKey: process.env.NUXT_PUBLIC_RECAPTCHA_SITE_KEY || ''
    }
  },

  plugins: [
    { src: '~/plugins/recaptcha.ts' }, // Re-habilitado con configuración correcta
    { src: '~/plugins/api.ts' }
  ],

  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
  // Otras configuraciones de Nitro pueden ir aquí
})

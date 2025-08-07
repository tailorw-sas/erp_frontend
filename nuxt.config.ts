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

  // ✅ FIX: Configuración de auth corregida
  auth: {
    globalAppMiddleware: {
      isEnabled: true
    },
    // ✅ Usar la variable correcta que existe en K8s
    baseURL: process.env.AUTH_ORIGIN,

  },

  runtimeConfig: {
    auth: {
      secret: process.env.NUXT_AUTH_SECRET,
      baseURL: `${process.env.AUTH_ORIGIN}/api/auth`
    },
    recaptcha: {
      secretKey: process.env.NUXT_RECAPTCHA_SECRET_KEY || '',
    },

    // ✅ Variables públicas (accesibles en cliente)
    public: {
      // ✅ CRÍTICO: URL base del sitio
      siteUrl: process.env.AUTH_ORIGIN,
      authUrl: `${process.env.AUTH_ORIGIN}/api/auth`,

      recaptcha: {
        siteKey: process.env.NUXT_PUBLIC_RECAPTCHA_SITE_KEY || ''
      },
      loadTableData: process.env.NUXT_PUBLIC_LOAD_TABLE_DATA === 'true',
      showSaveConfirm: false,
      showDeleteConfirm: false
    }
  },

  // ✅ Configuración de Nitro para producción
  nitro: {
    host: process.env.NITRO_HOST || '0.0.0.0',
    port: Number.parseInt(process.env.NITRO_PORT || '3000'),
    // devProxy: {
    //   // Proxy para API
    //   '/api': {
    //     target: 'http://192.168.86.70:9902',
    //     changeOrigin: true,
    //     prependPath: true,
    //   },
    //   // Proxy específico para Swagger
    //   '/swagger': {
    //     target: 'http://192.168.86.70:9902',
    //     changeOrigin: true,
    //     pathRewrite: {
    //       '^/swagger': ''
    //     }
    //   },
    //   // Proxy para docs OpenAPI
    //   '/v3/api-docs': {
    //     target: 'http://192.168.86.70:9902',
    //     changeOrigin: true,
    //   }
    // }
  },

  appConfig: {
    recaptcha: {
      siteKey: process.env.NUXT_PUBLIC_RECAPTCHA_SITE_KEY || ''
    }
  },

  plugins: [
    { src: '~/plugins/recaptcha.ts' },
    { src: '~/plugins/api.ts' },
    { src: '~/plugins/auth-interceptor.client.ts', mode: 'client' },
    { src: '~/plugins/inactivity-auth.client.ts', mode: 'client' },
    { src: '~/plugins/inactivity-auth.client.ts', mode: 'client' }
  ],

  components: [
    {
      path: '~/components',
      pathPrefix: false,
    },
  ],
})

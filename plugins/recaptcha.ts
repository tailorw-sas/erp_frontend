import { VueReCaptcha } from 'vue-recaptcha-v3'

export default defineNuxtPlugin((nuxtApp) => {
  const appConfig = useRuntimeConfig()
  const siteKey = appConfig.public.recaptcha.siteKey

  const options = {
    siteKey,
    loaderOptions: {
      autoHideBadge: true,
      useRecaptchaNet: true,
      renderParameters: {
      }
    }
  }

  nuxtApp.vueApp.use(VueReCaptcha, options)
})

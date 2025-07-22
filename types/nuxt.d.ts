declare module '#app' {
  interface NuxtApp {
    $api: <T>(url: string, options: any) => Promise<T>
  }
}

export {}

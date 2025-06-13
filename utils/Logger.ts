class Logger {
  private static instance: Logger

  private constructor() {}

  static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger()
    }
    return Logger.instance
  }

  private isDev(): boolean {
    return process.env.NODE_ENV !== 'production'
  }

  log(...args: any[]): void {
    if (this.isDev()) console.log('%c[LOG]', 'color: #1976D2; font-weight: bold;', ...args)
  }

  info(...args: any[]): void {
    if (this.isDev()) console.info('%c[INFO]', 'color: #2E7D32; font-weight: bold;', ...args)
  }

  warn(...args: any[]): void {
    console.warn('%c[WARN]', 'color: #F9A825; font-weight: bold;', ...args)
  }

  error(...args: any[]): void {
    console.error('%c[ERROR]', 'color: #C62828; font-weight: bold;', ...args)
  }

  table(data: any): void {
    if (this.isDev()) console.table(data)
  }

  group(label: string): void {
    if (this.isDev()) console.group(label)
  }

  groupEnd(): void {
    if (this.isDev()) console.groupEnd()
  }
}

export default Logger.getInstance()
/* eslint-disable no-console */
type LogLevel = 'log' | 'info' | 'warn' | 'error' | 'none'

class Logger {
  private static logLevel: LogLevel = 'log'

  static setLevel(level: LogLevel): void {
    Logger.logLevel = level
  }

  private static shouldLog(level: LogLevel): boolean {
    const levels: LogLevel[] = ['log', 'info', 'warn', 'error', 'none']
    return levels.indexOf(level) >= levels.indexOf(Logger.logLevel)
  }

  // ✅ FIXED: Detección correcta para Nuxt 3
  private static isClient(): boolean {
    return typeof window !== 'undefined'
  }

  static log(...args: any[]): void {
    if (Logger.isClient() && Logger.shouldLog('log')) {
      console.log('%c[LOG]', 'color: #1976D2; font-weight: bold;', ...args)
    }
  }

  static info(...args: any[]): void {
    if (Logger.isClient() && Logger.shouldLog('info')) {
      console.info('%c[INFO]', 'color: #2E7D32; font-weight: bold;', ...args)
    }
  }

  static warn(...args: any[]): void {
    if (Logger.isClient() && Logger.shouldLog('warn')) {
      console.warn('%c[WARN]', 'color: #F9A825; font-weight: bold;', ...args)
    }
  }

  static error(...args: any[]): void {
    if (Logger.isClient() && Logger.shouldLog('error')) {
      console.error('%c[ERROR]', 'color: #C62828; font-weight: bold;', ...args)
    }
  }

  static table(data: any): void {
    if (Logger.isClient() && Logger.shouldLog('log')) {
      console.table(data)
    }
  }

  static group(label: string): void {
    if (Logger.isClient() && Logger.shouldLog('log')) {
      console.group(label)
    }
  }

  static groupEnd(): void {
    if (Logger.isClient() && Logger.shouldLog('log')) {
      console.groupEnd()
    }
  }
}

export default Logger

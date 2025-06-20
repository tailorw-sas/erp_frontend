/* eslint-disable no-console */
type LogLevel = 'log' | 'info' | 'warn' | 'error' | 'none'

class Logger {
  private static instance: Logger
  private logLevel: LogLevel = 'log'

  private constructor() {}

  static getInstance(): Logger {
    if (!Logger.instance) {
      Logger.instance = new Logger()
    }
    return Logger.instance
  }

  setLevel(level: LogLevel): void {
    this.logLevel = level
  }

  private shouldLog(level: LogLevel): boolean {
    const levels: LogLevel[] = ['log', 'info', 'warn', 'error', 'none']
    return levels.indexOf(level) >= levels.indexOf(this.logLevel)
  }

  log(...args: any[]): void {
    if (process.client && this.shouldLog('log')) {
      console.log('%c[LOG]', 'color: #1976D2; font-weight: bold;', ...args)
    }
  }

  info(...args: any[]): void {
    if (process.client && this.shouldLog('info')) {
      console.info('%c[INFO]', 'color: #2E7D32; font-weight: bold;', ...args)
    }
  }

  warn(...args: any[]): void {
    if (process.client && this.shouldLog('warn')) {
      console.warn('%c[WARN]', 'color: #F9A825; font-weight: bold;', ...args)
    }
  }

  error(...args: any[]): void {
    if (process.client && this.shouldLog('error')) {
      console.error('%c[ERROR]', 'color: #C62828; font-weight: bold;', ...args)
    }
  }

  table(data: any): void {
    if (process.client && this.shouldLog('log')) {
      console.table(data)
    }
  }

  group(label: string): void {
    if (process.client && this.shouldLog('log')) {
      console.group(label)
    }
  }

  groupEnd(): void {
    if (process.client && this.shouldLog('log')) {
      console.groupEnd()
    }
  }
}

export default Logger.getInstance()

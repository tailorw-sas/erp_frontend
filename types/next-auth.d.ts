import type { DefaultSession } from 'next-auth'

declare module 'next-auth' {
  interface Session extends DefaultSession {
    user: DefaultSession['user'] & {
      userId?: string
      isAdmin?: boolean
      userName?: string
      lastName?: string
    }
  }
}

import 'next-auth'
import type { DefaultSession } from 'next-auth'

declare module 'next-auth' {
  interface Session extends DefaultSession {
    user: DefaultSession['user'] & {
      userId?: string
      isAdmin?: boolean
      userName?: string
      lastName?: string
    }
    accessTokenExpires?: number
  }

  interface JWT {
    userId?: string
    access_token?: string
    refresh_token?: string
    accessTokenExpires?: number
    refreshTokenExpires?: number
    isAdmin?: boolean
    userName?: string
    lastName?: string
    lastActivity?: number
  }
}

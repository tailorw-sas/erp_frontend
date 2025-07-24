// server/api/auth/[...].ts - Fixed TypeScript errors

import { jwtDecode } from 'jwt-decode'
import CredentialsProvider from 'next-auth/providers/credentials'
import { z } from 'zod'
import { NuxtAuthHandler } from '#auth'

const runtimeConfig = useRuntimeConfig()

export default NuxtAuthHandler({
  secret: runtimeConfig.auth.secret,
  pages: {
    signIn: '/auth/login',
  },
  providers: [
    // @ts-expect-error You need to use .default here for it to work during SSR. May be fixed via Vite at some point
    CredentialsProvider.default({
      name: 'Credentials',
      credentials: {
        username: { label: 'Username', type: 'text' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials: any) {
        const loginSchema = z.object({
          username: z.string().min(1, { message: 'Username is required' }),
          password: z.string().min(1, { message: 'Password is required' }),
          tokenCaptcha: z.string().min(1, { message: 'Captcha is required' }),
        })

        const result = loginSchema.safeParse(credentials)
        if (!result.success) {
          return null
        }

        const { username, password, tokenCaptcha: _tokenCaptcha } = result.data

        try {
          const userResponse = await $fetch<{
            scope: string
            access_token: string
            expires_in: number
            refresh_expires_in: number
            refresh_token: string
            token_type: string
            session_state: string
          }>(`${process.env.VITE_APP_BASE_URL}/identity/api/auth/authenticate`, {
            method: 'POST',
            body: {
              username,
              password
            }
          })

          const { access_token, expires_in, refresh_token, refresh_expires_in } = userResponse
          const authorization = `Bearer ${access_token}`
          const userMeResponse: any = await $fetch(`${process.env.VITE_APP_BASE_URL}/settings/api/manage-employee/me`, {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': authorization
            }
          })

          const { data: userData } = userMeResponse
          const decodedToken = jwtDecode(access_token)
          const isAdmin = (decodedToken as any).realm_access?.roles?.includes('ADMIN') || false

          const user = {
            access_token,
            accessTokenExpires: Date.now() + 1000 * expires_in,
            refresh_token,
            refreshTokenExpires: Date.now() + 1000 * refresh_expires_in,
            userId: userData.userId,
            userName: userData.userName,
            email: userData.email,
            name: userData.name,
            lastName: userData.lastName,
            image: userData.image,
            isAdmin,
          }

          return user
        }
        catch (error: any) {
          console.error('Auth error', error)
          throw createError({
            statusCode: error.statusCode || 500,
            statusMessage: error.statusCode === 428 ? 'Password change required' : 'Username or password is incorrect',
          })
        }
      },
    }),
  ],
  session: {
    strategy: 'jwt'
  },
  callbacks: {
    jwt: async ({ token, user, account }): Promise<any> => { // ✅ FIXED: Explicit return type
      if (account && user) {
        // ✅ FIXED: More specific typing
        const userTyped = user as any
        return {
          ...token,
          userId: userTyped.userId ? String(userTyped.userId) : undefined,
          access_token: userTyped.access_token ? String(userTyped.access_token) : undefined,
          refresh_token: userTyped.refresh_token ? String(userTyped.refresh_token) : undefined,
          accessTokenExpires: userTyped.accessTokenExpires ? Number(userTyped.accessTokenExpires) : undefined,
          refreshTokenExpires: userTyped.refreshTokenExpires ? Number(userTyped.refreshTokenExpires) : undefined,
          isAdmin: Boolean(userTyped.isAdmin),
          userName: userTyped.userName ? String(userTyped.userName) : undefined,
          lastName: userTyped.lastName ? String(userTyped.lastName) : undefined,
          lastActivity: Date.now(),
        }
      }

      // Check refresh token expiration
      const refreshExpires = token.refreshTokenExpires
      if (refreshExpires && typeof refreshExpires === 'number' && Date.now() > refreshExpires) {
        console.error('Refresh token expired')
        return null
      }

      // For strict inactivity policy, do NOT auto-refresh
      // Let client handle expiration
      const accessExpires = token.accessTokenExpires
      if (accessExpires && typeof accessExpires === 'number' && Date.now() > accessExpires) {
        console.info('Access token expired, client should handle logout')
        return null
      }

      return token
    },
    session: async ({ session, token }) => {
      if (token) {
        session.user = {
          ...session.user,
          userId: token.userId ? String(token.userId) : undefined, // ✅ FIXED: Explicit string conversion
          isAdmin: Boolean(token.isAdmin), // ✅ FIXED: Explicit boolean conversion
          userName: token.userName ? String(token.userName) : undefined,
          lastName: token.lastName ? String(token.lastName) : undefined,
        }
        session.accessTokenExpires = token.accessTokenExpires ? Number(token.accessTokenExpires) : undefined // ✅ FIXED: Explicit number conversion
      }

      return session
    },
  }
})

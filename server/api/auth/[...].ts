import { jwtDecode } from 'jwt-decode'
import CredentialsProvider from 'next-auth/providers/credentials'
import { z } from 'zod'
import { NuxtAuthHandler } from '#auth'
// import { helpers } from '~/utils/helpers' // Descomentado solo si se usa

/**
 * Takes a token, and returns a new token with updated
 * `accessToken` and `accessTokenExpires`. If an error occurs,
 * returns the old token and an error property
 */
async function refreshAccessToken(refreshToken: any) {
  try {
    const refreshedTokens = await $fetch<{
      data: {
        'scope': string
        'access_token': string
        'expires_in': number
        'refresh_expires_in': number
        'refresh_token': string
        'token_type': string
        'not-before-policy': number
        'session_state': string
      }
    } | null>(`${process.env.VITE_APP_BASE_URL}/identity/api/auth/refresh-token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: {
        refreshToken: refreshToken.refresh_token,
      },
    })
    if (!refreshedTokens || !refreshedTokens.data) {
      throw refreshedTokens
    }

    // Cambiado console.debug por console.info (permitido por ESLint)
    console.info('Refreshed tokens', refreshToken.refresh_token !== refreshedTokens.data.refresh_token)

    return {
      ...refreshToken,
      access_token: refreshedTokens.data.access_token,
      accessTokenExpires: Date.now() + 1000 * refreshedTokens.data.expires_in,
      refresh_token: refreshedTokens.data.refresh_token,
      refreshTokenExpires: Date.now() + 1000 * refreshedTokens.data.refresh_expires_in,
    }
  }
  catch (error) {
    // Agregado console.error para usar la variable error
    console.error('Error refreshing access token:', error)
    return {
      ...refreshToken,
      error: 'RefreshAccessTokenError',
    }
  }
}

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

        // Prefijo con underscore para indicar que no se usa actualmente
        const { username, password, tokenCaptcha: _tokenCaptcha } = result.data

        // const appRuntimeConfig = useRuntimeConfig()
        // const { recaptcha: { secretKey } } = appRuntimeConfig
        // const aux = helpers()
        // const isHuman = await aux.verifyRecaptchaToken(_tokenCaptcha, secretKey)

        // if (!isHuman) {
        //   console.error('Captcha verification failed')
        //   throw createError({
        //     statusCode: 400,
        //     statusMessage: 'Something went wrong, please try again',
        //   })
        // }

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
    jwt: async ({ token, user, account }) => {
      if (account && user) {
        return {
          ...token,
          userId: (user as any).userId,
          access_token: (user as any).access_token,
          refresh_token: (user as any).refresh_token,
          accessTokenExpires: (user as any).accessTokenExpires,
          refreshTokenExpires: (user as any).refreshTokenExpires,
          isAdmin: (user as any).isAdmin,
        }
      }

      if (token.refreshTokenExpires && Date.now() > (token as any).refreshTokenExpires) {
        console.error('Refresh token expired')
        return null
      }

      if (token.accessTokenExpires && Date.now() > (token as any).accessTokenExpires) {
        return refreshAccessToken(token)
      }

      return token
    },
    session: async ({ session, token }) => {
      session.user = {
        ...session.user,
        ...{
          userId: (token as any).userId,
          isAdmin: (token as any).isAdmin
        }
      }
      return session
    },
  }
})

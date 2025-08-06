import { getToken } from '#auth'

// MODIFICADO para manejar mejor errores

export default defineEventHandler(async (event) => {
  const token = await getToken({ event, secret: useRuntimeConfig().auth.secret })

  // ✅ NUEVO: Verificar si tenemos token válido
  if (!token || !token.access_token) {
    throw createError({
      statusCode: 401,
      statusMessage: 'No valid token found'
    })
  }

  // ✅ NUEVO: Verificar si token expiró en el lado servidor también
  if (token.accessTokenExpires && typeof token.accessTokenExpires === 'number' && Date.now() > token.accessTokenExpires) {
    throw createError({
      statusCode: 401,
      statusMessage: 'Token expired'
    })
  }

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token.access_token}`,
  }

  try {
    const result = await $fetch(`${process.env.VITE_APP_BASE_URL}/settings/api/manage-employee/me`, {
      headers: defaultHeaders
    })

    return result
  }
  catch (error: any) {
    // ✅ NUEVO: Manejar específicamente 401 del backend
    if (error.statusCode === 401) {
      throw createError({
        statusCode: 401,
        statusMessage: 'Backend authentication failed'
      })
    }

    // Re-lanzar otros errores
    throw error
  }
})

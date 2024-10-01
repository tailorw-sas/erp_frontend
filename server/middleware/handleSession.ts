import { defineEventHandler, getRequestHeader, readBody, sendRedirect } from 'h3'

// Handler para manejar el redireccionamiento cuando es cardnet y viene la propiedad SESSION en el body
// Se hace esto porque dicha propiedad interfiere con el middleware de auth por lo que se pasa la propiedad a la ruta como routeParam
export default defineEventHandler(async (event) => {
  const contentType = getRequestHeader(event, 'content-type')
  const isFormUrlEncoded = contentType && contentType.includes('application/x-www-form-urlencoded')
  const url = event.node.req.url ?? '' // Obtener la URL de la solicitud
  // Verifica si el contenido es application/x-www-form-urlencoded
  if (isFormUrlEncoded && url.includes('transaction-result')) {
    const body = await readBody(event) // Captura el body del formData

    if (body.SESSION) {
      // Guardamos la session para usarla en la redirección
      const session = body.SESSION

      // Eliminamos la session del cuerpo del evento (aunque no lo usamos directamente)
      delete body.SESSION

      // Realizamos la redirección sin volver a enviar la session en el cuerpo
      return sendRedirect(event, `/vcc-management/transaction-result?status=success&session=${session}`)
    }
  }
})

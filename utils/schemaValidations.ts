import { z } from 'zod'

interface FieldProperty {
  id: string
  name: string
  status: string
}
function isActive(value: FieldProperty | null) {
  if (!value) {
    return true
  }
  else {
    return value.status === 'ACTIVE'
  }
}

const stringSchemaMerchant = z.string().refine(value => typeof value !== 'string', { message: 'Select a merchant' })
const objectSchemaMerchant = z.object({
  id: z.string(),
  name: z.string(),
  status: z.enum(['ACTIVE', 'INACTIVE'], { message: 'The status must be either ACTIVE or INACTIVE' })
}).nullable()
  .refine(value => value && value.id && value.name, { message: 'The merchant field is required' })
  .refine(value => isActive(value), {
    message: 'This merchant is not active'
  })

export const merchantSchema = z.union([stringSchemaMerchant, objectSchemaMerchant])

const objectSchemaCreditCardType = z.object({
  id: z.string(),
  name: z.string(),
  status: z.enum(['ACTIVE', 'INACTIVE'], { message: 'The status must be either ACTIVE or INACTIVE' })
}).nullable()
  .refine(value => value && value.id && value.name, { message: 'The credit card type field is required' })
  .refine(value => isActive(value), {
    message: 'This credit card type is not active'
  })

export const creditCardTypeSchema = objectSchemaCreditCardType

export function validateEntityStatus(fieldName: string) {
  return z.object({
    id: z.string(),
    name: z.string(),
    status: z.enum(['ACTIVE', 'INACTIVE'], { message: `The ${fieldName} must be either ACTIVE or INACTIVE` })
  }).nullable()
    .refine(value => value && value.id && value.name, { message: `The ${fieldName} field is required` })
    .refine(value => isActive(value), {
      message: `This ${fieldName} is not active`
    })
}

export function validateEntityForAgency(fieldName: string) {
  return z.object({
    id: z.string(),
    name: z.string(),
    status: z.enum(['ACTIVE', 'INACTIVE'], { message: `The ${fieldName} must be either ACTIVE or INACTIVE` }),
    client: z.object({
      id: z.string(),
      name: z.string(),
      status: z.enum(['ACTIVE', 'INACTIVE'], { message: `The client must be either ACTIVE or INACTIVE` })
    })
  }).nullable()
    .refine(value => value && value.id && value.name, { message: `The ${fieldName} field is required` })
    .refine(value => value?.status === 'ACTIVE', { message: `This ${fieldName} is not active` })
    .refine(value => value?.client?.status === 'ACTIVE', { message: `The client associated with this ${fieldName} is not active` })
}

export function validateEntityStatusForNotRequiredField(fieldName: string) {
  return z.object({
    id: z.string(),
    name: z.string(),
    status: z.enum(['ACTIVE', 'INACTIVE'], { message: `The ${fieldName} must be either ACTIVE or INACTIVE` })
  }).nullable()
    .refine(value => value === null || value.status === 'ACTIVE', {
      message: `This ${fieldName} is not active`,
    })
}

export function validateEntitiesForSelectMultiple(fieldName: string) {
  const entitySchema = z.object({
    id: z.string(),
    name: z.string(),
    status: z.enum(['ACTIVE', 'INACTIVE'], { message: `The ${fieldName} must be either ACTIVE or INACTIVE` })
  })

  return z.array(
    entitySchema.nullable()
      .refine(value => value && value.id && value.name, { message: `The ${fieldName} field is required` })
      .refine(value => value === null || isActive(value), {
        message: `This ${fieldName} has inactive elements`
      })
  )
}

// Esta funcion es para validar el campo File que usamos en los formularios,
// el cual puede ser un string(Para que acepte la URL cuando hacemos el getBYId que ya no viene un file sino la referencia al archivo)
// o un array
export function validateFiles(size: number = 10000, acceptTypes: string[] = ['image/jpeg', 'image/png', 'application/pdf']) {
  const fileSchema = z.object({
    name: z.string().min(1, 'The file must have a name'),
    size: z.number().max(size * 1024 * 1024, `The file must be less than ${size} MB`), // Tamaño máximo 5 MB
    type: z.string().refine(value => acceptTypes.includes(value), { message: 'The file type is not allowed' }),
  })
  const stringSchema = z.string().trim().refine(value => typeof value === 'string' && value !== '', 'The files field is required')
  const dataSchema = z.object({
    files: z
      .array(fileSchema)
      .nonempty('The files array must contain at least one file'), // El array no puede estar vacío
  })
  return z.union([stringSchema, dataSchema])
}

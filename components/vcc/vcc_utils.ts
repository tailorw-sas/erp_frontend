export function formatCardNumber(cardNumber: string): string {
  if (cardNumber.length !== 16 || !cardNumber.includes('*')) {
    return cardNumber
  }

  // Mantiene los primeros 6 caracteres
  const prefix = cardNumber.slice(0, 6)
  // Cambia los caracteres intermedios (6 caracteres) a '_'
  const middlePart = '_'.repeat(6)
  // Mantiene los últimos 4 caracteres intactos
  const suffix = cardNumber.slice(12)

  return prefix + middlePart + suffix
}

export interface IData {
  [key: string]: any
}

export interface IPatient {
  id?: string
  identification: string
  name: string
  lastName: string
  gender: 'MALE' | 'FEMALE' | 'UNDEFINED'
  weight: number
  height: number
  hasDisability: boolean
  isPregnant: boolean
  photo: string
}

export interface IPerson {
  id: string
  identification: string
}

export interface IRole {
  id?: string
  name: string
  description: string
}

export interface IBusiness {
  id?: string
  name: string
  description: string
  latitude: string
  longitude: string
  geographicLocation: string
  logo: string
}

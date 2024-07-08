import { GenericService } from '~/services/generic-services'

export default async function getUrlByImage(file: File) {
  if (file) {
    const response: any = await GenericService.uploadFile('cloudbridges', 'files', file)
    return response.data.url
  }
}

// export interface ITreeNode {
//   key: string
//   data: {
//     name: string
//     type: string
//   }
//   children?: ITreeNode[]
// }

export interface ITreeNode {
  key: string
  label: string
  data: string
  icon: string
  children?: ITreeNode[]
}

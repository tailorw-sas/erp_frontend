
import { jsPDF } from 'jspdf'

/*
PrintToPdfFunction

content param must be html string, main father component must have a fixed width or the 
rendered html will have incorrect sizing

For a template example chech the printInvoice file in /utils/templates
*/


export interface PrintToPdfPayload {
  content: string
  filename: string
  docSize: 'a1' | 'a2' | 'a3' | 'a4',
  docOrientation: 'p' | 'l'  // 'portrait' 'landscape'

}

export async function printToPdf({ content, filename, docOrientation, docSize }: PrintToPdfPayload) {


  try {


    const elementHTML = document.createElement('div')
    elementHTML.innerHTML = content

    const doc = new jsPDF(docOrientation, 'pt', docSize)

    doc.html(elementHTML, {
      callback(doc) {
        
        doc.save(`${filename}.pdf`)
      },
      x: 10,
      y: 10,
      autoPaging: 'text'

    })


  }
  catch (error) {
    console.log(error)
  }

}

import { Typography } from '@material-ui/core'
import { useEffect, useRef, useState } from 'react'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import theme from '../../components/utils/theme'

// this simply doesn't support exporting to draft-js, and it'd be hard to make it
// if the user wants to edit a card, ig a warning will be shown and it will be imported to
// draft-js as plain text
export default function ImportCards() {
  const { width } = useWindowSize()
  const pasteInputRef = useRef()
  const [ pData, setPData ] = useState('')
  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')
  const [ bodyHtml, setBodyHtml ] = useState('')

  useEffect(() => {
    const processPaste = (elem, pastedData) => {
      setPData(pastedData)
      elem.current.focus()
    }
    const waitForPastedData = (elem, savedContent) => {
      if (elem.childNodes && elem.childNodes.length > 0) {
        const pastedData = elem.innerHTML
        elem.current.innerHTML = ''
        elem.current.appendChild(savedContent)

        processPaste(elem, pastedData)
      } else {
        setTimeout(() => {
          waitForPastedData(elem, savedContent)
        }, 20)
      }
    }

    const handlePaste = e => {
      if (e && e.clipboardData && e.clipboardData.types && e.clipboardData.getData) {
        const { types } = e.clipboardData
        if (((types instanceof DOMStringList) && types.contains('text/html')) || (types.indexOf && types.indexOf('text/html') !== -1)) {
          const pastedData = e.clipboardData.getData('text/html')
          processPaste(pasteInputRef, pastedData)
          e.stopPropagation()
          e.preventDefault()
          return false
        }
      }

      const savedContent = document.createDocumentFragment()
      while (pasteInputRef.current.childNodes.length > 0) {
        savedContent.appendChild(pasteInputRef.current.childNodes[0])
      }

      waitForPastedData(pasteInputRef, savedContent)
      return true
    }

    pasteInputRef.current.addEventListener('paste', handlePaste)
    return () => pasteInputRef.current?.removeEventListener('paste', handlePaste)
  }, [ ])

  const handleKeyDown = e => {
    e.preventDefault()
    console.log('k')
    if (e.key === 't') {
      setTag(window.getSelection().toString())
    } else if (e.key === 'c') {
      setCite(window.getSelection().toString())
    } else if (e.key === 'i') {
      setCiteInformation(window.getSelection().toString())
    } else if (e.key === 'b') {
      // selection range
      const range = window.getSelection().getRangeAt(0)
      // plain text of selected range (if you want it w/o html)
      const text = window.getSelection()
      // document fragment with html for selection
      const fragment = range.cloneContents()
      // make new element, insert document fragment, then get innerHTML!
      const div = document.createElement('div')
      div.appendChild(fragment.cloneNode(true))
      // your document fragment to a string (w/ html)! (yay!)
      const html = div.innerHTML
      setBodyHtml(html)
    }
  }

  return (
    <div
      style={{
        width: '100%',
        backgroundColor: theme.palette.lightBlue.main,
        minHeight: '100%',
        overflow: 'auto'
      }}
    >
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: width >= theme.breakpoints.values.lg ? '65%' : '80%', margin: '7.5vh auto' }}>
          <div>
            <Typography
              style={{
                color: theme.palette.darkGrey.main,
                textTransform: 'uppercase',
                fontSize: 11,
                marginTop: 19,
                letterSpacing: 0.5
              }}
            >
              Import
            </Typography>
            <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
              Import cards
            </BlackText>
            <div
              style={{
                width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
              }}
            />
          </div>
          <div>
            <input ref={pasteInputRef} type='text' placeholder='Paste here' />
            <div
              // eslint-disable-next-line jsx-a11y/no-noninteractive-tabindex
              tabIndex={0}
              dangerouslySetInnerHTML={{ __html: pData }}
              style={{ outline: 'none' }}
              onKeyDown={handleKeyDown}
            />
          </div>
          <div>
            <Typography>
              Tag:
              {` ${tag}`}
            </Typography>
            <Typography>
              Cite:
              {` ${cite}`}
            </Typography>
            <Typography>
              Cite information:
              {` ${citeInformation}`}
            </Typography>
            <Typography>
              BodyHtml:
              {` ${bodyHtml}`}
            </Typography>
          </div>
        </div>
      </div>
    </div>
  )
}

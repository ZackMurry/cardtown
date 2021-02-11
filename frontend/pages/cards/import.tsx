import { Button, TextField, Typography } from '@material-ui/core'
import Cookie from 'js-cookie'
import {
  FC, FormEvent, useEffect, useMemo, useRef, useState
} from 'react'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import ErrorAlert from '../../components/utils/ErrorAlert'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import SuccessAlert from '../../components/utils/SuccessAlert'
import theme from '../../components/utils/theme'
import styles from '../../styles/ImportCards.module.css'

const IMPORT_SUCCESS_TEXT = 'Card successfully imported'

// this simply doesn't support exporting to draft-js, and it'd be hard to make it
// if the user wants to edit a card, ig a warning will be shown and it will be imported to
// draft-js as plain text
const ImportCards: FC = () => {
  const { width } = useWindowSize(1920, 1080)
  const pasteInputRef = useRef(null)
  const [ pasteData, setPasteData ] = useState('')
  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')
  const [ bodyHtml, setBodyHtml ] = useState('')

  const [ feedbackText, setFeedbackText ] = useState('')

  const jwt = useMemo(() => Cookie.get('jwt'), [ ])

  useEffect(() => {
    const processPaste = (elem, pastedData) => {
      setPasteData(pastedData)
      elem.current.focus()
    }
    const waitForPastedData = (elem, savedContent) => {
      if (elem.childNodes && elem.childNodes.length > 0) {
        const pastedData = elem.innerHTML
        // eslint-disable-next-line no-param-reassign
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
      while (pasteInputRef?.current?.childNodes?.length) {
        savedContent.appendChild(pasteInputRef.current.childNodes[0])
      }

      waitForPastedData(pasteInputRef, savedContent)
      return true
    }

    // zero shame: the pasting mechanism is straight from stackoverflow
    pasteInputRef.current.addEventListener('paste', handlePaste)
    return () => pasteInputRef.current?.removeEventListener('paste', handlePaste)
  }, [ ])

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    e.preventDefault()
    if (e.key === 't') {
      setTag(window.getSelection().toString())
    } else if (e.key === 'c') {
      setCite(window.getSelection().toString())
    } else if (e.key === 'i') {
      setCiteInformation(window.getSelection().toString())
    } else if (e.key === 'b') {
      const range = window.getSelection().getRangeAt(0)
      const fragment = range.cloneContents()
      const div = document.createElement('div')
      div.appendChild(fragment.cloneNode(true))
      const html = div.innerHTML
      setBodyHtml(html)
    }
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (tag === '' || cite === '' || bodyHtml === '') {
      setFeedbackText('Your card must contain a tag, a cite, and a body.')
      return
    }

    const bodyText = new DOMParser()
      .parseFromString(bodyHtml, 'text/html')
      .documentElement.textContent

    const response = await fetch('/api/v1/cards', {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tag,
        cite,
        citeInformation,
        bodyHtml,
        bodyDraft: 'IMPORTED CARD -- NO DRAFT BODY',
        bodyText
      })
    })
    if (response.ok) {
      setTag('')
      setCite('')
      setCiteInformation('')
      setBodyHtml('')
      setPasteData('')
      setFeedbackText(IMPORT_SUCCESS_TEXT)
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
      <div style={{ paddingLeft: 38, paddingRight: 38 }}>
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
            <Typography color='textSecondary' id='tagDescription' style={{ fontSize: 15, margin: '6px 0' }}>
              First, paste the card into the box below. If you're importing it from a program that supports styled copying and pasting,
              the card will retain its formatting. With your mouse, select the tag of the card and then press 't' on your keyboard.
              This will automatically set the selected text to the tag. You can do the same for the rest of the fields using 'c' for cite,
              'i' for cite information, and 'b' for body. You'll see their values in the input boxes below your card.
              You can edit the values of tag, cite, and cite information with the input boxes.
            </Typography>
          </div>
          <div>
            <input
              ref={pasteInputRef}
              type='text'
              placeholder='Paste here'
              className={styles['paste-card-input']}
            />
            <div
              // eslint-disable-next-line jsx-a11y/no-noninteractive-tabindex
              tabIndex={0}
              // eslint-disable-next-line react/no-danger
              dangerouslySetInnerHTML={{ __html: pasteData }}
              style={{ outline: 'none' }}
              onKeyDown={handleKeyDown}
            />
          </div>
          <form onSubmit={handleSubmit}>
            <Button style={{ margin: '1.5vh 0' }} type='submit' variant='contained' color='primary'>
              Import
            </Button>
            {/* tag */}
            <div>
              <label htmlFor='tag' id='tagLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500, marginBottom: 5 }}>
                  Tag
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <TextField
                id='tag'
                variant='outlined'
                value={tag}
                onChange={e => setTag(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                multiline
                rows={2}
                rowsMax={5}
                InputProps={{
                  inputProps: {
                    name: 'tag',
                    'aria-labelledby': 'tagLabel',
                    'aria-describedby': 'tagDescription'
                  }
                }}
              />
            </div>

            {/* cite */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='cite' id='citeLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500, marginBottom: 5 }}>
                  Cite
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <TextField
                variant='outlined'
                value={cite}
                onChange={e => setCite(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                InputProps={{
                  inputProps: {
                    name: 'cite',
                    'aria-labelledby': 'citeLabel',
                    'aria-describedby': 'citeDescription'
                  }
                }}
              />
            </div>

            {/* cite information */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='citeInfo' id='citeInfoLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500, marginBottom: 5 }}>
                  Additional cite information
                </BlackText>
              </label>
              <TextField
                variant='outlined'
                value={citeInformation}
                onChange={e => setCiteInformation(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                multiline
                rowsMax={5}
                rows={3}
                InputProps={{
                  inputProps: {
                    name: 'citeInfo',
                    'aria-labelledby': 'citeInfoLabel',
                    'aria-describedby': 'citeInfoDescription'
                  }
                }}
              />
            </div>

            {/* body */}
            <div style={{ marginTop: 25 }}>
              <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                Body
                <span style={{ fontWeight: 300 }}>
                  *
                </span>
              </BlackText>
              {/* eslint-disable-next-line react/no-danger */}
              <div dangerouslySetInnerHTML={{ __html: bodyHtml }} />
            </div>
          </form>
          {
            feedbackText && (
              feedbackText === IMPORT_SUCCESS_TEXT
                ? <SuccessAlert text={feedbackText} onClose={() => setFeedbackText('')} />
                : <ErrorAlert text={feedbackText} onClose={() => setFeedbackText('')} />
            )
          }
        </div>
      </div>
    </div>
  )
}

export default ImportCards

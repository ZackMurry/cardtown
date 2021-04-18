import { GetServerSideProps } from 'next'
import { FC, FormEvent, useContext, useEffect, useRef, useState } from 'react'
import redirectToLogin from 'lib/redirectToLogin'
import SuccessAlert from 'components/utils/SuccessAlert'
import theme from 'lib/theme'
import styles from 'styles/ImportCards.module.css'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import DashboardPage from 'components/dash/DashboardPage'
import { Box, Text, Button, Input, useColorModeValue, Textarea, Heading } from '@chakra-ui/react'
import chakraTheme from 'lib/chakraTheme'
import PrimaryButton from 'components/utils/PrimaryButton'

// this simply doesn't support exporting to draft-js, and it'd be hard to make it
// if the user wants to edit a card, ig a warning will be shown and it will be imported to
// draft-js as plain text
const ImportCards: FC = () => {
  const pasteInputRef = useRef(null)
  const [pasteData, setPasteData] = useState('')
  const [tag, setTag] = useState('')
  const [cite, setCite] = useState('')
  const [citeInformation, setCiteInformation] = useState('')
  const [bodyHtml, setBodyHtml] = useState('')

  const [successText, setSuccessText] = useState('')
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const bodyBgColor = useColorModeValue('offWhite', 'grayBorder')

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
        if (
          (types instanceof DOMStringList && types.contains('text/html')) ||
          (types.indexOf && types.indexOf('text/html') !== -1)
        ) {
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
  }, [])

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
      setErrorMessage('Your card must contain a tag, a cite, and a body.')
      return
    }

    const bodyText = new DOMParser().parseFromString(bodyHtml, 'text/html').documentElement.textContent

    if (bodyText.length > 15000 || bodyHtml.length > 100000) {
      setErrorMessage('Your card is too long!')
      return
    }

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
      setSuccessText('Card successfully imported')
    } else {
      setErrorMessage(`An unknown error occured during your request. Status code: ${response.status}`)
    }
  }

  return (
    <DashboardPage>
      <Box w='50%' m='25px auto' mb='100px'>
        <div>
          <Text fontSize='24px' fontWeight='bold'>
            Import cards
          </Text>
          <div
            style={{
              width: '100%',
              margin: '2vh 0',
              height: 1,
              backgroundColor: theme.palette.lightGrey.main
            }}
          />
        </div>
        <div>
          <Text color='lightBlue' id='tagDescription' fontSize='14px' m='6px 0'>
            First, paste the card into the box below. If you're importing it from a program that supports styled copying and
            pasting, the card will retain its formatting. With your mouse, select the tag of the card and then press 't' on
            your keyboard. This will automatically set the selected text to the tag. You can do the same for the rest of the
            fields using 'c' for cite, 'i' for cite information, and 'b' for body. You'll see their values in the input boxes
            below your card. You can edit the values of tag, cite, and cite information with the input boxes.
          </Text>
        </div>
        <div>
          <input
            ref={pasteInputRef}
            type='text'
            placeholder='Paste here'
            className={styles['paste-card-input']}
            style={{ borderColor: chakraTheme.colors[borderColor], background: 'inherit' }}
          />
          <div
            // eslint-disable-next-line jsx-a11y/no-noninteractive-tabindex
            tabIndex={0}
            // eslint-disable-next-line react/no-danger
            dangerouslySetInnerHTML={{ __html: pasteData }}
            style={{
              outline: 'none',
              backgroundColor: chakraTheme.colors[bodyBgColor],
              padding: 5,
              borderRadius: 3,
              marginTop: 10
            }}
            onKeyDown={handleKeyDown}
          />
        </div>
        <form onSubmit={handleSubmit}>
          <PrimaryButton type='submit' m='1.5vh 0'>
            Import
          </PrimaryButton>
          {/* tag */}
          <div>
            <label htmlFor='tag' id='tagLabel'>
              <Heading as='h3' fontSize='18px' fontWeight='medium' mb='5px'>
                Tag
                <span style={{ fontWeight: 300 }}>*</span>
              </Heading>
            </label>
            <Textarea
              type='text'
              id='tag'
              value={tag}
              onChange={e => setTag(e.target.value)}
              w='100%'
              borderColor={borderColor}
              focusBorderColor='cardtownBlue'
              name='tag'
            />
          </div>

          {/* cite */}
          <div style={{ marginTop: 25 }}>
            <label htmlFor='cite' id='citeLabel'>
              <Heading as='h3' fontSize='18px' fontWeight='medium' mb='5px'>
                Cite
                <span style={{ fontWeight: 300 }}>*</span>
              </Heading>
            </label>
            <Input
              value={cite}
              onChange={e => setCite(e.target.value)}
              w='100%'
              borderColor={borderColor}
              focusBorderColor='cardtownBlue'
              name='cite'
            />
          </div>

          {/* cite information */}
          <div style={{ marginTop: 25 }}>
            <label htmlFor='citeInfo' id='citeInfoLabel'>
              <Heading as='h3' fontSize='18px' fontWeight='medium' mb='5px'>
                Additional cite information
              </Heading>
            </label>
            <Textarea
              value={citeInformation}
              onChange={e => setCiteInformation(e.target.value)}
              w='100%'
              borderColor={borderColor}
              focusBorderColor='cardtownBlue'
            />
          </div>

          {/* body */}
          <div style={{ marginTop: 25 }}>
            <Heading as='h3' fontSize='18px' fontWeight='medium'>
              Body
              <span style={{ fontWeight: 300 }}>*</span>
            </Heading>
            <div
              /* eslint-disable-next-line react/no-danger */
              dangerouslySetInnerHTML={{ __html: bodyHtml }}
              style={{
                outline: 'none',
                backgroundColor: chakraTheme.colors[bodyBgColor],
                padding: 5,
                borderRadius: 3,
                marginTop: 10
              }}
            />
          </div>
        </form>
      </Box>
      {successText && <SuccessAlert text={successText} onClose={() => setSuccessText('')} />}
    </DashboardPage>
  )
}

export default ImportCards

export const getServerSideProps: GetServerSideProps = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/cards/import')
  }
  return {
    props: {}
  }
}

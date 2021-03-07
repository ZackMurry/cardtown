import { Typography } from '@material-ui/core'
import {
  Button, Heading, Text, Input, Textarea
} from '@chakra-ui/react'
import { convertToRaw } from 'draft-js'
import { stateToHTML } from 'draft-js-export-html'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import {
  FC, FormEvent, useMemo, useState
} from 'react'
import mapStyleToReadable from '../../components/cards/mapStyleToReadable'
import CardBodyEditor from '../../components/cards/CardBodyEditor'
import NewCardFormattingPopover from '../../components/cards/NewCardFormattingPopover'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import theme from '../../components/utils/theme'
import initializeDraftContentState from '../../components/cards/initializeDraftEditorState'
import draftExportHtmlOptions from '../../components/cards/draftExportHtmlOptions'
import ErrorAlert from '../../components/utils/ErrorAlert'

const NewCard: FC = () => {
  const { width } = useWindowSize(1920, 1080)

  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')
  const [ bodyState, setBodyState ] = useState(initializeDraftContentState)
  const [ errorText, setErrorText ] = useState('')

  const jwt = useMemo(() => Cookie.get('jwt'), [])
  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const content = bodyState.getCurrentContent()

    const bodyDraft = convertToRaw(content)
    const bodyHtml = stateToHTML(content, draftExportHtmlOptions)
    const bodyText = content.getPlainText('\u0001')
    console.log(`htmll: ${bodyHtml.length} draftl: ${JSON.stringify(bodyDraft).length} textl: ${bodyText.length}`)

    if (tag.length < 1) {
      setErrorText('Your card must have a tag')
      return
    }
    if (tag.length > 256) {
      setErrorText('Your card\'s tag cannot be longer than 256 characters')
      return
    }
    if (cite.length === 0) {
      setErrorText('Your card must have a cite')
      return
    }
    if (cite.length > 128) {
      setErrorText('Your card\'s cite cannot be longer than 128 characters')
      return
    }
    if (citeInformation.length > 2048) {
      setErrorText('Your card\'s cite information cannot be longer than 2048 characters')
      return
    }
    if (bodyHtml.length > 100000 || bodyText.length > 50000) {
      setErrorText('Your card\'s body is too long!')
      return
    }

    const response = await fetch('/api/v1/cards', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` },
      body: JSON.stringify({
        tag,
        cite,
        citeInformation,
        bodyHtml,
        bodyDraft: JSON.stringify(bodyDraft),
        bodyText
      })
    })
    if (response.ok) {
      const newCardId = await response.text()
      router.push(`/cards/id/${encodeURIComponent(newCardId)}`)
    } else if (response.status === 500) {
      setErrorText('A server error occurred during your request. Please try again')
    } else {
      setErrorText('An unknown error occurred during your request. Please try again')
    }
  }

  const currentInlineStyles = []
  // eslint-disable-next-line no-restricted-syntax
  for (const s of bodyState.getCurrentInlineStyle().toArray()) {
    // don't show default font size
    if (s !== 'FONT_SIZE_11') {
      currentInlineStyles.push(' ' + mapStyleToReadable(s))
    }
  }

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />

      <div style={{ paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: width >= theme.breakpoints.values.lg ? '50%' : '80%', margin: '0 auto', padding: '6vh 0' }}>
          <div>
            <Text
              color='darkGray'
              textTransform='uppercase'
              fontSize='11'
              marginTop={19}
              letterSpacing={0.5}
            >
              New card
            </Text>
            <Heading as='h2' fontSize={24} fontWeight='bold' paddingTop={1}>
              Create a new card
            </Heading>
            <div
              style={{
                width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
              }}
            />
          </div>
          <form onSubmit={handleSubmit}>

            {/* tag */}
            <div>
              <label htmlFor='tag' id='tagLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Tag
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <Typography color='textSecondary' id='tagDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put a quick summary of what this card says.
              </Typography>
              <Textarea
                id='tag'
                value={tag}
                onChange={e => setTag(e.target.value)}
                rows={2}
                resize='none'
                focusBorderColor='blue.400'
                aria-labelledby='tagLabel'
                aria-describedby='tagDescription'
              />
            </div>

            {/* cite */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='cite' id='citeLabel'>
                <Heading as='h3' fontSize={18} fontWeight={500}>
                  Cite
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </Heading>
              </label>
              <Text color='lightBlue' id='citeDescription' fontSize={14} margin='6px 0'>
                Put the last name of the author and the year it was written. You can also put more information. Example: Miller 18.
              </Text>
              <Input
                value={cite}
                onChange={e => setCite(e.target.value)}
                focusBorderColor='blue.400'
                aria-labelledby='citeLabel'
                aria-describedby='citeDescription'
              />
            </div>

            {/* cite information */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='citeInfo' id='citeInfoLabel'>
                <Heading as='h3' fontSize={18} fontWeight={500}>
                  Additional cite information
                </Heading>
              </label>
              <Text color='lightBlue' id='citeInfoDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put some more information about the source of this card, like the author’s credentials and a link to the place you found it.
              </Text>
              <Textarea
                value={citeInformation}
                onChange={e => setCiteInformation(e.target.value)}
                focusBorderColor='blue.400'
                aria-labelledby='citeInfoLabel'
                aria-describedby='citeInfoDescription'
                resize='none'
                rows={2}
              />
            </div>

            {/* body */}
            <div style={{ marginTop: 20 }}>
              <Heading as='h3' fontSize={18} fontWeight={500}>
                Body
                <span style={{ fontWeight: 300 }}>
                  *
                </span>
              </Heading>
              <NewCardFormattingPopover />
              <CardBodyEditor
                editorState={bodyState}
                setEditorState={setBodyState}
                style={{ padding: 10 }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Text color='lightBlue' fontSize={11}>
                  *required field
                </Text>
                <Text color='lightBlue' fontSize={width >= theme.breakpoints.values.md ? 15 : 11}>
                  {
                    currentInlineStyles.toString()
                  }
                </Text>
              </div>
            </div>
            <div style={{ marginTop: 10, marginBottom: -5 }}>
              <Button colorScheme='blue' variant='solid' type='submit'>
                Finish
              </Button>
            </div>
          </form>
        </div>
      </div>
      {
        errorText && <ErrorAlert text={errorText} onClose={() => setErrorText('')} />
      }
    </div>
  )
}

export default NewCard

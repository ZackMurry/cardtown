import { Heading, Text, Input, Textarea, Box } from '@chakra-ui/react'
import { convertToRaw } from 'draft-js'
import { stateToHTML } from 'draft-js-export-html'
import { useRouter } from 'next/router'
import { FC, FormEvent, useContext, useState } from 'react'
import { GetServerSideProps } from 'next'
import mapStyleToReadable from 'components/cards/mapStyleToReadable'
import CardBodyEditor from 'components/cards/CardBodyEditor'
import NewCardFormattingPopover from 'components/cards/NewCardFormattingPopover'
import useWindowSize from 'lib/hooks/useWindowSize'
import theme from 'lib/theme'
import initializeDraftContentState from 'components/cards/initializeDraftEditorState'
import draftExportHtmlOptions from 'components/cards/draftExportHtmlOptions'
import redirectToLogin from 'lib/redirectToLogin'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'
import PrimaryButton from 'components/utils/PrimaryButton'

const NewCard: FC = () => {
  const { width } = useWindowSize(1920, 1080)

  const [tag, setTag] = useState('')
  const [cite, setCite] = useState('')
  const [citeInformation, setCiteInformation] = useState('')
  const [bodyState, setBodyState] = useState(initializeDraftContentState)

  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)

  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const content = bodyState.getCurrentContent()

    const bodyDraft = convertToRaw(content)
    const bodyHtml = stateToHTML(content, draftExportHtmlOptions)
    const bodyText = content.getPlainText('\u0001')

    if (tag.length < 1) {
      setErrorMessage('Your card must have a tag')
      return
    }
    if (tag.length > 256) {
      setErrorMessage("Your card's tag cannot be longer than 256 characters")
      return
    }
    if (cite.length === 0) {
      setErrorMessage('Your card must have a cite')
      return
    }
    if (cite.length > 128) {
      setErrorMessage("Your card's cite cannot be longer than 128 characters")
      return
    }
    if (citeInformation.length > 2048) {
      setErrorMessage("Your card's cite information cannot be longer than 2048 characters")
      return
    }
    if (bodyHtml.length > 100000 || bodyText.length > 50000) {
      setErrorMessage("Your card's body is too long!")
      return
    }

    const response = await fetch('/api/v1/cards', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${jwt}` },
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
      setErrorMessage('A server error occurred during your request. Please try again')
    } else {
      setErrorMessage('An unknown error occurred during your request. Please try again')
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
    <DashboardPage>
      <Box w={{ base: '85%', sm: '75%', md: '70%', lg: '60%', xl: '50%' }} m='0 auto' p='6vh 0'>
        <div>
          <Heading as='h2' fontSize={24} fontWeight='bold' paddingTop={1}>
            Create a new card
          </Heading>
          <div
            style={{
              width: '100%',
              margin: '2vh 0',
              height: 1,
              backgroundColor: theme.palette.lightGrey.main
            }}
          />
        </div>
        <form onSubmit={handleSubmit}>
          {/* tag */}
          <div>
            <label htmlFor='tag' id='tagLabel'>
              <Heading as='h3' fontSize={18} fontWeight={500}>
                Tag
                <span style={{ fontWeight: 300 }}>*</span>
              </Heading>
            </label>
            <Text color='lightBlue' id='tagDescription' fontSize='14px' m='6px 0'>
              Put a quick summary of what this card says.
            </Text>
            <Textarea
              id='tag'
              value={tag}
              onChange={e => setTag(e.target.value)}
              rows={2}
              resize='none'
              focusBorderColor='cardtownBlue'
              aria-labelledby='tagLabel'
              aria-describedby='tagDescription'
            />
          </div>

          {/* cite */}
          <div style={{ marginTop: 25 }}>
            <label htmlFor='cite' id='citeLabel'>
              <Heading as='h3' fontSize={18} fontWeight={500}>
                Cite
                <span style={{ fontWeight: 300 }}>*</span>
              </Heading>
            </label>
            <Text color='lightBlue' id='citeDescription' fontSize={14} margin='6px 0'>
              Put the last name of the author and the year it was written. You can also put more information. Example: Miller
              18.
            </Text>
            <Input
              value={cite}
              onChange={e => setCite(e.target.value)}
              focusBorderColor='cardtownBlue'
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
              Put some more information about the source of this card, like the author’s credentials and a link to the place
              you found it.
            </Text>
            <Textarea
              value={citeInformation}
              onChange={e => setCiteInformation(e.target.value)}
              focusBorderColor='cardtownBlue'
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
              <span style={{ fontWeight: 300 }}>*</span>
            </Heading>
            <NewCardFormattingPopover />
            <CardBodyEditor editorState={bodyState} setEditorState={setBodyState} style={{ padding: 10 }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <Text color='lightBlue' fontSize={11}>
                *required field
              </Text>
              <Text color='lightBlue' fontSize={width >= theme.breakpoints.values.md ? 15 : 11}>
                {currentInlineStyles.toString()}
              </Text>
            </div>
          </div>
          <div style={{ marginTop: 10, marginBottom: -5 }}>
            <PrimaryButton type='submit'>Finish</PrimaryButton>
          </div>
        </form>
      </Box>
    </DashboardPage>
  )
}

export default NewCard

export const getServerSideProps: GetServerSideProps = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/cards/new')
    return {
      props: {}
    }
  }
  return {
    props: {}
  }
}

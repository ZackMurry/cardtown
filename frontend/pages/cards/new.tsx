import { Button, TextField, Typography } from '@material-ui/core'
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

const NewCard: FC = () => {
  const { width } = useWindowSize(1920, 1080)

  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')
  const [ bodyState, setBodyState ] = useState(initializeDraftContentState)

  const jwt = useMemo(() => Cookie.get('jwt'), [])
  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    console.log(JSON.stringify(convertToRaw(bodyState.getCurrentContent())))
    const content = bodyState.getCurrentContent()

    const bodyDraft = convertToRaw(content)
    const bodyHtml = stateToHTML(content, draftExportHtmlOptions)
    const bodyText = content.getPlainText('\u0001')
    console.log(bodyText)

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
    } // todo else show error
  }

  const currentInlineStyles = []
  // eslint-disable-next-line no-restricted-syntax
  for (const s of bodyState.getCurrentInlineStyle()) {
    // don't show default font size
    if (s !== 'FONT_SIZE_11') {
      currentInlineStyles.push(' ' + mapStyleToReadable(s))
    }
  }

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
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
              New card
            </Typography>
            <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
              Create a new card
            </BlackText>
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
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Cite
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <Typography color='textSecondary' id='citeDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put the last name of the author and the year it was written. You can also put more information. Example: Miller 18.
              </Typography>
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
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Additional cite information
                </BlackText>
              </label>
              <Typography color='textSecondary' id='citeInfoDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put some more information about the source of this card, like the author’s credentials and a link to the place you found it.
              </Typography>
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
            <div style={{ marginTop: 20 }}>
              <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                Body
                <span style={{ fontWeight: 300 }}>
                  *
                </span>
              </BlackText>
              <NewCardFormattingPopover />
              <CardBodyEditor
                editorState={bodyState}
                setEditorState={setBodyState}
                style={{ padding: 10 }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <div>
                  <Typography color='textSecondary' style={{ fontSize: 11, marginTop: 5 }}>
                    *required field
                  </Typography>
                </div>
                <Typography color='textSecondary' style={{ fontSize: width >= theme.breakpoints.values.md ? 15 : 11, marginTop: 5 }}>
                  {
                    currentInlineStyles.toString()
                  }
                </Typography>
              </div>
            </div>
            <div style={{ marginTop: 10, marginBottom: -5 }}>
              <Button type='submit' variant='contained' color='primary' style={{ textTransform: 'none' }}>
                <Typography>
                  Finish
                </Typography>
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default NewCard

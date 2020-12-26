import { Button, TextField, Typography } from '@material-ui/core'
import { convertFromRaw, convertToRaw, EditorState } from 'draft-js'
import { stateToHTML } from 'draft-js-export-html'
import Cookie from 'js-cookie'
import { useMemo, useState } from 'react'
import NewCardBodyEditor from '../../components/cards/NewCardBodyEditor'
import NewCardFormattingPopover from '../../components/cards/NewCardFormattingPopover'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import theme from '../../components/utils/theme'

//used because EditorState.createFromEmpty() was producing errors.
//just an empty content state
const emptyContentState = convertFromRaw({
  entityMap: {},
  blocks: [
    {
      text: '',
      key: 'cardtown',
      type: 'unstyled',
      entityRanges: []
    }
  ]
})

const inlineStyles = {
  HIGHLIGHT: {
    style: {
      backgroundColor: 'rgb(255, 255, 0)'
    }
  },
  FONT_SIZE_6: {
    style: {
      fontSize: '6pt'
    }
  },
  FONT_SIZE_8: {
    style: {
      fontSize: '8pt'
    }
  },
  FONT_SIZE_9: {
    style: {
      fontSize: '9pt'
    }
  },
  FONT_SIZE_10: {
    style: {
      fontSize: '10pt'
    }
  },
  FONT_SIZE_11: {
    style: {
      fontSize: '11pt'
    }
  },
  OUTLINE: {
    style: {
      border: '2px solid black'
    }
  }
}

export default function NewCard() {
  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')
  const [ bodyState, setBodyState ] = useState(() => EditorState.createWithContent(emptyContentState))

  const jwt = useMemo(() => Cookie.get('jwt'), [])

  const handleSubmit = async e => {
    e.preventDefault()
    console.log(JSON.stringify(convertToRaw(bodyState.getCurrentContent())))
    const content = bodyState.getCurrentContent()

    const bodyDraft = convertToRaw(content)
    const bodyHtml = stateToHTML(content, { inlineStyles })

    const response = await fetch('/api/v1/cards', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` },
      body: JSON.stringify({
        tag,
        cite,
        citeInformation,
        bodyHtml,
        bodyDraft: JSON.stringify(bodyDraft)
      })
    })
    console.log(response.status)
    console.log(await response.text())
    // todo redirect to card's page
  }

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Cards' />

      <div style={{ marginLeft: '12.9vw', paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: '65%', margin: '7.5vh auto' }}>
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
              <NewCardBodyEditor editorState={bodyState} setEditorState={setBodyState} />
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

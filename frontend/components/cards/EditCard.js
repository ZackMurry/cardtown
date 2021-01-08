import CloseIcon from '@material-ui/icons/Close'
import DoneIcon from '@material-ui/icons/Done'
import { IconButton, Tooltip, Typography } from '@material-ui/core'
import { convertFromRaw, convertToRaw, EditorState } from 'draft-js'
import { useState } from 'react'
import theme from '../utils/theme'
import CardBodyEditor from './CardBodyEditor'
import { stateToHTML } from 'draft-js-export-html'
import draftExportHtmlOptions from './draftExportHtmlOptions'

export default function EditCard({
  jwt, onCancel, onDone, card, windowWidth
}) {
  const [ tag, setTag ] = useState(card.tag)
  const [ cite, setCite ] = useState(card.cite)
  const [ citeInformation, setCiteInformation ] = useState(card.citeInformation)
  const [ bodyState, setBodyState ] = useState(() => EditorState.createWithContent(convertFromRaw(JSON.parse(card.bodyDraft))))

  const handleDone = async () => {
    if (!jwt) {
      onCancel('You need to be signed in to do this')
    }
    
    const bodyHtml = stateToHTML(bodyState.getCurrentContent(), draftExportHtmlOptions)
    const bodyDraft = convertToRaw(bodyState.getCurrentContent())

    const response = await fetch(`/api/v1/cards/${encodeURIComponent(card.id)}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tag,
        cite,
        citeInformation,
        bodyHtml,
        bodyDraft: JSON.stringify(bodyDraft)
      })
    })
    
    if (response.ok) {
      onDone()
    } else {
      console.warn(response.status)
    }
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <textarea
          type='text'
          value={tag}
          onChange={e => setTag(e.target.value)}
          style={{
            color: theme.palette.black.main,
            fontWeight: 'bold',
            fontSize: 18,
            outline: 'none',
            border: 'none',
            width: '100%',
            fontFamily: 'Roboto',
            resize: 'none'
          }}
          rows={3}
        />
        <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <Tooltip title='Done'>
            <IconButton onClick={handleDone}>
              <DoneIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title='Cancel'>
            <IconButton onClick={() => onCancel()}>
              <CloseIcon />
            </IconButton>
          </Tooltip>
        </div>
      </div>
      <input
        type='text'
        value={cite}
        onChange={e => setCite(e.target.value)}
        style={{
          color: theme.palette.black.main,
          fontWeight: 'bold',
          fontSize: 18,
          outline: 'none',
          border: 'none',
          width: '100%',
          fontFamily: 'Roboto'
        }}
      />
      <textarea
        type='text'
        value={citeInformation}
        onChange={e => setCiteInformation(e.target.value)}
        style={{
          color: theme.palette.black.main,
          fontWeight: 'normal',
          fontSize: 12,
          outline: 'none',
          border: 'none',
          width: '100%',
          fontFamily: 'Roboto',
          resize: 'none'
        }}
        rows={3}
      />
      <CardBodyEditor
        editorState={bodyState}
        setEditorState={setBodyState}
        disableOutline
      />
    </div>
  )
}

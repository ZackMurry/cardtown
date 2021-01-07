import { Typography } from '@material-ui/core'
import { convertFromRaw, EditorState } from 'draft-js'
import { useState } from 'react'
import theme from '../utils/theme'
import CardBodyEditor from './CardBodyEditor'

export default function EditCard({
  jwt, onCancel, onDone, card, windowWidth
}) {
  const [ tag, setTag ] = useState(card.tag)
  const [ cite, setCite ] = useState(card.cite)
  const [ citeInformation, setCiteInformation ] = useState(card.citeInformation)
  const [ bodyDraft, setBodyDraft ] = useState(() => EditorState.createWithContent(convertFromRaw(JSON.parse(card.bodyDraft))))

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
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
            fontFamily: 'Roboto'
          }}
          rows={3}
        />
        {/* todo done button here */}
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
          fontFamily: 'Roboto'
        }}
        rows={3}
      />
      <CardBodyEditor
        editorState={bodyDraft}
        setEditorState={setBodyDraft}
        disableOutline
      />
    </div>
  )
}

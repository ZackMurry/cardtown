import CloseIcon from '@material-ui/icons/Close'
import DoneIcon from '@material-ui/icons/Done'
import { stateToHTML } from 'draft-js-export-html'
import { convertFromRaw, convertToRaw, EditorState } from 'draft-js'
import React, { FC, useContext, useState } from 'react'
import { useColorModeValue, IconButton, Tooltip } from '@chakra-ui/react'
import chakraTheme from 'lib/chakraTheme'
import { ResponseCard } from 'types/card'
import useErrorMessage from 'lib/hooks/useErrorMessage'
import userContext from 'lib/hooks/UserContext'
import CardBodyEditor from './CardBodyEditor'
import draftExportHtmlOptions from './draftExportHtmlOptions'

interface Props {
  onCancel: () => void
  onDone: (e: React.MouseEvent<HTMLInputElement, MouseEvent>) => void
  card: ResponseCard
  windowWidth: number
}

const EditCard: FC<Props> = ({ onCancel, onDone, card }) => {
  const [tag, setTag] = useState(card.tag)
  const [cite, setCite] = useState(card.cite)
  const [citeInformation, setCiteInformation] = useState(card.citeInformation)
  const [bodyState, setBodyState] = useState(() => EditorState.createWithContent(convertFromRaw(JSON.parse(card.bodyDraft))))
  const { setErrorMessage } = useErrorMessage()
  const { jwt } = useContext(userContext)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const textColor = useColorModeValue('black', 'white')

  const handleDone = async (e: React.MouseEvent<HTMLInputElement, MouseEvent>) => {
    if (!jwt) {
      setErrorMessage('You need to be signed in to do this')
      onCancel()
    }

    const content = bodyState.getCurrentContent()
    const bodyHtml = stateToHTML(content, draftExportHtmlOptions)
    const bodyDraft = convertToRaw(bodyState.getCurrentContent())
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

    const response = await fetch(`/api/v1/cards/id/${encodeURIComponent(card.id)}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
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
      onDone(e)
    } else {
      setErrorMessage(`An unexpected error occured. Status code: ${response.status}`)
    }
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <textarea
          value={tag}
          onChange={e => setTag(e.target.value)}
          style={{
            color: textColor,
            backgroundColor: chakraTheme.colors[bgColor],
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
          <Tooltip label='Done'>
            <IconButton aria-label='Done' onClick={handleDone} bg='none'>
              <DoneIcon />
            </IconButton>
          </Tooltip>
          <Tooltip label='Cancel'>
            <IconButton aria-label='Cancel' onClick={onCancel} bg='none'>
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
          color: textColor,
          backgroundColor: chakraTheme.colors[bgColor],
          fontWeight: 'bold',
          fontSize: 18,
          outline: 'none',
          border: 'none',
          width: '100%',
          fontFamily: 'Roboto'
        }}
      />
      <textarea
        value={citeInformation}
        onChange={e => setCiteInformation(e.target.value)}
        style={{
          color: textColor,
          backgroundColor: chakraTheme.colors[bgColor],
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
      <CardBodyEditor editorState={bodyState} setEditorState={setBodyState} disableOutline />
    </div>
  )
}

export default EditCard

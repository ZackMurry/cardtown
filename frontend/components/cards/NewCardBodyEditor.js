import { Typography } from '@material-ui/core'
import { Editor, getDefaultKeyBinding, RichUtils } from 'draft-js'
import styles from '../../styles/NewCard.module.css'
import theme from '../utils/theme'

const styleMap = {
  HIGHLIGHT: {
    backgroundColor: 'rgb(255, 255, 0)'
  },
  FONT_SIZE_6: {
    fontSize: '6pt'
  },
  FONT_SIZE_8: {
    fontSize: '8pt'
  },
  FONT_SIZE_9: {
    fontSize: '9pt'
  },
  FONT_SIZE_10: {
    fontSize: '10pt'
  },
  FONT_SIZE_11: {
    fontSize: '11pt'
  },
  OUTLINE: {
    border: '2px solid black'
  }
}

const styleToReadable = {
  BOLD: 'bold',
  HIGHLIGHT: 'highlight',
  FONT_SIZE_6: 'font size 6',
  FONT_SIZE_8: 'font size 8',
  FONT_SIZE_9: 'font size 9',
  FONT_SIZE_10: 'font size 10',
  FONT_SIZE_11: 'font size 11',
  UNDERLINE: 'underline',
  ITALIC: 'italics',
  OUTLINE: 'outline'
}

export default function NewCardBodyEditor({ editorState, setEditorState, windowWidth }) {

  const handleChange = newState => {
    setEditorState(newState)
  }

  const handleKeyCommand = command => {
    if (command.startsWith('FONT_SIZE_')) {
      let newEditorState = editorState
      for (let style of newEditorState.getCurrentInlineStyle()) {
        if (style.startsWith('FONT_SIZE_')) {
          newEditorState = RichUtils.toggleInlineStyle(newEditorState, style)
        }
      }
      handleChange(RichUtils.toggleInlineStyle(newEditorState, command))
      return 'handled'
    }
    if (command === 'HIGHLIGHT') {
      handleChange(RichUtils.toggleInlineStyle(editorState, command))
      return 'handled'
    }
    if (command === 'OUTLINE') {
      handleChange(RichUtils.toggleInlineStyle(editorState, command))
      return 'handled'
    }
    const newState = RichUtils.handleKeyCommand(editorState, command)
    if (newState) {
      handleChange(newState)
      return 'handled'
    }
    return 'not-handled'
  }

  // todo: after beta, have a visual toolbar
  const keyBindingFn = e => {
    if (e.key === 'Tab') {
      return 'tab'
    }

    if (!e.ctrlKey) {
      return getDefaultKeyBinding(e)
    }

    if (e.key === 'h') {
      return 'HIGHLIGHT'
    }

    if (e.key === 'o') {
      e.preventDefault()
      return 'OUTLINE'
    }

    if (e.key === '1') {
      return 'FONT_SIZE_6'
    }
    if (e.key === '2') {
      return 'FONT_SIZE_8'
    }
    if (e.key === '3') {
      return 'FONT_SIZE_9'
    }
    if (e.key === '4') {
      return 'FONT_SIZE_10'
    }
    if (e.key === '5') {
      return 'FONT_SIZE_11'
    }

    return getDefaultKeyBinding(e)
  }

  let currentInlineStyles = []
  for (let s of editorState.getCurrentInlineStyle()) {
    if (s === 'FONT_SIZE_11') {
      // don't show default font size
      continue
    }
    currentInlineStyles.push(' ' + (styleToReadable[s] ?? s))
  }

  return (
    // todo make this look more like the other inputs
    // todo get user's preferred formatting
    // todo guide for shortcuts somewhere
    <>
      <div
        style={{
          backgroundColor: theme.palette.secondary.main,
          border: `1px solid rgba(0, 0, 0, 0.23)`,
          borderRadius: 3
        }}
        className={styles['editor-container']}
      >
        <Editor
          editorState={editorState}
          onChange={handleChange}
          handleKeyCommand={handleKeyCommand}
          keyBindingFn={keyBindingFn}
          editorKey='newCardEditor'
          customStyleMap={styleMap}
        />
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div>
          <Typography color='textSecondary' style={{ fontSize: 11, marginTop: 5 }}>
            *required field
          </Typography>
        </div>
        <Typography color='textSecondary' style={{ fontSize: windowWidth >= theme.breakpoints.values.md ? 15 : 11, marginTop: 5 }}>
          {
            currentInlineStyles.toString()
          }
        </Typography>
      </div>
    </>
  )
}



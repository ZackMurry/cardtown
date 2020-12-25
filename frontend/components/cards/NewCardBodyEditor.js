import { convertFromRaw, Editor, EditorState, getDefaultKeyBinding, RichUtils } from 'draft-js'
import { useState } from 'react'
import styles from '../../styles/NewCard.module.css'
import theme from '../utils/theme'

//used because EditorState.createFromEmpty() was producing errors.
//just an empty content state
const emptyContentState = convertFromRaw({
  entityMap: {},
  blocks: [
    {
      text: '',
      key: 'nottte',
      type: 'unstyled',
      entityRanges: []
    }
  ]
})

const styleMap = {
  highlight: {
    backgroundColor: 'rgb(255, 255, 0)'
  }
}

export default function NewCardBodyEditor() {
  const [ editorState, setEditorState ] = useState(() => EditorState.createWithContent(emptyContentState))

  const handleChange = newState => {
    setEditorState(newState)
  }

  const handleKeyCommand = command => {
    if (command === 'highlight') {
      handleChange(RichUtils.toggleInlineStyle(editorState, command))
    }
    const newState = RichUtils.handleKeyCommand(editorState, command)
    if (newState) {
      handleChange(newState)
      return 'handled'
    }
    return 'not-handled'
  }

  const keyBindingFn = e => {
    if (e.key === 'Tab') {
      return 'tab'
    }

    if (e.ctrlKey && e.key === 'h') {
      return 'highlight'
    }

    return getDefaultKeyBinding(e)
  }

  return (
    // todo make this look more like the other inputs
    // todo get user's preferred formatting
    // todo guide for shortcuts somewhere
    // todo visual style editor
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
  )
}

import { Editor, EditorState, getDefaultKeyBinding, RichUtils } from 'draft-js'
import { CSSProperties, FC } from 'react'
import styles from 'styles/NewCard.module.css'
import { Box, useColorModeValue } from '@chakra-ui/react'

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

interface Props {
  editorState: EditorState
  setEditorState: (newEditorState: EditorState) => void
  disableOutline?: boolean
  style?: CSSProperties
}

const CardBodyEditor: FC<Props> = ({ editorState, setEditorState, disableOutline, style: customStyles = {} }) => {
  const bgColor = useColorModeValue('offWhite', 'grayBorder')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const handleChange = newState => {
    setEditorState(newState)
  }

  const handleKeyCommand = command => {
    if (command.startsWith('FONT_SIZE_')) {
      let newEditorState = editorState
      // eslint-disable-next-line no-restricted-syntax
      for (const inlineStyle of newEditorState.getCurrentInlineStyle().toArray()) {
        if (inlineStyle.startsWith('FONT_SIZE_')) {
          newEditorState = RichUtils.toggleInlineStyle(newEditorState, inlineStyle)
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

  return (
    // todo make this look more like the other inputs
    // todo get user's preferred formatting
    // todo guide for shortcuts somewhere
    <>
      <Box
        bg={bgColor}
        {...(!disableOutline && {
          borderWidth: '1px',
          borderStyle: 'solid',
          borderColor
        })}
        borderRadius='3px'
        color='black'
        style={{
          ...customStyles
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
      </Box>
    </>
  )
}

export default CardBodyEditor

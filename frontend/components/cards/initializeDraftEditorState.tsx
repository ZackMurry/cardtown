import { convertFromRaw, EditorState } from 'draft-js'

//just an empty content state
const emptyContentState = convertFromRaw({
  entityMap: {},
  blocks: [
    {
      text: '',
      key: 'cardtown',
      type: 'unstyled',
      entityRanges: [],
      depth: 0,
      inlineStyleRanges: []
    }
  ]
})

// used because EditorState.createFromEmpty() was producing errors.
const initializeDraftContentState = (): EditorState => (
  EditorState.createWithContent(emptyContentState)
)

export default initializeDraftContentState

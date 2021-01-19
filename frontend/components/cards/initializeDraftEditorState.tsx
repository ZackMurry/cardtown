import { convertFromRaw, EditorState } from 'draft-js'

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

//used because EditorState.createFromEmpty() was producing errors.
export default function initializeDraftContentState() {
  return EditorState.createWithContent(emptyContentState)
}

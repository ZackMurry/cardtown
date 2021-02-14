import { useRouter } from 'next/router'
import { FC, useState } from 'react'
import DragIndicatorIcon from '@material-ui/icons/DragIndicator'
import { Draggable, DraggableProvided, DraggableStateSnapshot } from 'react-beautiful-dnd'
import ArgumentCardOptionsButton from '../arguments/ArgumentCardOptionsButton'
import ResponseCard from '../types/ResponseCard'
import BlackText from '../utils/BlackText'
import EditCard from '../cards/EditCard'

interface Props {
  card: ResponseCard
  jwt: string
  windowWidth: number
  onError: (msg: string) => void
  argumentId: string
  index: number
}

const ArgumentCardDisplay: FC<Props> = ({
  card, jwt, windowWidth, onError, argumentId, index
}) => {
  const [ editing, setEditing ] = useState(false)
  const router = useRouter()

  const handleEdit = () => {
    if (card.bodyDraft !== 'IMPORTED CARD -- NO DRAFT BODY') {
      setEditing(true)
    } else {
      onError('Only non-imported cards can be edited.')
      // todo option to remove formatting from body and persist
    }
  }

  const handleDoneEditing = () => {
    setEditing(false)
    router.reload() // todo would be much better to just refresh contents
  }

  const handleCancelEditing = (msg: string) => {
    if (msg) {
      onError(msg)
    }
    setEditing(false)
  }

  return (
    <Draggable draggableId={card.id + 'handle'} index={index}>
      {(
        dragProvided: DraggableProvided,
        dragSnapshot: DraggableStateSnapshot
      ) => (
        <div ref={dragProvided.innerRef} {...dragProvided.draggableProps}>
          {
            editing
              ? (
                <EditCard
                  jwt={jwt}
                  card={card}
                  windowWidth={windowWidth}
                  onDone={handleDoneEditing}
                  onCancel={handleCancelEditing}
                />
              )
              : (
                <>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                    <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>
                      {card.tag}
                    </BlackText>
                    <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                      <div
                        style={{ padding: 12, cursor: 'grab' }}
                        {...dragProvided.dragHandleProps}
                      >
                        <DragIndicatorIcon style={{ color: 'rgba(0, 0, 0, 0.54)' }} />
                      </div>
                      {/* todo separate the ArgumentCardOptionsButton into separate icons
                      to avoid having two similar-looking icons */}
                      <ArgumentCardOptionsButton argumentId={argumentId} cardId={card.id} jwt={jwt} onEdit={handleEdit} />
                    </div>
                  </div>
                  <div>
                    <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>
                      {card.cite}
                    </BlackText>
                    <BlackText style={{ fontWeight: 'normal', fontSize: 11 }}>
                      {card.citeInformation}
                    </BlackText>
                  </div>
                  {/* all my homies just disable warnings :) */}
                  {/* eslint-disable-next-line react/no-danger */}
                  <div dangerouslySetInnerHTML={{ __html: card.bodyHtml }} />
                </>
              )
          }
        </div>

      )}
    </Draggable>
  )
}

export default ArgumentCardDisplay

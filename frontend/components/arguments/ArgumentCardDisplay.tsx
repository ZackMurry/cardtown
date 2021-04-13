import { useRouter } from 'next/router'
import { FC, useContext, useState } from 'react'
import DragIndicatorIcon from '@material-ui/icons/DragIndicator'
import { Draggable, DraggableProvided } from 'react-beautiful-dnd'
import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import { ResponseCard } from 'types/card'
import EditCard from 'components/cards/EditCard'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import ArgumentCardOptionsButton from './ArgumentCardOptionsButton'

interface Props {
  card: ResponseCard
  windowWidth: number
  argumentId: string
  indexInArgument: number
  onRemove: () => void
}

const ArgumentCardDisplay: FC<Props> = ({ card, windowWidth, argumentId, indexInArgument, onRemove }) => {
  const [editing, setEditing] = useState(false)
  const router = useRouter()
  const { setErrorMessage } = useContext(errorMessageContext)
  const bodyBgColor = useColorModeValue('offWhite', 'grayBorder')
  const handleColor = useColorModeValue('rgba(0, 0, 0, 0.54)', 'white')

  const handleEdit = () => {
    if (card.bodyDraft !== 'IMPORTED CARD -- NO DRAFT BODY') {
      setEditing(true)
    } else {
      setErrorMessage('Only non-imported cards can be edited.')
      // todo option to remove formatting from body and persist
    }
  }

  const handleDoneEditing = () => {
    setEditing(false)
    router.reload() // todo would be much better to just refresh contents
  }

  const handleCancelEditing = () => {
    setEditing(false)
  }

  return (
    <Draggable draggableId={`${card.id}@${indexInArgument}-handle`} index={indexInArgument}>
      {(dragProvided: DraggableProvided) => (
        <div ref={dragProvided.innerRef} {...dragProvided.draggableProps}>
          {editing ? (
            <EditCard card={card} windowWidth={windowWidth} onDone={handleDoneEditing} onCancel={handleCancelEditing} />
          ) : (
            <>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                <Text fontWeight='bold' fontSize='18px'>
                  {card.tag}
                </Text>
                <Flex justifyContent='flex-end'>
                  <Box p='12px' cursor='grab' {...dragProvided.dragHandleProps}>
                    <DragIndicatorIcon style={{ color: handleColor }} />
                  </Box>
                  {/* todo separate the ArgumentCardOptionsButton into separate icons
                      to avoid having two similar-looking icons */}
                  <ArgumentCardOptionsButton
                    argumentId={argumentId}
                    cardId={card.id}
                    indexInArgument={indexInArgument}
                    onEdit={handleEdit}
                    onRemove={onRemove}
                  />
                </Flex>
              </div>
              <div>
                <Text fontWeight='bold' fontSize='18px' style={{ fontWeight: 'bold', fontSize: 18 }}>
                  {card.cite}
                </Text>
                <Text style={{ fontWeight: 'normal', fontSize: 11 }}>{card.citeInformation}</Text>
              </div>
              {/* all my homies just disable warnings :) */}
              {/* eslint-disable-next-line react/no-danger */}
              <Box
                color='black'
                bg={bodyBgColor}
                borderRadius='3px'
                p='5px'
                mt='5px'
                dangerouslySetInnerHTML={{ __html: card.bodyHtml }}
              />
            </>
          )}
        </div>
      )}
    </Draggable>
  )
}

export default ArgumentCardDisplay

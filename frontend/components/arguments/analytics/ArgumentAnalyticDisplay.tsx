import { Text, Flex, Box, useColorModeValue } from '@chakra-ui/react'
import { FC, useState } from 'react'
import { DraggableProvided, Draggable } from 'react-beautiful-dnd'
import DragIndicatorIcon from '@material-ui/icons/DragIndicator'
import { ResponseAnalytic } from 'types/analytic'
import ArgumentAnalyticOptionsButton from './ArgumentAnalyticOptionsButton'
import EditArgumentAnalytic from './EditArgumentAnalytic'

interface Props {
  analytic: ResponseAnalytic
  argId: string
  onDelete: () => void
  onUpdate: (newValue: ResponseAnalytic) => void
}

// todo add options button for deleting, editing, etc
const ArgumentAnalyticDisplay: FC<Props> = ({ analytic, argId, onDelete, onUpdate }) => {
  const [editing, setEditing] = useState(false)
  const handleColor = useColorModeValue('rgba(0, 0, 0, 0.54)', 'white')

  const handleDoneEditing = (newValue: ResponseAnalytic) => {
    setEditing(false)
    // todo just update content
    onUpdate(newValue)
  }

  return (
    <Draggable draggableId={`${analytic.id}@${analytic.position}-handle`} index={analytic.position}>
      {(dragProvided: DraggableProvided) => (
        <div ref={dragProvided.innerRef} {...dragProvided.draggableProps}>
          {editing ? (
            <EditArgumentAnalytic
              analytic={analytic}
              argId={argId}
              onCancel={() => setEditing(false)}
              onDone={handleDoneEditing}
            />
          ) : (
            <Flex
              justifyContent='space-between'
              alignItems='flex-end'
              style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}
            >
              <Text color='white' fontWeight='bold' fontSize='18px' pt='12px' pb='13px'>
                {analytic.body}
              </Text>
              <Flex justifyContent='flex-end'>
                <Box p='12px' cursor='grab' {...dragProvided.dragHandleProps}>
                  <DragIndicatorIcon style={{ color: handleColor }} />
                </Box>
                <ArgumentAnalyticOptionsButton
                  analytic={analytic}
                  onDelete={onDelete}
                  argId={argId}
                  onEdit={() => setEditing(true)}
                />
              </Flex>
            </Flex>
          )}
        </div>
      )}
    </Draggable>
  )
}

export default ArgumentAnalyticDisplay

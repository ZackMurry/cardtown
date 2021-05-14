import { Text, Flex, Box, useColorModeValue } from '@chakra-ui/react'
import { FC, useState } from 'react'
import { DraggableProvided, Draggable } from 'react-beautiful-dnd'
import DragIndicatorIcon from '@material-ui/icons/DragIndicator'
import { ResponseAnalytic } from 'types/analytic'

interface Props {
  analytic: ResponseAnalytic
  argId: string
}

// todo add options button for deleting, editing, etc
const ArgumentAnalyticDisplay: FC<Props> = ({ analytic, argId }) => {
  const [editing, setEditing] = useState(false)
  const handleColor = useColorModeValue('rgba(0, 0, 0, 0.54)', 'white')

  return (
    <Draggable draggableId={`${analytic.id}@${analytic.position}-handle`} index={analytic.position}>
      {(dragProvided: DraggableProvided) => (
        <div ref={dragProvided.innerRef} {...dragProvided.draggableProps}>
          {editing ? (
            // todo
            <Box>Editing...</Box>
          ) : (
            <>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                <Text color='white' fontWeight='bold' fontSize='18px' pb='25px'>
                  {analytic.body}
                </Text>
                <Flex justifyContent='flex-end'>
                  <Box p='12px' cursor='grab' {...dragProvided.dragHandleProps}>
                    <DragIndicatorIcon style={{ color: handleColor }} />
                  </Box>
                </Flex>
              </div>
            </>
          )}
        </div>
      )}
    </Draggable>
  )
}

export default ArgumentAnalyticDisplay

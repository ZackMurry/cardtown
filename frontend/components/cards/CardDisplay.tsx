import { useRouter } from 'next/router'
import { FC, useContext, useState } from 'react'
import { ResponseCard } from 'types/card'
import { Box, Text, useColorModeValue } from '@chakra-ui/react'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import CardOptionsButton from './CardOptionsButton'
import EditCard from './EditCard'

interface Props {
  card: ResponseCard
  isRestored: boolean
}

const CardDisplay: FC<Props> = ({ card, isRestored }) => {
  const [editing, setEditing] = useState(false)
  const router = useRouter()
  const { setErrorMessage } = useContext(errorMessageContext)
  const bodyBgColor = useColorModeValue('offWhite', 'grayBorder')

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
    <div>
      {editing ? (
        <EditCard card={card} onDone={handleDoneEditing} onCancel={handleCancelEditing} />
      ) : (
        <>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
            <Text fontWeight='bold' fontSize='18px'>
              {card.tag}
            </Text>
            {(!card.deleted || isRestored) && <CardOptionsButton id={card.id} onEdit={handleEdit} />}
          </div>
          <div>
            <Text fontWeight='bold' fontSize='18px'>
              {card.cite}
            </Text>
            <Text fontSize='11px'>{card.citeInformation}</Text>
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
  )
}

export default CardDisplay

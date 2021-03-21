import { useRouter } from 'next/router'
import { FC, useContext, useState } from 'react'
import ResponseCard from 'types/ResponseCard'
import BlackText from 'components/utils/BlackText'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import CardOptionsButton from './CardOptionsButton'
import EditCard from './EditCard'

interface Props {
  card: ResponseCard
  windowWidth: number
}

const CardDisplay: FC<Props> = ({ card, windowWidth }) => {
  const [editing, setEditing] = useState(false)
  const router = useRouter()
  const { setErrorMessage } = useContext(errorMessageContext)

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
        <EditCard card={card} windowWidth={windowWidth} onDone={handleDoneEditing} onCancel={handleCancelEditing} />
      ) : (
        <>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
            <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>{card.tag}</BlackText>
            <CardOptionsButton id={card.id} onEdit={handleEdit} />
          </div>
          <div>
            <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>{card.cite}</BlackText>
            <BlackText style={{ fontWeight: 'normal', fontSize: 11 }}>{card.citeInformation}</BlackText>
          </div>
          {/* all my homies just disable warnings :) */}
          {/* eslint-disable-next-line react/no-danger */}
          <div dangerouslySetInnerHTML={{ __html: card.bodyHtml }} />
        </>
      )}
    </div>
  )
}

export default CardDisplay

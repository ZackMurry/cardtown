import { useRouter } from 'next/router'
import { FC, useState } from 'react'
import ResponseCard from 'types/ResponseCard'
import BlackText from 'components/utils/BlackText'
import CardOptionsButton from './CardOptionsButton'
import EditCard from './EditCard'

interface Props {
  card: ResponseCard
  jwt: string
  windowWidth: number
  onError: (msg: string) => void
}

const CardDisplay: FC<Props> = ({
  card, jwt, windowWidth, onError
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
    <div>
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
                <CardOptionsButton id={card.id} jwt={jwt} onEdit={handleEdit} />
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
  )
}

export default CardDisplay

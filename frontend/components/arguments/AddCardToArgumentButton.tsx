import { FC, useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import { IconButton } from '@material-ui/core'
import { useRouter } from 'next/router'
import theme from '../utils/theme'
import BlackText from '../utils/BlackText'
import CardSearchMenu from '../cards/CardSearchMenu'
import CardPreview from '../types/CardPreview'

interface Props {
  jwt: string
  onError: (msg: string) => void
  windowWidth: number
  argId: string
}

// todo don't reload after finished -- just update argument cards on client
const AddCardToArgumentButton: FC<Props> = ({
  jwt, onError, windowWidth, argId
}) => {
  const [ isOpen, setOpen ] = useState(false)
  const [ allCards, setAllCards ] = useState<CardPreview[] | null>(null)

  const router = useRouter()

  const handleClick = () => {
    setOpen(true)
    if (allCards === null) {
      console.log('fetching...')
      fetchCards()
    }
  }

  const fetchCards = async () => {
    if (!jwt) {
      router.push(`/login?redirect=${encodeURIComponent(router.pathname)}`)
      return
    }
    const response = await fetch('/api/v1/cards/previews', {
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      const c = await response.json() as CardPreview[]
      setAllCards(c)
    } else {
      onError(`Error fetching cards. Status code: ${response.status}`)
    }
  }

  const handleCardAdd = async id => {
    console.log(id)
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argId)}/cards`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id
      })
    })
    if (response.ok) {
      router.reload()
    } else {
      onError(`Error adding card to argument. Status code: ${response.status}`)
    }
  }

  return (
    <div
      style={{
        width: '100%',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: theme.palette.secondary.main,
        border: `1px solid ${theme.palette.lightGrey.main}`,
        borderRadius: 5,
        padding: '3vh 3vw',
        cursor: 'pointer'
      }}
      onClick={isOpen ? undefined : handleClick}
    >
      {
        isOpen
          ? (
            <div style={{ width: '75%' }}>
              <BlackText variant='h6'>
                Add Card
              </BlackText>
              <CardSearchMenu
                jwt={jwt}
                onCardSelect={handleCardAdd}
                cards={allCards}
                windowWidth={windowWidth}
              />
            </div>
          ) : (
            <IconButton>
              <AddIcon fontSize='large' />
            </IconButton>
          )
      }
    </div>
  )
}

export default AddCardToArgumentButton

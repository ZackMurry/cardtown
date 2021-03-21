import { FC, useContext, useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import CloseIcon from '@material-ui/icons/Close'
import { Grid, IconButton } from '@material-ui/core'
import { useRouter } from 'next/router'
import theme from 'lib/theme'
import BlackText from 'components/utils/BlackText'
import CardSearchMenu from 'components/cards/CardSearchMenu'
import CardPreview from 'types/CardPreview'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  windowWidth: number
  argId: string
}

// todo don't reload after finished -- just update argument cards on client
const AddCardToArgumentButton: FC<Props> = ({ windowWidth, argId }) => {
  const [isOpen, setOpen] = useState(false)
  const [allCards, setAllCards] = useState<CardPreview[] | null>(null)
  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)

  const router = useRouter()

  const handleClick = () => {
    setOpen(true)
    if (allCards === null) {
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
      const c = (await response.json()) as CardPreview[]
      setAllCards(c)
    } else {
      setErrorMessage(`Error fetching cards. Status code: ${response.status}`)
    }
  }

  const handleCardAdd = async id => {
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
      setErrorMessage(`Error adding card to argument. Status code: ${response.status}`)
    }
  }

  return (
    <div
      style={{
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
      {isOpen ? (
        <Grid container>
          <Grid item xs={1} />
          <Grid item xs={10}>
            <BlackText variant='h6' style={{ textAlign: 'center' }}>
              Add Card
            </BlackText>
            <CardSearchMenu onCardSelect={handleCardAdd} cards={allCards} windowWidth={windowWidth} />
          </Grid>
          <Grid item xs={1}>
            <IconButton onClick={() => setOpen(false)}>
              <CloseIcon />
            </IconButton>
          </Grid>
        </Grid>
      ) : (
        <IconButton>
          <AddIcon fontSize='large' />
        </IconButton>
      )}
    </div>
  )
}

export default AddCardToArgumentButton

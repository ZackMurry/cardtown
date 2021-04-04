import { FC, useContext, useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import CloseIcon from '@material-ui/icons/Close'
import { Grid } from '@material-ui/core'
import { useRouter } from 'next/router'
import BlackText from 'components/utils/BlackText'
import CardSearchMenu from 'components/cards/CardSearchMenu'
import CardPreview from 'types/CardPreview'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import { Flex, useColorModeValue, IconButton, Heading } from '@chakra-ui/react'

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
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

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
    <Flex
      justifyContent='center'
      alignItems='center'
      bg={bgColor}
      borderWidth='1px'
      borderStyle='solid'
      borderColor={borderColor}
      borderRadius='5px'
      p='3vh 3vw'
      cursor='pointer'
      onClick={isOpen ? undefined : handleClick}
    >
      {isOpen ? (
        <Grid container>
          <Grid item xs={1} />
          <Grid item xs={10}>
            <Heading as='h6' textAlign='center'>
              Add Card
            </Heading>
            <CardSearchMenu onCardSelect={handleCardAdd} cards={allCards} windowWidth={windowWidth} />
          </Grid>
          <Grid item xs={1}>
            <IconButton aria-label='Close' onClick={() => setOpen(false)} bg='none'>
              <CloseIcon />
            </IconButton>
          </Grid>
        </Grid>
      ) : (
        <IconButton aria-label='Add card' bg='none'>
          <AddIcon fontSize='large' />
        </IconButton>
      )}
    </Flex>
  )
}

export default AddCardToArgumentButton

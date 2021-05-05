import { FC, useContext, useState } from 'react'
import { Grid, GridItem, Flex, useColorModeValue, IconButton, Heading } from '@chakra-ui/react'
import { AddIcon, CloseIcon } from '@chakra-ui/icons'
import { useRouter } from 'next/router'
import CardSearchMenu from 'components/cards/CardSearchMenu'
import { CardPreview } from 'types/card'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  argId: string
}

// todo don't reload after finished -- just update argument cards on client
const AddCardToArgumentButton: FC<Props> = ({ argId }) => {
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
        <Grid templateColumns='repeat(12, 1fr)'>
          <GridItem colSpan={1} />
          <GridItem colSpan={10}>
            <Heading as='h6' textAlign='center'>
              Add Card
            </Heading>
            <CardSearchMenu onCardSelect={handleCardAdd} cards={allCards} />
          </GridItem>
          <GridItem colSpan={1}>
            <IconButton aria-label='Close' onClick={() => setOpen(false)} bg='none'>
              <CloseIcon />
            </IconButton>
          </GridItem>
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

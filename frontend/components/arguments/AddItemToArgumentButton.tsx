import { FC, useContext, useState } from 'react'
import { Grid, GridItem, Flex, useColorModeValue, IconButton, Heading, Text, Spinner } from '@chakra-ui/react'
import { AddIcon, CloseIcon } from '@chakra-ui/icons'
import { useRouter } from 'next/router'
import CardSearchMenu from 'components/cards/CardSearchMenu'
import { CardPreview, ResponseCard } from 'types/card'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import { ResponseAnalytic } from 'types/analytic'
import { ResponseArgumentCard } from 'types/argument'
import ArgumentAnalyticCreateForm from './analytics/ArgumentAnalyticCreateForm'

interface Props {
  argId: string
  onCreateAnalytic: (analytic: ResponseAnalytic) => void
  onAddCard: (card: ResponseArgumentCard) => void
}

// todo don't reload after finished -- just update argument cards on client
const AddItemToArgumentButton: FC<Props> = ({ argId, onCreateAnalytic, onAddCard }) => {
  const [isOpen, setOpen] = useState(false)
  const [isLoading, setLoading] = useState(false)
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

  const handleAddCard = async (id: string) => {
    const response = await fetch(`/api/v1/arguments/id/${argId}/cards`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id
      })
    })
    if (response.ok) {
      const card = (await response.json()) as ResponseArgumentCard
      setOpen(false)
      onAddCard(card)
    } else {
      setErrorMessage(`Error adding card to argument. Status code: ${response.status}`)
    }
  }

  const handleCreateAnalytic = async (body: string) => {
    if (!body) {
      setErrorMessage('The body of your analytic cannot be blank')
      return
    }
    if (body.length > 2048) {
      setErrorMessage('The body of your analytic cannot be longer than 2048 characters')
      return
    }
    setLoading(true)
    const response = await fetch(`/api/v1/arguments/id/${argId}/analytics`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${jwt}` },
      body: JSON.stringify({
        body
      })
    })
    if (response.ok) {
      const newAnalytic = (await response.json()) as ResponseAnalytic
      setLoading(false)
      setOpen(false)
      onCreateAnalytic(newAnalytic)
    } else {
      setErrorMessage(`Error creating analytic. Status code: ${response.status}`)
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
      cursor={isOpen ? undefined : 'pointer'}
      onClick={isOpen ? undefined : handleClick}
    >
      {isOpen && !isLoading && (
        <Grid templateColumns='repeat(12, 1fr)'>
          <GridItem colSpan={1} />
          <GridItem colSpan={10} mb='15px'>
            <Heading as='h6' textAlign='center'>
              Add Item
            </Heading>
            <CardSearchMenu onCardSelect={handleAddCard} cards={allCards} />
            <Text textAlign='center' mb='35px'>
              or create a new analytic
            </Text>
            <ArgumentAnalyticCreateForm onCreate={handleCreateAnalytic} />
          </GridItem>
          <GridItem colSpan={1}>
            <IconButton aria-label='Close' onClick={() => setOpen(false)} bg='none'>
              <CloseIcon />
            </IconButton>
          </GridItem>
        </Grid>
      )}
      {isOpen && isLoading && <Spinner />}
      {!isOpen && (
        <IconButton aria-label='Add card' bg='none'>
          <AddIcon fontSize='large' />
        </IconButton>
      )}
    </Flex>
  )
}

export default AddItemToArgumentButton

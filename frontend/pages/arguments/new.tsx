import { GetServerSideProps } from 'next'
import { FC, FormEvent, useContext, useEffect, useState } from 'react'
import { useRouter } from 'next/router'
import { Heading, Input, Text, Button, useColorModeValue } from '@chakra-ui/react'
import useWindowSize from 'lib/hooks/useWindowSize'
import redirectToLogin from 'lib/redirectToLogin'
import theme from 'lib/theme'
import ArgumentCardSelector from 'components/arguments/ArgumentCardSelector'
import CardPreview from 'types/CardPreview'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'

const NewArgument: FC = () => {
  const [name, setName] = useState('')
  const [unselectedCards, setUnselectedCards] = useState<CardPreview[] | null>(null)
  const [selectedCards, setSelectedCards] = useState<CardPreview[]>([])
  const { jwt } = useContext(userContext)

  const { setErrorMessage } = useContext(errorMessageContext)

  const { width } = useWindowSize(1920, 1080)
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (name.length > 128) {
      setErrorMessage("Your argument's name cannot be more than 128 characters")
      return
    }
    if (name.length < 1) {
      setErrorMessage('The name must be at least one character')
      return
    }

    const cardIds = selectedCards.map(c => c.id)
    const response = await fetch('/api/v1/arguments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${jwt}` },
      body: JSON.stringify({
        name,
        cardIds
      })
    })
    if (response.ok) {
      const argId = await response.text()
      router.push(`/arguments/id/${argId}`)
    } else if (response.status === 400) {
      setErrorMessage('Error creating argument')
    } else if (response.status === 401 || response.status === 403) {
      setErrorMessage('An authentication error occurred. Make sure that you are logged in')
    } else if (response.status === 500) {
      setErrorMessage('A server error occurred during your request. Please try again')
    } else {
      setErrorMessage(`An unknown error occurred. Status code: ${response.status}`)
    }
  }

  const fetchCards = async () => {
    if (!jwt) {
      router.push(`/login?redirect=${encodeURIComponent('/arguments/new')}`)
      return
    }
    const response = await fetch('/api/v1/cards/previews', {
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      const c = await response.json()
      setUnselectedCards(c)
    } else {
      setErrorMessage(`Error fetching cards. Status code: ${response.status}`)
    }
  }

  // fetch cards on the client so that it will load faster
  // todo probably want to make this load the 10 most recent cards accessed by the user and then search for the rest
  useEffect(() => {
    fetchCards()
  }, [])

  const handleCardSelect = (id: string) => {
    const filtered = unselectedCards.filter(c => c.id === id)
    if (filtered.length === 0) {
      setErrorMessage('Error selecting a card. Please try again')
    }
    const fullCard = filtered[0]
    setSelectedCards([...selectedCards, fullCard])
    setUnselectedCards(unselectedCards.filter(c => c.id !== id))
  }

  const handleCardRemove = (id: string) => {
    const filtered = selectedCards.filter(c => c.id === id)
    if (filtered.length === 0) {
      setErrorMessage('Error unselecting a card. Please try again')
    }
    const fullCard = filtered[0]
    setUnselectedCards([...unselectedCards, fullCard])
    setSelectedCards(selectedCards.filter(c => c.id !== id))
  }

  return (
    <DashboardPage>
      <div style={{ paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: width >= theme.breakpoints.values.lg ? '65%' : '80%', margin: '7.5vh auto' }}>
          <div>
            <Text fontSize='24px' fontWeight='bold'>
              Create a new argument
            </Text>
            <div
              style={{
                width: '100%',
                margin: '2vh 0',
                height: 1,
                backgroundColor: theme.palette.lightGrey.main
              }}
            />
          </div>

          {/* description of arguments */}
          <div>
            <Text color='darkGray' fontSize='16px' m='10px 0'>
              Arguments are lists of cards (or analytics) that go together. You can include all of the cards in an argument
              in a speech, making them very convenient for grouping cards that are commonly read together.
            </Text>
          </div>

          <form onSubmit={handleSubmit}>
            {/* tag */}
            <div>
              <label htmlFor='name' id='nameLabel'>
                <Heading as='h3' fontSize='18px' fontWeight='medium'>
                  Name
                  <span style={{ fontWeight: 300 }}>*</span>
                </Heading>
              </label>
              <Text color='darkGray' id='nameDescription' fontSize='14px' m='6px 0'>
                Give your argument a descriptive name -- it should be unique so you can find it easier
              </Text>
              <Input
                type='text'
                id='name'
                value={name}
                onChange={e => setName(e.target.value)}
                w='100%'
                borderColor={borderColor}
                _hover={{
                  borderColor: 'cardtownBlue'
                }}
                _focus={{
                  borderColor: 'cardtownBlue'
                }}
                name='Name'
                aria-labelledby='nameLabel'
                aria-describedby='nameDescription'
              />
            </div>

            {/* card selector */}
            <div style={{ marginTop: 25 }}>
              <Heading as='h3' fontSize='18px' fontWeight='medium'>
                Cards
              </Heading>
              <Text color='darkGray' fontSize='14px' m='6px 0'>
                Add some cards to your argument. If you're still working on them, you can always modify arguments later.
              </Text>
              {unselectedCards && (
                <ArgumentCardSelector
                  cardsInArgument={selectedCards}
                  cardsNotInArgument={unselectedCards}
                  onCardSelect={handleCardSelect}
                  onCardRemove={handleCardRemove}
                  windowWidth={width}
                />
              )}
            </div>

            <div style={{ marginTop: 10, marginBottom: -5 }}>
              <Button type='submit' colorScheme='blue' bg='cardtownBlue' color='white'>
                Finish
              </Button>
            </div>
          </form>
        </div>
      </div>
    </DashboardPage>
  )
}

export default NewArgument

export const getServerSideProps: GetServerSideProps = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/cards/all')
  }
  return {
    props: {}
  }
}

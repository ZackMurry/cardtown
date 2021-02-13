import { Button, TextField, Typography } from '@material-ui/core'
import { parse } from 'cookie'
import { GetServerSideProps, NextPage } from 'next'
import { FormEvent, useEffect, useState } from 'react'
import { useRouter } from 'next/router'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../components/utils/redirectToLogin'
import theme from '../../components/utils/theme'
import ArgumentCardSelector from '../../components/arguments/ArgumentCardSelector'
import CardPreview from '../../components/types/CardPreview'
import ErrorAlert from '../../components/utils/ErrorAlert'

interface Props {
  jwt?: string
}

const NewArgument: NextPage<Props> = ({ jwt }) => {
  const [ name, setName ] = useState('')
  const [ unselectedCards, setUnselectedCards ] = useState<CardPreview[] | null>(null)
  const [ selectedCards, setSelectedCards ] = useState<CardPreview[]>([])

  const [ errorText, setErrorText ] = useState('')

  const { width } = useWindowSize(1920, 1080)
  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (name.length > 128) {
      setErrorText('Your argument\'s name cannot be more than 128 characters')
      return
    }
    if (name.length < 1) {
      setErrorText('The name must be at least one character')
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
      console.log(await response.text())
    } else if (response.status === 400) {
      setErrorText('Error creating argument')
    } else if (response.status === 401 || response.status === 403) {
      setErrorText('An authentication error occurred. Make sure that you are logged in')
    } else if (response.status === 500) {
      setErrorText('A server error occurred during your request. Please try again')
    } else {
      setErrorText(`An unknown error occurred. Status code: ${response.status}`)
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
      setErrorText(`Error fetching cards. Status code: ${response.status}`)
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
      setErrorText('Error selecting a card. Please try again')
    }
    const fullCard = filtered[0]
    setSelectedCards([ ...selectedCards, fullCard ])
    setUnselectedCards(unselectedCards.filter(c => c.id !== id))
  }

  const handleCardRemove = (id: string) => {
    const filtered = selectedCards.filter(c => c.id === id)
    if (filtered.length === 0) {
      setErrorText('Error unselecting a card. Please try again')
    }
    const fullCard = filtered[0]
    setUnselectedCards([ ...unselectedCards, fullCard ])
    setSelectedCards(selectedCards.filter(c => c.id !== id))
  }

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar windowWidth={width} pageName='Arguments' />

      {/* todo probly want to center all of these on the page, ignoring the sidebar */}
      <div style={{ paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: width >= theme.breakpoints.values.lg ? '65%' : '80%', margin: '7.5vh auto' }}>
          <div>
            <Typography
              style={{
                color: theme.palette.darkGrey.main,
                textTransform: 'uppercase',
                fontSize: 11,
                marginTop: 19,
                letterSpacing: 0.5
              }}
            >
              New argument
            </Typography>
            <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
              Create a new argument
            </BlackText>
            <div
              style={{
                width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
              }}
            />
          </div>

          {/* description of arguments */}
          <div>
            <Typography color='textSecondary' style={{ fontSize: 16, margin: '10px 0' }}>
              Arguments are lists of cards (or analytics) that go together. You can include all of the cards in an argument in a speech, making
              them very convenient for grouping cards that are commonly read together.
            </Typography>
          </div>

          <form onSubmit={handleSubmit}>

            {/* tag */}
            <div>
              <label htmlFor='name' id='nameLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Name
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <Typography color='textSecondary' id='nameDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Give your argument a descriptive name -- it should be unique so you can find it easier
              </Typography>
              <TextField
                id='name'
                variant='outlined'
                value={name}
                onChange={e => setName(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                InputProps={{
                  inputProps: {
                    name: 'tag',
                    'aria-labelledby': 'nameLabel',
                    'aria-describedby': 'nameDescription'
                  }
                }}
              />
            </div>

            {/* card selector */}
            <div style={{ marginTop: 25 }}>
              <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                Cards
              </BlackText>
              <Typography color='textSecondary' style={{ fontSize: 14, margin: '6px 0' }}>
                Add some cards to your argument. If you're still working on them, you can always modify arguments later.
              </Typography>
              {
                unselectedCards && (
                  <ArgumentCardSelector
                    cardsInArgument={selectedCards}
                    cardsNotInArgument={unselectedCards}
                    onCardSelect={handleCardSelect}
                    onCardRemove={handleCardRemove}
                    windowWidth={width}
                  />
                )
              }
            </div>

            <div style={{ marginTop: 10, marginBottom: -5 }}>
              <Button type='submit' variant='contained' color='primary' style={{ textTransform: 'none' }}>
                <Typography>
                  Finish
                </Typography>
              </Button>
            </div>
          </form>
        </div>
      </div>
      {
        errorText && <ErrorAlert onClose={() => setErrorText('')} text={errorText} />
      }
    </div>
  )
}

export default NewArgument

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  let jwt: string | null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, '/cards/all')
    return {
      props: {}
    }
  }
  return {
    props: {
      jwt
    }
  }
}

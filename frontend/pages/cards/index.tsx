import { useContext, useEffect, useState } from 'react'
import { GetServerSideProps, NextPage } from 'next'
import Link from 'next/link'
import { Box, GridItem, Grid, Text, Tooltip, useColorModeValue, Stack } from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import useWindowSize from 'lib/hooks/useWindowSize'
import redirectToLogin from 'lib/redirectToLogin'
import SearchCards from 'components/cards/SearchCards'
import { CardPreview } from 'types/card'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'
import { useRouter } from 'next/router'
import PrimaryButton from 'components/utils/PrimaryButton'

interface Props {
  cards?: CardPreview[]
  errorText?: string
}

// todo option to include deleted cards
const AllCards: NextPage<Props> = ({ cards: initialCards, errorText }) => {
  const [cards, setCards] = useState(initialCards)
  const { width } = useWindowSize(1920, 1080)
  const { setErrorMessage } = useContext(errorMessageContext)
  const itemBgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('lightGrayBorder', 'darkGrayBorder')
  const tooltipBgColor = useColorModeValue('white', 'darkElevated')
  const router = useRouter()

  useEffect(() => {
    if (errorText) {
      setErrorMessage(errorText)
    }
  }, [])

  return (
    <DashboardPage>
      <Box w={{ base: '85%', sm: '80%', md: '70%', lg: '60%', xl: '55%' }} m='25px auto'>
        <Stack direction={{ base: 'column', lg: 'row' }} mb='15px' spacing='10px'>
          <Stack direction={{ base: 'column', md: 'row' }} spacing='10px'>
            <PrimaryButton onClick={() => router.push('/cards/import')}>Import cards</PrimaryButton>
            <PrimaryButton
              onClick={() => router.push('/cards/new')}
              leftIcon={<AddIcon w='24px' mt='-2px' />}
              iconSpacing='1'
              pl='10px'
            >
              Create new card
            </PrimaryButton>
          </Stack>
          <SearchCards
            cards={initialCards}
            onResults={setCards}
            onClear={() => setCards(initialCards)}
            windowWidth={width}
          />
        </Stack>
        <Grid templateColumns='repeat(4, 1fr)' visibility={{ base: 'hidden', lg: 'visible' }}>
          <GridItem colSpan={1} pl='20px'>
            <Text color='darkGray' fontWeight='medium'>
              Cite
            </Text>
          </GridItem>
          <GridItem colSpan={2} pl='10px'>
            <Text color='darkGray' fontWeight='medium'>
              Tag
            </Text>
          </GridItem>
        </Grid>

        {/* todo show information about the owner and make this expandable so that users can see the card body */}
        {cards.map(c => {
          let shortenedCite = c.cite
          let shortenedTag = c.tag
          if (c.cite.length > 50) {
            shortenedCite = c.cite.substring(0, 47) + '...'
          }
          if (c.tag.length > 100) {
            shortenedTag = c.tag.substring(0, 97) + '...'
          }
          return (
            <Link href={`/cards/id/${c.id}`} passHref key={c.id}>
              <a>
                <Grid
                  templateColumns='repeat(4, 1fr)'
                  bg={itemBgColor}
                  p='20px'
                  borderWidth='1px'
                  borderStyle='solid'
                  borderColor={borderColor}
                  style={{
                    borderRadius: 5,
                    margin: '15px 0',
                    cursor: 'pointer'
                  }}
                >
                  <GridItem colSpan={1}>
                    {shortenedCite === c.cite ? (
                      <Text color='darkGray'>{shortenedCite}</Text>
                    ) : (
                      <Tooltip
                        label={c.cite}
                        aria-label='Full cite of card'
                        bg={tooltipBgColor}
                        color='darkGray'
                        fontWeight='normal'
                      >
                        <Text color='darkGray'>{shortenedCite}</Text>
                      </Tooltip>
                    )}
                  </GridItem>
                  <GridItem colSpan={2}>
                    {shortenedTag === c.tag ? (
                      <Text color='darkGray'>{shortenedTag}</Text>
                    ) : (
                      <Tooltip
                        label={c.tag}
                        aria-label='Full tag of card'
                        bg={tooltipBgColor}
                        color='darkGray'
                        fontWeight='normal'
                      >
                        <Text color='darkGray'>{shortenedTag}</Text>
                      </Tooltip>
                    )}
                  </GridItem>
                </Grid>
              </a>
            </Link>
          )
        })}
      </Box>
    </DashboardPage>
  )
}

export default AllCards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/cards/all')
    return {
      props: {}
    }
  }

  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch((dev ? 'http://localhost' : 'https://cardtown.co') + '/api/v1/cards', {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let cards: CardPreview[] | null = null
  let errorText: string | null = null
  if (response.ok) {
    cards = await response.json()
  } else if (response.status === 500) {
    errorText = 'There was a server error. Please try again'
  } else if (response.status === 406) {
    errorText = 'Account not found. This is likely a bug and has been reported as such. Please try again'
  } else {
    errorText = 'There was an error finding the cards. Please try again'
  }
  return {
    props: {
      cards,
      errorText
    }
  }
}

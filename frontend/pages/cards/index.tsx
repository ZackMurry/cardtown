import { useContext, useEffect, useState } from 'react'
import { GetServerSideProps, NextPage } from 'next'
import Link from 'next/link'
import { Box, GridItem, Grid, Text, Tooltip, useColorModeValue, Stack, Flex, Checkbox } from '@chakra-ui/react'
import { AddIcon, WarningIcon } from '@chakra-ui/icons'
import redirectToLogin from 'lib/redirectToLogin'
import SearchCards from 'components/cards/SearchCards'
import { CardPreview } from 'types/card'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'
import { useRouter } from 'next/router'
import PrimaryButton from 'components/utils/PrimaryButton'
import userContext from 'lib/hooks/UserContext'

interface Props {
  cards?: CardPreview[]
  errorText?: string
  showDeleted?: boolean
}

// todo option to include deleted cards
const Cards: NextPage<Props> = ({ cards: initialCards, errorText, showDeleted: initialShowDeleted }) => {
  const [allCards, setAllCards] = useState(initialCards)
  const [cardsInSearch, setCardsInSearch] = useState(initialCards)
  const [showDeleted, setShowDeleted] = useState(initialShowDeleted)
  const { setErrorMessage } = useContext(errorMessageContext)
  const itemBgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const tooltipBgColor = useColorModeValue('white', 'darkElevated')
  const deletedWarningColor = useColorModeValue('red.500', 'red.200')
  const textColor = useColorModeValue('darkText', 'darkGray')
  const { jwt } = useContext(userContext)
  const router = useRouter()

  useEffect(() => {
    if (errorText) {
      setErrorMessage(errorText)
    }
  }, [])

  const handleCheckChange = async () => {
    let response: Response
    if (showDeleted) {
      router.push('/cards')
      setShowDeleted(false)
      response = await fetch('/api/v1/cards', {
        headers: { Authorization: `Bearer ${jwt}` }
      })
    } else {
      router.push('/cards?showDeleted=true')
      setShowDeleted(true)
      response = await fetch('/api/v1/cards?showDeleted=true', {
        headers: { Authorization: `Bearer ${jwt}` }
      })
    }
    if (response.ok) {
      const newCards = (await response.json()) as CardPreview[]
      setAllCards(newCards)
      setCardsInSearch(newCards)
    } else if (response.status === 500) {
      setErrorMessage('There was a server error. Please try again')
    } else if (response.status === 406) {
      setErrorMessage('Account not found. Please try again')
    } else {
      setErrorMessage('There was an error fetching your cards. Please try again')
    }
  }

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
            cards={allCards}
            onResults={setCardsInSearch}
            onClear={() => setCardsInSearch(allCards)}
            showDeleted={showDeleted}
          />
        </Stack>
        <Checkbox
          defaultChecked={showDeleted}
          onChange={handleCheckChange}
          iconColor='white'
          ml='15px'
          mb='10px'
          color={textColor}
        >
          Show deleted cards
        </Checkbox>
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
        {cardsInSearch.map(c => {
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
                  borderRadius='5px'
                  m='15px 0'
                  cursor='pointer'
                >
                  <GridItem colSpan={1}>
                    {shortenedCite === c.cite ? (
                      <Text color={textColor}>{shortenedCite}</Text>
                    ) : (
                      <Tooltip
                        label={c.cite}
                        aria-label='Full cite of card'
                        bg={tooltipBgColor}
                        color={textColor}
                        fontWeight='normal'
                      >
                        <Text color={textColor}>{shortenedCite}</Text>
                      </Tooltip>
                    )}
                  </GridItem>
                  <GridItem colSpan={2}>
                    {shortenedTag === c.tag ? (
                      <Text color={textColor}>{shortenedTag}</Text>
                    ) : (
                      <Tooltip
                        label={c.tag}
                        aria-label='Full tag of card'
                        bg={tooltipBgColor}
                        color={textColor}
                        fontWeight='normal'
                      >
                        <Text color={textColor}>{shortenedTag}</Text>
                      </Tooltip>
                    )}
                  </GridItem>
                  <GridItem colSpan={1}>
                    <Flex justifyContent='flex-end' p='0 15px'>
                      {c.deleted && (
                        <Tooltip
                          label='This card has been deleted'
                          bg={tooltipBgColor}
                          color={textColor}
                          fontWeight='normal'
                        >
                          <WarningIcon color={deletedWarningColor} fontSize='larger' />
                        </Tooltip>
                      )}
                    </Flex>
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

export default Cards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res, query }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/cards/all')
    return {
      props: {}
    }
  }
  const showDeleted = Boolean(query.showDeleted)

  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch(
    (dev ? 'http://localhost' : 'https://cardtown.co') + `/api/v1/cards?showDeleted=${showDeleted.toString()}`,
    {
      headers: { Authorization: `Bearer ${jwt}` }
    }
  )
  let cards: CardPreview[] | null = null
  let errorText: string | null = null
  if (response.ok) {
    cards = await response.json()
  } else if (response.status === 500) {
    errorText = 'There was a server error. Please try again'
  } else if (response.status === 406) {
    errorText = 'Account not found. Please try again'
  } else {
    errorText = 'There was an error fetching your cards. Please try again'
  }
  return {
    props: {
      cards,
      errorText,
      showDeleted
    }
  }
}

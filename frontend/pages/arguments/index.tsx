import { GetServerSideProps } from 'next'
import { FC, useContext, useEffect, useState } from 'react'
import { Box, Button, Checkbox, Flex, Grid, GridItem, Stack, Text, Tooltip, useColorModeValue } from '@chakra-ui/react'
import { AddIcon, ChevronDownIcon, ChevronUpIcon, WarningIcon } from '@chakra-ui/icons'
import Link from 'next/link'
import PrimaryButton from 'components/utils/PrimaryButton'
import { ArgumentPreview } from 'types/argument'
import SearchArguments from 'components/arguments/SearchArguments'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'
import redirectToLogin from 'lib/redirectToLogin'
import { useRouter } from 'next/router'
import userContext from 'lib/hooks/UserContext'

type Sort = { by: 'none' | 'name' | 'cards'; ascending: boolean }

interface Props {
  args?: ArgumentPreview[]
  fetchErrorText?: string
  showDeleted?: boolean
}

// todo: improve responsiveness and make top thing look better
// todo: add show deleted option and warning that an argument is deleted
const ArgumentsPage: FC<Props> = ({ args: initialArgs, fetchErrorText, showDeleted: initialShowDeleted }) => {
  const [allArgs, setAllArgs] = useState(initialArgs)
  const [argsInSearch, setArgsInSearch] = useState(initialArgs)
  const [sort, setSort] = useState<Sort>({ by: 'none', ascending: false })
  const [showDeleted, setShowDeleted] = useState(initialShowDeleted)
  const { setErrorMessage } = useContext(errorMessageContext)
  const itemBgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('lightGrayBorder', 'darkGrayBorder')
  const tooltipBgColor = useColorModeValue('white', 'darkElevated')
  const deletedWarningColor = useColorModeValue('red.500', 'red.200')
  const router = useRouter()
  const { jwt } = useContext(userContext)

  useEffect(() => {
    if (fetchErrorText) {
      setErrorMessage(fetchErrorText)
    }
  }, [])

  const handleSearchResults = (newArgs: ArgumentPreview[]) => {
    setSort({ by: 'none', ascending: false })
    setArgsInSearch(newArgs)
  }

  const handleClearSearch = () => {
    setSort({ by: 'none', ascending: false })
    setArgsInSearch(allArgs)
  }

  const handleNameSortUpdate = () => {
    if (sort.by !== 'name') {
      updateSort({ by: 'name', ascending: false })
    } else {
      updateSort({ by: sort.ascending ? 'none' : 'name', ascending: sort.by !== 'name' ? false : !sort.ascending })
    }
  }

  const handleCardSortUpdate = () => {
    if (sort.by !== 'cards') {
      updateSort({ by: 'cards', ascending: false })
    } else {
      updateSort({ by: sort.ascending ? 'none' : 'cards', ascending: sort.by !== 'cards' ? false : !sort.ascending })
    }
  }

  const updateSort = (options: Sort) => {
    setSort(options)
    if (options.by === 'name') {
      if (options.ascending) {
        setArgsInSearch(argsInSearch.sort((a, b) => b.name.localeCompare(a.name)))
      } else {
        setArgsInSearch(argsInSearch.sort((a, b) => a.name.localeCompare(b.name)))
      }
    } else if (options.by === 'cards') {
      if (options.ascending) {
        setArgsInSearch(argsInSearch.sort((a, b) => a.cards.length - b.cards.length))
      } else {
        setArgsInSearch(argsInSearch.sort((a, b) => b.cards.length - a.cards.length))
      }
    }
  }

  const handleCheckChange = async () => {
    let response: Response
    if (showDeleted) {
      router.push('/arguments')
      setShowDeleted(false)
      response = await fetch('/api/v1/arguments', {
        headers: { Authorization: `Bearer ${jwt}` }
      })
    } else {
      router.push('/arguments?showDeleted=true')
      setShowDeleted(true)
      response = await fetch('/api/v1/arguments?showDeleted=true', {
        headers: { Authorization: `Bearer ${jwt}` }
      })
    }
    if (response.ok) {
      const newArgs = (await response.json()) as ArgumentPreview[]
      setAllArgs(newArgs)
      setArgsInSearch(newArgs)
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
        <Stack direction={{ base: 'column', md: 'row' }} mb='15px' spacing='10px'>
          <PrimaryButton
            as='a'
            leftIcon={<AddIcon w='24px' mt='-2px' />}
            iconSpacing='1'
            pl='10px'
            href='/arguments/new'
            minW='215px'
          >
            Create new argument
          </PrimaryButton>
          <SearchArguments args={allArgs} onResults={handleSearchResults} onClear={handleClearSearch} />
        </Stack>
        <Checkbox defaultChecked={showDeleted} onChange={handleCheckChange} iconColor='white' ml='15px' mb='10px'>
          Show deleted arguments
        </Checkbox>
        <Grid templateColumns='repeat(4, 1fr)' display={{ base: 'none', md: 'grid' }}>
          <GridItem colSpan={2}>
            <Button
              variant='ghost'
              color='darkGray'
              fontWeight='medium'
              ml='5px'
              rightIcon={sort.by === 'name' && (sort.ascending ? <ChevronUpIcon /> : <ChevronDownIcon />)}
              onClick={handleNameSortUpdate}
            >
              Name
            </Button>
          </GridItem>
          <GridItem colSpan={1}>
            <Button
              variant='ghost'
              color='darkGray'
              fontWeight='medium'
              rightIcon={sort.by === 'cards' && (sort.ascending ? <ChevronUpIcon /> : <ChevronDownIcon />)}
              onClick={handleCardSortUpdate}
            >
              Number of cards
            </Button>
          </GridItem>
        </Grid>

        {/* todo show information about the owner and make this expandable so that users can see the card body */}
        {argsInSearch.map(a => (
          <Link href={`/arguments/id/${a.id}`} passHref key={a.id}>
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
                <GridItem colSpan={2}>
                  <Text color='darkGray'>{a.name}</Text>
                </GridItem>
                <GridItem colSpan={1} pl='20px'>
                  <Text color='darkGray'>{a.cards.length}</Text>
                </GridItem>
                <GridItem colSpan={1}>
                  <Flex justifyContent='flex-end' p='0 15px'>
                    {a.deleted && (
                      <Tooltip label='This card has been deleted' bg={tooltipBgColor} color='darkGray' fontWeight='normal'>
                        <WarningIcon color={deletedWarningColor} fontSize='larger' />
                      </Tooltip>
                    )}
                  </Flex>
                </GridItem>
              </Grid>
            </a>
          </Link>
        ))}
      </Box>
    </DashboardPage>
  )
}

export default ArgumentsPage

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res, query }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/arguments')
    return {
      props: {}
    }
  }
  const showDeleted = Boolean(query.showDeleted)
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const response = await fetch(`${domain}/api/v1/arguments?showDeleted=${showDeleted.toString()}`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (response.ok) {
    const args = (await response.json()) as ArgumentPreview[]
    return {
      props: {
        args,
        showDeleted
      }
    }
  }
  let fetchErrorText: string | null
  if (response.status === 400) {
    fetchErrorText = 'Error fetching arguments'
  } else if (response.status === 401 || response.status === 403) {
    redirectToLogin(res, '/arguments/all')
    return {
      props: {}
    }
  } else if (response.status === 500) {
    fetchErrorText = 'A server error occured during your request. Please try again'
  } else {
    fetchErrorText = `An unknown error occured during your request. Status code: ${response.status}`
  }
  return {
    props: {
      fetchErrorText,
      showDeleted
    }
  }
}

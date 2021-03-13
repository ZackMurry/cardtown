import {
  Box, Heading, Text, IconButton
} from '@chakra-ui/react'
import { ChevronDownIcon } from '@chakra-ui/icons'
import { FC, MutableRefObject } from 'react'
import theme from 'lib/theme'
import LandingPageJoinBetaMobile from './LandingPageJoinBetaMobile'

interface Props {
  tilesSectionRef: MutableRefObject<any>
}

const LandingPageMobileHero: FC<Props> = ({ tilesSectionRef }) => (
  <div
    style={{
      width: '90%',
      margin: '0vh auto 7.5vh auto',
      minHeight: '60vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center'
    }}
  >
    <Heading
      variant='h1'
      width='100%'
      padding='0 5%'
      textAlign='center'
      fontSize='36px'
      fontWeight='400px'
    >
      {/* eslint-disable-next-line react/jsx-one-expression-per-line */}
      The hub for your <span style={{ color: theme.palette.primary.main }}>debate cards</span>
    </Heading>
    <Heading
      as='h5'
      margin='25px auto'
      width='90%'
      textAlign='center'
      fontWeight='300'
      fontSize='18px'
    >
      Cardtown is the easiest way to store, search, and read all of your debate cards.
    </Heading>
    <div style={{ margin: '5px auto', width: '90%' }}>
      <LandingPageJoinBetaMobile />
    </div>

    <Box
      marginTop='3vh'
      display='flex'
      flexDir='column'
      alignItems='center'
    >
      <Text color='lightBlue' fontSize='14px' textAlign='center'>
        Or learn more
      </Text>
      <IconButton aria-label='Learn more' onClick={() => tilesSectionRef.current.scrollIntoView()}>
        <ChevronDownIcon color='lightBlue' fontSize='large' />
      </IconButton>
    </Box>

  </div>
)

export default LandingPageMobileHero

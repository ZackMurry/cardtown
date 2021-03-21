import { ChevronDownIcon } from '@chakra-ui/icons'
import { Heading, Text, IconButton } from '@chakra-ui/react'
import { FC, MutableRefObject } from 'react'
import theme from 'lib/theme'
import LandingPageJoinBeta from './LandingPageJoinBeta'

interface Props {
  tilesSectionRef: MutableRefObject<any>
}

const LandingPageHero: FC<Props> = ({ tilesSectionRef }) => (
  <div
    style={{
      width: '60%',
      margin: '5vh auto 7.5vh auto',
      height: '60vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center'
    }}
  >
    <Heading as='h1' width='100%' textAlign='center' fontSize={64} marginTop='9vh' fontWeight={400}>
      {/* eslint-disable-next-line react/jsx-one-expression-per-line */}
      The hub for your <span style={{ color: theme.palette.primary.main }}>debate cards</span>
    </Heading>
    <Heading as='h5' margin='25px auto' width='58%' textAlign='center' fontWeight={300} fontSize={24}>
      Cardtown is the easiest way to store, search, and read all of your debate cards.
    </Heading>
    <div style={{ margin: '5px auto' }}>
      <LandingPageJoinBeta />
    </div>

    <div
      style={{
        marginTop: '3vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
      }}
    >
      <Text color='lightBlue' style={{ fontSize: 14, textAlign: 'center' }}>
        Or learn more
      </Text>
      <IconButton
        aria-label='Learn more'
        onClick={() => tilesSectionRef.current.scrollIntoView()}
        backgroundColor='transparent'
      >
        <ChevronDownIcon color='lightBlue' fontSize='large' />
      </IconButton>
    </div>
  </div>
)

export default LandingPageHero

import Link from 'next/link'
import { FC } from 'react'
import { Heading, Button, useColorModeValue, Flex } from '@chakra-ui/react'
import theme from 'lib/theme'

const LandingPageNavbar: FC = () => (
  <>
    {/* for padding purposes */}
    <div style={{ height: '15vh' }} />
    <Flex
      position='absolute'
      top='0px'
      left='0px'
      justifyContent='space-between'
      p='31px 15vw'
      w='100%'
      h='10vh'
      alignItems='center'
      role='navigation'
    >
      <Heading as='h3' fontSize={28} fontWeight={500} paddingBottom={5}>
        card
        <span style={{ color: theme.palette.primary.main }}>town</span>
      </Heading>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          width: 349
        }}
      >
        <Link href='/features'>
          <a href='/features'>
            <Heading as='h4' fontSize={20} fontWeight={300}>
              Features
            </Heading>
          </a>
        </Link>

        <Link href='/about'>
          <a href='/about'>
            <Heading as='h4' fontSize={20} fontWeight={300}>
              About
            </Heading>
          </a>
        </Link>

        <Button color='cardtownBlue' variant='outline' style={{ border: '2px solid' }}>
          <Link href='/login'>
            <a href='/login'>
              <Heading as='h4' fontSize={20} padding='5px'>
                Login
              </Heading>
            </a>
          </Link>
        </Button>
      </div>
    </Flex>
  </>
)

export default LandingPageNavbar

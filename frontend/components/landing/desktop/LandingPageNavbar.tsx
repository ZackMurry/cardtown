import Link from 'next/link'
import { FC } from 'react'
import { Heading, Button } from '@chakra-ui/react'
import theme from 'lib/theme'

const LandingPageNavbar: FC = () => (
  <>
    {/* for padding purposes */}
    <div style={{ height: '15vh' }} />
    <div
      style={{
        position: 'absolute',
        top: 0,
        left: 0,
        backgroundColor: theme.palette.secondary.main,
        display: 'flex',
        justifyContent: 'space-between',
        padding: '31px 15vw',
        width: '100%',
        alignItems: 'center',
        height: '10vh'
      }}
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
    </div>
  </>
)

export default LandingPageNavbar

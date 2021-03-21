import { Heading } from '@chakra-ui/react'
import { FC } from 'react'

const LandingPageMobileNav: FC = () => (
  <div
    style={{
      height: '15vh',
      width: '100%',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    }}
  >
    <Heading variant='h5' textAlign='center' fontSize='22px' fontWeight='400'>
      Cardtown
    </Heading>
  </div>
)

export default LandingPageMobileNav

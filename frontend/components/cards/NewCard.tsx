import { Heading } from '@chakra-ui/react'
import AddRoundedIcon from '@material-ui/icons/AddRounded'
import Link from 'next/link'
import { FC } from 'react'
import theme from 'lib/theme'

const NewCard: FC = () => (
  <>
    <Link href='/cards/new' passHref>
      <a
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          width: '100%',
          height: '100%'
        }}
      >
        <Heading as='h5' fontSize={20} fontWeight='normal' paddingRight='5'>
          New card
        </Heading>
        <AddRoundedIcon style={{ fontSize: 50, color: theme.palette.text.secondary }} />
      </a>
    </Link>
  </>
)

export default NewCard

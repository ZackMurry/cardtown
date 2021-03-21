import { FC } from 'react'
import { Heading } from '@chakra-ui/react'
import Link from 'next/link'
import ArrowDownwardIcon from '@material-ui/icons/ArrowDownward'
import theme from 'lib/theme'

const ImportCard: FC = () => (
  <>
    <Link href='/cards/import' passHref>
      <a
        style={{
          width: '100%',
          height: '100%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}
      >
        <Heading as='h5' fontSize={20} fontWeight='normal' paddingRight={5}>
          Import cards
        </Heading>
        <ArrowDownwardIcon style={{ fontSize: 50, color: theme.palette.text.secondary }} />
      </a>
    </Link>
  </>
)

export default ImportCard

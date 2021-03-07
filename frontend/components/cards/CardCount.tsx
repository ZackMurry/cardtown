import { Heading } from '@chakra-ui/react'
import Link from 'next/link'
import { FC } from 'react'
import theme from '../utils/theme'

interface Props {
  count: number
}

const CardCount: FC<Props> = ({ count }) => (
  <Link href='/cards/all' passHref>
    <a
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        width: '100%',
        height: '100%'
      }}
    >
      <Heading as='h5' fontSize={20} fontWeight='normal'>
        <span style={{ fontWeight: 500, paddingRight: 5 }}>
          { count }
        </span>
        card
        {
          count !== 1 ? 's' : ''
        }
      </Heading>
      <div style={{ marginRight: 10 }}>
        <div
          style={{
            borderRadius: 5,
            border: `3px solid ${theme.palette.text.secondary}`,
            width: '1.75vw',
            minWidth: 20,
            height: '2.5vh',
            minHeight: 25
          }}
        />
        <div
          style={{
            marginLeft: 10,
            marginTop: -15,
            borderRadius: 5,
            border: `3px solid ${theme.palette.text.secondary}`,
            width: '1.75vw',
            minWidth: 20,
            height: '2.5vh',
            minHeight: 25
          }}
        />
      </div>
    </a>
  </Link>
)

export default CardCount

import { FC } from 'react'
import { Typography } from '@material-ui/core'
import Link from 'next/link'
import ArrowDownwardIcon from '@material-ui/icons/ArrowDownward'
import theme from '../utils/theme'

const ImportCard: FC = () => (
  <>
    <Link href='/cards/import'>
      <a
        href='/cards/import'
        style={{
          width: '100%',
          height: '100%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}
      >
        <Typography
          variant='h5'
          style={{
            fontSize: 22,
            fontWeight: 500,
            paddingRight: 5,
            color: theme.palette.blueBlack.main
          }}
        >
          Import cards
        </Typography>
        <ArrowDownwardIcon style={{ fontSize: 50, color: theme.palette.text.secondary }} />
      </a>
    </Link>
  </>
)

export default ImportCard

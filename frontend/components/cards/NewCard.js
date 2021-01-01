import { Typography } from '@material-ui/core'
import AddRoundedIcon from '@material-ui/icons/AddRounded'
import Link from 'next/link'
import theme from '../utils/theme'

export default function NewCard() {
  return (
    <>
      <Link href='/cards/new'>
        <a
          href='/cards/new'
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            width: '100%',
            height: '100%'
          }}
        >
          <Typography
            variant='h5'
            style={{
              fontSize: 28,
              fontWeight: 500,
              paddingRight: 5,
              color: theme.palette.blueBlack.main
            }}
          >
            Create new card
          </Typography>
          <AddRoundedIcon style={{ fontSize: 50, color: theme.palette.text.secondary }} />
        </a>
      </Link>
    </>
  )
}

import { Typography } from '@material-ui/core'
import AddRoundedIcon from '@material-ui/icons/AddRounded'
import Link from 'next/link'
import { FC } from 'react'
import theme from 'lib/theme'

const NewArgument: FC = () => (
  <>
    <Link href='/arguments/new'>
      <a
        href='/arguments/new'
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
            fontSize: 22,
            fontWeight: 500,
            paddingRight: 5,
            color: theme.palette.blueBlack.main
          }}
        >
          Create new argument
        </Typography>
        <AddRoundedIcon style={{ fontSize: 50, color: theme.palette.text.secondary }} />
      </a>
    </Link>
  </>
)

export default NewArgument

import { IconButton, Typography } from '@material-ui/core'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import BlackText from '../../utils/BlackText'
import theme from '../../utils/theme'
import LandingPageJoinBeta from './LandingPageJoinBeta'

export default function LandingPageHero({ tilesSectionRef }) {
  return (
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
      <BlackText
        variant='h1'
        style={{
          width: '100%', textAlign: 'center', fontSize: 72, marginTop: '9vh', fontWeight: 400
        }}
      >
        {/* eslint-disable-next-line react/jsx-one-expression-per-line */}
        The hub for your <span style={{ color: theme.palette.primary.main }}>debate cards</span>
      </BlackText>
      <BlackText
        variant='h5'
        style={{
          margin: '25px auto', width: '58%', textAlign: 'center', fontWeight: 300
        }}
      >
        Cardtown is the easiest way to store, search, and read all of your debate cards.
      </BlackText>
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
        <Typography color='textSecondary' style={{ fontSize: 14, textAlign: 'center' }}>
          Or learn more
        </Typography>
        <IconButton onClick={() => tilesSectionRef.current.scrollIntoView()}>
          <ExpandMoreIcon style={{ color: theme.palette.text.secondary }} fontSize='small' />
        </IconButton>
      </div>

    </div>
  )
}

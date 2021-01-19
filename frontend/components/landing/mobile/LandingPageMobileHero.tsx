import { IconButton, Typography } from '@material-ui/core'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { FC, MutableRefObject } from 'react'
import BlackText from '../../utils/BlackText'
import theme from '../../utils/theme'
import LandingPageJoinBetaMobile from './LandingPageJoinBetaMobile'

interface Props {
  tilesSectionRef: MutableRefObject<any>
}

const LandingPageMobileHero: FC<Props> = ({ tilesSectionRef }) => (
  <div
    style={{
      width: '90%',
      margin: '0vh auto 7.5vh auto',
      minHeight: '60vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center'
    }}
  >
    <BlackText
      variant='h1'
      style={{
        width: '100%', textAlign: 'center', fontSize: 56, fontWeight: 400
      }}
    >
      {/* eslint-disable-next-line react/jsx-one-expression-per-line */}
      The hub for your <span style={{ color: theme.palette.primary.main }}>debate cards</span>
    </BlackText>
    <BlackText
      variant='h5'
      style={{
        margin: '25px auto', width: '80%', textAlign: 'center', fontWeight: 300, fontSize: 20
      }}
    >
      Cardtown is the easiest way to store, search, and read all of your debate cards.
    </BlackText>
    <div style={{ margin: '5px auto', width: '90%' }}>
      <LandingPageJoinBetaMobile />
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

export default LandingPageMobileHero

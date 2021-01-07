import { parse } from 'cookie'
import { useRouter } from 'next/router'
import { Grid, Typography } from '@material-ui/core'
import styles from '../../styles/Cards.module.css'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import theme from '../../components/utils/theme'
import BlackText from '../../components/utils/BlackText'
import CardCount from '../../components/cards/CardCount'
import NewCard from '../../components/cards/NewCard'
import ImportCard from '../../components/cards/ImportCard'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../components/utils/redirectToLogin'

export default function Cards({ jwt }) {
  const width = useWindowSize()?.width ?? 1920

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ?  '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>
        <Typography
          style={{
            color: theme.palette.darkGrey.main,
            textTransform: 'uppercase',
            fontSize: 11,
            marginTop: 19,
            letterSpacing: 0.5
          }}
        >
          Overview
        </Typography>
        <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
          Cards
        </BlackText>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
        <Grid container className={styles['card-grid']} role='grid' style={{ width: '80%' }}>
          <Grid
            item
            xs={12}
            md={6}
            lg={3}
            style={{
              borderRadius: 10,
              backgroundColor: theme.palette.secondary.main,
              border: `1px solid ${theme.palette.lightGrey.main}`,
              minHeight: '7.5vh',
              padding: 20,
              display: 'flex',
              alignItems: 'center'
            }}
          >
            <CardCount jwt={jwt} />
          </Grid>

          <Grid
            item
            xs={12}
            md={6}
            lg={3}
            style={{
              borderRadius: 10,
              backgroundColor: theme.palette.secondary.main,
              border: `1px solid ${theme.palette.lightGrey.main}`,
              minHeight: '7.5vh',
              display: 'flex',
              alignItems: 'center',
              padding: 20
            }}
          >
            <NewCard />
          </Grid>

          <Grid
            item
            xs={12}
            md={6}
            lg={3}
            style={{
              borderRadius: 10,
              backgroundColor: theme.palette.secondary.main,
              border: `1px solid ${theme.palette.lightGrey.main}`,
              minHeight: '7.5vh',
              display: 'flex',
              alignItems: 'center',
              padding: 20
            }}
          >
            <ImportCard />
          </Grid>

        </Grid>
      </div>
    </div>
  )
}

export async function getServerSideProps({ req, res }) {
  let jwt = null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, '/cards')
    return {}
  }
  return {
    props: {
      jwt
    }
  }
}

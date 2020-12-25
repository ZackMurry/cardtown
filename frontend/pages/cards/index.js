import { Grid, Typography } from '@material-ui/core'
import AddRoundedIcon from '@material-ui/icons/AddRounded'
import Link from 'next/link'
import styles from '../../styles/Cards.module.css'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import theme from '../../components/utils/theme'
import BlackText from '../../components/utils/BlackText'

export default function Cards() {
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Cards' />
      <div style={{ marginLeft: '12.9vw', paddingLeft: 38, paddingRight: 38 }}>
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
        <Grid container className={styles['card-grid']} role='grid'>
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
            <Link href='/cards/all' passHref>
              <a style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', height: '100%' }}>
                <Typography variant='h5' style={{ fontSize: 28, color: theme.palette.blueBlack.main }}>
                  <span style={{ fontWeight: 500, paddingRight: 5 }}>
                    128
                    {/* temp value */}
                  </span>
                  cards
                </Typography>
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
            <Link href='/cards/new' passHref>
              <a style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', height: '100%' }}>
                <Typography variant='h5' style={{ fontSize: 28, fontWeight: 500, paddingRight: 5, color: theme.palette.blueBlack.main }}>
                  Create new card
                </Typography>
                <AddRoundedIcon style={{ fontSize: 50, color: theme.palette.text.secondary, marginRight: 10 }} />
              </a>
            </Link>
          </Grid>
        </Grid>
      </div>
    </div>
  )
}

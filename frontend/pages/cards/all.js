import { useEffect, useMemo, useState } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import theme from '../../components/utils/theme'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import { Grid, Tooltip, Typography } from '@material-ui/core'
import BlackText from '../../components/utils/BlackText'

export default function all() {
  const { width } = useWindowSize()
  const [ cards, setCards ] = useState([])
  const jwt = useMemo(() => Cookie.get('jwt'), [])
  const router = useRouter()

  const loadCards = async () => {
    if (!jwt) {
      router.push(`/login?redirect=${encodeURIComponent('/cards/all')}`)
      return
    }
    const response = await fetch('/api/v1/cards', {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
    })
    if (response.ok) {
      setCards(await response.json())
    } else {
      console.warn(response.status + ': ' + response.statusText)
    }
  }

  useEffect(() => {
    loadCards()
  }, [ ])

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main, minHeight: '100%', overflow: 'auto' }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>

        <Typography
          style={{
            color: theme.palette.darkGrey.main,
            textTransform: 'uppercase',
            fontSize: 11,
            marginTop: 19,
            letterSpacing: 0.5
          }}
        >
          All
        </Typography>
        <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
          All cards
        </BlackText>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
        {
          width >= theme.breakpoints.values.lg && (
            <Grid container>
              <Grid item lg={3} style={{ paddingLeft: 20 }}>
                <BlackText style={{ fontWeight: 500 }}>
                  Cite
                </BlackText>
              </Grid>
              <Grid item lg={6} style={{ paddingLeft: 10 }}>
                <BlackText style={{ fontWeight: 500 }}>
                  Tag
                </BlackText>
              </Grid>
            </Grid>
          )
        }
        
        {/* todo show information about the owner and make this expandable so that users can see the card body */}
        {
          cards.map(c => {
            let shortenedCite = c.cite
            let shortenedTag = c.tag
            if (c.cite.length > 50) {
              shortenedCite = c.cite.substring(0, 47) + '...'
            }
            if (c.tag.length > 100) {
              shortenedTag = c.tag.substring(0, 97) + '...'
            }
            return (
              <Grid
                container
                style={{
                  backgroundColor: theme.palette.secondary.main,
                  padding: 20,
                  border: `1px solid ${theme.palette.lightGrey.main}`,
                  borderRadius: 5,
                  margin: '15px 0',
                  cursor: 'pointer'
                }}
                key={c.id}
                onClick={() => router.push(`/cards/id/${c.id}`)}
              >
                <Grid item xs={12} lg={3}>
                  {
                    shortenedCite === c.cite
                      ? (
                        <BlackText style={{ fontWeight: 500 }}>
                          {shortenedCite}
                        </BlackText>
                      )
                      : (
                        <Tooltip title={c.cite} style={{ maxHeight: 50 }}>
                        <div>
                          <BlackText style={{ fontWeight: 500 }}>
                            {shortenedCite}
                          </BlackText>
                        </div>
                      </Tooltip>
                      )
                  }
                </Grid>
                <Grid item xs={12} lg={6}>
                  {
                    shortenedTag === c.tag
                      ? (
                        <BlackText>
                          {shortenedTag}
                        </BlackText>
                      )
                      : (
                        <Tooltip title={c.tag}>
                          <div>
                            <BlackText>
                              {shortenedTag}
                            </BlackText>
                          </div>
                        </Tooltip>
                      )
                  }
                </Grid>
              </Grid>
            )
          })
        }
      </div>
    </div>
  )
}

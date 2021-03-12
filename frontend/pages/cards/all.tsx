import { parse } from 'cookie'
import { useState } from 'react'
import { Grid, Tooltip, Typography } from '@material-ui/core'
import { GetServerSideProps, NextPage } from 'next'
import Link from 'next/link'
import DashboardNavbar from '../../components/dash/DashboardNavbar'
import theme from '../../components/utils/theme'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import BlackText from '../../components/utils/BlackText'
import ErrorAlert from '../../components/utils/ErrorAlert'
import redirectToLogin from '../../components/utils/redirectToLogin'
import SearchCards from '../../components/cards/SearchCards'
import CardPreview from '../../components/types/CardPreview'

interface Props {
  jwt?: string
  cards?: CardPreview[]
  errorText?: string
}

// todo sorting
const AllCards: NextPage<Props> = ({ cards: initialCards, errorText }) => {
  const [ cards, setCards ] = useState(initialCards)
  const { width } = useWindowSize(1920, 1080)

  return (
    <div
      style={{
        width: '100%',
        backgroundColor: theme.palette.lightBlue.main,
        minHeight: '100%',
        overflow: 'auto'
      }}
    >
      <DashboardNavbar windowWidth={width} pageName='Cards' />
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
        <div style={{ width: '100%', display: 'flex', justifyContent: 'flex-end' }}>
          <SearchCards
            cards={initialCards}
            onResults={setCards}
            onClear={() => setCards(initialCards)}
            windowWidth={width}
          />
        </div>
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
              <Link href={`/cards/id/${encodeURIComponent(c.id)}`} passHref key={c.id}>
                <a>
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
                </a>
              </Link>
            )
          })
        }
      </div>
      {
        errorText && <ErrorAlert text={errorText} disableClose />
      }
    </div>
  )
}

export default AllCards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  let jwt
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, '/cards/all')
    return {
      props: {}
    }
  }

  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch((dev ? 'http://localhost' : 'https://cardtown.co') + '/api/v1/cards', {
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
  })
  let cards: CardPreview[] | null = null
  let errorText: string | null = null
  if (response.ok) {
    cards = await response.json()
  } else if (response.status === 500) {
    errorText = 'There was a server error. Please try again in a few minutes'
  } else if (response.status === 406) {
    errorText = 'Account not found. This is likely a bug and has been reported as such. Please try again'
  } else {
    errorText = 'There was an error finding the cards. Please try again'
  }
  return {
    props: {
      jwt,
      cards,
      errorText
    }
  }
}

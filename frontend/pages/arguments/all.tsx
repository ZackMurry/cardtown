import Link from 'next/link'
import { Grid, IconButton, Typography } from '@material-ui/core'
import { parse } from 'cookie'
import { GetServerSideProps, NextPage } from 'next'
import { useState } from 'react'
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown'
import DashboardNavbar from '../../components/dash/DashboardNavbar'
import ArgumentPreview from '../../components/types/ArgumentPreview'
import BlackText from '../../components/utils/BlackText'
import ErrorAlert from '../../components/utils/ErrorAlert'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../components/utils/redirectToLogin'
import theme from '../../components/utils/theme'
import SearchArguments from '../../components/arguments/SearchArguments'
import styles from '../../styles/AllArguments.module.css'

interface Props {
  jwt?: string
  args?: ArgumentPreview[]
  fetchErrorText?: string
}

type Sort = { by: 'none' | 'name' | 'cards', ascending: boolean }

const AllArguments: NextPage<Props> = ({ args: initialArgs, fetchErrorText }) => {
  const [ args, setArgs ] = useState(initialArgs)
  const [ sort, setSort ] = useState<Sort>({ by: 'none', ascending: false })
  const { width } = useWindowSize(1920, 1080)

  const updateSort = (options: Sort) => {
    setSort(options)
    if (options.by === 'name') {
      if (options.ascending) {
        setArgs(args.sort((a, b) => b.name.localeCompare(a.name)))
      } else {
        setArgs(args.sort((a, b) => a.name.localeCompare(b.name)))
      }
    } else if (options.by === 'cards') {
      if (options.ascending) {
        setArgs(args.sort((a, b) => a.cards.length - b.cards.length))
      } else {
        setArgs(args.sort((a, b) => b.cards.length - a.cards.length))
      }
    }
  }

  const handleSearchResults = (newArgs: ArgumentPreview[]) => {
    setSort({ by: 'none', ascending: false })
    setArgs(newArgs)
  }

  const handleClearSearch = () => {
    setSort({ by: 'none', ascending: false })
    setArgs(initialArgs)
  }

  return (
    <div
      style={{
        width: '100%',
        backgroundColor: theme.palette.lightBlue.main,
        minHeight: '100%',
        overflow: 'auto'
      }}
    >
      <DashboardNavbar windowWidth={width} pageName='Arguments' />
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
          All arguments
        </BlackText>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
        <div
          style={{
            width: '100%', display: 'flex', justifyContent: 'flex-end', paddingBottom: 20
          }}
        >
          <SearchArguments
            args={initialArgs}
            onResults={handleSearchResults}
            onClear={handleClearSearch}
            windowWidth={width}
          />
        </div>
        {
          width >= theme.breakpoints.values.lg && (
            <Grid container>
              <Grid item lg={9} style={{ paddingLeft: 20 }}>
                <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center' }}>
                  <button
                    style={{
                      cursor: 'pointer',
                      background: 'none',
                      border: 'none',
                      padding: 0
                    }}
                    type='button'
                    onClick={() => updateSort({ by: sort.by === 'name' ? 'none' : 'name', ascending: false })}
                  >
                    <BlackText style={{ fontWeight: 500 }}>
                      Name
                    </BlackText>
                  </button>
                  {
                    sort.by === 'name' && (
                      <div className={styles['icon-container'] + ' ' + (sort.ascending && styles['icon-container-180'])}>
                        <IconButton
                          onClick={() => updateSort({ by: sort.by, ascending: !sort.ascending })}
                          style={{ width: 16, height: 16 }}
                        >
                          <ArrowDropDownIcon fontSize='small' />
                        </IconButton>
                      </div>
                    )
                  }
                </div>
              </Grid>
              <Grid item lg={3} style={{ marginLeft: -10 }}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <button
                    style={{
                      cursor: 'pointer',
                      background: 'none',
                      border: 'none',
                      padding: 0
                    }}
                    type='button'
                    onClick={() => updateSort({ by: sort.by === 'cards' ? 'none' : 'cards', ascending: false })}
                  >
                    <BlackText style={{ fontWeight: 500 }}>
                      Number of cards
                    </BlackText>
                  </button>
                  {
                    sort.by === 'cards' && (
                      <div className={styles['icon-container'] + ' ' + (sort.ascending && styles['icon-container-180'])}>
                        <IconButton
                          onClick={() => updateSort({ by: sort.by, ascending: !sort.ascending })}
                          style={{ width: 16, height: 16 }}
                        >
                          <ArrowDropDownIcon fontSize='small' />
                        </IconButton>
                      </div>
                    )
                  }
                </div>
              </Grid>
            </Grid>
          )
        }

        {/* todo show information about the owner and make this expandable so that users can see the card tags and click on indivual cards */}
        {
          args.map(({ id, name, cards }) => (
            <Link href={`/arguments/id/${encodeURIComponent(id)}`} passHref key={id}>
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
                  <Grid item xs={12} lg={9}>
                    {name}
                  </Grid>
                  <Grid item xs={12} lg={3}>
                    {`${cards.length} `}
                    cards
                  </Grid>
                </Grid>
              </a>
            </Link>
          ))
        }
      </div>
      {
        fetchErrorText && <ErrorAlert text={fetchErrorText} disableClose />
      }
    </div>

  )
}

export default AllArguments

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  let jwt: string | null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, '/arguments/all')
    return {
      props: {}
    }
  }

  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const response = await fetch(`${domain}/api/v1/arguments`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (response.ok) {
    const args = await response.json() as ArgumentPreview[]
    return {
      props: {
        jwt,
        args
      }
    }
  }
  let fetchErrorText: string | null
  if (response.status === 400) {
    fetchErrorText = 'Error fetching arguments'
  } else if (response.status === 401 || response.status === 403) {
    redirectToLogin(res, '/arguments/all')
    return {
      props: {
        jwt
      }
    }
  } else if (response.status === 500) {
    fetchErrorText = 'A server error occured during your request. Please try again'
  } else {
    fetchErrorText = `An unknown error occured during your request. Status code: ${response.status}`
  }
  return {
    props: {
      jwt,
      fetchErrorText
    }
  }
}

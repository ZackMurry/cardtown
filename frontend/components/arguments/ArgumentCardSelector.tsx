import { Grid, Tooltip, Typography } from '@material-ui/core'
import { FC, useEffect, useState } from 'react'
import SearchCards from 'components/cards/SearchCards'
import CardPreview from 'types/CardPreview'
import BlackText from 'lib/BlackText'
import theme from 'lib/theme'

interface Props {
  cardsInArgument: CardPreview[],
  cardsNotInArgument: CardPreview[],
  onCardRemove: (id: string) => void,
  onCardSelect: (id: string) => void,
  windowWidth: number
}

const ArgumentCardSelector: FC<Props> = ({
  cardsInArgument, cardsNotInArgument, windowWidth, onCardSelect, onCardRemove
}) => {
  const [ cardsInSearch, setCardsInSearch ] = useState(cardsNotInArgument)

  useEffect(() => {
    setCardsInSearch(cardsNotInArgument)
  }, [ cardsNotInArgument ])

  return (
    <div
      style={{
        backgroundColor: theme.palette.secondary.main,
        width: '100%',
        border: '1px solid rgba(0, 0, 0, 0.23)',
        borderRadius: 5,
        padding: 25
      }}
    >
      <div>
        <Typography variant='h5'>
          Cards in argument
        </Typography>
        {
          !cardsInArgument?.length && (
            <Typography color='textSecondary' style={{ margin: 25, fontSize: 16 }}>
              When you add cards, they'll appear here
            </Typography>
          )
        }
        {
          Boolean(cardsInArgument?.length) && cardsInArgument.map(c => {
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
                  padding: 10,
                  border: `1px solid ${theme.palette.lightGrey.main}`,
                  borderRadius: 5,
                  margin: '15px 0',
                  cursor: 'pointer'
                }}
                key={c.id}
                onClick={() => onCardRemove(c.id)}
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
      <div
        style={{
          width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
        }}
      />
      {/* todo: lazy load */}
      <div>
        <Grid container spacing={3} style={{ width: '100%' }}>
          <Grid item xs={12} md={6}>
            <Typography variant='h5'>
              Find more cards
            </Typography>
          </Grid>
          <Grid
            item
            xs={12}
            md={6}
            style={{
              display: 'flex',
              justifyContent: windowWidth >= theme.breakpoints.values.md ? 'flex-end' : 'flex-start'
            }}
          >
            <SearchCards
              cards={cardsNotInArgument}
              onResults={setCardsInSearch}
              onClear={() => setCardsInSearch(cardsNotInArgument)}
              windowWidth={windowWidth}
            />
          </Grid>
        </Grid>
        <div
          style={{
            backgroundColor: theme.palette.secondary.main,
            width: '100%',
            padding: 10,
            marginTop: 10
          }}
        >
          {
            !cardsInSearch?.length && (
              <Typography color='textSecondary' style={{ margin: 15, fontSize: 16 }}>
                No cards found!
              </Typography>
            )
          }
          {
            cardsInSearch.map(c => {
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
                    padding: 10,
                    border: `1px solid ${theme.palette.lightGrey.main}`,
                    borderRadius: 5,
                    margin: '15px 0',
                    cursor: 'pointer'
                  }}
                  key={c.id}
                  onClick={() => onCardSelect(c.id)}
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
    </div>
  )
}

export default ArgumentCardSelector

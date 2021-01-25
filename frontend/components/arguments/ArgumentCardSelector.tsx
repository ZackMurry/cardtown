import { Grid, Tooltip, Typography } from '@material-ui/core'
import { FC, useEffect, useState } from 'react'
import SearchCards from '../cards/SearchCards'
import CardPreview from '../types/CardPreview'
import BlackText from '../utils/BlackText'
import theme from '../utils/theme'

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
            cardsInArgument.map(c => {
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
      {/* todo: lazy load */}
      <div>
        <Typography variant='h5'>
          Find more cards
        </Typography>
        <div style={{ width: '100%', display: 'flex', justifyContent: 'flex-end' }}>
          <SearchCards
            cards={cardsNotInArgument}
            onResults={setCardsInSearch}
            onClear={() => setCardsInSearch(cardsNotInArgument)}
            windowWidth={windowWidth}
          />
        </div>
        <div
          style={{
            backgroundColor: theme.palette.secondary.main,
            width: '100%',
            padding: 10,
            marginTop: 10
          }}
        >
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

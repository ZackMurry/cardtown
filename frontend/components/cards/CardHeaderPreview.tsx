import { Grid, Tooltip } from '@material-ui/core'
import { FC } from 'react'
import BlackText from '../utils/BlackText'
import theme from '../utils/theme'

interface Props {
  tag: string
  cite: string
  onClick?: () => void
}

const CardHeaderPreview: FC<Props> = ({ tag, cite, onClick }) => {
  let shortenedCite = cite
  let shortenedTag = tag
  if (cite.length > 50) {
    shortenedCite = cite.substring(0, 47) + '...'
  }
  if (tag.length > 100) {
    shortenedTag = tag.substring(0, 97) + '...'
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
      onClick={onClick}
    >
      <Grid item xs={12} lg={3}>
        {
          shortenedCite === cite
            ? (
              <BlackText style={{ fontWeight: 500 }}>
                {shortenedCite}
              </BlackText>
            )
            : (
              <Tooltip title={cite} style={{ maxHeight: 50 }}>
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
          shortenedTag === tag
            ? (
              <BlackText>
                {shortenedTag}
              </BlackText>
            )
            : (
              <Tooltip title={tag}>
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
}

export default CardHeaderPreview

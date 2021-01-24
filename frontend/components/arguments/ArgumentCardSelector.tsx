import { Tooltip, Typography } from '@material-ui/core'
import { FC, useState } from 'react'
import CardPreview from '../types/CardPreview'
import theme from '../utils/theme'

interface Props {
  cardsInArgument: CardPreview[],
  cardsNotInArgument: CardPreview[],
  onCardRemove: (id: string) => void,
  onCardSelect: (id: string) => void
}

const ArgumentCardSelector: FC<Props> = ({ cardsInArgument, cardsNotInArgument }) => {
  const [ query, setQuery ] = useState('')

  return (
    <div
      style={{
        backgroundColor: theme.palette.secondary.main,
        width: '100%',
        border: '1px solid rgba(0, 0, 0, 0.23)',
        borderRadius: 5
      }}
    >
      {
        cardsInArgument.map(({ cite, tag, id }) => (
          <Tooltip key={id} title={tag}>
            <Typography>
              {cite}
            </Typography>
          </Tooltip>
        ))
      }
      {
        cardsNotInArgument.map(({ cite, tag, id }) => (
          <Tooltip key={id} title={tag}>
            <Typography>
              {cite}
            </Typography>
          </Tooltip>
        ))
      }
    </div>
  )
}

export default ArgumentCardSelector

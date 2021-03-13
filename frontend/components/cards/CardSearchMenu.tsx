import { FC, useEffect, useState } from 'react'
import CardPreview from 'types/CardPreview'
import theme from 'lib/theme'
import CardHeaderPreview from './CardHeaderPreview'
import SearchCards from './SearchCards'

interface Props {
  jwt: string
  onCardSelect: (id: string) => void
  cards: CardPreview[] // null if still loading
  windowWidth: number
}

const CardSearchMenu: FC<Props> = ({
  onCardSelect, cards, windowWidth
}) => {
  const [ cardsInSearch, setCardsInSearch ] = useState(cards)

  useEffect(() => setCardsInSearch(cards), [ cards ])

  return (
    <div
      style={{
        backgroundColor: theme.palette.secondary.main,
        width: '100%',
        padding: 25
      }}
    >
      <SearchCards
        cards={cards}
        onResults={setCardsInSearch}
        onClear={() => setCardsInSearch(cards)}
        windowWidth={windowWidth}
      />
      {
        cardsInSearch && cardsInSearch.slice(0, 5).map(c => (
          <CardHeaderPreview {...c} onClick={() => onCardSelect(c.id)} key={c.id} />
        ))
      }
    </div>
  )
}

export default CardSearchMenu

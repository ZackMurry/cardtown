import { FC, useEffect, useState } from 'react'
import { Box, useColorModeValue } from '@chakra-ui/react'
import { CardPreview } from 'types/card'
import CardHeaderPreview from './CardHeaderPreview'
import SearchCards from './SearchCards'

interface Props {
  onCardSelect: (id: string) => void
  cards: CardPreview[] // null if still loading
  windowWidth: number
}

const CardSearchMenu: FC<Props> = ({ onCardSelect, cards, windowWidth }) => {
  const [cardsInSearch, setCardsInSearch] = useState(cards)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')

  useEffect(() => setCardsInSearch(cards), [cards])

  return (
    <Box bg={bgColor} w='100%' p='25px'>
      <SearchCards
        cards={cards}
        onResults={setCardsInSearch}
        onClear={() => setCardsInSearch(cards)}
        windowWidth={windowWidth}
      />
      {cardsInSearch &&
        cardsInSearch.slice(0, 5).map(c => <CardHeaderPreview {...c} onClick={() => onCardSelect(c.id)} key={c.id} />)}
    </Box>
  )
}

export default CardSearchMenu

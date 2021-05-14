import SearchIcon from '@material-ui/icons/Search'
import ClearIcon from '@material-ui/icons/Clear'
import { FC, useEffect, useState } from 'react'
import { CardPreview } from 'types/card'
import { Input, InputGroup, InputRightElement, IconButton, useColorModeValue } from '@chakra-ui/react'

interface Props {
  cards: CardPreview[]
  onResults: (results: CardPreview[]) => void
  onClear: () => void
  showDeleted?: boolean
}

interface SearchPoint {
  points: number
  card: CardPreview
}

// todo include a link to the card's page that opens in a new tab
const SearchCards: FC<Props> = ({ cards, onResults, onClear, showDeleted }) => {
  const [query, setQuery] = useState('')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  useEffect(() => {
    setQuery('')
  }, [showDeleted])

  const handleSearch = () => {
    if (!query) {
      onClear()
      return
    }

    if (!cards) {
      return
    }

    const queryWords = query.split(' ')

    let searchPoints: SearchPoint[] = []
    cards.forEach(card => {
      const { tag, cite, bodyText } = card
      let points = 0
      queryWords.forEach(queryWord => {
        const regExp = new RegExp(queryWord.toLocaleLowerCase(), 'gi')
        points += 5 * (tag.match(regExp) || []).length
        points += 10 * (cite.match(regExp) || []).length
        points += 3 * (bodyText.match(regExp) || []).length
      })
      searchPoints.push({ points, card })
    })
    searchPoints = searchPoints
      .filter(({ points }) => points > 0)
      .sort((a, b) => {
        if (a.points < b.points) {
          return 1
        }
        if (a.points > b.points) {
          return -1
        }
        return 0
      })
    const results = searchPoints.map(({ card }) => card)
    onResults(results)
  }

  const handleClear = () => {
    setQuery('')
    onClear()
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleSearch()
    }
  }

  return (
    <InputGroup size='md' w='100%'>
      <Input
        value={query}
        placeholder='Search cards...'
        onChange={e => setQuery(e.target.value)}
        onKeyDown={handleKeyDown}
        borderColor={borderColor}
        _focus={{
          borderColor: 'cardtownBlue'
        }}
      />
      <InputRightElement pr='35px'>
        <IconButton aria-label='Clear search' bg='none' onClick={handleClear} size='sm'>
          <ClearIcon fontSize='small' />
        </IconButton>
        <IconButton aria-label='Search' bg='none' onClick={handleSearch} size='sm'>
          <SearchIcon fontSize='small' />
        </IconButton>
      </InputRightElement>
    </InputGroup>
  )
}

export default SearchCards

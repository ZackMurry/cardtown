import { IconButton, TextField } from '@material-ui/core'
import SearchIcon from '@material-ui/icons/Search'
import ClearIcon from '@material-ui/icons/Clear'
import { FC, FormEvent, useState } from 'react'
import ResponseCard from '../types/ResponseCard'

interface Props {
  cards: ResponseCard[]
  onResults: (results: ResponseCard[]) => void
  onClear: () => void
  windowWidth: number
}

const SearchCards: FC<Props> = ({ cards, onResults, onClear, windowWidth }) => {
  const [ query, setQuery ] = useState('')

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (!query) {
      onClear()
      return
    }
    console.log('submit')

    const queryWords = query.split(' ')
    
    interface SearchPoint {
      points: number
      card: ResponseCard
    }
    let searchPoints: SearchPoint[] = []
    for (const card of cards) {
      const { tag, cite, bodyText, citeInformation } = card
      let points = 0
      for (const queryWord of queryWords) {
        const regExp = new RegExp(queryWord, 'g')
        points += 5 * (tag.match(regExp) || []).length
        points += 10 * (cite.match(regExp) || []).length
        points += 3 * (bodyText.match(regExp) || []).length
        points += 2 * (citeInformation.match(regExp) || []).length
      }
      searchPoints.push({ points, card })
    }
    searchPoints = searchPoints.filter(({ points }) => points > 0).sort((a, b) => {
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

  return (
    <form
      onSubmit={handleSubmit}
      style={windowWidth < 500 ? { width: '100%' } : undefined}
    >
      <TextField
        variant='outlined'
        value={query}
        placeholder='Search cards...'
        onChange={e => setQuery(e.target.value)}
        style={windowWidth < 500 ? { width: '100%' } : undefined}
        InputProps={{
          endAdornment: (
            <>
              <IconButton onClick={handleClear}>
                <ClearIcon fontSize='small' />
              </IconButton>
              <IconButton type='submit'>
                <SearchIcon fontSize='small' />
              </IconButton>
            </>
          )
        }}
      />
    </form>
  )
}

export default SearchCards

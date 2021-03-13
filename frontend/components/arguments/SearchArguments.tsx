import { IconButton, TextField } from '@material-ui/core'
import SearchIcon from '@material-ui/icons/Search'
import ClearIcon from '@material-ui/icons/Clear'
import { FC, useState } from 'react'
import ArgumentPreview from 'types/ArgumentPreview'

interface Props {
  args: ArgumentPreview[]
  onResults: (results: ArgumentPreview[]) => void
  onClear: () => void
  windowWidth: number
}

interface SearchPoint {
  points: number
  arg: ArgumentPreview
}

const SearchArguments: FC<Props> = ({
  args, onResults, onClear, windowWidth
}) => {
  const [ query, setQuery ] = useState('')

  const handleSearch = () => {
    if (!query) {
      onClear()
      return
    }

    const queryWords = query.split(' ')

    let searchPoints: SearchPoint[] = []
    args.forEach(arg => {
      const { name, cards } = arg
      let points = 0
      queryWords.forEach(queryWord => {
        const regExp = new RegExp(queryWord.toLocaleLowerCase(), 'gi')
        points += 25 * (name.match(regExp) || []).length
        cards.forEach(({ tag, cite }) => {
          points += 5 * (tag.match(regExp) || []).length
          points += 10 * (cite.match(regExp) || []).length
        })
      })
      searchPoints.push({ points, arg })
    })
    searchPoints = searchPoints.filter(({ points }) => points > 0).sort((a, b) => {
      if (a.points < b.points) {
        return 1
      }
      if (a.points > b.points) {
        return -1
      }
      return 0
    })
    const results = searchPoints.map(({ arg }) => arg)
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
    <div style={windowWidth < 500 ? { width: '100%' } : undefined}>
      <TextField
        variant='outlined'
        value={query}
        placeholder='Search arguments...'
        onChange={e => setQuery(e.target.value)}
        onKeyDown={handleKeyDown}
        style={windowWidth < 500 ? { width: '100%' } : undefined}
        InputProps={{
          endAdornment: (
            <>
              <IconButton onClick={handleClear}>
                <ClearIcon fontSize='small' />
              </IconButton>
              <IconButton onClick={handleSearch}>
                <SearchIcon fontSize='small' />
              </IconButton>
            </>
          )
        }}
      />
    </div>
  )
}

export default SearchArguments

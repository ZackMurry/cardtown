import { Tooltip } from '@material-ui/core'
import { FC, useEffect, useState } from 'react'
import SearchCards from 'components/cards/SearchCards'
import { CardPreview } from 'types/card'
import theme from 'lib/theme'
import { Box, Grid, GridItem, Stack, Text, useColorModeValue } from '@chakra-ui/react'

interface Props {
  cardsInArgument: CardPreview[]
  cardsNotInArgument: CardPreview[]
  onCardRemove: (id: string) => void
  onCardSelect: (id: string) => void
  windowWidth: number
}

const ArgumentCardSelector: FC<Props> = ({
  cardsInArgument,
  cardsNotInArgument,
  windowWidth,
  onCardSelect,
  onCardRemove
}) => {
  const [cardsInSearch, setCardsInSearch] = useState(cardsNotInArgument)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const itemBgColor = useColorModeValue('offWhiteAccent', 'darkElevated')

  useEffect(() => {
    setCardsInSearch(cardsNotInArgument)
  }, [cardsNotInArgument])

  return (
    <Box bg={bgColor} w='100%' borderWidth='1px' borderStyle='solid' borderColor={borderColor} borderRadius='5px' p='25px'>
      <div>
        <Text>Cards in argument</Text>
        {!cardsInArgument?.length && (
          <Text color='darkGray' m='25px' fontSize='14px'>
            When you add cards, they'll appear here
          </Text>
        )}
        {/* todo reordering cards in creation menu */}
        {Boolean(cardsInArgument?.length) &&
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
                bg={itemBgColor}
                borderWidth='1px'
                borderStyle='solid'
                borderColor={borderColor}
                p='10px'
                borderRadius='5px'
                m='10px'
                cursor='pointer'
                key={c.id}
                onClick={() => onCardRemove(c.id)}
                templateColumns='repeat(4, 1fr)'
              >
                <GridItem colSpan={{ sm: 2, md: 1 }}>
                  {shortenedCite === c.cite ? (
                    <Text fontWeight='medium'>{shortenedCite}</Text>
                  ) : (
                    <Tooltip title={c.cite} style={{ maxHeight: 50 }}>
                      <div>
                        <Text fontWeight='medium'>{shortenedCite}</Text>
                      </div>
                    </Tooltip>
                  )}
                </GridItem>
                <GridItem colSpan={2}>
                  {shortenedTag === c.tag ? (
                    <Text>{shortenedTag}</Text>
                  ) : (
                    <Tooltip title={c.tag}>
                      <div>
                        <Text>{shortenedTag}</Text>
                      </div>
                    </Tooltip>
                  )}
                </GridItem>
              </Grid>
            )
          })}
      </div>
      <div
        style={{
          width: '100%',
          margin: '2vh 0',
          height: 1,
          backgroundColor: theme.palette.lightGrey.main
        }}
      />
      {/* todo: lazy load */}
      <div>
        <Stack direction={{ base: 'column', md: 'row' }} justifyContent='space-between'>
          <Text>Find more cards</Text>
          <Box w={{ base: '100%', md: '60%' }}>
            <SearchCards
              cards={cardsNotInArgument}
              onResults={setCardsInSearch}
              onClear={() => setCardsInSearch(cardsNotInArgument)}
              windowWidth={windowWidth}
            />
          </Box>
        </Stack>
        <Box w='100%' p='10px' mt='10px'>
          {!cardsInSearch?.length && (
            <Text color='darkGray' m='15px' fontSize='16px'>
              No cards found!
            </Text>
          )}
          {cardsInSearch.map(c => {
            let shortenedCite = c.cite
            let shortenedTag = c.tag
            if (c.cite.length > 50) {
              shortenedCite = c.cite.substring(0, 47) + '...'
            }
            if (c.tag.length > 100) {
              shortenedTag = c.tag.substring(0, 97) + '...'
            }
            return (
              // todo way to view card body (even a new tab would work)
              <Grid
                bg={itemBgColor}
                p='10px'
                borderWidth='1px'
                borderStyle='solid'
                borderColor={borderColor}
                borderRadius='5px'
                m='15px 0'
                cursor='pointer'
                key={c.id}
                onClick={() => onCardSelect(c.id)}
                templateColumns='repeat(4, 1fr)'
              >
                <GridItem colSpan={{ sm: 2, md: 1 }}>
                  {shortenedCite === c.cite ? (
                    <Text fontWeight='medium'>{shortenedCite}</Text>
                  ) : (
                    <Tooltip title={c.cite} style={{ maxHeight: 50 }}>
                      <div>
                        <Text fontWeight='medium'>{shortenedCite}</Text>
                      </div>
                    </Tooltip>
                  )}
                </GridItem>
                <GridItem colSpan={2}>
                  {shortenedTag === c.tag ? (
                    <Text>{shortenedTag}</Text>
                  ) : (
                    <Tooltip title={c.tag}>
                      <div>
                        <Text>{shortenedTag}</Text>
                      </div>
                    </Tooltip>
                  )}
                </GridItem>
              </Grid>
            )
          })}
        </Box>
      </div>
    </Box>
  )
}

export default ArgumentCardSelector

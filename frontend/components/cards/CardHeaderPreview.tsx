import { Tooltip } from '@material-ui/core'
import { FC } from 'react'
import { Grid, GridItem, Text, useColorModeValue } from '@chakra-ui/react'
import BlackText from 'components/utils/BlackText'
import theme from 'lib/theme'

interface Props {
  tag: string
  cite: string
  onClick?: () => void
}

const CardHeaderPreview: FC<Props> = ({ tag, cite, onClick }) => {
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
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
      bg={bgColor}
      p='20px'
      borderWidth='1px'
      borderStyle='solid'
      borderColor={borderColor}
      borderRadius='5px'
      m='15px 0'
      cursor='pointer'
      onClick={onClick}
      templateColumns='repeat(4, 1fr)'
    >
      <GridItem colSpan={{ sm: 2, md: 1 }}>
        {shortenedCite === cite ? (
          <Text fontWeight='medium'>{shortenedCite}</Text>
        ) : (
          <Tooltip title={cite} style={{ maxHeight: 50 }}>
            <div>
              <Text fontWeight='medium'>{shortenedCite}</Text>
            </div>
          </Tooltip>
        )}
      </GridItem>
      <GridItem colSpan={2}>
        {shortenedTag === tag ? (
          <Text>{shortenedTag}</Text>
        ) : (
          <Tooltip title={tag}>
            <div>
              <Text>{shortenedTag}</Text>
            </div>
          </Tooltip>
        )}
      </GridItem>
    </Grid>
  )
}

export default CardHeaderPreview

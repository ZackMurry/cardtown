import { Tooltip } from '@material-ui/core'
import { FC } from 'react'
import { Grid, GridItem, Text, useColorModeValue } from '@chakra-ui/react'
import { LinkIcon } from '@chakra-ui/icons'
import Link from 'next/link'

interface Props {
  id: string
  tag: string
  cite: string
  onClick?: () => void
}

const CardHeaderPreview: FC<Props> = ({ id, tag, cite, onClick }) => {
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const onClickLink = (e: MouseEvent) => {
    e.stopPropagation()
  }
  return (
    <Grid
      bg={bgColor}
      p='10px 25px'
      borderWidth='1px'
      borderStyle='solid'
      borderColor={borderColor}
      borderRadius='5px'
      m='10px 0'
      cursor='pointer'
      onClick={onClick}
      templateColumns='repeat(8, 1fr)'
    >
      <GridItem colSpan={{ sm: 4, md: 2 }}>
        <Text fontWeight='medium' isTruncated noOfLines={1}>
          {cite}
        </Text>
      </GridItem>
      <GridItem colSpan={5}>
        <Tooltip title={tag}>
          <div>
            <Text isTruncated noOfLines={1} whiteSpace='normal'>
              {tag}
            </Text>
          </div>
        </Tooltip>
      </GridItem>
      <GridItem colSpan={1} display='flex' justifyContent='center' alignItems='center'>
        <Link href={`/cards/id/${id}`} passHref>
          <a target='_blank' rel='noopener noreferrer' onClick={onClickLink}>
            <LinkIcon />
          </a>
        </Link>
      </GridItem>
    </Grid>
  )
}

export default CardHeaderPreview

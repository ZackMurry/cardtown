import Link from 'next/link'
import { FC } from 'react'
import { Box, Heading, useColorModeValue, Grid, GridItem, Text } from '@chakra-ui/react'
import { ArgumentWithCardModel } from 'types/argument'
import theme from 'lib/theme'

interface Props {
  relatedArguments: ArgumentWithCardModel[]
}

const CardArgumentsDisplay: FC<Props> = ({ relatedArguments }) => {
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <Box
      w='100%'
      m='1vh auto'
      bg={bgColor}
      borderWidth='1px'
      borderStyle='solid'
      borderColor={borderColor}
      borderRadius='5px'
      p='3vh 3vw'
    >
      <Heading as='h6' fontSize='20px'>
        Related Arguments
      </Heading>
      <Grid mt='1vh' templateColumns='repeat(4, 1fr)'>
        <GridItem colSpan={3}>
          <Text fontWeight='bold'>Argument Name</Text>
        </GridItem>
        <GridItem colSpan={1}>
          <Text fontWeight='bold' textAlign='right'>
            Position in Argument
          </Text>
        </GridItem>
      </Grid>
      {relatedArguments.map(arg => (
        <div key={arg.id} style={{ padding: '5px 0' }}>
          <div
            style={{
              width: '100%',
              height: 1,
              backgroundColor: theme.palette.lightGrey.main,
              marginBottom: 3
            }}
          />
          <Link href={`/arguments/id/${arg.id}`} passHref>
            <a>
              <Grid templateColumns='repeat(4, 1fr)'>
                <GridItem colSpan={3}>
                  <Text>{arg.name}</Text>
                </GridItem>
                <GridItem>
                  <Text textAlign='right'>{arg.indexInArgument + 1}</Text>
                </GridItem>
              </Grid>
            </a>
          </Link>
        </div>
      ))}
    </Box>
  )
}

export default CardArgumentsDisplay

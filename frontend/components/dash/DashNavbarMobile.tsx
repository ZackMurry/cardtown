import { HamburgerIcon } from '@chakra-ui/icons'
import Link from 'next/link'
import { FC, useState } from 'react'
import { Box, Flex, Collapse, useColorModeValue, IconButton, Text } from '@chakra-ui/react'
import chakraTheme from 'lib/chakraTheme'

const DashNavbarMobile: FC = () => {
  const [isExpanded, setExpanded] = useState(false)
  const bgColor = useColorModeValue('white', 'darkElevated')

  return (
    <header style={{ background: chakraTheme.colors[bgColor] }}>
      <Flex h='7vh' p='12.5px 25px' justifyContent='space-between' alignItems='center'>
        <IconButton onClick={() => setExpanded(!isExpanded)} bg='transparent' aria-label='Open navigation'>
          <HamburgerIcon w='35px' h='35px' color='darkGray' />
        </IconButton>
      </Flex>
      <Collapse in={isExpanded}>
        <Box bg={bgColor} pl='30px'>
          <Link href='/dash' passHref>
            <a>
              <Text color='darkGray' fontSize='16px' m='12.5px 0'>
                Dashboard
              </Text>
            </a>
          </Link>
          <Link href='/cards' passHref>
            <a>
              <Text color='darkGray' fontSize='16px' m='12.5px 0'>
                Cards
              </Text>
            </a>
          </Link>
          <Link href='/arguments' passHref>
            <a>
              <Text color='darkGray' fontSize='16px' m='12.5px 0'>
                Arguments
              </Text>
            </a>
          </Link>
          <Link href='/speeches' passHref>
            <a>
              <Text color='darkGray' fontSize='16px' m='12.5px 0'>
                Speeches
              </Text>
            </a>
          </Link>
          <Link href='/rounds'>
            <a href='/rounds'>
              <Text color='darkGray' fontSize='16px' m='12.5px 0'>
                Rounds
              </Text>
            </a>
          </Link>
        </Box>
      </Collapse>
    </header>
  )
}

export default DashNavbarMobile

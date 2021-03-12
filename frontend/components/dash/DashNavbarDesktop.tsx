import Link from 'next/link'
import { Box, Heading, IconButton, Text } from '@chakra-ui/react'
import { FC, useState } from 'react'
import theme from '../utils/theme'
import PageName from '../types/PageName'
import { AddIcon, BellIcon } from '@chakra-ui/icons'

interface Props {
  pageName: PageName
}

const PageTitleDisplay: FC<{ href: string, title: string }> = ({ href, title }) => {
  const [ mouseOver, setMouseOver ] = useState(false)
  return (
    <Link href={href} passHref>
      <a>
        <Text
          color={mouseOver ? 'darkGrayHover' : 'darkGray'}
          fontSize={16}
          margin='12.5px 5px'
          onMouseEnter={() => setMouseOver(true)}
          onMouseLeave={() => setMouseOver(false)}
          transition='color 0.1s ease-in-out'
        >
          {title}
        </Text>
      </a>
    </Link>
  )
}

const DashNavbarDesktop: FC<Props> = ({ pageName }) => (
  <header
    style={{
      display: 'flex',
      padding: '10px 5%',
      justifyContent: 'space-between',
      alignItems: 'center',
      backgroundColor: 'white'
    }}
  >
    <Box display='flex' alignItems='center'>
      {/* todo icon here */}
      <PageTitleDisplay href='/cards' title='Cards' />
      <PageTitleDisplay href='/arguments' title='Arguments' />
      <PageTitleDisplay href='/speeches' title='Speeches' />
      <PageTitleDisplay href='/rounds' title='Rounds' />
    </Box>
    <Box display='flex' alignItems='center'>
      <IconButton
        aria-label='Notifications'
        icon={<BellIcon fontSize='large' color='darkGray' />}
        bg='transparent'
      />
      <IconButton
        aria-label='New'
        icon={<AddIcon color='darkGray' />}
        bg='transparent'
      />
      {/* todo profile pic here with account settings popover */}
    </Box>
  </header>
)

export default DashNavbarDesktop

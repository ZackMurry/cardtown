import Link from 'next/link'
import { Box, IconButton, Text } from '@chakra-ui/react'
import { FC, useState } from 'react'
import { AddIcon, BellIcon } from '@chakra-ui/icons'
import PageName from 'types/PageName'
import JwtBody from 'types/JwtBody'
import DashNavbarDesktopAvatar from './DashNavbarDesktopAvatar'

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

interface Props {
  pageName: PageName
  jwt: JwtBody
}

const DashNavbarDesktop: FC<Props> = ({ pageName, jwt }) => (
  <header
    style={{
      display: 'flex',
      padding: '10px 5%',
      height: '7vh',
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
      <DashNavbarDesktopAvatar jwt={jwt} />
    </Box>
  </header>
)

export default DashNavbarDesktop

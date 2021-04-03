import Link from 'next/link'
import { Box, IconButton, Text, useColorModeValue } from '@chakra-ui/react'
import { FC, useState } from 'react'
import { AddIcon } from '@chakra-ui/icons'
import PageName from 'types/PageName'
import DashNavbarDesktopAvatar from './DashNavbarDesktopAvatar'
import DashNavbarColorSwitch from './DashNavbarColorSwitch'
import chakraTheme from 'lib/chakraTheme'

const PageTitleDisplay: FC<{ href: string; title: string }> = ({ href, title }) => {
  const [mouseOver, setMouseOver] = useState(false)
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
}

const DashNavbarDesktop: FC<Props> = ({ pageName }) => {
  const bgColor = useColorModeValue('white', chakraTheme.colors.darkElevated)
  return (
    <header
      style={{
        display: 'flex',
        padding: '10px 5%',
        height: '7vh',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: bgColor
      }}
    >
      <Box display='flex' alignItems='center'>
        {/* todo logo here */}
        <PageTitleDisplay href='/cards' title='Cards' />
        <PageTitleDisplay href='/arguments' title='Arguments' />
        <PageTitleDisplay href='/speeches' title='Speeches' />
        <PageTitleDisplay href='/rounds' title='Rounds' />
      </Box>
      <Box display='flex' alignItems='center'>
        <DashNavbarColorSwitch />
        <IconButton aria-label='New' icon={<AddIcon color='darkGray' />} bg='transparent' />
        <DashNavbarDesktopAvatar />
      </Box>
    </header>
  )
}

export default DashNavbarDesktop

import { Typography } from '@material-ui/core'
import Link from 'next/link'
import { Text } from '@chakra-ui/react'
import { FC } from 'react'
import theme from '../utils/theme'
import PageName from '../types/PageName'

interface Props {
  pageName: PageName
}

const PageTitleDisplay: FC<{ href: string, title: string, current: string }> = ({ href, title, current }) => (
  <Link href={href} passHref>
    <a>
      <Text
        color={current === title ? 'darkBlue' : 'darkGray'}
        fontSize={16}
        margin='12.5px 0'
      >
        {title}
      </Text>
    </a>
  </Link>
)

const DashSidebarDesktop: FC<Props> = ({ pageName }) => (
  <div
    style={{
      position: 'absolute',
      height: '100vh',
      width: '12.9vw',
      left: 0,
      top: 0,
      backgroundColor: theme.palette.secondary.main,
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      borderRight: `1px solid ${theme.palette.lightGrey.main}`
    }}
    role='navigation'
  >
    <Text
      as='h3'
      fontSize={28}
      fontWeight={500}
      padding='3vh 0'
      width='50%'
    >
      card
      <span style={{ color: theme.palette.primary.main }}>
        town
      </span>
    </Text>
    <div style={{ width: '50%' }}>
      <PageTitleDisplay href='/dash' current={pageName} title='Dashboard' />
      <PageTitleDisplay href='/cards' current={pageName} title='Cards' />
      <PageTitleDisplay href='/arguments' current={pageName} title='Arguments' />
      <PageTitleDisplay href='/speeches' current={pageName} title='Speeches' />
      <PageTitleDisplay href='/rounds' current={pageName} title='Rounds' />
    </div>
  </div>
)

export default DashSidebarDesktop

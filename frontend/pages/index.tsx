import Head from 'next/head'
import { FC } from 'react'
import theme from '../components/utils/theme'
import useWindowSize from '../components/utils/hooks/useWindowSize'
import LandingPageMobile from '../components/landing/mobile/LandingPageMobile'
import LandingPageDesktop from '../components/landing/desktop/LandingPageDesktop'

// todo: switch to chakra-ui
const Home: FC = () => {
  const { width } = useWindowSize(1920, 1080)

  return (
    <>
      <Head>
        <title>Debate cards in the cloud</title>
      </Head>

      {
        width >= theme.breakpoints.values.md
          ? <LandingPageDesktop />
          : <LandingPageMobile />
      }
    </>
  )
}

export default Home

import Head from 'next/head'
import theme from '../components/utils/theme'
import useWindowSize from '../components/utils/hooks/useWindowSize'
import LandingPageMobile from '../components/landing/mobile/LandingPageMobile'
import LandingPageDesktop from '../components/landing/desktop/LandingPageDesktop'

export default function Home() {
  const width = useWindowSize()?.width ?? 1920

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

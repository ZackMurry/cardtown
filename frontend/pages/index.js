import Head from 'next/head'
import BlackText from '../components/utils/BlackText'
import LandingPageNavbar from '../components/landing/LandingPageNavbar'
import theme from '../components/utils/theme'
import LandingPageJoinBeta from '../components/landing/LandingPageJoinBeta'

export default function Home() {
  return (
    <>
      <Head>
        <title>Debate cards in the cloud</title>
      </Head>

      <LandingPageNavbar />

      {/* hero */}
      <div style={{
        width: '60%', margin: '0 auto', height: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center'
      }}
      >
        <BlackText
          variant='h1'
          style={{
            width: '100%', textAlign: 'center', fontSize: 72, marginTop: '9vh', fontWeight: 400
          }}
        >
          {/* eslint-disable-next-line react/jsx-one-expression-per-line */}
          The hub for your <span style={{ color: theme.palette.primary.main }}>debate cards</span>
        </BlackText>
        <BlackText
          variant='h5'
          style={{
            margin: '25px auto', width: '58%', textAlign: 'center', fontWeight: 300
          }}
        >
          Cardtown is the easiest way to store, search, and read all of your debate cards.
        </BlackText>
        <div style={{ margin: '5px auto' }}>
          <LandingPageJoinBeta />
        </div>
      </div>
    </>
  )
}

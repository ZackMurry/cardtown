import { useRef } from 'react'
import LandingPageSquares from '../LandingPageSquares'
import LandingPageHero from './LandingPageHero'
import LandingPageNavbar from './LandingPageNavbar'

export default function LandingPageDesktop() {
  const tilesSectionRef = useRef(null)

  return (
    <>
      <LandingPageNavbar />
      <LandingPageHero tilesSectionRef={tilesSectionRef} />

      <div
        ref={tilesSectionRef}
        style={{
          paddingTop: '15vh',
          height: '100vh',
          maxWidth: 900,
          width: '50%',
          minWidth: 250,
          margin: '0 auto'
        }}
      >
        <LandingPageSquares />
      </div>

    </>
  )
}

import { FC, useRef } from 'react'
import LandingPageSquares from 'components/landing/LandingPageSquares'
import LandingPageMobileHero from './LandingPageMobileHero'
import LandingPageMobileNav from './LandingPageMobileNav'

// todo need to add login somewhere on mobile
const LandingPageMobile: FC = () => {
  const tilesSectionRef = useRef(null)
  return (
    <>
      <LandingPageMobileNav />

      <LandingPageMobileHero tilesSectionRef={tilesSectionRef} />

      <div
        ref={tilesSectionRef}
        style={{
          paddingTop: '15vh',
          height: '100vh',
          maxWidth: 900,
          width: '85%',
          minWidth: 250,
          margin: '0 auto'
        }}
      >
        <LandingPageSquares />
      </div>
    </>
  )
}

export default LandingPageMobile

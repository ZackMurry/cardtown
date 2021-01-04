import { useEffect, useMemo, useState } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import DashboardSidebar from '../../../components/dash/DashboardSidebar'
import theme from '../../../components/utils/theme'
import BlackText from '../../../components/utils/BlackText'
import useWindowSize from '../../../components/utils/hooks/useWindowSize'
import CardOptionsButton from '../../../components/cards/CardOptionsButton'

// todo styling
export default function ViewCard({ id }) {
  const { width } = useWindowSize()
  const [ card, setCard ] = useState(null)
  const jwt = useMemo(() => Cookie.get('jwt'), [])
  const router = useRouter()

  const getCardData = async () => {
    if (!jwt) {
      router.push(`/login?redirect=${encodeURIComponent(`/cards/id/${id}`)}`)
      return
    }
    console.log(encodeURIComponent(id))
    const response = await fetch(`/api/v1/cards/${encodeURIComponent(id)}`, {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
    })
    if (response.ok) {
      setCard(await response.json())
    } else {
      console.warn(`${response.status}: ${response.statusText}`)
    }
  }

  useEffect(() => {
    getCardData()
  }, [ ])

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main, minHeight: '100%', overflow: 'auto' }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div
        style={{
          width: '50%',
          margin: '10vh auto',
          backgroundColor: theme.palette.secondary.main,
          border: `1px solid ${theme.palette.lightGrey.main}`,
          borderRadius: 5,
          padding: '3vh 3vw'
        }}
      >
        {
          card && (
            <>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>
                  {card.tag}
                </BlackText>
                <CardOptionsButton id={id} jwt={jwt} />
              </div>
              <div>
                <BlackText style={{ fontWeight: 'bold', fontSize: 18 }}>
                  {card.cite}
                </BlackText>
                <BlackText style={{ fontWeight: 'normal', fontSize: 11 }}>
                  {card.citeInformation}
                </BlackText>
              </div>
              <div dangerouslySetInnerHTML={{ __html: card.bodyHtml }} />
            </>
          )
        }
      </div>
    </div>
  )
}

export async function getServerSideProps({ query }) {
  return {
    props: {
      id: query.id
    }
  }
}

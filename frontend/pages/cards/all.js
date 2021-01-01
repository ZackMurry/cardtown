import { useEffect, useMemo, useState } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import theme from '../../components/utils/theme'
import useWindowSize from '../../components/utils/hooks/useWindowSize'

export default function all() {
  const { width } = useWindowSize()
  const [ cards, setCards ] = useState([])
  const jwt = useMemo(() => Cookie.get('jwt'), [])
  const router = useRouter()

  const loadCards = async () => {
    if (!jwt) {
      router.push(`/login?redirect=${encodeURIComponent('/cards/all')}`)
      return
    }
    const response = await fetch('/api/v1/cards', {
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
    })
    if (response.ok) {
      setCards(await response.json())
    } else {
      console.warn(response.status + ': ' + response.statusText)
    }
  }

  useEffect(() => {
    loadCards()
  }, [ ])

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main, minHeight: '100%', overflow: 'auto' }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0 }}>
        {/* todo style this page */}
        {
          cards.map(c => (
            <div key={c.id}>
              {c.cite}: {c.id}
            </div>
          ))
        }
      </div>
    </div>
  )
}

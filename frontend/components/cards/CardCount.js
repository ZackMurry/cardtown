import Link from 'next/link'
import { Typography } from '@material-ui/core'
import { useEffect, useState } from 'react'
import theme from '../utils/theme'

export default function CardCount({ jwt }) {

  const [ cardCount, setCardCount ] = useState(null)

  const getCardCount = async () => {
    if (!jwt) {
      return
    }
    const response = await fetch('/api/v1/cards/count', {
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      setCardCount(await response.text())
    }
  }

  useEffect(() => getCardCount(), [ ])

  return (
    <Link href='/cards/all' passHref>
      <a style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', height: '100%' }}>
        <Typography variant='h5' style={{ fontSize: 28, color: theme.palette.blueBlack.main }}>
          <span style={{ fontWeight: 500, paddingRight: 5 }}>
            { cardCount }
          </span>
          cards
        </Typography>
        <div style={{ marginRight: 10 }}>
          <div
            style={{
              borderRadius: 5,
              border: `3px solid ${theme.palette.text.secondary}`,
              width: '1.75vw',
              minWidth: 20,
              height: '2.5vh',
              minHeight: 25
            }}
          />
          <div
            style={{
              marginLeft: 10,
              marginTop: -15,
              borderRadius: 5,
              border: `3px solid ${theme.palette.text.secondary}`,
              width: '1.75vw',
              minWidth: 20,
              height: '2.5vh',
              minHeight: 25
            }}
          />
        </div>
      </a>
    </Link>
  )
}

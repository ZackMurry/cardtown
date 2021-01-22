import Link from 'next/link'
import { Typography } from '@material-ui/core'
import { FC, useEffect, useState } from 'react'
import theme from '../utils/theme'

interface Props {
  count: number
}

const CardCount: FC<Props> = ({ count }) => (
  <Link href='/cards/all'>
    <a
      href='/cards/all'
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        width: '100%',
        height: '100%'
      }}
    >
      <Typography variant='h5' style={{ fontSize: 22, color: theme.palette.blueBlack.main }}>
        <span style={{ fontWeight: 500, paddingRight: 5 }}>
          { count }
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

export default CardCount

import { TextField, Typography } from '@material-ui/core'
import { useState } from 'react'
import NewCardBodyEditor from '../../components/cards/NewCardBodyEditor'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import BlackText from '../../components/utils/BlackText'
import theme from '../../components/utils/theme'

export default function NewCard() {
  const [ tag, setTag ] = useState('')
  const [ cite, setCite ] = useState('')
  const [ citeInformation, setCiteInformation ] = useState('')

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Cards' />

      <div style={{ marginLeft: '12.9vw', paddingLeft: 38, paddingRight: 38 }}>
        <div style={{ width: '65%', margin: '10vh auto' }}>
          <div>
            <Typography
              style={{
                color: theme.palette.darkGrey.main,
                textTransform: 'uppercase',
                fontSize: 11,
                marginTop: 19,
                letterSpacing: 0.5
              }}
            >
              New card
            </Typography>
            <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
              Create a new card
            </BlackText>
            <div
              style={{
                width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
              }}
            />
          </div>
          <div>

            {/* tag */}
            <div>
              <label htmlFor='tag' id='tagLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Tag
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <Typography color='textSecondary' id='tagDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put a quick summary of what this card says.
              </Typography>
              <TextField
                variant='outlined'
                value={tag}
                onChange={e => setTag(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                multiline
                rows={2}
                rowsMax={5}
                InputProps={{
                  inputProps: {
                    name: 'tag',
                    'aria-labelledby': 'tagLabel',
                    'aria-describedby': 'tagDescription'
                  }
                }}
              />
            </div>

            {/* cite */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='cite' id='citeLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Cite
                  <span style={{ fontWeight: 300 }}>
                    *
                  </span>
                </BlackText>
              </label>
              <Typography color='textSecondary' id='citeDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put the last name of the author and the year it was written. You can also put more information. Example: Miller 18.
              </Typography>
              <TextField
                variant='outlined'
                value={cite}
                onChange={e => setCite(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                InputProps={{
                  inputProps: {
                    name: 'cite',
                    "aria-labelledby": 'citeLabel',
                    "aria-describedby": 'citeDescription'
                  }
                }}
              />
            </div>

            {/* cite information */}
            <div style={{ marginTop: 25 }}>
              <label htmlFor='citeInfo' id='citeInfoLabel'>
                <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                  Additional cite information
                </BlackText>
              </label>
              <Typography color='textSecondary' id='citeInfoDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put some more information about the source of this card, like the author’s credentials and a link to the place you found it.
              </Typography>
              <TextField
                variant='outlined'
                value={citeInformation}
                onChange={e => setCiteInformation(e.target.value)}
                style={{ width: '100%', backgroundColor: theme.palette.secondary.main }}
                InputProps={{
                  inputProps: {
                    name: 'citeInfo',
                    "aria-labelledby": 'citeInfoLabel',
                    "aria-describedby": 'citeInfoDescription'
                  }
                }}
              />
            </div>

            {/* body */}
            <div style={{ marginTop: 25 }}>
              <BlackText variant='h3' style={{ fontSize: 18, fontWeight: 500 }}>
                Body
                <span style={{ fontWeight: 300 }}>
                  *
                </span>
              </BlackText>
              <Typography color='textSecondary' id='citeInfoDescription' style={{ fontSize: 14, margin: '6px 0' }}>
                Put the main text of your card here. You can use formatting.
              </Typography>
              <NewCardBodyEditor />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

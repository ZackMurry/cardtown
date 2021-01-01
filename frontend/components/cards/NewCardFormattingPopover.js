import { Popover, Typography } from '@material-ui/core'
import { useState } from 'react'
import BlackText from '../utils/BlackText'
import theme from '../utils/theme'

// todo make this look a bit better
export default function NewCardFormattingPopover() {
  const [ anchorEl, setAnchorEl ] = useState(null)
  const open = Boolean(anchorEl)

  const handleClose = () => setAnchorEl(null)

  return (
    <div>
      <Typography color='textSecondary' id='citeInfoDescription' style={{ fontSize: 14, margin: '6px 0' }}>
        Put the main text of your card here. You can
        <span
          onClick={e => setAnchorEl(e.currentTarget)}
          onMouseEnter={e => setAnchorEl(e.currentTarget)}
          style={{ color: theme.palette.primary.main }}
          // todo make alternate shortcut system for mobile
        >
          {' use keyboard shortcuts '}
        </span>
        for formatting.
      </Typography>
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center'
        }}
        transformOrigin={{
          vertical: 'bottom',
          horizontal: 'center'
        }}
        PaperProps={{ style: { backgroundColor: 'transparent' }, elevation: 0 }}
      >
        <div onMouseLeave={handleClose}>
          <div
            style={{
              backgroundColor: theme.palette.secondary.main,
              border: '1px solid rgba(0, 0, 0, 0.23)',
              borderRadius: 3,
              padding: 10,
              width: '20vw',
              minWidth: 300
            }}
          >
            <BlackText variant='h4' style={{ fontSize: 18 }}>
              Formatting shortcuts
            </BlackText>
            <div
              style={{
                width: '100%', margin: '0.5vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
              }}
            />
            <BlackText style={{ fontSize: 14 }}>
              Here are the keyboard shortcuts you can use to add formatting to your cards:
            </BlackText>
            <ul style={{ margin: 0, fontSize: 13, color: theme.palette.text.primary }}>
              <li>
                <b>Control+1</b>
                — Set font size to 6
              </li>
              <li>
                <b>Control+2</b>
                — Set font size to 8
              </li>
              <li>
                <b>Control+3</b>
                — Set font size to 9
              </li>
              <li>
                <b>Control+4</b>
                — Set font size to 10
              </li>
              <li>
                <b>Control+5</b>
                — Set font size to 11
              </li>
              <div style={{ paddingTop: 3 }} />
              <li>
                <b>Control+H</b>
                — Highlight
              </li>
              <li>
                <b>Control+B</b>
                — Bold
              </li>
              <li>
                <b>Control+U</b>
                — Underline
              </li>
              <li>
                <b>Control+I</b>
                — Italicize
              </li>
              <li>
                <b>Control+O</b>
                — Outline
              </li>
            </ul>
          </div>
          <div style={{ color: 'transparent', height: 20 }} />
        </div>
      </Popover>
    </div>
  )
}

import { useState, FC } from 'react'
import { Typography } from '@material-ui/core'
import SearchIcon from '@material-ui/icons/Search'
import BuildIcon from '@material-ui/icons/Build'
import LockIcon from '@material-ui/icons/Lock'
import EnhancedEncryptionIcon from '@material-ui/icons/EnhancedEncryption'
import { Box, Heading, SimpleGrid } from '@chakra-ui/react'
import CodeIcon from '@material-ui/icons/Code'
import GetAppIcon from '@material-ui/icons/GetApp'
import LanguageIcon from '@material-ui/icons/Language'
import Link from 'next/link'
import BlackText from 'components/utils/BlackText'
import theme from 'lib/theme'
import styles from 'styles/Home.module.css'
import ToggleIcon from 'components/utils/ToggleIcon'

// todo maybe have the icons' animations automatically trigger once every ~15 secs (independently)
const LandingPageSquares: FC = () => {
  const [ lockState, setLockState ] = useState(false)

  return (
    <>
      <Heading as='h2' fontWeight={400} fontSize={36}>
        Made for debate
      </Heading>
      <SimpleGrid columns={{ sm: 1, md: 2 }} spacing='40px' marginTop='15px'>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <div className={styles['icon-container']}>
                <SearchIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
            <BlackText variant='h5'>
              Searchable cards
            </BlackText>
            <Typography color='textSecondary'>
              Instead of endlessly looking through documents for the exact card you want,
              you can easily search all of your cards — by tag, cite, and body.
            </Typography>
          </div>
        </div>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <div className={styles['icon-container']}>
                <BuildIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
            <BlackText variant='h5'>
              Form speeches with ease
            </BlackText>
            <Typography color='textSecondary'>
              Click a single button to add a card to a speech. Once you have your speech ready,
              you can export your speech as a PDF for file sharing.
            </Typography>
          </div>
        </div>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <div className={styles['icon-container-360']}>
                <LanguageIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
            <BlackText variant='h5'>
              Collaborative work
            </BlackText>
            <Typography color='textSecondary'>
              Share cards, arguments, speeches, and more with your entire team.
            </Typography>
          </div>
        </div>
        <Box display='flex'>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <div className={styles['icon-container-180']}>
                <GetAppIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
              <BlackText variant='h5'>
                Easily import and export cards
              </BlackText>
              <Typography color='textSecondary'>
                {/* todo see if microsoft word works for this */}
                Importing your cards is as simple as copying them into an box and selecting the different parts.
                You can export all of your cards to a PDF at any time.
              </Typography>
          </div>
        </Box>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <ToggleIcon
                offIcon={<LockIcon fontSize='large' style={{ color: theme.palette.primary.main }} />}
                onIcon={<EnhancedEncryptionIcon fontSize='large' style={{ color: theme.palette.primary.main }} />}
                onMouseEnter={() => setLockState(true)}
                onMouseLeave={() => setLockState(false)}
                on={lockState}
                timeout={500}
              />
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
            <BlackText variant='h5'>
              Privacy first
            </BlackText>
            <Typography color='textSecondary'>
              Your data is always secured — even we can't access it. Everything in our database
              is encrypted.
            </Typography>
          </div>
        </div>
        <Box display='flex'>
          <div style={{ marginTop: 10 }}>
            <div className={styles['icon-backdrop']} style={{ backgroundColor: theme.palette.lightGrey.translucent }}>
              <div className={styles['icon-container-180']}>
                <CodeIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </div>
          </div>
          <div style={{ marginLeft: 25 }}>
            <BlackText variant='h5'>
              Open source
            </BlackText>
            <Typography color='textSecondary'>
              Cardtown is proudly open source. You can find the code on
              <Link href='https://github.com/ZackMurry/cardtown'>
                <a
                  target='_blank'
                  rel='noopener noreferrer'
                  href='https://github.com/ZackMurry/cardtown'
                  style={{ color: theme.palette.primary.main }}
                >
                  {' '}
                  our Github page
                </a>
              </Link>
              .
            </Typography>
          </div>
        </Box>
      </SimpleGrid>
    </>
  )
}

export default LandingPageSquares

import { useState, FC } from 'react'
import SearchIcon from '@material-ui/icons/Search'
import BuildIcon from '@material-ui/icons/Build'
import LockIcon from '@material-ui/icons/Lock'
import EnhancedEncryptionIcon from '@material-ui/icons/EnhancedEncryption'
import { Box, Heading, SimpleGrid, Text, useColorModeValue } from '@chakra-ui/react'
import CodeIcon from '@material-ui/icons/Code'
import GetAppIcon from '@material-ui/icons/GetApp'
import LanguageIcon from '@material-ui/icons/Language'
import Link from 'next/link'
import theme from 'lib/theme'
import styles from 'styles/Home.module.css'
import ToggleIcon from 'components/utils/ToggleIcon'

// todo maybe have the icons' animations automatically trigger once every ~15 secs (independently)
const LandingPageSquares: FC = () => {
  const [lockState, setLockState] = useState(false)
  const iconBgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')

  return (
    <>
      <Heading as='h2' fontWeight={400} fontSize={36}>
        Made for debate
      </Heading>
      <SimpleGrid columns={{ sm: 1, md: 2 }} spacing='40px' marginTop='15px'>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <div className={styles['icon-container']}>
                <SearchIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Searchable cards
            </Heading>
            <Text color='darkGray'>
              Instead of endlessly looking through documents for the exact card you want, you can easily search all of your
              cards — by tag, cite, and body.
            </Text>
          </div>
        </div>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <div className={styles['icon-container']}>
                <BuildIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Form speeches with ease
            </Heading>
            <Text color='darkGray'>
              Click a single button to add a card to a speech. Once you have your speech ready, you can export your speech as
              a PDF for file sharing.
            </Text>
          </div>
        </div>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <div className={styles['icon-container-360']}>
                <LanguageIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Collaborative work
            </Heading>
            <Text color='darkGray'>Share cards, arguments, speeches, and more with your entire team.</Text>
          </div>
        </div>
        <Box display='flex'>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <div className={styles['icon-container-180']}>
                <GetAppIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Easily import and export cards
            </Heading>
            <Text color='darkGray'>
              {/* todo see if microsoft word works for this */}
              Importing your cards is as simple as copying them into an box and selecting the different parts. You can export
              all of your cards to a PDF at any time.
            </Text>
          </div>
        </Box>
        <div style={{ display: 'flex' }}>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <ToggleIcon
                offIcon={<LockIcon fontSize='large' style={{ color: theme.palette.primary.main }} />}
                onIcon={<EnhancedEncryptionIcon fontSize='large' style={{ color: theme.palette.primary.main }} />}
                onMouseEnter={() => setLockState(true)}
                onMouseLeave={() => setLockState(false)}
                on={lockState}
                timeout={500}
              />
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Privacy first
            </Heading>
            <Text color='darkGray' fontWeight='medium'>
              Your data is always secured — even we can't access it. Everything in our database is encrypted.
            </Text>
          </div>
        </div>
        <Box display='flex'>
          <div style={{ marginTop: 10 }}>
            <Box className={styles['icon-backdrop']} bg={iconBgColor}>
              <div className={styles['icon-container-180']}>
                <CodeIcon fontSize='large' style={{ color: theme.palette.primary.main }} />
              </div>
            </Box>
          </div>
          <div style={{ marginLeft: 25 }}>
            <Heading as='h5' fontSize='26px' fontWeight='medium'>
              Open source
            </Heading>
            <Text color='darkGray'>
              Cardtown is proudly open source. You can find the code on
              <Link href='https://github.com/ZackMurry/cardtown' passHref>
                <a target='_blank' rel='noopener noreferrer' style={{ color: theme.palette.primary.main }}>
                  {' '}
                  our Github page
                </a>
              </Link>
              .
            </Text>
          </div>
        </Box>
      </SimpleGrid>
    </>
  )
}

export default LandingPageSquares

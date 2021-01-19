import { Button, Typography } from '@material-ui/core'
import { FC, FormEvent, useState } from 'react'
import { useRouter } from 'next/router'
import theme from '../../utils/theme'

const LandingPageJoinBetaMobile: FC = () => {
  const [ email, setEmail ] = useState('')
  const [ invalid, setInvalid ] = useState(false)

  const router = useRouter()

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setInvalid(false)
    if (email === '') {
      setInvalid(true)
      return
    }
    router.push(`/signup?email=${email}`)
  }

  // todo validation message
  const handleInvalid = e => {
    e.preventDefault()
    setInvalid(true)
  }

  return (
    <>
      <form onSubmit={handleSubmit} onInvalid={handleInvalid} style={{ display: 'flex' }}>
        <input
          type='email'
          placeholder='Email address'
          style={{
            outline: 'none',
            padding: 15,
            fontSize: 16,
            maxWidth: '60%',
            border: '2px solid #CBCEDA',
            borderRadius: '10px 0 0 10px',
            color: theme.palette.black.main
          }}
          value={email}
          onChange={e => setEmail(e.target.value)}
          autoComplete='email'
          aria-label='Email'
        />
        <Button
          type='submit'
          variant='contained'
          color='primary'
          style={{
            borderRadius: '0 10px 10px 0',
            boxShadow: 'none',
            textTransform: 'none',
            width: 200
          }}
        >
          <Typography variant='h5' style={{ fontWeight: 500, fontSize: 15 }}>
            Join the beta
          </Typography>
        </Button>
      </form>
      {/* todo maybe make invalid look a bit better */}
      {
        invalid && (
          <Typography
            style={{
              color: theme.palette.error.main, textAlign: 'center', fontWeight: 500, margin: 10
            }}
          >
            This isn't a valid email address.
          </Typography>
        )
      }
    </>
  )
}

export default LandingPageJoinBetaMobile

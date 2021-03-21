import { Grid, Typography } from '@material-ui/core'
import Link from 'next/link'
import { FC } from 'react'
import ArgumentWithCardModel from 'types/ArgumentWithCardModel'
import BlackText from 'components/utils/BlackText'
import theme from 'lib/theme'

interface Props {
  relatedArguments: ArgumentWithCardModel[]
}

const CardArgumentsDisplay: FC<Props> = ({ relatedArguments }) => (
  <div
    style={{
      width: '50%',
      margin: '1vh auto',
      backgroundColor: theme.palette.secondary.main,
      border: `1px solid ${theme.palette.lightGrey.main}`,
      borderRadius: 5,
      padding: '3vh 3vw'
    }}
  >
    <BlackText variant='h6'>Related Arguments</BlackText>
    <Grid container style={{ marginTop: '1vh' }}>
      <Grid item xs={9}>
        <Typography style={{ fontWeight: 'bold' }}>Argument Name</Typography>
      </Grid>
      <Grid item xs={3}>
        <Typography style={{ fontWeight: 'bold', textAlign: 'right' }}>Position in Argument</Typography>
      </Grid>
    </Grid>
    {relatedArguments.map(arg => (
      <div key={arg.id} style={{ padding: '5px 0' }}>
        <div
          style={{
            width: '100%',
            height: 1,
            backgroundColor: theme.palette.lightGrey.main,
            marginBottom: 3
          }}
        />
        <Link href={`/arguments/id/${arg.id}`} passHref>
          <a>
            <Grid container>
              <Grid item xs={9}>
                <Typography>{arg.name}</Typography>
              </Grid>
              <Grid item xs={3}>
                <Typography style={{ textAlign: 'right' }}>{arg.indexInArgument + 1}</Typography>
              </Grid>
            </Grid>
          </a>
        </Link>
      </div>
    ))}
  </div>
)

export default CardArgumentsDisplay

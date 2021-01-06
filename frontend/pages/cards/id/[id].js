import { parse } from 'cookie'
import DashboardSidebar from '../../../components/dash/DashboardSidebar'
import theme from '../../../components/utils/theme'
import BlackText from '../../../components/utils/BlackText'
import useWindowSize from '../../../components/utils/hooks/useWindowSize'
import CardOptionsButton from '../../../components/cards/CardOptionsButton'
import ErrorAlert from '../../../components/utils/ErrorAlert'
import redirectToLogin from '../../../components/utils/redirectToLogin'

// todo styling
export default function ViewCard({ id, errorText, card, jwt }) {
  const width = useWindowSize()?.width ?? 1920

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
      {
        errorText && <ErrorAlert disableClose text={errorText} />
      }
    </div>
  )
}

export async function getServerSideProps({ query, req, res }) {
  let errorText = null
  let card = null
  const { id } = query
  let jwt
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin()
    return
  }
  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch((dev ? 'http://localhost' : 'https://cardtown.co') + `/api/v1/cards/${encodeURIComponent(id)}`, {
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
  })
  if (response.ok) {
    card = await response.json()
  } else if (response.status === 404 || response.status === 400) {
    // todo could probably make the error system look better, but whatever
    errorText = 'Card not found'
  } else if (response.status === 403) {
    errorText = "You don't have access to this card"
  } else if (response.status === 401) {
    redirectToLogin()
  } else if (response.status === 500) {
    errorText = 'There was an unknown server error. Please try again later'
  } else if (response.status === 406) {
    errorText = 'The ID for this card is invalid.'
  } else {
    errorText = `There was an unrecognized error. Status: ${response.status}`
  }
  return {
    props: {
      id,
      errorText,
      card,
      jwt
    }
  }
}

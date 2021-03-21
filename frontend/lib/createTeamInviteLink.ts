import { TeamLinkData } from 'types/team'

const createTeamInviteLink = (data: TeamLinkData): string => {
  const domain = process.env.NODE_ENV === 'production' ? 'https://cardtown.co' : 'http://localhost'
  return `${domain}/teams/join?id=${data.id}&key=${data.secretKey}`
}

export default createTeamInviteLink

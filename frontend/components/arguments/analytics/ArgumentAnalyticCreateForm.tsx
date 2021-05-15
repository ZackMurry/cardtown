import { Box, Flex, Textarea } from '@chakra-ui/react'
import PrimaryButton from 'components/utils/PrimaryButton'
import { FC, FormEvent, useState } from 'react'

interface Props {
  onCreate: (body: string) => void
}

const ArgumentAnalyticCreateForm: FC<Props> = ({ onCreate }) => {
  const [body, setBody] = useState('')

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    onCreate(body)
  }

  return (
    <form onSubmit={handleSubmit}>
      <Flex justifyContent='center'>
        <Textarea
          value={body}
          onChange={e => setBody(e.target.value)}
          placeholder='Body'
          rows={4}
          resize='none'
          focusBorderColor='cardtownBlue'
          w='93%'
        />
      </Flex>

      <Box pl='3.5%' w='95%' pt='20px'>
        <PrimaryButton type='submit'>Create</PrimaryButton>
      </Box>
    </form>
  )
}

export default ArgumentAnalyticCreateForm

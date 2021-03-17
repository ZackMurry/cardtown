import {
  Box, Heading, Input, Text
} from '@chakra-ui/react'
import React, { FC, useState } from 'react'
import chakraTheme from 'lib/chakraTheme'
import styles from 'styles/Dashboard.module.css'

// todo using sample topics for now
const sampleTopics = [
  'Mandatory Minimums',
  'Marijuana Legalization',
  'SOOL',
  'Life Without Parole',
  'Section 702',
  'Overcrowding DA',
  'Recidivism DA',
  'Topicality',
  'Militarism'
]

// todo change to links once topics are set up
const TopicNameDisplay: FC<{ name: string }> = ({ name }) => (
  <Text
    color='cardtownBlue'
    fontWeight='medium'
    fontSize={14}
    margin='4px 0'
    className={styles['topic-link']}
    cursor='pointer'
  >
    {name}
  </Text>
)

const DashSidebarTopicList: FC = () => {
  const [ showAllTopics, setShowAllTopics ] = useState(false)

  return (
    <div style={{ padding: '10%' }}>
      <Heading as='h4' fontSize={16} fontWeight='medium'>
        Topics
      </Heading>
      <Input
        type='text'
        placeholder='Find a topic...'
        size='sm'
        marginTop='10px'
        focusBorderColor='cardtownBlue'
      />
      <Box padding='15px 0px 15px 15px'>
        {
          // sample topics for now
          // todo use id as key
          showAllTopics
            ? (
              sampleTopics.map(name => <TopicNameDisplay key={name} name={name} />)
            )
            : (
              sampleTopics.slice(0, 7).map(name => <TopicNameDisplay key={name} name={name} />)
            )
        }
        {
          showAllTopics
            ? (
              <button onClick={() => setShowAllTopics(false)} type='button' style={{ fontSize: 12, color: chakraTheme.colors.darkGray }}>
                Show less
              </button>
            )
            : (
              <button onClick={() => setShowAllTopics(true)} type='button' style={{ fontSize: 12, color: chakraTheme.colors.darkGray }}>
                Show more
              </button>
            )
        }
      </Box>

    </div>

  )
}

export default DashSidebarTopicList

import { FC, useState } from 'react'
import {
  Box,
  Text,
  Popover,
  PopoverContent,
  PopoverHeader,
  PopoverBody,
  useColorModeValue,
  UnorderedList,
  ListItem,
  PopoverTrigger
} from '@chakra-ui/react'
import chakraTheme from 'lib/chakraTheme'

// todo make this look a bit better
const NewCardFormattingPopover: FC = () => {
  const [isOpen, setOpen] = useState(false)
  const openPopover = () => setOpen(true)
  const handleClose = () => setOpen(false)
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  return (
    <Box>
      <Popover onOpen={openPopover} isOpen={isOpen} placement='top' onClose={handleClose}>
        <PopoverTrigger>
          <Text color='darkGray' id='citeInfoDescription' fontSize='14px' m='6px 0'>
            Put the main text of your card here. You can
            <span
              onClick={openPopover}
              onMouseEnter={openPopover}
              style={{ color: chakraTheme.colors.cardtownBlue }}
              // todo make alternate shortcut system for mobile
            >
              {' use keyboard shortcuts '}
            </span>
            for formatting.
          </Text>
        </PopoverTrigger>
        <PopoverContent onMouseLeave={handleClose} bg={bgColor} borderColor={borderColor}>
          <PopoverHeader>Formatting shortcuts</PopoverHeader>
          <PopoverBody>
            <Text fontSize='14px'>Here are the keyboard shortcuts you can use to add formatting to your cards:</Text>
            <UnorderedList fontSize='12px'>
              <ListItem>
                <b>Control+1</b>— Set font size to 6
              </ListItem>
              <ListItem>
                <b>Control+2</b>— Set font size to 8
              </ListItem>
              <ListItem>
                <b>Control+3</b>— Set font size to 9
              </ListItem>
              <ListItem>
                <b>Control+4</b>— Set font size to 10
              </ListItem>
              <ListItem>
                <b>Control+5</b>— Set font size to 11
              </ListItem>
              <Box pt='3px' />
              <ListItem>
                <b>Control+H</b>— Highlight
              </ListItem>
              <ListItem>
                <b>Control+B</b>— Bold
              </ListItem>
              <ListItem>
                <b>Control+U</b>— Underline
              </ListItem>
              <ListItem>
                <b>Control+I</b>— Italicize
              </ListItem>
              <ListItem>
                <b>Control+O</b>— Outline
              </ListItem>
            </UnorderedList>
          </PopoverBody>
        </PopoverContent>
      </Popover>
    </Box>
  )
}

export default NewCardFormattingPopover

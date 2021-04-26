import {
  AlertDialog,
  AlertDialogBody,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogOverlay,
  Button,
  useColorModeValue
} from '@chakra-ui/react'
import { FC, useRef } from 'react'
import PrimaryButton from './PrimaryButton'

interface Props {
  open: boolean
  onCancel: () => void
  onConfirm: () => void
  body: string
  title: string
}

const ConfirmationDialog: FC<Props> = ({ open, body, title, onCancel, onConfirm }) => {
  const bgColor = useColorModeValue('white', 'darkElevated')
  const cancelRef = useRef()
  return (
    <AlertDialog isOpen={open} onClose={onCancel} leastDestructiveRef={cancelRef}>
      <AlertDialogOverlay>
        <AlertDialogContent bgColor={bgColor}>
          <AlertDialogHeader fontSize='lg' fontWeight='bold'>
            {title}
          </AlertDialogHeader>
          <AlertDialogBody>{body}</AlertDialogBody>
          <AlertDialogFooter>
            <Button ref={cancelRef} onClick={onCancel}>
              Cancel
            </Button>
            <PrimaryButton onClick={onConfirm} ml={3}>
              Confirm
            </PrimaryButton>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialogOverlay>
    </AlertDialog>
  )
}

export default ConfirmationDialog

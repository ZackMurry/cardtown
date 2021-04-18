import { Button, ButtonProps, ComponentWithAs } from '@chakra-ui/react'

const PrimaryButton: ComponentWithAs<'button', ButtonProps> = ({ children, ...props }) => (
  <Button
    colorScheme='blue'
    color='white'
    bgColor='cardtownBlue'
    _hover={{ bgColor: 'blueAccent' }}
    _active={{ bgColor: 'blueAccent' }}
    {...props}
  >
    {children}
  </Button>
)

export default PrimaryButton

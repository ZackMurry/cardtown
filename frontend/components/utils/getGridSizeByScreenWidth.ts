import theme from './theme'

const getGridSizeByScreenWidth = (width: number) => {
  if (width === undefined || width === null) {
    return 'xl'
  }
  if (width < theme.breakpoints.values.sm) {
    return 'xs'
  }
  if (width < theme.breakpoints.values.md) {
    return 'sm'
  }
  if (width < theme.breakpoints.values.lg) {
    return 'md'
  }
  if (width < theme.breakpoints.values.xl) {
    return 'lg'
  }
  return 'xl'
}

export default getGridSizeByScreenWidth

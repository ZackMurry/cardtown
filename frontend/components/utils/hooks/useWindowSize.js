import { useEffect, useState } from 'react'

// from https://usehooks.com/useWindowSize/
export default function useWindowSize(initialWidth, initialHeight) {
  const [ windowSize, setWindowSize ] = useState({
    width: initialWidth,
    height: initialHeight
  })

  useEffect(() => {
    const handleResize = () => {
      setWindowSize({
        width: window.innerWidth,
        height: window.innerHeight
      })
    }
    window.addEventListener('resize', handleResize)
    handleResize()
    return () => window.removeEventListener('resize', handleResize)
  }, [ ])

  return windowSize
}

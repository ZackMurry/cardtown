const styleToReadable: { [key: string]: string } = {
  BOLD: 'bold',
  HIGHLIGHT: 'highlight',
  FONT_SIZE_6: 'font size 6',
  FONT_SIZE_8: 'font size 8',
  FONT_SIZE_9: 'font size 9',
  FONT_SIZE_10: 'font size 10',
  FONT_SIZE_11: 'font size 11',
  UNDERLINE: 'underline',
  ITALIC: 'italics',
  OUTLINE: 'outline'
}

export default function mapStyleToReadable(s: string): string {
  return styleToReadable[s] ?? s
}

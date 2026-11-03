export interface Book {
  id: number
  isbn: string
  title: string
  author: string
  publisher?: string
  publicationDate?: string
  genre: string
  quantityAvailable: number
  createdAt?: string
  updatedAt?: string
}

export interface BookPage {
  content: Book[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface ApiEnvelope<T> {
  code: number
  message: string
  data?: T
  timestamp?: string
  errors?: Record<string, string>
}

export interface BookFormValues {
  isbn: string
  title: string
  author: string
  publisher: string
  publicationDate: string
  genre: string
  quantityAvailable: string
}

export function emptyBookForm(): BookFormValues {
  return {
    isbn: '',
    title: '',
    author: '',
    publisher: '',
    publicationDate: '',
    genre: '',
    quantityAvailable: '0',
  }
}

export function bookToForm(book: Book): BookFormValues {
  return {
    isbn: book.isbn,
    title: book.title,
    author: book.author,
    publisher: book.publisher ?? '',
    publicationDate: book.publicationDate ?? '',
    genre: book.genre,
    quantityAvailable: String(book.quantityAvailable),
  }
}

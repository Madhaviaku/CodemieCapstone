import type { BookFormValues } from './types.ts'

const ISBN_PATTERN = /^(?:\d-?){9}\d$|^(?:\d-?){12}\d$/

export function validateBookForm(values: BookFormValues, mode: 'create' | 'edit'): Record<string, string> {
  const errors: Record<string, string> = {}

  if (mode === 'create') {
    const isbn = values.isbn.trim()
    if (!isbn) {
      errors.isbn = 'ISBN is required'
    } else if (!ISBN_PATTERN.test(isbn)) {
      errors.isbn = 'ISBN must be 10 or 13 digits, with optional hyphens'
    }
  }

  const title = values.title.trim()
  if (!title) {
    errors.title = 'Title is required'
  } else if (title.length > 200) {
    errors.title = 'Title length must be between 1 and 200 characters'
  }

  const author = values.author.trim()
  if (!author) {
    errors.author = 'Author is required'
  } else if (author.length > 100) {
    errors.author = 'Author length must be between 1 and 100 characters'
  }

  const genre = values.genre.trim()
  if (!genre) {
    errors.genre = 'Genre is required'
  } else if (genre.length > 50) {
    errors.genre = 'Genre length must be between 1 and 50 characters'
  }

  if (values.publisher.trim().length > 100) {
    errors.publisher = 'Publisher length must not exceed 100 characters'
  }

  if (values.publicationDate) {
    const selected = new Date(`${values.publicationDate}T00:00:00`)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (Number.isNaN(selected.getTime())) {
      errors.publicationDate = 'Publication date is invalid'
    } else if (selected > today) {
      errors.publicationDate = 'Publication date cannot be in the future'
    }
  }

  const quantity = values.quantityAvailable.trim()
  if (quantity !== '' && !/^\d+$/.test(quantity)) {
    errors.quantityAvailable = 'Quantity must be a non-negative integer'
  }

  return errors
}

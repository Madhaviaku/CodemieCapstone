import type { BookFormValues } from './types.ts'

const YEAR_MIN = 1450

function currentYearMax() {
  return new Date().getFullYear() + 1
}

export function normalizeIsbn(isbn: string) {
  return isbn.replaceAll('-', '').replaceAll(' ', '').trim().toUpperCase()
}

function isValidIsbn10(value: string) {
  if (!/^\\d{9}[\\dX]$/.test(value)) return false
  let sum = 0
  for (let i = 0; i < 9; i += 1) {
    sum += Number(value[i]) * (10 - i)
  }
  const last = value[9]
  const check = last === 'X' ? 10 : Number(last)
  if (Number.isNaN(check)) return false
  sum += check
  return sum % 11 === 0
}

function isValidIsbn13(value: string) {
  if (!/^\\d{13}$/.test(value)) return false
  let sum = 0
  for (let i = 0; i < 12; i += 1) {
    const digit = Number(value[i])
    sum += digit * ((i % 2) === 0 ? 1 : 3)
  }
  const check = Number(value[12])
  const mod = sum % 10
  const calc = mod === 0 ? 0 : 10 - mod
  return calc === check
}

export function isValidIsbn(isbn: string) {
  const normalized = normalizeIsbn(isbn)
  if (normalized.length === 10) return isValidIsbn10(normalized)
  if (normalized.length === 13) return isValidIsbn13(normalized)
  return false
}

export function validateBookForm(values: BookFormValues, mode: 'create' | 'edit'): Record<string, string> {
  const errors: Record<string, string> = {}

  const isbnRaw = values.isbn.trim()
  if (!isbnRaw) {
    errors.isbn = 'ISBN is required'
  } else if (!isValidIsbn(isbnRaw)) {
    errors.isbn = 'Invalid ISBN-10 or ISBN-13'
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

  if (!values.publicationDate.trim()) {
    errors.publicationDate = 'Year is required'
  } else {
    const selected = new Date(`${values.publicationDate}T00:00:00`)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (Number.isNaN(selected.getTime())) {
      errors.publicationDate = 'Publication date is invalid'
    } else if (selected > today) {
      errors.publicationDate = 'Publication date cannot be in the future'
    } else {
      const year = selected.getFullYear()
      const max = currentYearMax()
      if (year < YEAR_MIN || year > max) {
        errors.publicationDate = `Year must be a 4-digit number between ${YEAR_MIN} and ${max}`
      }
    }
  }

  const quantity = values.quantityAvailable.trim()
  if (!quantity) {
    errors.quantityAvailable = 'Copies is required'
  } else if (!/^\\d+$/.test(quantity)) {
    errors.quantityAvailable = 'Copies must be an integer ♥ 0'
  }

  return errors
}

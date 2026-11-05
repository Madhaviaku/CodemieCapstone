import type { ApiEnvelope, Book, BookFormValues, BookPage } from '../types.ts'
import { api } from './client.ts'

const PAGE_SIZE = 10

export interface BookSearchFilters {
  q?: string
  genre?: string
  yearMin?: number
  yearMax?: number
}

export function listBooks(page: number): Promise<BookPage> {
  return getPage('/api/books', { page, size: PAGE_SIZE })
}

export function searchBooks(title: string, page: number): Promise<BookPage> {
  return getPage('/api/books/search', { title, page, size: PAGE_SIZE })
}

export function searchBooksFiltered(filters: BookSearchFilters, page: number): Promise<BookPage> {
  const params: Record<string, string | number> = { page, size: PAGE_SIZE }
  if (filters.q?.trim()) {
    params.q = filters.q.trim()
  }
  if (filters.genre?.trim()) {
    params.genre = filters.genre.trim()
  }
  if (typeof filters.yearMin === 'number') {
    params.yearMin = filters.yearMin
  }
  if (typeof filters.yearMax === 'number') {
    params.yearMax = filters.yearMax
  }
  return getPage('/api/books', params)
}

export async function createBook(values: BookFormValues): Promise<ApiEnvelope<Book>> {
  const response = await api.post<ApiEnvelope<Book>>('/api/books', {
    isbn: values.isbn.trim(),
    title: values.title.trim(),
    author: values.author.trim(),
    publisher: values.publisher.trim() || undefined,
    publicationDate: values.publicationDate || undefined,
    genre: values.genre.trim(),
    quantityAvailable: Number(values.quantityAvailable || 0),
  })
  return response.data
}

export async function updateBook(isbn: string, values: BookFormValues): Promise<ApiEnvelope<Book>> {
  const response = await api.put<ApiEnvelope<Book>>(`/api/books/${encodeURIComponent(isbn)}`, {
    title: values.title.trim(),
    author: values.author.trim(),
    publisher: values.publisher.trim(),
    publicationDate: values.publicationDate || undefined,
    genre: values.genre.trim(),
    quantityAvailable: Number(values.quantityAvailable || 0),
  })
  return response.data
}

export async function deleteBook(isbn: string): Promise<void> {
  await api.delete(`/api/books/${encodeURIComponent(isbn)}`)
}

async function getPage(url: string, params: Record<string, string | number>): Promise<BookPage> {
  const response = await api.get<ApiEnvelope<BookPage>>(url, { params })
  if (!response.data.data) {
    throw new Error(response.data.message)
  }
  return response.data.data
}

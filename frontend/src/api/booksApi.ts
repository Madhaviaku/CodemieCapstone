import type { ApiEnvelope, Book, BookFormValues, BookPage } from '../types.ts'
import { api } from './client.ts'

const PAGE_SIZE = 10

export function listBooks(page: number): Promise<BookPage> {
  return getPage('/api/books', { page, size: PAGE_SIZE })
}

export function searchBooks(title: string, page: number): Promise<BookPage> {
  return getPage('/api/books/search', { title, page, size: PAGE_SIZE })
}

export async function createBook(values: BookFormValues): Promise<ApiEnvelope<Book>> {
  const response = await api.post<ApiEnvelope<Book>>(/api/books', {
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
  const response = await api.put<ApiEnvelope<Book>>(`/{url}`, {
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
  await api.delete(`/{url}`)
}

export async function restoreBook(isbn: string): Promise<void> {
  await api.post(`/api/books/${encodeURIComponent(isbn)}/restore`)
}

async function getPage(url: string, params: Record<string, string | number>): Promise<BookPage> {
  const response = await api.get<ApiEnvelope<BookPage>>(url, { params })
  if (!response.data.data) {
    throw new Error(response.data.message)
  }
  return response.data.data
}

import type { ApiEnvelope, Book, BookFormValues, BookPage } from '../types.ts'
import { api } from './client.ts'

const PAGE_SIZE = 10

export function listBooks(page: number): Promise<BookPage> {
  return getPage('/api/books', { page, size: PAGE_SIZE })
}

export function searchBooks(title: string, page: number): Promise<BookPage> {
  return getPage('/api/books/search', { title, page, size: PAGE_SIZE })
}

export interface CatalogQuery {
  q?: string
  genre?: string
  page?: number
  pageSize?: number
}

export interface CatalogPage {
  items: Book[]
  totalCount: number
  page: number
  pageSize: number
}

export async function getCatalog(query: CatalogQuery): Promise<CatalogPage> {
  const params: Record<string, string | number> = {
    page: query.page ?? 1,
    pageSize: query.pageSize ?? PAGE_SIZE,
  }
  if (query.q && query.q.trim()) {
    params.q = query.q.trim()
  }
  if (query.genre && query.genre.trim() && query.genre !== 'All') {
    params.genre = query.genre.trim()
  }

  const response = await api.get<ApiEnvelope<CatalogPage>>('/api/catalog', { params })
  if (!response.data.data) {
    throw new Error(response.data.message)
  }
  return response.data.data
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

import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import App from './App.tsx'

vi.mock('./api/booksApi.ts', () => ({
  listBooks: vi.fn().mockResolvedValue({
    content: [
      {
        id: 1,
        isbn: '9780134685991',
        title: 'Effective Java',
        author: 'Joshua Bloch',
        genre: 'Programming',
        quantityAvailable: 5,
        publicationDate: '2018-01-06',
      },
    ],
    totalElements: 1,
    totalPages: 1,
    currentPage: 0,
    pageSize: 10,
  }),
  searchBooks: vi.fn(),
  createBook: vi.fn(),
  updateBook: vi.fn(),
  deleteBook: vi.fn(),
}))

describe('App', () => {
  it('renders books returned by the catalog', async () => {
    render(<App />)
    expect(await screen.findByText('Effective Java')).toBeTruthy()
    expect(screen.getByText('Joshua Bloch')).toBeTruthy()
  })

  it('shows required-field messages before creating a book', async () => {
    render(<App />)
    fireEvent.click(screen.getByRole('button', { name: 'Add book' }))
    fireEvent.click(await screen.findByRole('button', { name: 'Add to catalog' }))
    expect(await screen.findByText('Title is required')).toBeTruthy()
    expect(screen.getByText('ISBN is required')).toBeTruthy()
  })
})

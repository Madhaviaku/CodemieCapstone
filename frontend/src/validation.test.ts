import { describe, expect, it } from 'vitest'
import { validateBookForm } from './validation.ts'
import { emptyBookForm } from './types.ts'

describe('validateBookForm', () => {
  it('requires the catalog fields when creating a book', () => {
    const errors = validateBookForm(emptyBookForm(), 'create')
    expect(errors.isbn).toBe('ISBN is required')
    expect(errors.title).toBe('Title is required')
    expect(errors.author).toBe('Author is required')
    expect(errors.genre).toBe('Genre is required')
  })

  it('accepts a hyphenated ISBN and rejects a future date', () => {
    const errors = validateBookForm(
      {
        ...emptyBookForm(),
        isbn: '978-0-13-468599-1',
        title: 'Effective Java',
        author: 'Joshua Bloch',
        genre: 'Programming',
        publicationDate: '2099-01-01',
      },
      'create',
    )
    expect(errors.isbn).toBeUndefined()
    expect(errors.publicationDate).toBe('Publication date cannot be in the future')
  })
})

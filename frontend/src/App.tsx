import { useCallback, useEffect, useState } from 'react'
import { deleteBook, listBooks, searchBooks } from './api/booksApi.ts'
import { toApiError } from './api/client.ts'
import { BookDetailDialog } from './components/BookDetailDialog.tsx'
import { BookFormDialog } from './components/BookFormDialog.tsx'
import { BookTable } from './components/BookTable.tsx'
import { Modal } from './components/Modal.tsx'
import { Pagination } from './components/Pagination.tsx'
import { bookToForm, emptyBookForm, type Book, type BookPage } from './types.ts'

interface Banner {
  tone: 'success' | 'error'
  text: string
}

export default function App() {
  const [page, setPage] = useState(0)
  const [query, setQuery] = useState('')
  const [activeQuery, setActiveQuery] = useState('')
  const [books, setBooks] = useState<BookPage | null>(null)
  const [loading, setLoading] = useState(true)
  const [banner, setBanner] = useState<Banner | null>(null)
  const [formMode, setFormMode] = useState<'create' | 'edit' | null>(null)
  const [editing, setEditing] = useState<Book | null>(null)
  const [detail, setDetail] = useState<Book | null>(null)
  const [pendingDelete, setPendingDelete] = useState<Book | null>(null)
  const [deleting, setDeleting] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const result = activeQuery ? await searchBooks(activeQuery, page) : await listBooks(page)
      setBooks(result)
    } catch (error) {
      setBooks({ content: [], totalElements: 0, totalPages: 0, currentPage: page, pageSize: 10 })
      setBanner({ tone: 'error', text: toApiError(error).message })
    } finally {
      setLoading(false)
    }
  }, [activeQuery, page])

  useEffect(() => {
    void load()
  }, [load])

  function openCreate() {
    setEditing(null)
    setDetail(null)
    setFormMode('create')
  }

  function openEdit(book: Book) {
    setDetail(null)
    setEditing(book)
    setFormMode('edit')
  }

  async function confirmDelete() {
    if (!pendingDelete) {
      return
    }
    setDeleting(true)
    try {
      await deleteBook(pendingDelete.isbn)
      setBanner({ tone: 'success', text: `Removed “${pendingDelete.title}”.` })
      setPendingDelete(null)
      if ((books?.content.length ?? 0) === 1 && page > 0) {
        setPage(page - 1)
      } else {
        await load()
      }
    } catch (error) {
      setBanner({ tone: 'error', text: toApiError(error).message })
    } finally {
      setDeleting(false)
    }
  }

  const emptyMessage = activeQuery
    ? `No books match “${activeQuery}”.`
    : 'The shelf is empty. Add the first book.'

  return (
    <div className="min-h-screen">
      <header className="border-b border-line bg-card/90">
        <div className="mx-auto flex max-w-6xl flex-wrap items-end justify-between gap-4 px-6 py-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.22em] text-brass">Library desk</p>
            <h1 className="font-display text-4xl text-ink">Book catalog</h1>
          </div>
          <button type="button" className="rounded-full bg-brass px-4 py-2 text-sm font-semibold text-white hover:bg-brass-dark" onClick={openCreate}>
            Add book
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-8">
        <form
          className="mb-4 flex flex-wrap gap-2"
          onSubmit={(event) => {
            event.preventDefault()
            setPage(0)
            setActiveQuery(query.trim())
          }}
        >
          <label className="min-w-[240px] flex-1">
            <span className="sr-only">Search by title</span>
            <input
              className="w-full rounded-xl border border-line bg-card px-4 py-3 outline-none focus:border-brass"
              placeholder="Search by title"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
            />
          </label>
          <button type="submit" className="rounded-full bg-pine px-4 py-2 text-sm font-semibold text-white">
            Search
          </button>
          {activeQuery && (
            <button
              type="button"
              className="rounded-full border border-line px-4 py-2 text-sm font-semibold"
              onClick={() => {
                setQuery('')
                setActiveQuery('')
                setPage(0)
              }}
            >
              Clear
            </button>
          )}
        </form>

        {banner && (
          <p
            role="status"
            className={`mb-4 rounded-xl px-4 py-3 text-sm ${banner.tone === 'error' ? 'bg-danger-soft text-danger' : 'bg-moss text-pine'}`}
          >
            {banner.text}
          </p>
        )}

        <section className="rounded-2xl border border-line bg-card shadow-sm">
          <BookTable
            books={books?.content ?? []}
            loading={loading}
            emptyMessage={emptyMessage}
            onView={setDetail}
            onEdit={openEdit}
            onDelete={setPendingDelete}
          />
          {books && !loading && (
            <Pagination page={books.currentPage} totalPages={books.totalPages} totalElements={books.totalElements} onPage={setPage} />
          )}
        </section>
      </main>

      {formMode && (
        <BookFormDialog
          mode={formMode}
          initial={formMode === 'edit' && editing ? bookToForm(editing) : emptyBookForm()}
          onClose={() => setFormMode(null)}
          onSaved={(message) => {
            setFormMode(null)
            setEditing(null)
            setBanner({ tone: 'success', text: message })
            void load()
          }}
        />
      )}

      {detail && <BookDetailDialog book={detail} onClose={() => setDetail(null)} onEdit={openEdit} />}

      {pendingDelete && (
        <Modal title="Remove book" onClose={() => setPendingDelete(null)}>
          <p>
            Remove “{pendingDelete.title}” from the catalog? This cannot be undone.
          </p>
          <div className="mt-6 flex justify-end gap-3">
            <button type="button" className="rounded-full px-4 py-2 text-sm font-semibold text-muted" onClick={() => setPendingDelete(null)}>
              Cancel
            </button>
            <button
              type="button"
              className="rounded-full bg-danger px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
              onClick={() => void confirmDelete()}
              disabled={deleting}
            >
              {deleting ? 'Removing…' : 'Remove book'}
            </button>
          </div>
        </Modal>
      )}
    </div>
  )
}

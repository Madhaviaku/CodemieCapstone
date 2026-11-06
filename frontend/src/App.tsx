import { useCallback, useEffect, useMemo, useState } from 'react'
import { deleteBook, listBooks, searchBooksFiltered, type BookSearchFilters } from './api/booksApi.ts'
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

interface CatalogFilters {
  q: string
  genre: string
  yearMin: string
  yearMax: string
  inStock: boolean
}

const emptyFilters: CatalogFilters = { q: '', genre: '', yearMin: '', yearMax: '', inStock: false }

function toSearchFilters(filters: CatalogFilters): BookSearchFilters {
  const yearMin = filters.yearMin.trim() ? Number(filters.yearMin) : undefined
  const yearMax = filters.yearMax.trim() ? Number(filters.yearMax) : undefined
  return {
    q: filters.q.trim() || undefined,
    genre: filters.genre.trim() || undefined,
    yearMin: Number.isFinite(yearMin) ? yearMin : undefined,
    yearMax: Number.isFinite(yearMax) ? yearMax : undefined,
    inStock: filters.inStock || undefined,
  }
}

function filtersAreActive(filters: CatalogFilters) {
  return Boolean(filters.q.trim() || filters.genre.trim() || filters.yearMin.trim() || filters.yearMax.trim() || filters.inStock)
}

function yearRangeError(filters: CatalogFilters) {
  if (!filters.yearMin.trim() || !filters.yearMax.trim()) {
    return null
  }
  const min = Number(filters.yearMin)
  const max = Number(filters.yearMax)
  if (!Number.isFinite(min) || !Number.isFinite(max)) {
    return null
  }
  return min > max ? 'Min year cannot be greater than max year' : null
}

export default function App() {
  const [page, setPage] = useState(0)
  const [filters, setFilters] = useState<CatalogFilters>(emptyFilters)
  const [activeFilters, setActiveFilters] = useState<CatalogFilters>(emptyFilters)
  const yearError = useMemo(() => yearRangeError(filters), [filters])
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
      const result = filtersAreActive(activeFilters)
        ? await searchBooksFiltered(toSearchFilters(activeFilters), page)
        : await listBooks(page)
      setBooks(result)
    } catch (error) {
      setBooks({ content: [], totalElements: 0, totalPages: 0, currentPage: page, pageSize: 10 })
      setBanner({ tone: 'error', text: toApiError(error).message })
    } finally {
      setLoading(false)
    }
  }, [activeFilters, page])

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

  const filtersApplied = filtersAreActive(activeFilters)
  const emptyMessage = filtersApplied
    ? 'No books found. Try clearing filters or adjusting your search.'
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
            if (yearError) {
              return
            }
            setPage(0)
            setActiveFilters({ ...filters })
          }}
        >
          <label className="min-w-[240px] flex-1">
            <span className="sr-only">Search by title, author, or ISBN</span>
            <input
              className="w-full rounded-xl border border-line bg-card px-4 py-3 outline-none focus:border-brass"
              placeholder="Search by title, author, or ISBN"
              value={filters.q}
              onChange={(event) => setFilters((current) => ({ ...current, q: event.target.value }))}
            />
          </label>
          <label className="min-w-[140px]">
            <span className="sr-only">Genre</span>
            <input
              className="w-full rounded-xl border border-line bg-card px-4 py-3 outline-none focus:border-brass"
              placeholder="Genre"
              value={filters.genre}
              onChange={(event) => setFilters((current) => ({ ...current, genre: event.target.value }))}
            />
          </label>
          <label className="w-[110px]">
            <span className="sr-only">Min year</span>
            <input
              className="w-full rounded-xl border border-line bg-card px-4 py-3 outline-none focus:border-brass"
              placeholder="Year min"
              inputMode="numeric"
              value={filters.yearMin}
              onChange={(event) => setFilters((current) => ({ ...current, yearMin: event.target.value }))}
              aria-invalid={yearError ? true : undefined}
            />
          </label>
          <label className="w-[110px]">
            <span className="sr-only">Max year</span>
            <input
              className="w-full rounded-xl border border-line bg-card px-4 py-3 outline-none focus:border-brass"
              placeholder="Year max"
              inputMode="numeric"
              value={filters.yearMax}
              onChange={(event) => setFilters((current) => ({ ...current, yearMax: event.target.value }))}
              aria-invalid={yearError ? true : undefined}
            />
          </label>
          <label className="flex items-center gap-2 rounded-xl border border-line bg-card px-4 py-3 cursor-pointer">
            <input
              type="checkbox"
              checked={filters.inStock}
              onChange={(event) => setFilters((current) => ({ ...current, inStock: event.target.checked }))}
              className="h-4 w-4 cursor-pointer"
            />
            <span className="text-sm font-medium">In stock</span>
          </label>
          <button type="submit" className="rounded-full bg-pine px-4 py-2 text-sm font-semibold text-white disabled:opacity-50" disabled={Boolean(yearError)}>
            Search
          </button>
          {(filtersAreActive(filters) || filtersApplied) && (
            <button
              type="button"
              className="rounded-full border border-line px-4 py-2 text-sm font-semibold"
              onClick={() => {
                setFilters(emptyFilters)
                setActiveFilters(emptyFilters)
                setPage(0)
              }}
            >
              Clear
            </button>
          )}
          {yearError && (
            <p className="basis-full text-sm text-danger" role="alert">
              {yearError}
            </p>
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

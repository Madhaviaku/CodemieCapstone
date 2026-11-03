import type { Book } from '../types.ts'

interface BookTableProps {
  books: Book[]
  loading: boolean
  emptyMessage: string
  onView: (book: Book) => void
  onEdit: (book: Book) => void
  onDelete: (book: Book) => void
}

export function BookTable({ books, loading, emptyMessage, onView, onEdit, onDelete }: BookTableProps) {
  if (loading) {
    return <p className="px-2 py-10 text-muted">Loading catalog…</p>
  }

  if (books.length === 0) {
    return <p className="px-2 py-10 text-muted">{emptyMessage}</p>
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[720px] border-collapse text-left">
        <thead>
          <tr className="border-b border-line text-xs uppercase tracking-wide text-muted">
            <th className="px-3 py-3 font-semibold">Title</th>
            <th className="px-3 py-3 font-semibold">Author</th>
            <th className="px-3 py-3 font-semibold">Genre</th>
            <th className="px-3 py-3 font-semibold">Year</th>
            <th className="px-3 py-3 font-semibold">Copies</th>
            <th className="px-3 py-3 font-semibold">ISBN</th>
            <th className="px-3 py-3 font-semibold">Actions</th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.isbn} className="border-b border-line/80 last:border-0">
              <td className="px-3 py-3 font-semibold">{book.title}</td>
              <td className="px-3 py-3">{book.author}</td>
              <td className="px-3 py-3">{book.genre}</td>
              <td className="px-3 py-3">{book.publicationDate?.slice(0, 4) ?? '—'}</td>
              <td className="px-3 py-3">{book.quantityAvailable}</td>
              <td className="px-3 py-3 font-mono text-sm">{book.isbn}</td>
              <td className="px-3 py-3">
                <div className="flex gap-2">
                  <button type="button" className="text-sm font-semibold text-pine" onClick={() => onView(book)}>
                    View
                  </button>
                  <button type="button" className="text-sm font-semibold text-brass" onClick={() => onEdit(book)}>
                    Edit
                  </button>
                  <button type="button" className="text-sm font-semibold text-danger" onClick={() => onDelete(book)}>
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

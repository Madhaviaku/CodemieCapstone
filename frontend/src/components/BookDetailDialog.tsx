import type { Book } from '../types.ts'
import { Modal } from './Modal.tsx'

interface BookDetailDialogProps {
  book: Book
  onClose: () => void
  onEdit: (book: Book) => void
}

export function BookDetailDialog({ book, onClose, onEdit }: BookDetailDialogProps) {
  return (
    <Modal title={book.title} onClose={onClose}>
      <dl className="grid gap-4 sm:grid-cols-2">
        <Detail label="Author" value={book.author} />
        <Detail label="Genre" value={book.genre} />
        <Detail label="ISBN" value={book.isbn} />
        <Detail label="Copies available" value={String(book.quantityAvailable)} />
        <Detail label="Publisher" value={book.publisher || '—'} />
        <Detail label="Publication date" value={book.publicationDate || '—'} />
        <Detail label="Added" value={formatWhen(book.createdAt)} />
        <Detail label="Updated" value={formatWhen(book.updatedAt)} />
      </dl>
      <div className="mt-6 flex justify-end">
        <button
          type="button"
          className="rounded-full bg-pine px-4 py-2 text-sm font-semibold text-white"
          onClick={() => onEdit(book)}
        >
          Edit this book
        </button>
      </div>
    </Modal>
  )
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-xs uppercase tracking-wide text-muted">{label}</dt>
      <dd className="mt-1 text-base">{value}</dd>
    </div>
  )
}

function formatWhen(value?: string) {
  if (!value) {
    return '—'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return date.toLocaleString()
}

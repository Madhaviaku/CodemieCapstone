interface PaginationProps {
  page: number
  totalPages: number
  totalElements: number
  onPage: (page: number) => void
}

export function Pagination({ page, totalPages, totalElements, onPage }: PaginationProps) {
  const label = `${totalElements} ${totalElements === 1 ? 'book' : 'books'}`
  return (
    <div className="flex items-center justify-between gap-3 border-t border-line px-3 py-3 text-sm">
      <p className="text-muted">{label}</p>
      {totalPages > 1 && (
        <div className="flex items-center gap-2">
          <button
            type="button"
            className="rounded-full border border-line px-3 py-1 disabled:opacity-40"
            onClick={() => onPage(page - 1)}
            disabled={page <= 0}
          >
            Previous
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            className="rounded-full border border-line px-3 py-1 disabled:opacity-40"
            onClick={() => onPage(page + 1)}
            disabled={page + 1 >= totalPages}
          >
            Next
          </button>
        </div>
      )}
    </div>
  )
}

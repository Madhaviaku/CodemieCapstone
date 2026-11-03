import type { ReactNode } from 'react'

interface ModalProps {
  title: string
  onClose: () => void
  children: ReactNode
}

export function Modal({ title, onClose, children }: ModalProps) {
  return (
    <div className="fixed inset-0 z-20 flex items-start justify-center overflow-y-auto bg-ink/40 px-4 py-10" onMouseDown={onClose}>
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        className="w-full max-w-2xl rounded-2xl border border-line bg-card p-6 shadow-xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="mb-5 flex items-start justify-between gap-4">
          <h2 id="dialog-title" className="font-display text-3xl text-ink">
            {title}
          </h2>
          <button type="button" className="rounded-full px-3 py-1 text-sm text-muted hover:bg-paper" onClick={onClose}>
            Close
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}

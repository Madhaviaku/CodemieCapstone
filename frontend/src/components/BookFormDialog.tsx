import { useState, type FormEvent, type ReactNode } from 'react'
import { createBook, updateBook } from '../api/booksApi.ts'
import { toApiError } from '../api/client.ts'
import type { BookFormValues } from '../types.ts'
import { validateBookForm } from '../validation.ts'
import { Modal } from './Modal.tsx'

interface BookFormDialogProps {
  mode: 'create' | 'edit'
  initial: BookFormValues
  onClose: () => void
  onSaved: (message: string) => void
}

const inputClass = 'mt-1 w-full rounded-lg border border-line bg-paper px-3 py-2 outline-none focus:border-brass'

export function BookFormDialog({ mode, initial, onClose, onSaved }: BookFormDialogProps) {
  const [values, setValues] = useState(initial)
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [formError, setFormError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  function update<K extends keyof BookFormValues>(key: K, value: BookFormValues[K]) {
    setValues((current) => ({ ...current, [key]: value }))
  }

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const fieldErrors = validateBookForm(values, mode)
    setErrors(fieldErrors)
    setFormError(null)
    if (Object.keys(fieldErrors).length > 0) {
      return
    }

    setSaving(true)
    try {
      const result = mode === 'create' ? await createBook(values) : await updateBook(initial.isbn, values)
      onSaved(result.message)
    } catch (error) {
      const apiError = toApiError(error)
      if (apiError.errors) {
        setErrors(apiError.errors)
      } else {
        setFormError(apiError.message)
      }
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal title={mode === 'create' ? 'Add a book' : 'Edit book'} onClose={onClose}>
      <form className="grid gap-4 sm:grid-cols-2" onSubmit={onSubmit}>
        <Field label="ISBN" error={errors.isbn} className="sm:col-span-2">
          <input
            className={inputClass}
            value={values.isbn}
            onChange={(event) => update('isbn', event.target.value)}
            readOnly={mode === 'edit'}
            aria-invalid={Boolean(errors.isbn)}
          />
        </Field>
        <Field label="Title" error={errors.title} className="sm:col-span-2">
          <input className={inputClass} value={values.title} onChange={(event) => update('title', event.target.value)} aria-invalid={Boolean(errors.title)} />
        </Field>
        <Field label="Author" error={errors.author}>
          <input className={inputClass} value={values.author} onChange={(event) => update('author', event.target.value)} aria-invalid={Boolean(errors.author)} />
        </Field>
        <Field label="Genre" error={errors.genre}>
          <input className={inputClass} value={values.genre} onChange={(event) => update('genre', event.target.value)} aria-invalid={Boolean(errors.genre)} />
        </Field>
        <Field label="Publisher" error={errors.publisher}>
          <input className={inputClass} value={values.publisher} onChange={(event) => update('publisher', event.target.value)} aria-invalid={Boolean(errors.publisher)} />
        </Field>
        <Field label="Publication date" error={errors.publicationDate}>
          <input
            className={inputClass}
            type="date"
            value={values.publicationDate}
            onChange={(event) => update('publicationDate', event.target.value)}
            aria-invalid={Boolean(errors.publicationDate)}
          />
        </Field>
        <Field label="Copies available" error={errors.quantityAvailable}>
          <input
            className={inputClass}
            inputMode="numeric"
            value={values.quantityAvailable}
            onChange={(event) => update('quantityAvailable', event.target.value)}
            aria-invalid={Boolean(errors.quantityAvailable)}
          />
        </Field>
        {mode === 'edit' && (
          <p className="text-sm text-muted sm:col-span-2">Leave the publication date blank to keep the current date. ISBN cannot be changed.</p>
        )}
        {formError && (
          <p role="alert" className="text-sm text-danger sm:col-span-2">
            {formError}
          </p>
        )}
        <div className="flex justify-end gap-3 sm:col-span-2">
          <button type="button" className="rounded-full px-4 py-2 text-sm font-semibold text-muted" onClick={onClose}>
            Cancel
          </button>
          <button type="submit" className="rounded-full bg-brass px-4 py-2 text-sm font-semibold text-white hover:bg-brass-dark disabled:opacity-60" disabled={saving}>
            {saving ? 'Saving…' : mode === 'create' ? 'Add to catalog' : 'Save changes'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

function Field({ label, error, className, children }: { label: string; error?: string; className?: string; children: ReactNode }) {
  return (
    <label className={className ?? ''}>
      <span className="text-sm font-semibold">{label}</span>
      {children}
      {error && <span className="mt-1 block text-sm text-danger">{error}</span>}
    </label>
  )
}

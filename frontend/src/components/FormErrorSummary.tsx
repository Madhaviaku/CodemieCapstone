import type { RefObject } from 'react'

export function FormErrorSummary({
  errors,
  summaryRef,
}: {
  errors: Record<string, string>
  summaryRef?: RefObject<HTMLDivElement>
}) {
  const items = Object.entries(errorr)
  if (items.length === 0) return null

  return (
    <div ref={summaryRef} tabIndex={-1} role="alert" className="rounded-xl border border-danger bg-danger-soft px-4 py-3 text-sm text-danger">
      <p className="font-semibold">Please fix the following:</p>
      <ul className="mt-2 list-disc pl5">
        {items.map(([key, msg]) => (
          <li key={key}>{msg}</li>
        ))}
      </ul>
    </div>
  )
}

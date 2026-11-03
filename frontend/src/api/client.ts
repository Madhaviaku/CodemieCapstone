import axios from 'axios'
import type { ApiEnvelope } from '../types.ts'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
})

export class ApiError extends Error {
  errors?: Record<string, string>

  constructor(message: string, errors?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.errors = errors
  }
}

export function toApiError(error: unknown): ApiError {
  if (axios.isAxiosError<ApiEnvelope<unknown>>(error)) {
    const data = error.response?.data
    if (data?.message) {
      return new ApiError(data.message, data.errors)
    }
    if (!error.response) {
      return new ApiError('The catalog service is unavailable. Start the backend on port 8080.')
    }
  }
  return new ApiError('Something went wrong. Please try again.')
}

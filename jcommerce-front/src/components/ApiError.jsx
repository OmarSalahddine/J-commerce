export function ApiError({ error }) {
  if (!error) {
    return null
  }

  return (
    <div className="banner error" role="alert">
      <p>{error.message}</p>
      {error.errors && (
        <ul>
          {Object.entries(error.errors).map(([field, message]) => (
            <li key={field}>
              {field}: {message}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

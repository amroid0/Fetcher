# Simple HTTP Client for Testing Backend API

This is a simple Android application designed for testing backend APIs. It allows users to input URLs, select HTTP methods, add parameters and headers, and view responses. The app also caches requests for future reference.

## Features

- Input URL and select HTTP method (GET, POST)
- Add parameters and headers and formdata and json body 
- View API responses
- Cache requests with SQLite
- Filter cached requests by method type or time elapsed or response status 

## Tech Stack

- **Manual Dependency Injection:** Managing dependencies manually without using frameworks like Dagger or Hilt.
- **ViewModel:** Managing UI-related data in a lifecycle-conscious way.
- **LiveData:** Observing data changes in a lifecycle-aware manner.
- **HttpURLConnection:** Handling networking operations.
- **SQLite:** Caching requests locally.
- **ExecutorService:** Managing threading and background tasks.

/**
 * Service API để giao tiếp với Backend Spring Boot.
 * Trong môi trường dev, các yêu cầu '/api/*' sẽ được Vite Proxy
 * chuyển tiếp tự động sang http://localhost:8080/api/*
 */

export async function fetchHealthStatus() {
  const startTime = performance.now();
  try {
    const response = await fetch('/api/health', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
      },
    });

    const endTime = performance.now();
    const latency = Math.round(endTime - startTime);

    if (!response.ok) {
      throw new Error(`HTTP Error ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    return {
      success: true,
      latency,
      data,
    };
  } catch (error) {
    const endTime = performance.now();
    const latency = Math.round(endTime - startTime);
    return {
      success: false,
      latency,
      error: error.message || 'Không thể kết nối đến Backend',
    };
  }
}

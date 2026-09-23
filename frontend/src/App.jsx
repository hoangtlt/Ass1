import { useState, useEffect, useCallback } from 'react';
import { fetchHealthStatus } from './services/api';
import './App.css';

export default function App() {
  const [status, setStatus] = useState('CHECKING'); // 'CHECKING' | 'UP' | 'DOWN'
  const [healthData, setHealthData] = useState(null);
  const [latency, setLatency] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [lastChecked, setLastChecked] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [autoRefresh, setAutoRefresh] = useState(false);

  const checkHealth = useCallback(async () => {
    setIsLoading(true);
    setStatus((prev) => (prev === 'UP' ? 'UP' : 'CHECKING'));

    const result = await fetchHealthStatus();
    setLatency(result.latency);
    setLastChecked(new Date().toLocaleTimeString('vi-VN'));

    if (result.success && result.data?.status === 'UP') {
      setStatus('UP');
      setHealthData(result.data);
      setErrorMessage('');
    } else {
      setStatus('DOWN');
      setHealthData(null);
      setErrorMessage(result.error || 'Backend chưa phản hồi.');
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    let isMounted = true;

    fetchHealthStatus().then((result) => {
      if (!isMounted) return;
      setLatency(result.latency);
      setLastChecked(new Date().toLocaleTimeString('vi-VN'));
      if (result.success && result.data?.status === 'UP') {
        setStatus('UP');
        setHealthData(result.data);
        setErrorMessage('');
      } else {
        setStatus('DOWN');
        setHealthData(null);
        setErrorMessage(result.error || 'Backend chưa phản hồi.');
      }
    });

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    if (!autoRefresh) return;
    const intervalId = setInterval(() => {
      checkHealth();
    }, 10000);
    return () => clearInterval(intervalId);
  }, [autoRefresh, checkHealth]);

  return (
    <div className="app-container fade-in">
      {/* Top Navbar */}
      <header className="app-header">
        <div className="brand">
          <div className="brand-icon">📰</div>
          <div className="brand-info">
            <h2>FUNewsManagementSystem</h2>
            <span>SBA301 Assignment 01 • Khởi tạo Dự án</span>
          </div>
        </div>
        <div className="header-badges">
          <span className="badge badge-indigo">Stage 1: Skeleton</span>
          <span className="badge badge-cyan">Java 21 + React 19</span>
        </div>
      </header>

      {/* Main Content */}
      <main>
        {/* Hero */}
        <section className="hero-section">
          <div className="hero-tag">
            <span className="code-pill">Vite Proxy: /api ➔ http://localhost:8080</span>
          </div>
          <h1 className="hero-title">FUNews Management System</h1>
          <p className="hero-desc">
            Hệ thống quản lý tin tức trường đại học — Cấu trúc bộ khung full-stack đồng bộ giữa Frontend và Backend.
          </p>
        </section>

        {/* Status Card (Core Feature for Stage 1) */}
        <section
          className={`status-card ${status === 'UP' ? 'status-up' : status === 'DOWN' ? 'status-down' : ''}`}
          aria-label="Backend Health Status"
        >
          <div className="status-card-header">
            <div className="status-title-group">
              <div className="status-icon-wrapper">
                {status === 'UP' ? '🟢' : status === 'DOWN' ? '🔴' : '🟡'}
              </div>
              <div>
                <h3>Trạng thái kết nối Backend</h3>
                <p>Kiểm tra giao tiếp qua endpoint <code className="code-pill">GET /api/health</code></p>
              </div>
            </div>

            <div
              id="health-status-badge"
              className={`connection-pill ${
                status === 'UP' ? 'online' : status === 'DOWN' ? 'offline' : 'checking'
              }`}
            >
              <span className="status-dot"></span>
              {status === 'UP' && 'KẾT NỐI THÀNH CÔNG (UP)'}
              {status === 'DOWN' && 'CHƯA KẾT NỐI (OFFLINE)'}
              {status === 'CHECKING' && 'ĐANG KIỂM TRA...'}
            </div>
          </div>

          {/* Details Grid */}
          <div className="status-details-grid">
            <div className="detail-item">
              <div className="detail-label">Endpoint</div>
              <div className="detail-value">/api/health</div>
            </div>
            <div className="detail-item">
              <div className="detail-label">HTTP Status</div>
              <div className="detail-value" style={{ color: status === 'UP' ? '#34d399' : '#f87171' }}>
                {status === 'UP' ? '200 OK' : status === 'DOWN' ? 'Connection Error' : 'Pending...'}
              </div>
            </div>
            <div className="detail-item">
              <div className="detail-label">Độ trễ (Latency)</div>
              <div className="detail-value">{latency !== null ? `${latency} ms` : '--'}</div>
            </div>
            <div className="detail-item">
              <div className="detail-label">Lần kiểm tra cuối</div>
              <div className="detail-value">{lastChecked || '--'}</div>
            </div>
          </div>

          {/* Response Viewer */}
          <div className="json-container">
            <div className="json-header">
              <span>Phản hồi từ máy chủ (JSON)</span>
              <span className="code-pill">Content-Type: application/json</span>
            </div>
            <div
              id="raw-json-response"
              className={`json-body ${status === 'DOWN' ? 'json-error' : ''}`}
            >
              {status === 'UP' && healthData && (
                JSON.stringify(healthData, null, 2)
              )}
              {status === 'DOWN' && (
                `⚠️ Lỗi kết nối: ${errorMessage}\n\nHướng dẫn khắc phục:\n1. Mở terminal tại thư mục 'backend/'\n2. Chạy lệnh: mvn spring-boot:run (hoặc .\\mvnw.cmd spring-boot:run)\n3. Đợi backend khởi động hoàn tất trên port 8080 rồi bấm 'Kiểm tra lại kết nối' bên dưới.`
              )}
              {status === 'CHECKING' && 'Đang gửi yêu cầu đến backend...'}
            </div>
          </div>

          {/* Controls */}
          <div className="card-actions">
            <div className="action-left">
              <button
                id="btn-refresh-health"
                className="btn btn-primary"
                onClick={checkHealth}
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <span className="spinner"></span>
                    <span>Đang kiểm tra...</span>
                  </>
                ) : (
                  <>
                    <span>🔄</span>
                    <span>Kiểm tra lại kết nối</span>
                  </>
                )}
              </button>
            </div>

            <label className="auto-refresh-toggle">
              <input
                type="checkbox"
                checked={autoRefresh}
                onChange={(e) => setAutoRefresh(e.target.checked)}
              />
              <span>Tự động cập nhật mỗi 10 giây</span>
            </label>
          </div>
        </section>

        {/* Architectural Overview Grid */}
        <section style={{ marginTop: '36px' }}>
          <h2 className="info-section-title">
            <span>📐</span>
            <span>Cấu trúc & Kế hoạch triển khai hệ thống</span>
          </h2>

          <div className="info-cards-grid">
            <div className="info-card">
              <div className="info-card-header">
                <span className="info-card-icon">⚡</span>
                <h4>Frontend (React + Vite)</h4>
              </div>
              <ul className="info-card-list">
                <li>React 19 với JSX thuần, cấu trúc trực quan</li>
                <li>Vite dev proxy định tuyến <code className="code-pill">/api</code> tới port 8080</li>
                <li>CSS Vanilla với biến token, dark mode chuẩn hiện đại</li>
                <li>Không phụ thuộc thư viện UI cồng kềnh</li>
              </ul>
            </div>

            <div className="info-card">
              <div className="info-card-header">
                <span className="info-card-icon">☕</span>
                <h4>Backend (Spring Boot)</h4>
              </div>
              <ul className="info-card-list">
                <li>Spring Boot 3.4.3 với Java 21 LTS</li>
                <li>Endpoint <code className="code-pill">/api/health</code> chuẩn REST DTO</li>
                <li>Cấu hình CORS mở cho Frontend local</li>
                <li>Cấu trúc phân tầng: controller, service, repository, entity, dto</li>
              </ul>
            </div>

            <div className="info-card">
              <div className="info-card-header">
                <span className="info-card-icon">🛡️</span>
                <h4>Cơ chế Độc lập Stage 1</h4>
              </div>
              <ul className="info-card-list">
                <li>Bỏ qua <code className="code-pill">DataSourceAutoConfiguration</code></li>
                <li>Khởi động độc lập, không báo lỗi thiếu database</li>
                <li>Không dùng H2 hay mock data giả lập SQL Server</li>
                <li>Bộ khung hoàn toàn sạch sẽ, sẵn sàng cho Stage 2</li>
              </ul>
            </div>

            <div className="info-card">
              <div className="info-card-header">
                <span className="info-card-icon">🗺️</span>
                <h4>Lộ trình Stage 2</h4>
              </div>
              <ul className="info-card-list">
                <li>Kết nối Microsoft SQL Server (<code className="code-pill">FUNewsManagement</code>)</li>
                <li>Spring Data JPA Entities: Category, NewsArticle, SystemAccount, Tag</li>
                <li>Xác thực & Phân quyền Spring Security (Admin, Staff, Lecturer)</li>
                <li>Triển khai API Authentication & chức năng quản trị</li>
              </ul>
            </div>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="app-footer">
        <div>
          FUNewsManagementSystem • SBA301 Assignment 01 • Thư mục: <strong>HoangTLT_SE1910</strong>
        </div>
        <div>
          Trạng thái: <strong>Stage 1 - Project Skeleton Initialized & Verified</strong>
        </div>
      </footer>
    </div>
  );
}

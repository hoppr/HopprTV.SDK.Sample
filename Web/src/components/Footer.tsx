import './Footer.css';

export function Footer() {
  const version = import.meta.env.VITE_HOPPR_SDK_VERSION || 'dev';

  return (
    <footer className="app-footer">
      <div className="footer-content">
        <span>Hoppr: {version}</span>
      </div>
    </footer>
  );
}

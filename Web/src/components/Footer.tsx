import './Footer.css';

export function Footer() {
  const version = import.meta.env.HOPPR_SDK_VERSION || 'dev';

  return (
    <footer className="app-footer">
      <div className="footer-content">
        <span>Hoppr SDK Sample v{version}</span>
      </div>
    </footer>
  );
}

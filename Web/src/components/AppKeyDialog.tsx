import React, { useState, useEffect } from 'react';
import './AppKeyDialog.css';

interface AppKeyDialogProps {
  onSubmit: (appKey: string) => void;
}

const STORAGE_KEY = 'hoppr-app-key';

export function AppKeyDialog({ onSubmit }: AppKeyDialogProps) {
  const [appKey, setAppKey] = useState('');

  useEffect(() => {
    const savedAppKey = localStorage.getItem(STORAGE_KEY);
    if (savedAppKey) {
      setAppKey(savedAppKey);
    }
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (appKey.trim()) {
      onSubmit(appKey.trim());
    }
  };

  return (
    <div className="app-key-dialog-overlay">
      <div className="app-key-dialog">
        <h2 className="app-key-dialog-title">Enter Hoppr APP Key</h2>
        <form onSubmit={handleSubmit} className="app-key-dialog-form">
          <input
            type="text"
            value={appKey}
            onChange={(e) => setAppKey(e.target.value)}
            placeholder="Enter your APP key..."
            className="app-key-dialog-input"
            autoFocus
          />
          <button
            type="submit"
            disabled={!appKey.trim()}
            className={`app-key-dialog-button ${appKey.trim() ? 'enabled' : 'disabled'}`}
          >
            Initialize Hoppr
          </button>
        </form>
      </div>
    </div>
  );
}
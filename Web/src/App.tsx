import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { init } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr from '@hoppr/hoppr-web-sdk';
import './App.css';
import { HomeScreen } from './home/HomeScreen';
import { Screen1 } from './screen1/Screen1';
import { Screen2 } from './screen2/Screen2';
import { Screen3 } from './screen3/Screen3';
import { Screen3A } from './screen3/Screen3A';
import { Screen3B } from './screen3/Screen3B';
import { AppKeyDialog } from './components/AppKeyDialog';
import { Screen4 } from './screen4/Screen4';
import { Footer } from './components/Footer';

init({
  debug: false,
  visualDebug: false,
});

const test = "a25LbnCC63vg6tFlpbB9ymn7Z4acYFIbNj_zEJB6SrzfNKpCGzJt6PWc5FqZ_Cp8jsrOeTxzXgk9oo8NFkB2qA";

export function isTizenAvailable(): boolean {
  return typeof (window as any).tizen !== "undefined";
}

export function App() {
  const [appKey, setAppKey] = useState<string | null>(null);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    // Auto-use test key on Tizen
    if (isTizenAvailable() && !appKey) {
      setAppKey(test);
    }
  }, [appKey]);

  useEffect(() => {
    if (appKey && !isInitialized) {
      Hoppr.init(appKey, "WebUserId", {
        userLevel: "Beginner",
        age: 36,
        premium: true,
        location: {
          country: "US",
          city: "New York",
        },
      }, (type: string, data: Record<string, any>): Record<string, any> | void => {

        if (type === "SHOW_ALERT") {
          const banner = document.createElement("div");
          banner.textContent = "Alert with data " + JSON.stringify(data);

          Object.assign(banner.style, {
            position: "fixed",
            top: "20px",
            left: "20px",
            padding: "12px 18px",
            background: "#00FF00",
            color: "#000",
            fontSize: "14px",
            borderRadius: "8px",
            boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
            zIndex: 999999,
            transition: "opacity 0.3s",
            opacity: "1",
          });

          document.body.appendChild(banner);

          setTimeout(() => {
            banner.style.opacity = "0";
            setTimeout(() => banner.remove(), 300);
          }, 3000);
        }

        return { dataString: "Some String", "dataBool": true }
      });

      localStorage.setItem('hoppr-app-key', appKey);
      setIsInitialized(true);
    }
  }, [appKey, isInitialized]);

  // Show dialog only if not on Tizen and no app key
  if (!appKey && !isTizenAvailable()) {
    return <AppKeyDialog onSubmit={setAppKey} />;
  }

  return (
    <>
      <MemoryRouter>
        <Routes>
          <Route path="/" element={<HomeScreen />} />
          <Route path="/screen1" element={<Screen1 />} />
          <Route path="/screen2" element={<Screen2 />} />
          <Route path="/screen3" element={<Screen3 />} />
          <Route path="/screen3a" element={<Screen3A />} />
          <Route path="/screen3b" element={<Screen3B />} />
          <Route path="/screen4" element={<Screen4 />} />
        </Routes>
      </MemoryRouter>
      <Footer />
    </>
  );
}

export default App;

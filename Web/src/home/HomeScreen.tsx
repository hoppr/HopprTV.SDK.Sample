"use client";

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFocusable, FocusContext, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export function HomeScreen() {
  console.log("📱 HomeScreen loaded");

  const navigate = useNavigate();
  const { ref, focusKey } = useFocusable({ focusKey: 'HOME_SCREEN' });

  const { ref: button1Ref, focused: button1Focused } = useFocusable({
    focusKey: 'BTN_1',
    onEnterPress: () => navigate('/screen1')
  });

  const { ref: button2Ref, focused: button2Focused } = useFocusable({
    focusKey: 'BTN_2',
    onEnterPress: () => navigate('/screen2')
  });

  const { ref: button3Ref, focused: button3Focused } = useFocusable({
    focusKey: 'BTN_3',
    onEnterPress: () => navigate('/screen3')
  });

  const { ref: button4Ref, focused: button4Focused } = useFocusable({
    focusKey: 'BTN_4',
    onEnterPress: () => navigate('/screen4')
  });

  const screenData = { screenName: "HomeScreen" };

  // Set initial focus to container on mount
  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData)
    setFocus('BTN_1');
  }, []);

  return (
    <FocusContext.Provider value={focusKey}>
      <div
        ref={ref}
        className="min-h-screen p-8"
      >
        <div className="max-w-4xl mx-auto">
          <h1 className="text-3xl font-bold mb-6">Home Screen</h1>

          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px' }}>

            <button
              ref={button1Ref}
              onClick={() => navigate('/screen1')}
              className={`px-6 py-3 rounded ${button1Focused ? 'focused' : ''
                } bg-green-500 text-white`}
            >
              Screen 1
            </button>

            <button
              ref={button2Ref}
              onClick={() => navigate('/screen2')}
              className={`px-6 py-3 rounded ${button2Focused ? 'focused' : ''
                } bg-green-500 text-white`}
            >
              Screen 2
            </button>

            <button
              ref={button3Ref}
              onClick={() => navigate('/screen3')}
              className={`px-6 py-3 rounded ${button3Focused ? 'focused' : ''
                } bg-green-500 text-white`}
            >
              Screen 3
            </button>

            <button
              ref={button4Ref}
              onClick={() => navigate('/screen4')}
              className={`px-6 py-3 rounded ${button4Focused ? 'focused' : ''
                } bg-green-500 text-white`}
            >
              Screen 4
            </button>

            <h3>Hoppr WEB</h3>
          </div>
        </div>
      </div>
    </FocusContext.Provider>
  );
}

"use client";

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useFocusable, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export const Screen3A = () => {
  const navigate = useNavigate();
  const screenData = {
    screenName: "Screen3A"
  };

  const { ref, focused } = useFocusable({
    focusKey: 'BACK_BTN',
    onEnterPress: () => navigate('/screen3')
  });

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);

    setFocus('BACK_BTN');
  }, []);

  return (
    <div className="min-h-screen p-8">
      <div className="max-w-4xl mx-auto text-center">
        <h1 className="text-4xl font-bold mb-8 text-blue-600">Screen Three A</h1>

        <div className="bg-gray-100 p-8 rounded-lg mb-8">
          <p className="text-lg text-gray-600">
            This is Screen Three A - a simple sub-screen accessed from Screen Three.
          </p>
        </div>

        <button
          ref={ref}
          onClick={() => navigate('/screen3')}
          className={`px-6 py-3 rounded ${focused ? 'focused' : ''
            } bg-blue-500 text-white`}
        >
          Back to Screen 3
        </button>
      </div>
    </div>
  );
};
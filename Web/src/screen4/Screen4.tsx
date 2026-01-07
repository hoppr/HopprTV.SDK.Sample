"use client";

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useFocusable, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export const Screen4 = () => {
  const navigate = useNavigate();
  const screenData = {
    screenName: "ScreenFour",
    id: 4,
    name: "ColorGrid",
  };

  const { ref, focused } = useFocusable({
    focusKey: 'BACK_BTN',
    onEnterPress: () => navigate('/')
  });

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);
    setFocus('BACK_BTN');
  }, []);

  const colors = [
    '#FF6B6B', // Red
    '#4ECDC4', // Teal
    '#45B7D1', // Blue
    '#96CEB4', // Green
    '#FFEAA7', // Yellow
    '#DDA0DD', // Plum
    '#98D8C8', // Mint
    '#F7DC6F', // Gold
    '#BB8FCE', // Lavender
  ];

  return (
    <div className="min-h-screen p-8">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold mb-6 text-center">Screen Four</h1>

        <div
          className="mb-8"
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(3, 320px)',
            gridTemplateRows: 'repeat(3, 250px)',
            gap: '16px',
            justifyContent: 'center',
            marginBottom: '24px'
          }}
        >
          {colors.map((color, index) => (
            <div
              key={index}
              style={{
                backgroundColor: color,
                width: '320px',
                height: '250px',
              }}
              className="rounded-lg shadow-lg flex items-center justify-center text-white font-bold text-2xl"
            >
              {/* {index + 1} */}
            </div>
          ))}
        </div>

        <div className="text-center">
          <button
            ref={ref}
            onClick={() => navigate('/')}
            className={`px-6 py-3 rounded ${focused ? 'focused' : ''
              } bg-green-500 text-white`}
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};
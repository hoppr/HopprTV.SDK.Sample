"use client";

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useFocusable, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export const Screen3 = () => {
  const navigate = useNavigate();
  const screenData = {
    screenName: "ScreenThree",
    stringArray: ["one", "two", "three"],
    numberArray: [1, 2, 3, 4.5],
    booleanArray: [true, false, true],
    mixedArray: ["text", 42, false, null, { key: "value" }],
    emptyArray: []
  };

  const { ref: backBtnRef, focused: backBtnFocused } = useFocusable({
    focusKey: 'BACK_BTN',
    onEnterPress: () => navigate('/')
  });

  const { ref: screen3ABtnRef, focused: screen3ABtnFocused } = useFocusable({
    focusKey: 'SCREEN_3A_BTN',
    onEnterPress: () => navigate('/screen3a')
  });

  const { ref: screen3BBtnRef, focused: screen3BBtnFocused } = useFocusable({
    focusKey: 'SCREEN_3B_BTN',
    onEnterPress: () => navigate('/screen3b')
  });

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);

    setFocus('SCREEN_3A_BTN');
  }, []);

  return (
    <div className="min-h-screen p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-6">Screen Three</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">String Array</h2>
            <ul className="list-disc list-inside bg-white p-3 rounded">
              {screenData.stringArray.map((item, index) => (
                <li key={index}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">Number Array</h2>
            <ol className="list-decimal list-inside bg-white p-3 rounded">
              {screenData.numberArray.map((item, index) => (
                <li key={index}>{item}</li>
              ))}
            </ol>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">Boolean Array</h2>
            <ul className="bg-white p-3 rounded">
              {screenData.booleanArray.map((item, index) => (
                <li key={index} className="mb-1">
                  <span className={`inline-block w-3 h-3 rounded-full mr-2 ${item ? 'bg-green-500' : 'bg-red-500'}`}></span>
                  {item ? 'True' : 'False'}
                </li>
              ))}
            </ul>
          </div>

          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">Empty Array</h2>
            <div className="bg-white p-3 rounded">
              {screenData.emptyArray.length === 0 ? (
                <p className="italic text-gray-500">No items to display</p>
              ) : (
                <ul>
                  {screenData.emptyArray.map((item, index) => (
                    <li key={index}>{item}</li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>

        {/* <div className="bg-gray-100 p-4 rounded mb-8">
          <h2 className="text-xl font-semibold mb-3">Mixed Array</h2>
          <div className="bg-white p-3 rounded">
            {screenData.mixedArray.map((item, index) => (
              <div key={index} className="mb-2 pb-2 border-b border-gray-200 last:border-b-0">
                <span className="font-medium">Item {index + 1}:</span>{' '}
                <span className="text-sm text-gray-600">({typeof item})</span>{' '}
                {typeof item === 'object' && item !== null ? (
                  <pre className="mt-1 text-sm bg-gray-50 p-2 rounded">
                    {JSON.stringify(item, null, 2)}
                  </pre>
                ) : (
                  <span>{item === null ? 'null' : String(item)}</span>
                )}
              </div>
            ))}
          </div>
        </div> */}

        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px' }}>
          <button
            ref={screen3ABtnRef}
            onClick={() => navigate('/screen3a')}
            className={`px-6 py-3 rounded ${screen3ABtnFocused ? 'focused' : ''
              } bg-blue-500 text-white`}
          >
            Screen 3 A
          </button>
          <button
            ref={screen3BBtnRef}
            onClick={() => navigate('/screen3b')}
            className={`px-6 py-3 rounded ${screen3BBtnFocused ? 'focused' : ''
              } bg-purple-500 text-white`}
          >
            Screen 3 B
          </button>
          <button
            ref={backBtnRef}
            onClick={() => navigate('/')}
            className={`px-6 py-3 rounded ${backBtnFocused ? 'focused' : ''
              } bg-green-500 text-white`}
          >
            Back to Home
          </button>
        </div>
      </div>
    </div>
  );
};

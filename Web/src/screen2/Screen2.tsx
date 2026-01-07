"use client";

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useFocusable, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export const Screen2 = () => {
  const navigate = useNavigate();
  const screenData = {
    screenName: "ScreenTwo",
    stringValue: "sampleString",
    numberValue: 12345,
    floatValue: 98.76,
    booleanValue: true,
    nullValue: null,
    undefinedValue: undefined
  };

  const { ref, focused } = useFocusable({
    focusKey: 'BACK_BTN',
    onEnterPress: () => navigate('/')
  });

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);

    setFocus('BACK_BTN');
  }, []);

  return (
    <div className="min-h-screen p-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-6">Screen Two</h1>

        <div className="bg-gray-100 p-6 rounded mb-8">
          <h2 className="text-2xl font-semibold mb-4">Data Types</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <h3 className="text-lg font-medium mb-3">String Value</h3>
              <p className="bg-white p-3 rounded border">{screenData.stringValue}</p>
            </div>

            <div>
              <h3 className="text-lg font-medium mb-3">Number Value</h3>
              <p className="bg-white p-3 rounded border">{screenData.numberValue}</p>
            </div>

            <div>
              <h3 className="text-lg font-medium mb-3">Float Value</h3>
              <p className="bg-white p-3 rounded border">{screenData.floatValue}</p>
            </div>

            <div>
              <h3 className="text-lg font-medium mb-3">Boolean Value</h3>
              <p className="bg-white p-3 rounded border">{screenData.booleanValue ? 'True' : 'False'}</p>
            </div>

            <div>
              <h3 className="text-lg font-medium mb-3">Null Value</h3>
              <p className="bg-white p-3 rounded border italic text-gray-500">
                {screenData.nullValue === null ? 'null' : screenData.nullValue}
              </p>
            </div>

            <div>
              <h3 className="text-lg font-medium mb-3">Undefined Value</h3>
              <p className="bg-white p-3 rounded border italic text-gray-500">
                {screenData.undefinedValue === undefined ? 'undefined' : screenData.undefinedValue}
              </p>
            </div>
          </div>
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

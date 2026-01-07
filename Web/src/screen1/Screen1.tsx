"use client";

import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useFocusable, setFocus } from '@noriginmedia/norigin-spatial-navigation';
import Hoppr, { HopprTrigger } from '@hoppr/hoppr-web-sdk';

export const Screen1 = () => {
  const navigate = useNavigate();
  const screenData = {
    screenName: "ScreenOne",
    simpleObject: { key1: "value1", key2: 42, key3: false },
    id: 12345,
    name: "SampleName",
    isActive: true,
    score: 98.76,
    tags: ["tag1", "tag2", "tag3"],
    metadata: {
      updatedAt: "2024-02-05T12:00:00Z",
      flags: {
        isVerified: false,
        hasAccess: true
      },
      details: {
        description: "Nested object sample",
        count: 42,
        extraData: {
          key1: "value1",
          key2: 999,
          key3: null
        }
      }
    }
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
        <h1 className="text-3xl font-bold mb-6">Screen One</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">Basic Information</h2>
            <p><strong>ID:</strong> {screenData.id}</p>
            <p><strong>Name:</strong> {screenData.name}</p>
            <p><strong>Active:</strong> {screenData.isActive ? 'Yes' : 'No'}</p>
            <p><strong>Score:</strong> {screenData.score}</p>
          </div>

          <div className="bg-gray-100 p-4 rounded">
            <h2 className="text-xl font-semibold mb-3">Simple Object</h2>
            <p><strong>Key1:</strong> {screenData.simpleObject.key1}</p>
            <p><strong>Key2:</strong> {screenData.simpleObject.key2}</p>
            <p><strong>Key3:</strong> {screenData.simpleObject.key3 ? 'True' : 'False'}</p>
          </div>
        </div>

        <div className="bg-gray-100 p-4 rounded mb-8">
          <h2 className="text-xl font-semibold mb-3">Tags</h2>
          <ul className="list-disc list-inside">
            {screenData.tags.map((tag, index) => (
              <li key={index}>{tag}</li>
            ))}
          </ul>
        </div>

        {/* <div className="bg-gray-100 p-4 rounded mb-8">
          <h2 className="text-xl font-semibold mb-3">Metadata</h2>
          <p><strong>Updated At:</strong> {screenData.metadata.updatedAt}</p>
          
          <h3 className="text-lg font-medium mt-4 mb-2">Flags</h3>
          <p><strong>Verified:</strong> {screenData.metadata.flags.isVerified ? 'Yes' : 'No'}</p>
          <p><strong>Has Access:</strong> {screenData.metadata.flags.hasAccess ? 'Yes' : 'No'}</p>
          
          <h3 className="text-lg font-medium mt-4 mb-2">Details</h3>
          <p><strong>Description:</strong> {screenData.metadata.details.description}</p>
          <p><strong>Count:</strong> {screenData.metadata.details.count}</p>
          
          <h4 className="text-md font-medium mt-3 mb-1">Extra Data</h4>
          <p><strong>Key1:</strong> {screenData.metadata.details.extraData.key1}</p>
          <p><strong>Key2:</strong> {screenData.metadata.details.extraData.key2}</p>
          <p><strong>Key3:</strong> {screenData.metadata.details.extraData.key3 || 'null'}</p>
        </div> */}

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

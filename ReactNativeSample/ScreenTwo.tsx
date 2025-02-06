import { View, Text, Button } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback } from 'react';
import Hoppr, { HopprTrigger } from 'react-native-hoppr';

export function ScreenTwo() {
  const screenData = {
    screenName: "SreenTwo",
    stringValue: "sampleString",
    numberValue: 12345,
    floatValue: 98.76,
    booleanValue: true,
    nullValue: null,
    undefinedValue: undefined
  };

  useFocusEffect(
    useCallback(() => {
      Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);
  
      return () => {
        Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, screenData);
      };
    }, [])
  );

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Screen Two</Text>
    </View>
  );
}
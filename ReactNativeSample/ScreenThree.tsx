import { View, Text, Button } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback } from 'react';
import Hoppr, { HopprTrigger } from '@hoppr/react-native-hoppr';

export function ScreenThree() {
  const screenData = {
    screenName: "ScreenThree",
    stringArray: ["one", "two", "three"],
    numberArray: [1, 2, 3, 4.5],
    booleanArray: [true, false, true],
    mixedArray: ["text", 42, false, null, { key: "value" }],
    emptyArray: []
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
      <Text>Screen Three</Text>
    </View>
  );
}
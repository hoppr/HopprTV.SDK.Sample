import { View, Text, Button, Alert } from 'react-native';
import { useEffect } from 'react';
import Hoppr, { HopprTrigger } from 'react-native-hoppr';

export function ScreenOne() {
  var screenData = {
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

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);

    return () => {
      Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, screenData)
    };
  });

  const clickButton = async (buttonName: string, buttonNumber: number) => {
    await Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, {
      buttonId: buttonName,
      buttonNumber: buttonNumber
    })
    Alert.alert('Button pressed ' + buttonName);
  };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Screen One</Text>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonOne", 1)}
          title="Button One"
        />
      </View>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonTwo", 2)}
          title="Button Two"
        />
      </View>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonThree", 3)}
          title="Button Three"
        />
      </View>
    </View>
  );
}

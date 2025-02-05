import { View, Text, Button } from 'react-native';
import {
  useNavigation, ParamListBase
} from '@react-navigation/native';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';
import Hoppr, { HopprTrigger } from 'react-native-hoppr';
import { useEffect } from 'react';

export function HomeScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<ParamListBase>>();

  var screenData = {
    screenName: "HomeScreen",
  };

  useEffect(() => {
    Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, screenData);

    return () => {
      Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, screenData)
    };
  });

  const navigateToScreen = async (screenName: string) => {
    await Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, {
      buttonId: screenName
    })
    navigation.navigate(screenName);
  };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Home Screen</Text>
      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => navigateToScreen("ScreenOne")}
          title="Screen One"
        />
      </View>
      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => navigateToScreen("ScreenTwo")}
          title="Screen Two"
        />
      </View>
      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => navigateToScreen("ScreenThree")}
          title="Screen Three"
        />
      </View>
      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => navigateToScreen("ScreenVideo")}
          title="Video Screen"
        />
      </View>
    </View>
  );
}

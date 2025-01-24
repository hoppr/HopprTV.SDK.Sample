import { View, Text, Button, Alert } from 'react-native';
import { useEffect } from 'react';

export function ScreenOne() {
  useEffect(() => {
    // Hoppr.trigger(HopprTrigger.ON_SCREEN_ENTER, {
    //   screenName: "MovieSynopsis",
    //   title: "SuperMan",
    //   rating: 5.2,
    //   genre: "Action",
    //   isPopular: true,
    //   cast: ["Actor1", "Actor2"], 
    //   description: null
    // });

    return () => {
      // Hoppr.trigger(HopprTrigger.ON_SCREEN_EXIT, {
      //   screenName: "MovieSynopsis"
      // })
    };
  });

  const clickButton = async (buttonName: string) => {
    // await Hoppr.trigger("ShowToastClick", {})
    // navigation.navigate(screenName);
    Alert.alert('Button pressed ' + buttonName);
  };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Screen One</Text>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonOne")}
          title="Button One"
        />
      </View>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonTwo")}
          title="Button Two"
        />
      </View>

      <View style={{ marginBottom: 10 }}>
        <Button
          onPress={() => clickButton("buttonThree")}
          title="Button Three"
        />
      </View>
    </View>
  );
}

import { useState, useEffect } from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import { HomeScreen } from './HomeScreen';
import { ScreenOne } from './ScreenOne';
import { ScreenTwo } from './ScreenTwo';
import { ScreenThree } from './ScreenThree';
import { ScreenVideo } from './ScreenVideo';
import Hoppr from 'react-native-hoppr';

export default function App() {
  const [result, setResult] = useState<number | undefined>();
  
  const Stack = createNativeStackNavigator();

  useEffect(() => {
    Hoppr.init("DEFAULT", "TestApiKey2", "TestOpUserId", {
      userLevel: "Beginner",
      location: {
        country: "US",
        city: "New York",
      },
    });
  }, []);

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{title: 'Welcome - React Native'}}
        />
        <Stack.Screen name="ScreenOne" component={ScreenOne} />
        <Stack.Screen name="ScreenTwo" component={ScreenTwo} />
        <Stack.Screen name="ScreenThree" component={ScreenThree} />
        <Stack.Screen name="ScreenVideo" component={ScreenVideo} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});


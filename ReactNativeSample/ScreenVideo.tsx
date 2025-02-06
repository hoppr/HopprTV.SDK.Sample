import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Video from 'react-native-video';
import Hoppr, { HopprTrigger } from 'react-native-hoppr';
import { useFocusEffect } from '@react-navigation/native';
import { useCallback } from 'react';

export function ScreenVideo() {
  const screenData = {
    screenName: "ScreenVideo"
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
    <View style={styles.container}>
      <Video
        source={{ uri: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4' }} // Replace with your video URL
        style={styles.video}
        resizeMode="cover" // Adjust to fit your design needs
        onEnd={() => {
          console.log('Video has finished playing');
        }}
        repeat // Set to true if you want the video to loop
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'black',
  },
  video: {
    width: '100%',
    height: '100%',
    position: 'absolute',
  },
  text: {
    color: 'white',
    fontSize: 20,
  },
});

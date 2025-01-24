import { View, Text, Button } from 'react-native';
import { useEffect } from 'react';

export function ScreenThree() {
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
  })
  
    const playClick = async() => {
      
      // await Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, {
      //   id: "MovieSynopsisPlay"
      // })

      console.log("Trigger action completed")
      // Perform Intended Action
    };

  return (
    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
      <Text>Screen Three</Text>
    </View>
  );
}
import AsyncStorage from '@react-native-async-storage/async-storage';
import default_user_data from './data_user.js'

export const getUserSettings = async () => {
    return getData("userSettings").then((settings)=>{
        if (settings == null) {
            console.log('ne postoji')
            return default_user_data
        } else {
            return JSON.parse(settings)
        }
    })

}

export const setUserSettings = (settingsObj) => {
    storeData("userSettings", JSON.stringify(settingsObj))
}

export const storeData = async (key, value) => {		
    try {
      console.log("storing", key, value)
      await AsyncStorage.setItem(key, value)
    } catch (e) {
      // saving error
      console.log("storing error", e)
    }
  }
  
  // get item
  export const getData = async (key) => {
    try {
      const value = await AsyncStorage.getItem(key)
      if(value !== null) {
        // value previously stored
        console.log(value)
        return value
      }
    } catch(e) {
      // error reading value
      console.log("fetching error", e)
    }
  }
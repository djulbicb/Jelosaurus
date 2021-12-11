import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert  } from 'react-native';
import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/FontAwesome';
import { Provider as PaperProvider } from 'react-native-paper';
import { Col, Row, Grid } from "react-native-easy-grid";
import * as store from './../store.js'
import * as common from './common/Common'

const SettingsScreen = () => {
    const [user, setUser] = React.useState(store.getUserSettings);
    const [totalDay, setTotalDay] = React.useState(0)
    const [day, setDay] = React.useState(0)
    const [week, setWeek] = React.useState(0)
  
    const daysBetween = (startDate, endDate) => {
      // The number of milliseconds in all UTC days (no DST)
      const oneDay = 1000 * 60 * 60 * 24;
    
      // A day in UTC always lasts 24 hours (unlike in other time formats)
      const start = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
      const end = Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
    
      // so it's safe to divide by 24 hours
      return (start - end) / oneDay;
    }
  
  
    const setCurrentUserDate = (settings) => {
      const today = new Date()
      const userStartDay =new Date(settings.startDate);
      const totalDays = daysBetween(userStartDay, today)
  
      console.log(totalDays)
      setTime(totalDays)
    }
  
    const setTime = (totalDays) => {
      setTotalDay(totalDays)
      setDay(totalDays % 7)
      setWeek(Math.floor(totalDays / 7))
    }
  
    // Run on tab change
    useFocusEffect(
      React.useCallback(() => {
        store.getUserSettings().then((settings) => {
          setUser(settings)
          setTime(settings.totalDays)
        })
      }, [])
    );
  
  
    // USER SETTINGS
    ///////////////////////////////////////////////////////////
    const startUserSettings = (e) => {
      const today = new Date().toISOString().substring(0, 10);
      const userSettings = {
        startDate: today,
        totalDays: 0
      }
      store.setUserSettings(userSettings)
      setUser(userSettings)
      setCurrentUserDate(userSettings)
    }
    const resetUserSettings = (e) => {
      Alert.alert(
        "Resetovanje korisnickih podesavanja",
        "Da li ste sigurni da zelite obrisati podesavanja?",
        [
          // The "Yes" button
          {
            text: "Da",
            onPress: () => {
              const userSettings = {
                startDate: undefined,
                totalDays: 0
              }
              store.setUserSettings(userSettings)
              setUser(userSettings)      
            },
          },
          // The "No" button
          // Does nothing but dismiss the dialog when tapped
          {
            text: "Ne",
          },
        ]
      );
    }
  
    const saveTotalDateChange= () => {
      const userSettings = {
        startDate: user.startDate,
        totalDays: totalDay
      }
      store.setUserSettings(userSettings)
    }
  
    const totalDateIncrease = () => {
      const newTotalDays = totalDay + 1
        setTime(newTotalDays)
    }
  
    const totalDateDecrease = () => {
      if (totalDay > 0) {
        const newTotalDays = totalDay - 1
        setTime(newTotalDays)
      }
    }
  
    return (
      <Grid style={{ ...common.container }}>
        <Row size={30} style={{ backgroundColor: 'green', justifyContent: 'flex-end', alignContent: 'flex-end' }}>
          {/* USER SETTINGS - SHOW CURRENT DATE - START */}
          {
            user.startDate ?
              <Col>
                <Row size={10} >
                  <Text style={{ ...common.title, width: "100%", textAlign: 'center', alignSelf: 'flex-end' }}>{totalDay + 1}</Text>
                </Row>
  
                <Row size={10} style={{ backgroundColor: 'orange' }}>
                  <Col size={20}>
                    <Button title='<' onPress={totalDateDecrease}></Button>
                  </Col>
                  <Col size={80}>
                    <Text style={{ alignSelf: 'center', textAlign: 'center', fontSize: 25 }}>Dan {day + 1} Nedelja {week + 1}</Text>
                    <Text>
                      {JSON.stringify(user)}
                    </Text>
                  </Col>
                  <Col size={20}>
                    <Button title='>' onPress={totalDateIncrease}></Button>
                  </Col>
                </Row>
              </Col>
              :
              <Text>Klikni na Start na zapocnes pracenje dijete</Text>
          }
          {/* USER SETTINGS - SHOW CURRENT DATE - END */}
  
        </Row>
        <Row size={8}>
          <Col>
            <Button onPress={saveTotalDateChange} title='Sacuvaj izmenu'></Button>
          </Col>
        </Row>
  
        <Row size={8}>
          <Col>
            <Button onPress={startUserSettings} title='Start'></Button>
          </Col>
        </Row>
  
        <Row size={8}>
          <Col>
            <Button title='Export'></Button>
          </Col>
        </Row>
  
        <Row size={8}>
          <Col>
            <Button title='Import'></Button>
          </Col>
        </Row>
        <Row size={8}>
          <Col>
            <Button onPress={resetUserSettings} title='Reset'></Button>
          </Col>
        </Row>
      </Grid>
    );
  }

  export default SettingsScreen
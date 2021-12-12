import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { Col, Row, Grid } from "react-native-easy-grid";
import * as store from './../store.js'
import * as common from './common/Common'
import TouchButton from './ui/TouchButton.js';
import menu from '../data/data_menu.js';

const SettingsScreen = () => {
  const [user, setUser] = React.useState(store.getUserSettings);
  const [totalDay, setTotalDay] = React.useState(0)
  const [day, setDay] = React.useState(0)
  const [week, setWeek] = React.useState(0)

  const setCurrentUserDate = (settings) => {
    const _today = new Date()
    const _userStartDay = new Date(settings.startDate);
    const _totalDays = common.daysBetween(_userStartDay, _today)

    setTime(_totalDays)
  }

  const setTime = (_totalDays) => {
    setTotalDay(_totalDays)
    setDay(_totalDays % 7)
    setWeek(Math.floor(_totalDays / 7))
  }

  // Run on tab change
  useFocusEffect(
    React.useCallback(() => {
      store.getUserSettings().then((settings) => {
        setUser(settings)
        const dayDiff = common.daysBetween(new Date(settings.startDate), new Date())
        setTime(dayDiff)
      })
    }, [])
  );


  // USER SETTINGS
  ///////////////////////////////////////////////////////////
  const startUserSettings = (e) => {
    const today = new Date().toISOString().substring(0, 10);
    const userSettings = {
      startDate: today
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
              startDate: undefined
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

  const saveTotalDateChange = () => {
    const userSettings = {
      startDate: user.startDate
    }
    store.setUserSettings(userSettings)
  }

  const totalDateIncrease = () => {
    const startDate = new Date(user.startDate)
    const newDate = common.changeDateByDay(startDate, -1)

    const nextDay = common.daysBetween(newDate, new Date())
    if (nextDay >= menu.days.length) {
      return
    }

    setUser({ startDate: newDate })
    setTime(nextDay)
  }

  const totalDateDecrease = () => {
    const currentDiff = common.daysBetween(new Date(user.startDate), new Date())

    if (currentDiff <= 0) {
      return
    }

    const startDate = new Date(user.startDate)
    const newDate = common.changeDateByDay(startDate, 1)

    setTime(common.daysBetween(new Date(newDate), new Date()))
    setUser({ startDate: newDate })

  }

  return (
    <Grid style={{ ...common.container }}>
      <Row size={10}></Row>
      <Row size={20} style={{ justifyContent: 'flex-end', alignContent: 'flex-end' }}>
        {/* USER SETTINGS - SHOW CURRENT DATE - START */}
        {
          user.startDate ?
            <Col>
              <Row size={10} style={{}}>
                <Col size={20} style={{ alignContent: 'stretch', justifyContent: 'center', marginRight: common.margin }}>
                  <TouchButton onPress={totalDateDecrease} text="&lt;"></TouchButton>
                </Col>
                <Col size={60}>
                  <Text style={{ ...common.title, width: "100%", textAlign: 'center', alignSelf: 'flex-end' }}>{totalDay + 1}</Text>
                  <Text style={{ alignSelf: 'center', textAlign: 'center', fontSize: 25 }}>Dan {day + 1} Nedelja {week + 1}</Text>
                </Col>
                <Col size={20} style={{ alignContent: 'stretch', justifyContent: 'center', marginLeft: common.margin }}>
                  <TouchButton onPress={totalDateIncrease} text="&gt;"></TouchButton>
                </Col>
              </Row>
            </Col>
            :
            <Col style={{ alignItems: 'center', justifyContent: 'center', borderRadius: 5, borderColor: common.DEF_COLOR, borderWidth: 2 }}>
              <Text style={{ fontSize: 25, textAlign: 'center', color: common.DEF_COLOR }}>Klikni na Start na započneš praćenje dijete</Text>
            </Col>

        }
        {/* USER SETTINGS - SHOW CURRENT DATE - END */}

      </Row>
      <Row size={3}>
        <Col style={{ flexDirection: 'row', alignItems: 'center' }}>
          <Col style={{ flex: 1, height: 1, backgroundColor: 'black' }} />
        </Col>
      </Row>
      <Row size={37}>
        <Col>
          <Row>
            <Col style={{ alignContent: 'stretch', justifyContent: 'center', marginBottom: 10, borderRadius: 5 }}>
              <TouchButton onPress={saveTotalDateChange} text="Sačuvaj izmene"></TouchButton>
            </Col>
          </Row>

          <Row>
            <Col style={{ alignContent: 'stretch', justifyContent: 'center', marginBottom: 10, borderRadius: 5 }}>
              <TouchButton onPress={startUserSettings} text="Start"></TouchButton>
            </Col>
          </Row>

          <Row>
            <Col style={{ alignContent: 'stretch', justifyContent: 'center', marginBottom: 10, borderRadius: 5 }}>
              <TouchButton onPress={resetUserSettings} text="Reset"></TouchButton>
            </Col>

          </Row>
        </Col>
      </Row>


    </Grid>
  );
}

export default SettingsScreen
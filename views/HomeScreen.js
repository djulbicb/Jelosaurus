import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert  } from 'react-native';
import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/FontAwesome';
import { Provider as PaperProvider } from 'react-native-paper';
import { Col, Row, Grid } from "react-native-easy-grid";
import * as store from './../store.js'
import menu from './../data_menu.js'

const HomeScreen = () => {
    const [totalDays, setTotalDays] = React.useState(0)
    const [day, setDay] = React.useState(0)
    const [week, setWeek] = React.useState(0)

    const [meals, setMeals] = React.useState([])
    const [drinks, setDrinks] = React.useState([])

    const loadMeal = (totalDayCount) => {
        const _meals = menu.days[totalDayCount].meals;
        setMeals(_meals)
    }
    const loadDrinks = (totalDayCount) => {
        const _drinks = menu.days[totalDayCount].drinks;
        setDrinks(_drinks)
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
            setTotalDays(settings.totalDays)

            console.log(settings.totalDays)
            loadMeal(settings.totalDays)
            loadDrinks(settings.totalDays)
          })    
        }, [])
      );
      
    

    const styles = StyleSheet.create({
      orange_box: {
        backgroundColor: '#ddd'
      },
      green_box: {
        backgroundColor: '#eee'
      }
    });


    const menuTitleSize = 30
    const menuListSize = 70
    const cssMenuTitle = StyleSheet.create({
        header : {
            justifyContent: 'center', 
            alignItems: 'center',
        },
        body : {
            padding: 5
        },
        title: {
            fontSize: 25
        }
    })

    const listItems = (pTitle, pMeals) => {
        if (!pMeals) {
            return []
        }
        if (!Array.isArray(pMeals)) {
            pMeals = [pMeals]
        }
        const elements = []

        if (pMeals) {
            pMeals.forEach((meal, i) => {
                elements.push(
                    <Row key={pTitle + i}>
                        <Text adjustsFontSizeToFit style={{fontSize: 20}}>
                            {meal.name} {meal.weight}{meal.measure}
                        </Text>
                    </Row>
                )
            })
        }

        return elements
    }
  

    const goDateBackward = () => {
        const newTotalDays = totalDays - 1
        if (newTotalDays >= 0) {
            setTotalDays(newTotalDays)
            loadMeal(newTotalDays)
        }
    }

    const goDateForward = () => {
        const newTotalDays = totalDays + 1
        if (meals.length > newTotalDays) {
            setTotalDays(newTotalDays)
            loadMeal(newTotalDays)
        }
    }


    return (
      <Grid style={{ padding: 10 }}>
        <Row size={67}>
          <Col>
            <Row style={styles.orange_box}>
                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                    <Text style={cssMenuTitle.title}>Dorucak</Text>
                </Col>
                <Col size={menuListSize} style={cssMenuTitle.body}>
                    {listItems("Dorucak", meals.breakfast)}
                </Col>
            </Row>
            <Row style={styles.green_box}>
                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                <Text style={cssMenuTitle.title}>Uzina 1</Text>
                </Col>
                <Col size={menuListSize} style={cssMenuTitle.body}>
                    {listItems("Uzina01", meals.snack_01)}
                </Col>
            </Row>
            <Row style={styles.orange_box}>
                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                    <Text style={cssMenuTitle.title}>Rucak</Text>
                </Col>
                <Col size={menuListSize} style={cssMenuTitle.body}>
                    {listItems("Rucak",meals.lunch)}
                </Col>
            </Row>
            <Row style={styles.green_box}>
                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                    <Text style={cssMenuTitle.title}>Uzina 2</Text>
                </Col>
                <Col size={menuListSize} style={cssMenuTitle.body}>
                    {listItems("Uzina02",meals.snack_02)}
                </Col>
            </Row>
            <Row style={styles.orange_box}>
                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                    <Text style={cssMenuTitle.title}>Vecera</Text>
                </Col>
                <Col size={menuListSize} style={cssMenuTitle.body}>
                    {listItems("Vecera", meals.dinner)}
                </Col>
            </Row>
          </Col>
        </Row>
        <Row size={3}>
            <Col style={{flexDirection: 'row', alignItems: 'center'}}>
                <Col style={{flex: 1, height: 1, backgroundColor: 'black'}} />
            </Col>
        </Row>
        <Row size={17}>
          <Col size={49}>
            <Row size={menuTitleSize} style={cssMenuTitle.header}>
                <Text style={cssMenuTitle.title}>Budjenje</Text>
            </Row>
            <Row size={menuListSize} style={cssMenuTitle.body}>
                {listItems("Dorucak", drinks.morning)}
            </Row>
          </Col>
          <Col size={2}>

          </Col>
          <Col size={49}>
            <Row size={menuTitleSize} style={cssMenuTitle.header}>
                <Text style={cssMenuTitle.title}>Tokom dana</Text>
            </Row>
            <Row size={menuListSize} style={cssMenuTitle.body}>
                {listItems("Dorucak", drinks.day?.[0])}
            </Row>
          </Col>
        </Row>
        <Row size={3}>
            <Col style={{flexDirection: 'row', alignItems: 'center'}}>
                <Col style={{flex: 1, height: 1, backgroundColor: 'black'}} />
            </Col>
        </Row>
        <Row size={10}>
          <Col size={25}>
            <Button onPress={goDateBackward} title='<'></Button>
          </Col>
          <Col size={50}>
            <Text>Dan 1 Nedelja 1 - {totalDays}</Text>
          </Col>
          <Col size={25}>
            <Button onPress={goDateForward} title='>'></Button>
          </Col>
        </Row>
      </Grid>
  
  
    );
  }
  
  export default HomeScreen
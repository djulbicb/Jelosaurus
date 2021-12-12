import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert } from 'react-native';
import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/FontAwesome';
import { Provider as PaperProvider } from 'react-native-paper';
import { Col, Row, Grid } from "react-native-easy-grid";
import * as store from './../store.js'
import menu from '../data/data_menu.js'
import * as common from './common/Common.js'
import TouchButton from './ui/TouchButton.js';
import userSettings from '../data/data_user.js';

const HomeScreen = () => {
    const [user, setUser] = React.useState({})
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
        setTotalDays(totalDays)
        setDay(totalDays % 7)
        setWeek(Math.floor(totalDays / 7))
    }

    const normalizeDayCount = (dayDiff) => {
        const length = menu.days.length
        let currentDay = dayDiff

        while (currentDay >= length) {
            currentDay = currentDay % length
        }

        return currentDay
    }

    // Run on tab change
    useFocusEffect(
        React.useCallback(() => {
            store.getUserSettings().then((settings) => {
                setUser(settings)
                if (!settings.startDate) {
                    return
                }

                const dayDiff = common.daysBetween(new Date(settings.startDate), new Date())
                setTotalDays(dayDiff)

                let currentDay = normalizeDayCount(dayDiff)
                loadMeal(currentDay)
                loadDrinks(currentDay)

                setTime(dayDiff)
            })
        }, [])
    );



    const styles = StyleSheet.create({
        dark_field: {
            backgroundColor: '#ddd'
        },
        light_field: {
            backgroundColor: '#eee'
        }
    });


    const menuTitleSize = 30
    const menuListSize = 70
    const cssMenuTitle = StyleSheet.create({
        header: {
            justifyContent: 'center',
            alignItems: 'center',
        },
        body: {
            padding: 5
        },
        title: {
            fontSize: 25
        }
    })

    const listItems = (pTitle, pMeals, style = {}) => {
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
                    <Row key={pTitle + i} style={style}>
                        <Text adjustsFontSizeToFit style={{ fontSize: 20 }}>
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
        if (newTotalDays >= 0 && menu.days.length > newTotalDays) {
            setTotalDays(newTotalDays)
            setTime(newTotalDays)
            loadMeal(newTotalDays)
        }
    }

    const goDateForward = () => {
        const newTotalDays = totalDays + 1
        if (newTotalDays >= 0 && menu.days.length > newTotalDays) {
            setTotalDays(newTotalDays)
            setTime(newTotalDays)
            loadMeal(newTotalDays)
        }
    }


    return (
        <>
            <>
                {!user.startDate && 
                    <Grid style={{ padding: 10 }}>
                        <Col style={{alignItems: 'center', justifyContent: 'center', borderRadius: 5, borderColor: common.DEF_COLOR, borderWidth: 2}}>
                            <Text style={{fontSize: 25, textAlign: 'center', color: common.DEF_COLOR}}>Aktiviraj praćenje u podešavanju</Text>
                        </Col>
                    </Grid>}
            </>
            <>
                {user.startDate && <Grid style={{ padding: 10 }}>
                    <Row size={67}>
                        <Col>
                            <Row style={styles.dark_field}>
                                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                                    <Text style={cssMenuTitle.title}>Dorucak</Text>
                                </Col>
                                <Col size={menuListSize} style={cssMenuTitle.body}>
                                    {listItems("Dorucak", meals.breakfast)}
                                </Col>
                            </Row>
                            <Row style={styles.light_field}>
                                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                                    <Text style={cssMenuTitle.title}>Uzina 1</Text>
                                </Col>
                                <Col size={menuListSize} style={cssMenuTitle.body}>
                                    {listItems("Uzina01", meals.snack_01)}
                                </Col>
                            </Row>
                            <Row style={styles.dark_field}>
                                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                                    <Text style={cssMenuTitle.title}>Rucak</Text>
                                </Col>
                                <Col size={menuListSize} style={cssMenuTitle.body}>
                                    {listItems("Rucak", meals.lunch)}
                                </Col>
                            </Row>
                            <Row style={styles.light_field}>
                                <Col size={menuTitleSize} style={cssMenuTitle.header}>
                                    <Text style={cssMenuTitle.title}>Uzina 2</Text>
                                </Col>
                                <Col size={menuListSize} style={cssMenuTitle.body}>
                                    {listItems("Uzina02", meals.snack_02)}
                                </Col>
                            </Row>
                            <Row style={styles.dark_field}>
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
                        <Col style={{ flexDirection: 'row', alignItems: 'center' }}>
                        <Col style={{ flex: 1, height: 1, backgroundColor: common.DEF_COLOR }} />
                        </Col>
                    </Row>
                    <Row size={17}>
                        <Col size={49}>
                            <Row size={50} style={{ alignItems: 'center', justifyContent: 'center' }}>
                                <Text style={cssMenuTitle.title}>Budjenje</Text>
                            </Row>
                            <Row size={menuListSize} style={{ alignItems: 'center', justifyContent: 'center' }}>
                                {listItems("Dorucak", drinks.morning, { justifyContent: 'center' })}
                            </Row>
                        </Col>
                        <Col size={2}>

                        </Col>
                        <Col size={49}>
                            <Row size={50} style={{ alignItems: 'center', justifyContent: 'center' }}>
                                <Text style={cssMenuTitle.title}>Tokom dana</Text>
                            </Row>
                            <Row size={menuListSize} style={{ alignItems: 'center', justifyContent: 'center' }}>
                                {listItems("Dorucak", drinks.day?.[0], { justifyContent: 'center' })}
                            </Row>
                        </Col>
                    </Row>
                    <Row size={3}>
                        <Col style={{ flexDirection: 'row', alignItems: 'center' }}>
                            <Col style={{ flex: 1, height: 1, backgroundColor: common.DEF_COLOR }} />
                        </Col>
                    </Row>
                    <Row size={10}>
                        <Col size={25}>
                            <TouchButton onPress={goDateBackward} text="&lt;"></TouchButton>
                        </Col>
                        <Col size={50} style={{ justifyContent: 'center', alignItems: 'center' }}>
                            <Text style={{ fontSize: 20 }}>Dan {day + 1} Nedelja {week + 1}</Text>
                        </Col>
                        <Col size={25}>
                            <TouchButton onPress={goDateForward} text="&gt;"></TouchButton>
                        </Col>
                    </Row>
                </Grid>}
            </>

        </>
    );
}

export default HomeScreen
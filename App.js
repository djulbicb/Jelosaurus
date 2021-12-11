import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert } from 'react-native';
import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import Icon from 'react-native-vector-icons/FontAwesome';
import SettingsScreen from './views/SettingsScreen'
import HomeScreen from './views/HomeScreen'

import * as common from './views/common/Common.js'


const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={({ route }) => ({
          tabBarIcon: ({ focused, color, size }) => {
            let iconName;

            if (route.name === common.MEAL) {
              iconName = 'calendar'
            } else if (route.name === common.SETTINGS) {
              iconName = 'gear';
            }

            // You can return any component that you like here!
            return <Icon size={size} color={color} name={iconName} type='FontAwesome' />
          },
          tabBarActiveTintColor: 'tomato',
          tabBarInactiveTintColor: 'gray',
        })}
      >
        <Tab.Screen name={common.MEAL} component={HomeScreen} />
        <Tab.Screen name={common.SETTINGS} component={SettingsScreen} />
      </Tab.Navigator>
    </NavigationContainer>
  );
}
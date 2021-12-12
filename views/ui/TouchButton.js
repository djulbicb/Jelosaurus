import * as React from 'react';
import { StyleSheet, Text, View, Button, TextInput, Alert, TouchableOpacity } from 'react-native';
import * as common from '../common/Common'
const TouchButton = (props) => {
    const DEFAULT_COLOR = props.bgColor ? props.bgColor : common.DEF_COLOR
    const [bgColor, setBgColor] = React.useState(DEFAULT_COLOR)

    const onPressIn = (e) => {
        setBgColor('#45aaf2')
        if (props.onPress) {
            props.onPress()
        }
    }

    const onPressOut = (e) => {
        setBgColor(DEFAULT_COLOR)
    }

    return (
        <View style={{borderRadius: 5, backgroundColor: bgColor}}>
            <TouchableOpacity activeOpacity={0.5} onPressOut={onPressOut} onPressIn={e => onPressIn(e, this)} style={{ borderRadius: 5, height: "100%", alignItems:'center', justifyContent:'center' }}>
                <Text style={{color: 'white', fontWeight: '500', textTransform: 'uppercase'}}>{props.text}</Text>
            </TouchableOpacity>
        </View>
        
    )
}

export default TouchButton


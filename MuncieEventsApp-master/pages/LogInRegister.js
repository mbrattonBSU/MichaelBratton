import React from 'react';
import {Text, View, Button} from 'react-native';
import TopBar from './top_bar';

export default class LogInRegister extends React.Component {
    render() {
      return (
        <View style={{paddingTop:20}}>
          <TopBar />
          <View style={{paddingTop:30}}>
            <Text> Log In/Register </Text>
          </View>
        </View>
      )
    }
}
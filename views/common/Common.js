import { StyleSheet } from 'react-native';

export const CALENDAR = 'Kalendar'
export const SETTINGS = 'Podešavanje'

export const DEF_COLOR = "#2d98da"

export const margin = 10
export const border = StyleSheet.create({
  borderBottomWidth: 2,
  borderBottomColor: "black"
})
export const container = StyleSheet.create({
  padding: 10
})
export const title = StyleSheet.create({
  fontSize: 80
})


export const daysBetween = (startDate, endDate) => {
  startDate = new Date(startDate)
  endDate = new Date(endDate)
  // The number of milliseconds in all UTC days (no DST)
  const oneDay = 1000 * 60 * 60 * 24;

  // A day in UTC always lasts 24 hours (unlike in other time formats)
  const start = Date.UTC(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
  const end = Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());

  // so it's safe to divide by 24 hours
  return (start - end) / oneDay;
}

export const changeDateByDay = (someDate, numberOfDaysToAdd) => {
  const newDate = new Date(someDate.getTime() + (numberOfDaysToAdd * 24 * 60 * 60 * 1000));
  return newDate;
}
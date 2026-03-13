import axiosInstance from "./axiosInstance";


export const getMatchPin = (param) =>
  axiosInstance.get(`/center/getmpin?param=${param}`);

export const getPinDet = (param) => axiosInstance.get(`/center/getpindet?id=${param}`)

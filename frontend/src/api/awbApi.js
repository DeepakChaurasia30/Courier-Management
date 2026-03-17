import axiosInstance from "./axiosInstance";

export const getAwbStatus = (awb) => axiosInstance.get(`/entry/getstatus?awb=${awb}`, {

})

export const addAwb = (data) => axiosInstance.post("/entry/add", data);

export const getSigAwb = (awb) => axiosInstance.get(`/entry/getawb?awb=${awb}`)

export const updateAwb = (id, data) => axiosInstance.put(`/entry/update/${id}`, data)

export const deleteAwb = (id) => axiosInstance.delete(`/entry/delete/${id}`)

export const tableFill = () => axiosInstance.get("/entry/gettable");

export const getSummary = (custID) => axiosInstance.get(`/entry/summary?custID=${custID}`);

export const fetchShipments = (clientId, startdate, enddate) => axiosInstance.get(`entry/btwdates?clientid=${clientId}&startDate=${startdate}&endDate=${enddate}`);
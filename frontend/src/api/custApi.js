import axiosInstance from "./axiosInstance";


export const getAllCust = (id) => axiosInstance.get(`/cust/getcust?id=${id}`);

export const addCustomer = (data) =>
  axiosInstance.post("/cust/add", data);

export const updateCustomer = (id, data) =>
  axiosInstance.put(`/cust/update/${id}`, data);

export const deleteCustomer = (id) =>
  axiosInstance.delete(`/cust/delete/${id}`);

export const getCustomerById = (id) =>
  axiosInstance.get(`/cust/getent/${id}`);
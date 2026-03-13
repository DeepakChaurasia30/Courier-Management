import axiosInstance from "./axiosInstance";

export const genInv =(data)=>axiosInstance.post("/inv/add",data)

export const getStatus = (inv_no) => axiosInstance.get(`/inv/getStatus?inv_no=${inv_no}`)

export const getinvpdf = (inv_no) => axiosInstance.get(`/inv/getpdf?inv_no=${inv_no}` );

// export const deleteInv =(inv_no)=>axiosInstance.delete(`/inv/delete?inv_no=${inv_no}`);

export const cancelInv =(inv_no)=>axiosInstance.put(`/inv/cancel?inv_no=${inv_no}`);

export const getTableData =(clientID)=>axiosInstance.get(`/inv/gettable?clientID=${clientID}`);

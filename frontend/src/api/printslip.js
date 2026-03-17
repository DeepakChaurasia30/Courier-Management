import axiosInstance from "./axiosInstance";

export const getpdfSlip =(data) => axiosInstance.post("/slip/getpdf",data , {
      responseType: "blob"
    });
import axiosInstance from "./axiosInstance";

export const getInvno =(fy,gst)=> axiosInstance.get(`invseq/getseq?fy=${fy}&id=${gst}`);
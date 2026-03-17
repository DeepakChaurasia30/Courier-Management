import axiosInstance from "./axiosInstance";

export const getInvno =(fy,clientId,gst)=> axiosInstance.get(`invseq/getseq?fy=${fy}&clientId=${clientId}&id=${gst}`);
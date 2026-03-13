import axiosInstance from "./axiosInstance";

export const getCompDet =(id)=>(axiosInstance.get(`/client/get/${id}`));

export const updateCompDet = (id, formData) => {
  return axiosInstance.put(`/client/update/${id}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
};
import React, { useState, useEffect } from "react";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import { fetchCustomers } from "../utilities/customer";
import { getMatchPin, getPinDet } from "../../api/destApi";
import { addCustomer, updateCustomer, getCustomerById } from "../../api/custApi";
import Spinner from "../../components/ui/Spinner";

const emptyCustomer = {
  cust_code: "",
  cust_name: "",
  contact_person: "",
  mobile: "",
  email: "",
  is_gst: "0",
  cust_gst: "",
  address1: "",
  address2: "",
  cust_pin: "",
  city: "",
  cust_state_code: "",
  gst_state_code: "",
  fuel_rate: 0,
  discount_rate: "",
  clientId: 1,
};

export default function CustomerForm() {

  const [customer, setCustomer] = useState(emptyCustomer);
  const [customerOptions, setCustomerOptions] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formEnabled, setFormEnabled] = useState(false);
  const [loading,setloading] = useState(false);

  /* ---------- HELPERS ---------- */

  const formatUpper = (v) => v.toUpperCase();

  const validateMobile = (m) => /^[6-9]\d{9}$/.test(m);

  const validateEmail = (e) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(e);

  const formatPercent = (value) => {
    let v = value.replace(/[^\d.]/g, "");
    if (v.includes(".")) {
      const parts = v.split(".");
      v = parts[0].slice(0, 2) + "." + parts[1].slice(0, 1);
    } else {
      v = v.slice(0, 2);
    }
    return v;
  };

  /* ---------- LOAD CUSTOMERS ---------- */

  const loadCustomerOptions = async () => {
    try {
      const data = await fetchCustomers(1);
      setCustomerOptions(data || []);
    } catch {
      toast.error("Customer load failed");
    }
  };

  useEffect(() => {
    loadCustomerOptions();
  }, []);

  /* ---------- INPUT CHANGE ---------- */

  const handleChange = (e) => {

    let { name, value } = e.target;

    if (
      ["cust_name", "cust_code", "cust_gst", "address1", "contact_person", "address2"]
        .includes(name)
    ) value = formatUpper(value);

    if (name === "mobile") value = value.replace(/\D/g, "").slice(0, 10);

    if (name === "fuel_rate" || name === "discount_rate")
      value = formatPercent(value);

    if (name === "is_gst" && value === "0") {
      setCustomer((prev) => ({
        ...prev,
        is_gst: value,
        cust_gst: "",
      }));
      return;
    }

    setCustomer((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  /* ---------- SEARCH CUSTOMER ---------- */

  const handleSearchChange = async (id) => {

    if (!id) return;
     setloading(true)
    try {

      const res = await getCustomerById(id);

      await mapBackendToForm(res.data);

      setEditingId(res.data.custId);
      setFormEnabled(true);
      setloading(false)

    } catch {

      toast.error("Failed to fetch customer");
      setloading(false)

    }
  };

  /* ---------- MAP BACKEND DATA ---------- */

  const mapBackendToForm = async (apiData) => {

    let pinData = { centerName: "", stateCode: "" };

    if (apiData.custPin) {
      try {
        const res = await getPinDet(apiData.custPin);
        pinData = res.data;
      } catch { }
    }

    setCustomer({
      cust_code: apiData.custCode || "",
      cust_name: apiData.custName || "",
      contact_person: apiData.contPerson || "",
      mobile: apiData.contNo || "",
      email: apiData.custMail || "",
      is_gst: apiData.isGst ? "1" : "0",
      cust_gst: apiData.custGst || "",
      address1: apiData.custAdd || "",
      address2: "",
      cust_pin: apiData.custPin || "",
      city: pinData.centerName || "",
      cust_state_code: pinData.stateCode || "",
      gst_state_code: pinData.stateCode || "",
      fuel_rate: apiData.fuelRate || 0,
      discount_rate: apiData.discountRate || "",

      clientId: 1,
    });
    console.log(customer);
    
  };

  /* ---------- PINCODE SEARCH ---------- */

  const loadPincode = async (inputValue) => {

    if (!inputValue) return [];

    try {

      const res = await getMatchPin(inputValue);

      return res.data.map((item) => ({
        value: item.destId,
        label: item.pincode,
        data: item,
      }));

    } catch {

      toast.error("Failed to load pincodes");
      return [];

    }
  };

  const handlePincodeSelect = async (opt) => {

    if (!opt) return;

    const p = opt.data;

    try {

      const res = await getPinDet(p.pincode);

      setCustomer((prev) => ({
        ...prev,
        cust_pin: p.pincode,
        city: res.data.centerName,
        cust_state_code: res.data.stateCode,
        gst_state_code: res.data.stateCode,
      }));

    } catch {

      toast.error("Failed to fetch pincode details");

    }
  };

  /* ---------- VALIDATION ---------- */

  const validateForm = () => {

  if (!customer.cust_code) {
    toast.error("Customer Code Required");
    return false;
  }

  if (!customer.cust_name) {
    toast.error("Company Name Required");
    return false;
  }

  if (customer.mobile && !validateMobile(customer.mobile)) {
    toast.error("Invalid Mobile");
    return false;
  }

  if (customer.email && !validateEmail(customer.email)) {
    toast.error("Invalid Email");
    return false;
  }

  if (!customer.cust_pin) {
    toast.error("Pincode Required");
    return false;
  }

  if (customer.is_gst === "1" && !customer.cust_gst) {
    toast.error("GSTIN Required");
    return false;
  }

  return true;
};

  /* ---------- DTO ---------- */

  const formToBackendObj = () => {

    return {
      clientId: 1,
      contNo: customer.mobile,
      contPerson: customer.contact_person,
      custAdd: customer.address1,
      custCode: customer.cust_code,
      custGst: customer.cust_gst,
      custMail: customer.email,
      custName: customer.cust_name,
      custPin: customer.cust_pin,
      custStateCode: customer.cust_state_code,
      discountRate: customer.discount_rate,
      fuelRate: customer.fuel_rate,
      isGst: customer.is_gst === "1",
    };
  };

  /* ---------- SUBMIT ---------- */

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (!validateForm()) return;
      setloading(true)
    try {

      const dto = formToBackendObj();

      if (editingId) {

        await updateCustomer(editingId, dto);
        toast.success("Customer Updated");

      } else {

        await addCustomer(dto);
        toast.success("Customer Added");

      }

      resetForm();
      loadCustomerOptions();
      setloading(false)

    } catch(err) {

      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Add/Update Failed";

        toast.error(msg)

        setloading(false)

    }
  };

  /* ---------- RESET ---------- */

  const resetForm = () => {
    setCustomer(emptyCustomer);
    setEditingId(null);
    setFormEnabled(false);
  };

  const handleAddNew = () => {
    resetForm();
    setFormEnabled(true);
  };

  return (
    <div className="p-2 md:p-4 bg-gray-100 min-h-screen">
      {loading && <Spinner />}

      <ToastContainer />

      <div className="max-w-7xl mx-auto bg-white shadow rounded-lg p-4 space-y-3">

        <h2 className="text-lg font-semibold">Customer Master</h2>

        <button
          onClick={handleAddNew}
          className="bg-blue-600 text-white px-4 py-1 rounded text-xs"
        >
          Add New Customer
        </button>

        

        {/* SEARCH */}

        <div className="max-w-md">
          <label className="text-xs font-medium block mb-1">
            Search Customer
          </label>

          <Select
            options={customerOptions}
            placeholder="Search Customer Name"
            className="text-xs"
            classNamePrefix="react-select"
            onChange={(selected) => handleSearchChange(selected.value)}
          />
        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-3"
          style={{
            pointerEvents: formEnabled ? "auto" : "none",
            opacity: formEnabled ? 1 : 0.6,
          }}
        >

          {/* CUSTOMER DETAILS */}

          <div className="border rounded p-2 space-y-2">

            <h3 className="text-xs font-semibold">Customer Details</h3>

            <div className="grid grid-cols-12 gap-2">

              <div className="col-span-2">
                <label className="text-xs">Customer Code</label>
                <input name="cust_code" value={customer.cust_code} onChange={handleChange} className="input" />
              </div>

              <div className="col-span-4">
                <label className="text-xs">Company Name</label>
                <input name="cust_name" value={customer.cust_name} onChange={handleChange} className="input" />
              </div>

              <div className="col-span-3">
                <label className="text-xs">Contact Person</label>
                <input name="contact_person" value={customer.contact_person} onChange={handleChange} className="input" />
              </div>

              <div className="col-span-3">
                <label className="text-xs">Mobile</label>
                <input name="mobile" value={customer.mobile} onChange={handleChange} className="input" />
              </div>

              <div className="col-span-4">
                <label className="text-xs">Email</label>
                <input name="email" value={customer.email} onChange={handleChange} className="input" />
              </div>

            </div>

          </div>

          {/* GST */}

          <div className="border rounded p-2 space-y-2">

            <h3 className="text-xs font-semibold">GST Details</h3>

            <div className="grid grid-cols-12 gap-2">

              <div className="col-span-3">
                <label className="text-xs">GST Registered</label>
                <select name="is_gst" value={customer.is_gst} onChange={handleChange} className="input">
                  <option value="0">No</option>
                  <option value="1">Yes</option>
                </select>
              </div>

              <div className="col-span-4">
                <label className="text-xs">
                  GSTIN ||
                  <a
                    href="https://services.gst.gov.in/services/quicklinks/searchtxp"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-600 underline ml-1"
                  >
                    Get GSTIN Details
                  </a>
                </label>                
                <input
                  name="cust_gst"
                  value={customer.cust_gst}
                  onChange={handleChange}
                  disabled={customer.is_gst !== "1"}
                  className="input"
                />
              </div>

              <div className="col-span-2">
                <label className="text-xs">Pincode</label>

                <AsyncSelect
                  cacheOptions
                  defaultOptions
                  loadOptions={loadPincode}
                  placeholder="Search"
                  value={
                    customer.cust_pin
                      ? { value: customer.cust_pin, label: customer.cust_pin }
                      : null
                  }
                  onChange={handlePincodeSelect}
                  className="text-xs"
                  classNamePrefix="react-select"
                />
              </div>

              <div className="col-span-2">
                <label className="text-xs">City</label>
                <input value={customer.city} readOnly className="input bg-gray-50" />
              </div>

              <div className="col-span-1">
                <label className="text-xs">State</label>
                <input value={customer.cust_state_code} readOnly className="input bg-gray-50" />
              </div>

            </div>

          </div>

          {/* ADDRESS */}

          <div className="border rounded p-2 space-y-2">

            <h3 className="text-xs font-semibold">Address</h3>

            <input
              name="address1"
              value={customer.address1}
              onChange={handleChange}
              className="input"
            />

          </div>

          {/* ADDITIONAL CHARGES */}

          <div className="border rounded p-2 space-y-2">

            <h3 className="text-xs font-semibold">Additional Charges</h3>

            <div className="grid grid-cols-10 gap-2">
              {/* 
              <div>
                <label className="text-xs">Discount %</label>
                <input
                  name="discount_rate"
                  value={customer.discount_rate}
                  onChange={handleChange}
                  className="input text-right"
                />
              </div> */}

              <div>
                <label className="text-xs ">Fuel %</label>
                <input
                  name="fuel_rate"
                  value={customer.fuel_rate}
                  onChange={handleChange}
                  className="input text-right "
                />
              </div>

            </div>

          </div>

          {/* BUTTONS */}

          <div className="flex gap-2">

            {!editingId && (
              <button className="bg-blue-600 text-white px-4 py-1 rounded text-xs">
                Add
              </button>
            )}

            {editingId && (
              <button className="bg-green-600 text-white px-4 py-1 rounded text-xs">
                Update
              </button>
            )}

            <button
              type="button"
              onClick={resetForm}
              className="bg-gray-500 text-white px-4 py-1 rounded text-xs"
            >
              Cancel
            </button>

          </div>

        </form>

        <style>{`
        .input{
          border:1px solid #d1d5db;
          border-radius:4px;
          padding:4px 8px;
          width:100%;
          font-size:12px;
          height:32px;
        }
        `}</style>

      </div>
    </div>
  );
}
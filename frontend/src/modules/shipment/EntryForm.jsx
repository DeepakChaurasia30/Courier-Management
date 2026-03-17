import { useEffect, useRef, useState ,useContext} from "react";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import toast, { Toaster } from "react-hot-toast";
import { fetchCustomers } from "../utilities/customer";
import Spinner from "../../components/ui/Spinner"



import { getAllCust } from "../../api/custApi";
import {
  addAwb,
  deleteAwb,
  getAwbStatus,
  getSigAwb,
  updateAwb,
  tableFill,
} from "../../api/awbApi";
import { getMatchPin, getPinDet } from "../../api/destApi";
import { getSummaryAWB } from "../utilities/getSummaryAWB";
import { GlobalContext } from "../../context/GlobalContext";
const EntryForm = () => {

    const {clientID:cID}  = useContext(GlobalContext)
  
  const keyPressListen = useRef([]);

  const [customer, setCustomer] = useState([]);
  const [loading, setLoading] = useState(false);
  const [awbStatus, setAwbStatus] = useState("NEW");
  const [awbDisabled, setAwbDisabled] = useState(false);
  const [gstStatus, setgstStatus] = useState(true);
  const [entries, setEntries] = useState([]);
  const [confirmType, setConfirmType] = useState(null);
  const [customerSummary, setCustomerSummary] = useState({ count: 0, total: 0 });

  const handleTable = async () => {
    setLoading(true);

    try {
      const res = await tableFill();

      const formatted = res.data.map((item) => ({
        date: item.awbDate,
        awb: item.awbNo,
        destPin: item.pinCode,
        serviceType: item.srvType,
        weight: item.weight,
        charges: item.charge,
      }));

      setEntries(formatted);
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Table Load failed";

      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const defaultForm = {
    awbid: "",
    customer: "",
    awb: "",
    date: new Date().toLocaleDateString("en-CA"),

    courier: "SMCS",

    destCode: "",
    destPin: "",
    destName: "",

    doxType: "DX",
    serviceType: "SF",

    weight: "0.1",
    volWeight: "",

    length: "",
    breadth: "",
    height: "",
    dimension: "",

    charges: "",
    piece: 1,

    remark: "",
    invoiceNo: ""
  };

  //volume weight calco
  const calcVolWeight = (l, b, h) => {
    if (!l || !b || !h) return "";
    return ((Number(l) * Number(b) * Number(h)) / 5000).toFixed(2);
  };

  const [form, setForm] = useState(defaultForm);

  const inputStyle =
    "w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500";

  /* ---------------- FETCH CUSTOMERS ---------------- */

  useEffect(() => {
    const loadCust = async () => {
      try {
        setLoading(true);
        const formatted = await fetchCustomers(cID); //will fix hardcore value

        if (formatted === null) {
          throw new Error("Data is null");
        }
        setCustomer(formatted);
      }
      catch (err) {
        const msg =
          err?.response?.data?.message ||
          err?.response?.data ||
          err.message ||
          "Customer Load failed";
        toast.error(msg);
      } finally {
        setLoading(false);
      }
    };

    loadCust();
    handleTable();
  }, []);

  /* ---------------- COMMON ---------------- */

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => {
      let updated = { ...prev, [name]: value };

      if (name === "doxType") {
        if (value === "DX") {
          updated.weight = "0.1";
          updated.length = "";
          updated.breadth = "";
          updated.height = "";
          updated.dimension = "";
          updated.volWeight = "";
        }
      }

      return updated;
    });
  };

  // Handle Summary 

  const handleSummary = async (id) => {

    setLoading(true);

    try {

      const res = await getSummaryAWB(id);
      setCustomerSummary({ count: res.count, total: res.totalCharge })
      setLoading(false);

    } catch (err) {

      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Customer Load failed";
      toast.error(msg);
      setLoading(false);

    }
  }

  const handleKeyDown = (e, index) => {
    if (e.key !== "Enter") return;

    // e.preventDefault();

    // If Charges field
    if (index === 8) {
      if (awbStatus === "NEW") {
        keyPressListen.current[9]?.focus(); // Add
      } else {
        keyPressListen.current[10]?.focus(); // Update
      }
      return;
    }

    let nextIndex = index + 1;

    // // Skip disabled elements
    // while (
    //   keyPressListen.current[nextIndex] &&
    //   keyPressListen.current[nextIndex].disabled
    // ) {
    //   nextIndex++;
    // }

    keyPressListen.current[nextIndex]?.focus();
    console.log(nextIndex);

  };

  const formClean = () => {
    setForm(defaultForm);
    setAwbStatus("NEW");
    setAwbDisabled(false);
  };

  useEffect(() => {
    keyPressListen.current[0]?.focus();
  }, []);

  const isObjectComplete = (obj) =>
    Object.values(obj).every((v) => v !== "" && v !== null);


  // const handleTypeChange = (e) => {
  //   const value = e.target.value;

  //   setForm((prev) => ({
  //     ...prev,
  //     doxType: value,
  //     weight: value === "DX" ? 0.1 : "",
  //   }));
  // };

  /* ---------------- AWB CHECK ---------------- */

  const handleAwbBlur = async (e) => {
    const awbValue = e.target.value;
    if (!awbValue) return;

    try {
      setLoading(true);
      const statusRes = await getAwbStatus(awbValue);
      const status = statusRes.data;
      console.log(status);

      setAwbStatus(status);

      if (status === "UPDATE" || status === "NOT_UPDATABLE") {
        await loadAwbDetails(awbValue);
        setAwbDisabled(true);
      } else {
        setAwbDisabled(false);
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Get Status failed";

      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const loadAwbDetails = async (awbNo) => {
    const res = await getSigAwb(awbNo);
    const data = res.data;
    console.log(res);

    const pinDet = await getPinDet(data.pinCode);


    let l = "", b = "", h = "";

    if (data.dimension) {
      const parts = data.dimension.split("*");
      l = parts[0] || "";
      b = parts[1] || "";
      h = parts[2] || "";
    }

    setForm({
      awbid: data.id || "",
      customer: data.customerId || "",
      awb: awbNo || "",
      date: data.awbDate || "",
      courier: data.courierName || "SMCS",

      destCode: data.destid || "",
      destPin: data.pinCode || "",
      destName: pinDet.data.centerName || "",

      doxType: data.ptype || "DX",
      serviceType: data.srvType || "SF",

      weight: data.weight || "",
      charges: data.charge || "",

      invoiceNo: data.invoiceId || "",
      piece: data.noPcs || 1,

      length: l,
      breadth: b,
      height: h,

      dimension: data.dimension || "",
      volWeight: data.volWeight || "",

      remark: data.remark || ""
    });



  };

  /* ---------------- ADD ---------------- */

  const saveEntry = async () => {
    const payload = {
      customerId: form.customer,
      awbNo: form.awb,
      awbDate: form.date,
      courierName: form.courier,

      destid: form.destCode,
      pinCode: form.destPin,

      srvType: form.serviceType,
      ptype: form.doxType,

      weight: form.weight,
      volWeight: form.volWeight || null,

      dimension: form.dimension || null,

      charge: form.charges,

      noPcs: form.piece,

      remark: form.remark || null,

      clientId: cID
    };

    if (!isObjectComplete(payload)) {
      toast.error("Fill all required fields");
      return;
    }

    try {
      setLoading(true);
      await addAwb(payload);

      toast.success(`AWB ${form.awb} Added`);

      setEntries((prev) => [
        {
          date: form.date,
          awb: form.awb,
          destPin: form.destPin,
          serviceType: form.serviceType,
          weight: form.weight,
          charges: form.charges,
        },
        ...prev
      ].slice(0, 50));   // keep only latest 50

      setForm((prev) => ({
        ...prev,
        awb: Number(prev.awb) ? Number(prev.awb) + 1 : "",
      }));
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Add failed";

      toast.error(msg);
    } finally {
      setLoading(false);
      keyPressListen.current[0].focus();
    }
  };

  /* ---------------- UPDATE ---------------- */

  const confirmUpdate = async () => {
    try {
      setLoading(true);

      const payload = {
        customerId: form.customer,
        awbNo: form.awb,
        awbDate: form.date,
        courierName: form.courier,

        destid: form.destCode,
        pinCode: form.destPin,

        srvType: form.serviceType,
        ptype: form.doxType,

        weight: form.weight,
        volWeight: form.volWeight || null,

        dimension: form.dimension || null,

        charge: form.charges,

        noPcs: form.piece,

        remark: form.remark || null,

        clientId: cID
      };

      await updateAwb(form.awbid, payload);

      toast.success("Updated Successfully");

      setEntries((prev) =>
        prev.map((item) =>
          item.awbid === form.awbid ? form : item
        )
      );

      formClean();
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Updatee failed";

      toast.error(msg);
    } finally {
      setLoading(false);
      setConfirmType(null);
    }
  };

  /* ---------------- DELETE ---------------- */

  const confirmDelete = async () => {
    try {
      setLoading(true);
      await deleteAwb(form.awbid);

      toast.success("Deleted Successfully");

      setEntries((prev) =>
        prev.filter((item) => item.awbid !== form.awbid)
      );

      formClean();
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Delete failed";

      toast.error(msg);
    } finally {
      setLoading(false);
      setConfirmType(null);
    }
  };

  /* ---------------- PIN SEARCH ---------------- */

  const loadPincodeOptions = async (inputValue) => {

    if (!inputValue) return [];

    try {

      const res = await getMatchPin(inputValue);

      return res.data.map((item) => ({
        value: item.destId,
        label: `${item.destName.toUpperCase()} - ${item.pincode}`,
      }));

    } catch (err) {

      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Pincode Load failed";

      toast.error(msg);

      return [];

    }
  };

  /* ---------------- MODAL ---------------- */

  const ConfirmModal = () => {
    if (!confirmType) return null;

    return (
      <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded-xl shadow-lg w-96">
          <h3 className="text-lg font-semibold mb-4">
            Confirm {confirmType}
          </h3>
          <div className="flex justify-end gap-3">
            <button
              onClick={() => (setConfirmType(null), formClean())}
              className="px-4 py-2 bg-gray-500 text-white rounded-lg"
            >
              Cancel
            </button>
            <button
              onClick={
                confirmType === "UPDATE"
                  ? confirmUpdate
                  : confirmDelete
              }
              className={`px-4 py-2 text-white rounded-lg ${confirmType === "DELETE"
                ? "bg-red-600"
                : "bg-blue-600"
                }`}
            >
              Confirm
            </button>
          </div>
        </div>
      </div>
    );
  };

  /* ---------------- UI ---------------- */

  return (
    <div className="relative">
      <Toaster position="top-right" />

      {loading && <Spinner />}

      <ConfirmModal />

      <div className="max-w-5xl mx-auto p-6 bg-white shadow-lg rounded-xl">
        <div className="flex justify-between mb-4">
          <h2 className="text-xl font-semibold">
            Enterprise Entry Form
          </h2>
          {awbStatus === "NOT_UPDATABLE" ? (<div className="text-xl font-semibold text-red-700">{form.invoiceNo}</div>) : null}
          <div className="bg-gray-100 px-4 py-2 text-sm rounded">
            <div className="text-red-500">Total Entries: {customerSummary.count}</div>
            <div className="text-red-500">Total Amount: ₹{customerSummary.total}</div>
          </div>
        </div>


   <div className="grid grid-cols-4 gap-4 text-sm">

          { /* COURIER */}

          <div>
            <label className="block mb-1">Courier</label>
            <select
              name="courier"
              value={form.courier}
              onChange={handleChange}
              className={inputStyle}
            >
              <option value="SMCS">SMCS</option>
              <option value="DTDC">DTDC</option>
              <option value="BD">BD</option>
              <option value="TRAC">TRAC</option>
              <option value="SKY">SKY</option>
              <option value="OLS">OLS</option>
              <option value="OTHER">OTHER</option>
            </select>
          </div>



          {/* AWB */}
          <div>
            <label className="block mb-1">AWB</label>
            <input
              ref={(el) => (keyPressListen.current[0] = el)}
              onKeyDown={(e) => handleKeyDown(e, 0)}
              type="text"
              name="awb"
              value={form.awb}
              disabled={awbDisabled}
              onBlur={handleAwbBlur}
              onChange={handleChange}
              className={`${inputStyle} ${awbDisabled ? "bg-gray-200" : ""} uppercase`}
            />
          </div>

          {/* Customer */}
          <div>

            <div className="flex items-center gap-2">
              <label className="block">Customer || </label>
              <label className="text-sm text-red-400 font-medium">
                Type : {gstStatus ? "GST Customer" : "CASH Customer"}
              </label>
            </div>

            <Select
              menuPortalTarget={document.body}
              styles={{ menuPortal: (base) => ({ ...base, zIndex: 9999 }) }}
              ref={(el) => (keyPressListen.current[1] = el)}
              onKeyDown={(e) => handleKeyDown(e, 1)}
              options={customer}
              value={
                customer.find((c) => c.value === form.customer) || null
              }
              onChange={(selected) => {
                setForm((prev) => ({
                  ...prev,
                  customer: selected?.value || "",
                }))
                setgstStatus(selected.isGst);
                handleSummary(selected?.value)
              }
              }
            />
          </div>

          {/* Date */}
          <div>
            <label className="block mb-1">Date</label>
            <input
              ref={(el) => (keyPressListen.current[2] = el)}
              onKeyDown={(e) => handleKeyDown(e, 2)}
              type="date"
              name="date"
              value={form.date}
              onChange={handleChange}
              className={inputStyle}
            />
          </div>

          {/* Destination */}
          <div>
            <label className="block mb-1">Destination</label>
            <AsyncSelect
              menuPortalTarget={document.body} styles={{ menuPortal: (base) => ({ ...base, zIndex: 9999 }) }}
              ref={(el) => (keyPressListen.current[3] = el)}
              onKeyDown={(e) => handleKeyDown(e, 3)}
              cacheOptions
              defaultOptions
              loadOptions={loadPincodeOptions}
              value={
                form.destCode
                  ? {
                    value: form.destCode,
                    label: `${form.destName}-${form.destPin}`,
                  }
                  : null
              }
              onChange={(selected) => {
                if (!selected) return;
                const [destName, destPin] =
                  selected.label.split("-");
                setForm((prev) => ({
                  ...prev,
                  destCode: selected.value,
                  destName: destName.trim(),
                  destPin: destPin.trim(),
                }));
              }}
            />
          </div>

          {/* DOX */}
          <div>
            <label className="block mb-1">Dox Type</label>
            <select
              ref={(el) => (keyPressListen.current[4] = el)}
              onKeyDown={(e) => handleKeyDown(e, 4)}
              name="doxType"
              value={form.doxType}
              onChange={handleChange} // custom to handle user
              className={inputStyle}
            >
              <option value="DX">DOX</option>
              <option value="ND">NDOX</option>
            </select>
          </div>

          {/* Service */}
          <div>
            <label className="block mb-1">Service</label>
            <select
              ref={(el) => (keyPressListen.current[5] = el)}
              onKeyDown={(e) => handleKeyDown(e, 5)}
              name="serviceType"
              value={form.serviceType}
              onChange={handleChange}
              className={inputStyle}
            >
              <option value="FT">FT</option>
              <option value="SF">SF</option>
              <option value="AR">AR</option>
            </select>
          </div>

          {/* Weight */}
          <div>
            <label className="block mb-1">Weight</label>
            <input
              ref={(el) => (keyPressListen.current[6] = el)}
              onKeyDown={(e) => handleKeyDown(e, 6)}
              type="number"
              name="weight"
              value={form.weight}
              onChange={handleChange}
              readOnly={form.doxType === "DX"}
              className={`${inputStyle} ${form.doxType === "DX" ? "bg-gray-200" : ""}`}
            />
          </div>

          {/* No. of Pcs */}
          <div>
            <label className="block mb-1">Pieces</label>
            <input
              ref={(el) => (keyPressListen.current[7] = el)}
              onKeyDown={(e) => handleKeyDown(e, 7)}
              type="number"
              name="piece"
              value={form.piece}
              onChange={handleChange}
              className={inputStyle}
            />
          </div>



          {/* DIMENSION */}

          <div>
            <label className="block mb-1">Dimension (L × B × H)</label>

            <div className="grid grid-cols-3 gap-2">

              <input
                type="number"
                placeholder="L"
                disabled={form.doxType === "DX"}
                value={form.length}
                onChange={(e) => {
                  const l = e.target.value;
                  const b = form.breadth;
                  const h = form.height;

                  setForm(prev => ({
                    ...prev,
                    length: l,
                    dimension: l && b && h ? `${l}*${b}*${h}` : "",
                    volWeight: calcVolWeight(l, b, h)
                  }));
                }}
                className={`${inputStyle} ${form.doxType === "DX" ? "bg-gray-200" : ""}`}
              />

              <input
                type="number"
                placeholder="B"
                disabled={form.doxType === "DX"}
                value={form.breadth}
                onChange={(e) => {
                  const b = e.target.value;
                  const l = form.length;
                  const h = form.height;

                  setForm(prev => ({
                    ...prev,
                    breadth: b,
                    dimension: l && b && h ? `${l}*${b}*${h}` : "",
                    volWeight: calcVolWeight(l, b, h)
                  }));
                }}
                className={`${inputStyle} ${form.doxType === "DX" ? "bg-gray-200" : ""}`}
              />

              <input
                type="number"
                placeholder="H"
                disabled={form.doxType === "DX"}
                value={form.height}
                onChange={(e) => {
                  const h = e.target.value;
                  const l = form.length;
                  const b = form.breadth;

                  setForm(prev => ({
                    ...prev,
                    height: h,
                    dimension: l && b && h ? `${l}*${b}*${h}` : "",
                    volWeight: calcVolWeight(l, b, h)
                  }));
                }}
                className={`${inputStyle} ${form.doxType === "DX" ? "bg-gray-200" : ""}`}
              />

            </div>
          </div>
      

        {/* VOL WEIGHT */}

        <div>
          <label className="block mb-1">Vol Weight</label>
          <input
            type="number"
            name="volWeight"
            value={form.volWeight}
            disabled={form.doxType === "DX"}
            onChange={handleChange}
            className={`${inputStyle} ${form.doxType === "DX" ? "bg-gray-200" : ""}`}
          />
        </div>

        {/* Charges */}
        <div>
          <label className="block mb-1">Charges</label>
          <input
            ref={(el) => (keyPressListen.current[8] = el)}
            onKeyDown={(e) => handleKeyDown(e, 8)}
            type="number"
            name="charges"
            value={form.charges}
            onChange={handleChange}
            className={inputStyle}
          />
        </div>

        {/* REMARK */}

        <div className="col-span-3">
          <label className="block mb-1">Remark</label>
          <input
            name="remark"
            value={form.remark}
            onChange={handleChange}
            className={inputStyle}
          />
        </div>

        </div>
 


      {/* Buttons */}
      <div className="flex justify-end gap-4 mt-6">
        {awbStatus === "NEW" ? (
          <button
            ref={(el) => (keyPressListen.current[9] = el)}
            onClick={saveEntry}
            className="px-5 py-2 bg-green-600 text-white rounded-lg"
          >
            Add
          </button>
        ) : awbStatus !== "NOT_UPDATABLE" ? (
          <>
            <button
              ref={(el) => (keyPressListen.current[10] = el)}
              onClick={() => setConfirmType("UPDATE")}
              className="px-5 py-2 bg-blue-600 text-white rounded-lg"
            >
              Update
            </button>

            <button
              onClick={() => setConfirmType("DELETE")}
              className="px-5 py-2 bg-red-600 text-white rounded-lg"
            >
              Delete
            </button>
          </>
        ) : null}


        <button
          onClick={formClean}
          className="px-5 py-2 bg-gray-600 text-white rounded-lg"
        >
          Cancel
        </button>
      </div>

      {/* TABLE */}
      <div className="mt-8">

        <div className="border border-gray-200 rounded-lg overflow-hidden shadow-sm">

          {/* Scroll Container */}
          <div className="max-h-64 overflow-y-auto">

            <table className="w-full text-xs">

              <thead className="bg-gray-50 sticky top-0 z-10 border-b border-gray-200">
                <tr className="text-gray-600 uppercase tracking-wide text-[11px]">
                  <th className="px-3 py-2 text-left">Date</th>
                  <th className="px-3 py-2 text-left">AWB</th>
                  <th className="px-3 py-2 text-left">Pincode</th>
                  <th className="px-3 py-2 text-left">Service</th>
                  <th className="px-3 py-2 text-right">Weight</th>
                  <th className="px-3 py-2 text-right">Charge</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-100">

                {entries.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="text-center py-6 text-gray-400">
                      No entries yet
                    </td>
                  </tr>
                ) : (
                  entries.map((item, index) => (
                    <tr
                      key={index}
                      className="hover:bg-blue-50 transition-colors"
                    >

                      <td className="px-3 py-2 text-gray-700">
                        {item.date}
                      </td>

                      <td className="px-3 py-2 font-medium text-gray-800">
                        {item.awb}
                      </td>

                      <td className="px-3 py-2 text-gray-700">
                        {item.destPin}
                      </td>

                      <td className="px-3 py-2">
                        <span className="px-2 py-0.5 rounded bg-gray-100 text-gray-700 text-[11px]">
                          {item.serviceType}
                        </span>
                      </td>

                      <td className="px-3 py-2 text-right text-gray-700">
                        {item.weight}
                      </td>

                      <td className="px-3 py-2 text-right font-semibold text-gray-800">
                        ₹{item.charges}
                      </td>

                    </tr>
                  ))
                )}

              </tbody>

            </table>

          </div>

        </div>

      </div>
    </div>
      </div>
  );
};

export default EntryForm;
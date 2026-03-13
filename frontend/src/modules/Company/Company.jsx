import React, { useState, useEffect } from "react";
import AsyncSelect from "react-select/async";
import { getCompDet, updateCompDet } from "../../api/compApi";
import { getMatchPin, getPinDet } from "../../api/destApi";
import { toast, ToastContainer } from "react-toastify";
import { FiImage } from "react-icons/fi";
import "react-toastify/dist/ReactToastify.css";
import Spinner from "../../components/ui/Spinner";

export default function CompanyForm() {

  const companyId = 1;

  const [isEdit, setIsEdit] = useState(false);

  const [form, setForm] = useState({
    name: "",
    address: "",
    pin: "",
    dest: "",
    gstin: "",
    pan: "",
    phone: "",
    email: "",
    tag_line: "",
    comp_statecode: "",
    bankName: "",
    accountNo: "",
    IFCCode: "",
    saccode: "",
    uamno: "",
    condition1: "",
    condition2: "",
    condition3: "",
    condition4: "",
    condition5: ""
  });

  const [logo, setLogo] = useState(null);
  const [signature, setSignature] = useState(null);
  const [upi, setUpi] = useState(null);

  const [logoPreview, setLogoPreview] = useState("");
  const [signPreview, setSignPreview] = useState("");
  const [upiPreview, setUpiPreview] = useState("");
  const [loading,setloading] = useState(false)

  useEffect(() => { loadCompany(); }, []);

  // ===== LOAD COMPANY =====
  const loadCompany = async () => {

    try {

      const { data } = await getCompDet(companyId);

      setForm({
        name: data.clientName || "",
        address: data.clientAdd || "",
        pin: data.clientPin || "",
        dest: "",
        gstin: data.clientGstin || "",
        pan: data.compPan || "",
        phone: data.contNo || "",
        email: data.contMail || "",
        tag_line: data.tagLine || "",
        comp_statecode: data.stateId || "",
        bankName: data.bName || "",
        accountNo: data.bAcc || "",
        IFCCode: data.bIfsc || "",
        saccode: data.sacCode || "",
        uamno: data.compMsme || "",
        condition1: data.condition1 || "",
        condition2: data.condition2 || "",
        condition3: data.condition3 || "",
        condition4: data.condition4 || "",
        condition5: data.condition5 || ""
      });

      handlePin(data.clientPin);

    } catch {
      toast.error("Failed to load company");
    }
  };

  // ===== GENERIC INPUT HANDLER =====
  const handleChange = e =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));


  // ===== PIN SEARCH =====
  const loadPin = async (input) => {

    if (!input) return [];
    setloading(true)

    try {

      const res = await getMatchPin(input);
      setloading(false)

      return res.data.map(p => ({
        label: p.pincode,
        value: p.pincode
      }));

      

    } catch {

      toast.error("Failed to load pincode");
      setloading(false)

      return [];
    }
  };

  const handlePin = async (pin) => {

    if (!pin) return;
    setloading(true)

    try {

      const { data } = await getPinDet(pin);

      setForm(prev => ({
        ...prev,
        pin,
        dest: data.centerName,
        comp_statecode: data.stateCode
      }));
      setloading(false)

    } catch {
       setloading(false)
      toast.error("Invalid pincode");

    }
  };

  // ===== IMAGE CHANGE =====
  const handleImage = (setter, setPreview, file) => {

    if (!file) return;

    setter(file);

    setPreview(URL.createObjectURL(file));
  };

  // ===== PAYLOAD MAPPER =====
  const buildPayload = () => ({
    clientName: form.name,
    clientAdd: form.address,
    clientPin: form.pin,
    clientGstin: form.gstin,
    compPan: form.pan,
    contNo: form.phone,
    contMail: form.email,
    tagLine: form.tag_line,
    stateId: form.comp_statecode,
    bName: form.bankName,
    bAcc: form.accountNo,
    bIfsc: form.IFCCode,
    sacCode: form.saccode,
    compMsme: form.uamno,
    condition1: form.condition1,
    condition2: form.condition2,
    condition3: form.condition3,
    condition4: form.condition4,
    condition5: form.condition5,
    clientIsgst: true,
    taxRate: 18
  });

  // ===== UPDATE =====
  const handleUpdate = async () => {

    setloading(true)

    try {

      const payload = buildPayload();
      console.log(payload);

      const formData = new FormData();

      // JSON DTO
      formData.append(
        "data",
        new Blob([JSON.stringify(payload)], { type: "application/json" })
      );
      for (const [key, value] of formData.entries()) {
        console.log(key, value);
      }


      // Images
      if (logo) formData.append("logo", logo);
      if (upi) formData.append("upi", upi);
      if (signature) formData.append("signature", signature);

      await updateCompDet(companyId, formData);

      toast.success("Company updated");

      setIsEdit(false);
      setloading(false)

    } catch (err) {
       const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err.message ||
        "Update failed";
        setloading(false)

    }
  };

  const handleCancel = () => {

    setIsEdit(false);
    loadCompany();

  };

  // ===== IMAGE PREVIEW BLOCK =====
  const ImageBox = ({ title, preview, setter, setPreview }) => (

    <div className="flex flex-col items-center gap-2 hidden">

      <div className="w-28 h-28 border rounded flex items-center justify-center bg-gray-50">

        {preview
          ? <img src={preview} alt="" className="object-contain w-full h-full " />
          : <FiImage size={28} className="text-gray-400" />}

      </div>

      <label className="text-sm">{title}</label>

      <input
        type="file"
        disabled={!isEdit}
        onChange={(e) => handleImage(setter, setPreview, e.target.files[0])}
      />

    </div>
  );

  return (

    <div className="max-w-6xl mx-auto bg-white shadow rounded p-6">

      {loading && <Spinner/>}

      <ToastContainer />

      <h2 className="text-lg font-semibold mb-4">Company Details</h2>

      {/* BASIC */}
      <div className="grid grid-cols-2 gap-4 mb-4">

        <input name="name" placeholder="Company Name" className="input uppercase "
          value={form.name} onChange={handleChange} disabled={!isEdit} />

        <input name="tag_line" placeholder="Tag Line" className="input uppercase "
          value={form.tag_line} onChange={handleChange} disabled={!isEdit} />

      </div>

      {/* ADDRESS */}
      <input name="address" placeholder="Address" className="input w-full mb-4 uppercase "
        value={form.address} onChange={handleChange} disabled={!isEdit} />

      {/* PIN */}
      <div className="grid grid-cols-3 gap-4 mb-6">

        <AsyncSelect
          cacheOptions
          loadOptions={loadPin}
          onChange={(o) => handlePin(o.value)}
          value={form.pin ? { label: form.pin, value: form.pin } : null}
          isDisabled={!isEdit}
          placeholder="Search Pincode"
        />

        <input className="input" value={form.dest} readOnly placeholder="Destination" />
        <input className="input" value={form.comp_statecode} readOnly placeholder="State Code" />

      </div>

      {/* TAX */}
      <div className="grid grid-cols-4 gap-4 mb-6">

        <input name="gstin" placeholder="GSTIN" className="input uppercase "
          value={form.gstin} onChange={handleChange} disabled={!isEdit} />

        <input name="saccode" placeholder="SAC Code" className="input"
          value={form.saccode} onChange={handleChange} disabled={!isEdit} />

        <input name="phone" placeholder="Phone" className="input"
          value={form.phone} onChange={handleChange} disabled={!isEdit} />

        <input name="email" placeholder="Email" className="input"
          value={form.email} onChange={handleChange} disabled={!isEdit} />

      </div>

      {/* BANK */}
      <h3 className="font-semibold mb-2">Bank Details</h3>

      <div className="grid grid-cols-3 gap-4 mb-6">

        <input name="bankName" placeholder="Bank Name" className="input uppercase "
          value={form.bankName} onChange={handleChange} disabled={!isEdit} />

        <input name="accountNo" placeholder="Account Number" className="input"
          value={form.accountNo} onChange={handleChange} disabled={!isEdit} />

        <input name="IFCCode" placeholder="IFSC Code" className="input uppercase "
          value={form.IFCCode} onChange={handleChange} disabled={!isEdit} />

        <input name="pan" placeholder="PAN" className="input uppercase "
          value={form.pan} onChange={handleChange} disabled={!isEdit} />

        <input name="uamno" placeholder="MSME" className="input uppercase "
          value={form.uamno} onChange={handleChange} disabled={!isEdit} />

      </div>

      {/* IMAGE SECTION */}
      <h3 className="font-semibold mb-3">Company Assets</h3>

      <div className="grid grid-cols-3 gap-6 mb-6">

        <ImageBox title="Company Logo" preview={logoPreview} setter={setLogo} setPreview={setLogoPreview} />
        <ImageBox title="UPI QR" preview={upiPreview} setter={setUpi} setPreview={setUpiPreview} />
        <ImageBox title="Signature" preview={signPreview} setter={setSignature} setPreview={setSignPreview} />

      </div>

      {/* CONDITIONS */}
      <h3 className="font-semibold mb-2">Invoice Conditions</h3>

      {[1, 2, 3, 4, 5].map(i => (
        <input
          key={i}
          name={`condition${i}`}
          placeholder={`Condition ${i}`}
          className="input w-full mb-2"
          value={form[`condition${i}`]}
          onChange={handleChange}
          disabled={!isEdit}
        />
      ))}

      {/* BUTTONS */}
      {!isEdit ? (

        <button
          onClick={() => setIsEdit(true)}
          className="bg-yellow-500 text-white px-6 py-2 rounded">
          Update
        </button>

      ) : (

        <div className="flex gap-3">

          <button
            onClick={handleUpdate}
            className="bg-blue-600 text-white px-6 py-2 rounded">
            Save
          </button>

          <button
            onClick={handleCancel}
            className="bg-gray-500 text-white px-6 py-2 rounded">
            Cancel
          </button>

        </div>

      )}

    </div>

  );
}
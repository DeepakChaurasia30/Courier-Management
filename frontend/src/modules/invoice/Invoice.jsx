import React, { useState, useEffect, useContext } from "react";
import { fetchCustomers } from "../utilities/customer";
import Select from "react-select";
import { cancelInv, genInv, getinvpdf } from "../../api/invoice";
import { getFinacialYear } from "../utilities/getFinacialYear";
import { getInvno } from "../../api/invoiceno";
import toast from "react-hot-toast";
import Spinner from "../../components/ui/Spinner";
import { getInvoiceTableData } from "../utilities/getInvoiceTableData";
import { GlobalContext } from "../../context/GlobalContext";

const Invoice = () => {

  const {clientID:cID}  = useContext(GlobalContext)

  // const cID =1;
  console.log(cID);
  

  const [activeTab, setActiveTab] = useState("create");
  const [customer, setCustomer] = useState([]);
  const [fy, setFy] = useState(getFanicalyr()[0]);
  const [gstStatus, setgstStatus] = useState(true);
  const [loading, setLoading] = useState(false);

  const [createForm, setCreateForm] = useState({
    invNo: "",
    custid: "",
    invDate: "",
    invDateFrom: "",
    invDateTo: "",
    discount: 0,
    clientid: cID
  });

  const [editInvoiceNo, setEditInvoiceNo] = useState({ inv: "", gstMidlabell: "INV" });

  const [invoiceList, setInvoiceList] = useState([]);

  function getFanicalyr(start) {
    return getFinacialYear(start);
  }

  /* ================= LOAD TABLE ================= */
  const loadInvoiceTable = async () => {
    try {
      const res = await getInvoiceTableData(cID);  //fix hardcode value
      console.log(res);

      setInvoiceList(res.reverse() || []);
    } catch {
      toast.error("Invoice table load failed");
    }
  };

  /* ================= GET INV NUMBER ================= */
  const getInv = async (fy, gstStatus) => {
    setLoading(true);
    try {
      const res = await getInvno(fy,cID, gstStatus);
      setCreateForm((prev) => ({
        ...prev,
        invNo: res.data,
      }));
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  }

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setCreateForm(prev => ({
      ...prev,
      [name]: type === "number" ? parseFloat(value) : value
    }));
  }

  /* ================= VALIDATION ================= */
  const validateCreate = () => {
    if (!createForm.invNo) { toast.error("Invoice number required"); return false; }
    if (!createForm.custid) { toast.error("Customer required"); return false; }
    if (!createForm.invDate) { toast.error("Invoice date required"); return false; }
    if (!createForm.invDateFrom || !createForm.invDateTo) { toast.error("Date range required"); return false; }
    if (createForm.discount == null || isNaN(createForm.discount)) { toast.error("Discount required"); return false; }
    return true;
  };

  const validateEdit = () => {
    if (!editInvoiceNo.inv) { toast.error("Enter invoice number"); return false; }
    return true;
  };

  /* ================= CREATE ================= */
  const handleCreate = async () => {

    if (!validateCreate()) return;

    let invNo;
    const padded = createForm.invNo.toString().padStart(4, "0");
    invNo = gstStatus ? `${fy}/INV/${padded}` : `${fy}/NG/${padded}`;

    const payload = {
      ...createForm,
      discount: Number(createForm.discount).toFixed(2),
      invNo
    };

    setLoading(true);

    try {

      await genInv(payload);

      toast.success("Invoice Created");

      await loadInvoiceTable();   // reload table

      setCreateForm({
        invNo: "",
        custid: "",
        invDate: "",
        invDateFrom: "",
        invDateTo: "",
        discount: 0,
        clientid: cID
      });

    } catch (e) {
      const msg = e?.response?.data?.message || "Invoice creation failed";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  /* ================= DELETE ================= */
  const confirmDelete = () => {
    toast((t) => (
      <div className="flex flex-col gap-2">
        <span>Cancel this invoice?</span>
        <div className="flex gap-2">
          <button
            className="bg-red-600 text-white px-3 py-1 rounded"
            onClick={() => { toast.dismiss(t.id); handleDelete(); }}
          >
            Yes
          </button>
          <button
            className="bg-gray-300 px-3 py-1 rounded"
            onClick={() => toast.dismiss(t.id)}
          >
            No
          </button>
        </div>
      </div>
    ));
  };

  const handleDelete = async () => {

    if (!validateEdit()) return;

    const padded = editInvoiceNo.inv.padStart(4, "0");
    const inv = `${fy}/${editInvoiceNo.gstMidlabell}/${padded}`;

    setLoading(true);

    try {

      await cancelInv(inv);

      toast.success(`Invoice :${inv} Deleted`);

      await loadInvoiceTable();   // reload table

      setEditInvoiceNo((prev) => ({ ...prev, inv: "" }));

    } catch (e) {
      const msg = e?.response?.data?.message || "Invoice Cancel Failed";
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  /* ================= VIEW ================= */
  const handleView = async () => {
  if (!validateEdit()) return;

  const padded = editInvoiceNo.inv.padStart(4, "0");
  const inv = `${fy}/${editInvoiceNo.gstMidlabell}/${padded}`;

  setLoading(true);

  try {
    // Fetch PDF as blob
    const response = await getinvpdf(inv, { responseType: "blob" });

    // Create a blob URL
    const file = new Blob([response.data], { type: "application/pdf" });
    const fileURL = URL.createObjectURL(file);

    // Open in new tab
    window.open(fileURL, "_blank");

    toast.success(`Invoice: ${inv} found`);
  } catch (e) {
    const msg = e?.response?.data?.message || "Invoice failed";
    toast.error(msg);
  } finally {
    setLoading(false);
  }
};

  /* ================= LOAD CUSTOMERS ================= */
  useEffect(() => {

    const loadCust = async () => {

      setLoading(true);

      try {

        const formatted = await fetchCustomers(cID);
        setCustomer(formatted || []);

      } catch {

        toast.error("Customer load failed");

      } finally {

        setLoading(false);
      }
    };

    loadCust();
    loadInvoiceTable();   // load table on page start

  }, []);

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center p-8">

      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-5xl relative">

        {loading && <Spinner />}

        {/* Tabs */}
        <div className="flex border-b mb-6">

          <button
            className={`px-4 py-2 ${activeTab === "create" ? "border-b-2 border-blue-600 text-blue-600" : "text-gray-600"}`}
            onClick={() => setActiveTab("create")}
          >
            Create
          </button>

          <button
            className={`px-4 py-2 ${activeTab === "edit" ? "border-b-2 border-blue-600 text-blue-600" : "text-gray-600"}`}
            onClick={() => setActiveTab("edit")}
          >
            Edit
          </button>

          <div className="ml-auto">
            <select value={fy} onChange={(e) => setFy(e.target.value)} className="border px-2 py-1 rounded">
              {getFanicalyr().map((year, i) => (
                <option key={i} value={year}>{year}</option>
              ))}
            </select>
          </div>

        </div>

        {/* CREATE TAB */}
        {activeTab === "create" && (
          <div className="grid grid-cols-2 gap-4 mb-6">

            <div>
              <label className="text-sm font-medium">Invoice No</label>
              <div className="flex gap-2">
                <span className="border px-3 py-2 whitespace-nowrap rounded">{fy}/</span>
                <input type="text" name="invNo" value={createForm.invNo} readOnly className="border px-3 py-2 rounded w-full" />
              </div>
            </div>

            <div>
              <label className="text-sm font-medium">Customer</label>

              <Select
                options={customer}
                value={customer.find((c) => c.value === createForm.custid) || null}
                onChange={(selected) => {
                  setCreateForm((prev) => ({ ...prev, custid: selected?.value || "" }));
                  setgstStatus(selected.isGst);
                  getInv(fy, selected.isGst);
                }}
                menuPortalTarget={document.body}
                styles={{
                  menuPortal: (base) => ({ ...base, zIndex: 9999 })
                }}
              />

              <label className="text-sm text-red-400 font-medium">
                {gstStatus ? "GST Customer" : "Non GST Customer"}
              </label>

            </div>

            <InputField label="Invoice Date" name="invDate" type="date" value={createForm.invDate} onChange={handleChange} />
            <InputField label="From Date" name="invDateFrom" type="date" value={createForm.invDateFrom} onChange={handleChange} />
            <InputField label="To Date" name="invDateTo" type="date" value={createForm.invDateTo} onChange={handleChange} />
            <InputField label="Discount %" name="discount" type="number" value={createForm.discount} onChange={handleChange} />

            <div className="col-span-2 flex gap-3">
              <button onClick={handleCreate} className="px-4 py-2 bg-blue-600 text-white rounded">
                Create
              </button>
              <button
                onClick={() => setCreateForm({ invNo: "", custid: "", invDate: "", invDateFrom: "", invDateTo: "", clientid: cID })}
                className="px-4 py-2 bg-gray-300 rounded"
              >
                Cancel
              </button>
            </div>

          </div>
        )}

        {/* EDIT TAB */}
        {activeTab === "edit" && (

          <div className="flex gap-3 mb-6">

            <span className="border px-3 py-2 whitespace-nowrap rounded">{fy}/</span>

            <select
              className="border px-3 py-2 whitespace-nowrap rounded"
              value={editInvoiceNo.gstMidlabell}
              onChange={(e) => setEditInvoiceNo((prev) => ({ ...prev, gstMidlabell: e.target.value }))}
            >
              <option value="INV">GST</option>
              <option value="NG">NGST</option>
            </select>

            <input
              type="text"
              value={editInvoiceNo.inv}
              onChange={(e) => setEditInvoiceNo((prev) => ({ ...prev, inv: e.target.value }))}
              className="border px-3 py-2 rounded"
              placeholder="0001"
              onKeyDown={(e) => { if (e.key === "Enter") handleView(); }}
            />

            <button onClick={handleView} className="bg-green-600 text-white px-4 py-2 rounded">
              View
            </button>

            <button onClick={confirmDelete} className="bg-red-600 text-white px-4 py-2 rounded">
              Cancel
            </button>

          </div>

        )}

        <InvoiceTable data={invoiceList} />

      </div>
    </div>
  );
};

const InputField = ({ label, name, value, onChange, type = "text" }) => (
  <div>
    <label className="text-sm font-medium">{label}</label>
    <input type={type} name={name} value={value} onChange={onChange} className="w-full border px-3 py-2 rounded" />
  </div>
);

const InvoiceTable = ({ data }) => {

  return (

    <div className="mt-8">

      <div className="border border-gray-200 rounded-lg overflow-hidden shadow-sm">

        {/* Scroll Container */}
        <div className="w-full max-h-105 overflow-auto">
          <table className="w-full text-xs">

            <thead className="bg-gray-50 sticky top-0 z-10 border-b border-gray-200">

              <tr className="text-gray-600 uppercase tracking-wide text-[11px]">

                <th className="px-3 py-2 text-left">Invoice</th>

                <th className="px-3 py-2 text-left">Customer</th>

                <th className="px-3 py-2 text-left">Invoice Date</th>

                <th className="px-3 py-2 text-left">From</th>

                <th className="px-3 py-2 text-left">To</th>

                <th className="px-3 py-2 text-center">Status</th>

              </tr>

            </thead>

            <tbody className="divide-y divide-gray-100">

              {data.length === 0 ? (

                <tr>
                  <td
                    colSpan="6"
                    className="text-center py-6 text-gray-400"
                  >
                    No invoices found
                  </td>
                </tr>

              ) : (

                data.map((inv, index) => (

                  <tr
                    key={index}
                    className={`transition-colors ${inv.isCancel
                      ? "bg-red-50 text-red-700 font-semibold"
                      : "hover:bg-blue-50"
                      }`}
                  >

                    <td className="px-3 py-2 font-medium text-gray-800">
                      {inv.invNo}
                    </td>

                    <td className="px-3 py-2 text-gray-700">
                      {inv.customerCustCode}
                    </td>

                    <td className="px-3 py-2 text-gray-700">
                      {inv.invDate}
                    </td>

                    <td className="px-3 py-2 text-gray-700">
                      {inv.invDateFrom}
                    </td>

                    <td className="px-3 py-2 text-gray-700">
                      {inv.invDateTo}
                    </td>

                    <td className="px-3 py-2 text-center">

                      {inv.isCancel ? (

                        <span className="px-2 py-0.5 rounded bg-red-100 text-red-700 text-[11px]">
                          CANCELLED
                        </span>

                      ) : (

                        <span className="px-2 py-0.5 rounded bg-green-100 text-green-700 text-[11px]">
                          ACTIVE
                        </span>

                      )}

                    </td>

                  </tr>

                ))

              )}

            </tbody>

          </table>

        </div>

      </div>

    </div>

  );

};



export default Invoice;
import React, { useState, useMemo, useEffect,useContext } from "react";
import {
  useReactTable,
  getCoreRowModel,
  getPaginationRowModel,
  flexRender
} from "@tanstack/react-table";

import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

import { fetchShipments } from "../../api/awbApi";
import { getpdfSlip } from "../../api/printslip";

import Spinner from "../../components/ui/Spinner"
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { GlobalContext } from "../../context/GlobalContext";

const ShipmentTable = () => {

    const {clientID:cID}  = useContext(GlobalContext)
  


  const today = new Date();

  const [data, setData] = useState([]);
  const [filterCustomer, setFilterCustomer] = useState("");
  const [filterDestination, setFilterDestination] = useState("");
  const [startDate, setStartDate] = useState(today);
  const [endDate, setEndDate] = useState(today);

  const [rowSelection, setRowSelection] = useState({});
  const [loading, setLoading] = useState(false);

  /* ---------------- PRINT SELECTED ---------------- */

  const printSelected = async () => {

    const selected = table.getSelectedRowModel().rows.map(r => r.original);

    if (!selected.length) {
      toast.error("Select rows first");
      return;
    }

    try {

      setLoading(true);

      const res = await getpdfSlip(selected);

      const blob = new Blob([res.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);

      window.open(url, "_blank");

      toast.success("Slip Generated");

    } catch (error) {

      console.log(error);

      const msg =
        error?.response?.data?.message ||
        error?.response?.data ||
        error.message ||
        "PDF Generation Failed";

      toast.error(msg);

    } finally {
      setLoading(false);
    }

  };

  /* ---------------- TABLE COLUMNS ---------------- */

  const columns = useMemo(() => [

    {
      id: "select",
      header: ({ table }) => (
        <input
          type="checkbox"
          checked={table.getIsAllPageRowsSelected()}
          onChange={table.getToggleAllPageRowsSelectedHandler()}
        />
      ),
      cell: ({ row }) => (
        <input
          type="checkbox"
          checked={row.getIsSelected()}
          onChange={row.getToggleSelectedHandler()}
        />
      )
    },

    {
      header: "SNo",
      cell: ({ row, table }) =>
        row.index +
        1 +
        table.getState().pagination.pageIndex *
        table.getState().pagination.pageSize
    },

    { header: "AWB No", accessorKey: "awbNo" },

    {
      header: "Date",
      accessorKey: "awbDate",
      cell: ({ row }) => {
        const d = new Date(row.original.awbDate);
        return d.toLocaleDateString("en-GB");
      }
    },

    { header: "Customer", accessorKey: "customerCustCode" },
    { header: "Destination", accessorKey: "centerDestName" },
    { header: "Pincode", accessorKey: "pinCode" },
    { header: "Service", accessorKey: "srvType" },
    { header: "PType", accessorKey: "ptype" },
    { header: "Weight", accessorKey: "weight" },
    { header: "Vol Weight", accessorKey: "volWeight" },
    { header: "Charge", accessorKey: "charge" },
    { header: "Courier", accessorKey: "courierName" },
    { header: "No PCS", accessorKey: "noPcs" },
    { header: "Remark", accessorKey: "remark" }

  ], []);

  /* ---------------- LOAD DATA ---------------- */

  const loadShipments = async () => {

    if (!startDate || !endDate) {
      toast.error("Select dates");
      return;
    }

    try {

      setLoading(true);

      const start = startDate.toISOString().split("T")[0];
      const end = endDate.toISOString().split("T")[0];

      const res = await fetchShipments(cID, start, end);

      setData(res.data);
      setRowSelection({});

      toast.success(res?.data?.message || "Shipments Loaded");

    } catch (error) {

      console.log(error);

      const msg =
        error?.response?.data?.message ||
        error?.response?.data ||
        error.message ||
        "Failed to load shipments";

      toast.error(msg);

    } finally {
      setLoading(false);
    }

  };

  /* ---------------- DEFAULT LOAD ---------------- */

  useEffect(() => {
    loadShipments();
  }, []);

  /* ---------------- FILTER DATA ---------------- */

  const filteredData = useMemo(() => {

    return data.filter((row) => {

      const custMatch =
        row.customerCustCode
          ?.toLowerCase()
          .includes(filterCustomer.toLowerCase());

      const destMatch =
        row.centerDestName
          ?.toLowerCase()
          .includes(filterDestination.toLowerCase());

      return custMatch && destMatch;

    });

  }, [data, filterCustomer, filterDestination]);

  /* ---------------- TABLE ---------------- */

  const table = useReactTable({

    data: filteredData,
    columns,

    state: { rowSelection },

    onRowSelectionChange: setRowSelection,

    enableRowSelection: true,

    initialState: {
      pagination: { pageSize: 25 }
    },

    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel()

  });

  const selectedCount = table.getSelectedRowModel().rows.length;

  /* ---------------- UI ---------------- */

  return (

    <div className="w-full h-full p-4">

      {loading && <Spinner />}

      <div className="bg-white shadow rounded-lg border flex flex-col h-full">

        {/* HEADER */}

        <div className="flex justify-between items-center px-5 py-4 border-b">

          <h2 className="text-lg font-semibold text-gray-700">
            Shipment List
          </h2>

          <div className="flex items-center gap-4">

            <span className="text-sm">
              Selected: {selectedCount}
            </span>

            <button
              disabled={!selectedCount || loading}
              onClick={printSelected}
              className="bg-indigo-600 disabled:bg-gray-400 hover:bg-indigo-700 text-white px-4 py-2 rounded text-sm"
            >
              Print Selected
            </button>

          </div>

        </div>

        {/* FILTERS */}

        <div className="flex flex-wrap justify-between items-center px-5 py-4 gap-3">

          <div className="flex gap-2">

            <input
              placeholder="Customer Code"
              value={filterCustomer}
              onChange={(e) => setFilterCustomer(e.target.value)}
              className="border px-3 py-2 rounded text-sm w-40"
            />

            <input
              placeholder="Destination"
              value={filterDestination}
              onChange={(e) => setFilterDestination(e.target.value)}
              className="border px-3 py-2 rounded text-sm w-40"
            />

          </div>

          <div className="flex items-center gap-2">

            <DatePicker
              selected={startDate}
              onChange={(date) => setStartDate(date)}
              dateFormat="dd/MM/yyyy"
              className="border px-3 py-2 rounded text-sm"
            />

            <DatePicker
              selected={endDate}
              onChange={(date) => setEndDate(date)}
              dateFormat="dd/MM/yyyy"
              className="border px-3 py-2 rounded text-sm"
            />

            <button
              onClick={loadShipments}
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white px-4 py-2 rounded text-sm"
            >
              Search
            </button>

          </div>

        </div>

        {/* TABLE */}

        <div className="flex-1 overflow-auto">

          <table className="w-full text-sm">

            <thead className="bg-gray-100 sticky top-0">

              {table.getHeaderGroups().map((headerGroup) => (

                <tr key={headerGroup.id}>

                  {headerGroup.headers.map((header) => (

                    <th
                      key={header.id}
                      className="px-3 py-2 border text-left"
                    >
                      {flexRender(
                        header.column.columnDef.header,
                        header.getContext()
                      )}
                    </th>

                  ))}

                </tr>

              ))}

            </thead>

            <tbody>

              {table.getRowModel().rows.map((row) => (

                <tr key={row.id} className="hover:bg-gray-50">

                  {row.getVisibleCells().map((cell) => (

                    <td key={cell.id} className="px-3 py-2 border">

                      {flexRender(
                        cell.column.columnDef.cell ??
                        cell.column.columnDef.accessorKey,
                        cell.getContext()
                      )}

                    </td>

                  ))}

                </tr>

              ))}

            </tbody>

          </table>

        </div>

        {/* PAGINATION */}

        <div className="flex justify-between items-center px-5 py-3 border-t">

          <span className="text-sm">
            Page {table.getState().pagination.pageIndex + 1} of {table.getPageCount()}
          </span>

          <div className="flex gap-2">

            <button
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
              className="border px-3 py-1 rounded"
            >
              Prev
            </button>

            <button
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
              className="border px-3 py-1 rounded"
            >
              Next
            </button>

          </div>

        </div>

      </div>

      <ToastContainer autoClose={2500} />

    </div>

  );

};

export default ShipmentTable;
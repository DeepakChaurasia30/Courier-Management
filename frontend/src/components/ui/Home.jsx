import React from "react";
import { useNavigate } from "react-router-dom";

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">

      {/* Page Title */}
      <h1 className="text-2xl font-semibold text-slate-700">
        Dashboard
      </h1>

      {/* Stats Cards */}
      <div className="grid grid-cols-4 gap-4">

        <div className="bg-white shadow rounded-lg p-4">
          <p className="text-sm text-gray-500">Today's Bookings</p>
          <h2 className="text-2xl font-bold text-slate-800">124</h2>
        </div>

        <div className="bg-white shadow rounded-lg p-4">
          <p className="text-sm text-gray-500">Invoices</p>
          <h2 className="text-2xl font-bold text-slate-800">38</h2>
        </div>

        <div className="bg-white shadow rounded-lg p-4">
          <p className="text-sm text-gray-500">Customers</p>
          <h2 className="text-2xl font-bold text-slate-800">420</h2>
        </div>

        <div className="bg-white shadow rounded-lg p-4">
          <p className="text-sm text-gray-500">Revenue</p>
          <h2 className="text-2xl font-bold text-slate-800">₹52,000</h2>
        </div>

      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-lg font-semibold text-slate-700 mb-3">
          Quick Actions
        </h2>

        <div className="grid grid-cols-3 gap-4">

          <button
            onClick={() => navigate("/reports")}
            className="bg-green-600 text-white p-4 rounded-lg shadow hover:bg-green-700"
          >
            🔍 Search Booking
          </button>

          <button
            onClick={() => navigate("/booking")}
            className="bg-blue-600 text-white p-4 rounded-lg shadow hover:bg-blue-700"
          >
            📦 New Booking
          </button>

          <button
            onClick={() => navigate("/customers")}
            className="bg-green-600 text-white p-4 rounded-lg shadow hover:bg-green-700"
          >
            👥 Add Customer
          </button>

          <button
            onClick={() => navigate("/invoice")}
            className="bg-purple-600 text-white p-4 rounded-lg shadow hover:bg-purple-700"
          >
            🧾 Generate Invoice
          </button>

        </div>
      </div>

      {/* Sales Graph */}
      <div className="bg-white shadow rounded-lg p-6">

        <h2 className="text-lg font-semibold text-slate-700 mb-4">
          Sales Overview
        </h2>

        <div className="h-64 flex items-center justify-center text-gray-400">
          Graph will be displayed here
        </div>

      </div>

    </div>
  );
};

export default Home;
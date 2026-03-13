import React from "react";

const Navbar = ({ currentClient }) => {
  return (
    <div className="h-14 bg-white border-b flex items-center justify-between px-6">

      {/* Current Client */}
      <div className="text-sm text-gray-600">
        Current Client :
        <span className="ml-2 font-semibold text-slate-800">
          {currentClient || "Select Client"}
        </span>
      </div>

      {/* Right Side */}
      <div className="flex items-center gap-4">

        <button className="bg-blue-600 text-white px-3 py-1 rounded text-sm">
          New Booking
        </button>

        <span className="text-sm text-gray-500">
          Admin
        </span>

      </div>
    </div>
  );
};

export default Navbar;
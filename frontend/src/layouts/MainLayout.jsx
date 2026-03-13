import React from "react";
import { Routes, Route } from "react-router-dom";

import Sidebar from "../components/navigation/Sidebar";
import Navbar from "../components/navigation/Navbar";

import EntryForm from "../modules/shipment/EntryForm";
import Invoice from "../modules/invoice/Invoice";
import Customer from "../modules/Customer/Cusotmer";
import Company from "../modules/Company/Company";
import Home from "../components/ui/Home";
const menuItems = [
  { name: "Home", path: "/", icon: "🏠", end: true },
  { name: "Booking", path: "/booking", icon: "📦" },
  { name: "Customers", path: "/customers", icon: "👥" },
  { name: "Invoice", path: "/invoice", icon: "🧾" },
  { name: "Company", path: "/company", icon: "🏢" },
  { name: "Reports", path: "/reports", icon: "📊" }
];

const MainLayout = () => {

  const currentClient = "ABC Logistics Pvt Ltd";

  return (
    <div className="flex">

      {/* Sidebar */}
      <Sidebar menu={menuItems} />

      {/* Right Section */}
      <div className="flex-1 flex flex-col min-h-screen bg-slate-100">

        {/* Top Navbar */}
        <Navbar currentClient={currentClient} />

        {/* Main Content */}
        <div className="p-6">

          <Routes>

            <Route path="/" element={<Home />} />

            <Route path="/booking" element={<EntryForm />} />

            <Route path="/customers" element={<Customer />} />

            <Route path="/invoice" element={<Invoice />} />

            <Route path="/company" element={<Company />} />

            <Route path="/reports" element={<div>Reports Page</div>} />

          </Routes>

        </div>

      </div>
    </div>
  );
};

export default MainLayout;
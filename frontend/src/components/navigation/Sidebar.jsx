import { useState } from "react";
import { NavLink } from "react-router-dom";

const Sidebar = ({ menu }) => {
  const [collapsed, setCollapsed] = useState(false);

  const base =
    "flex items-center gap-3 p-3 rounded-lg text-sm transition-all";

  const active = "bg-slate-700";
  const inactive = "hover:bg-slate-600";

  return (
    <div
      className={`bg-slate-800 text-white h-screen flex flex-col transition-all duration-300 ${
        collapsed ? "w-20" : "w-64"
      }`}
    >
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-slate-700">

        {!collapsed && (
          <h1 className="text-lg font-semibold">
            CourierMS
          </h1>
        )}

        <button
          onClick={() => setCollapsed(!collapsed)}
          className="text-lg"
        >
          ☰
        </button>

      </div>

      {/* Navigation */}
      <nav className="flex-1 p-3 space-y-2">

        {menu.map((item, index) => (
          <NavLink
            key={index}
            to={item.path}
            end={item.end}
            className={({ isActive }) =>
              `${base} ${isActive ? active : inactive}`
            }
          >
            <span>{item.icon}</span>

            {!collapsed && <span>{item.name}</span>}
          </NavLink>
        ))}

      </nav>

      {!collapsed && (
        <div className="p-4 text-xs border-t border-slate-700 text-slate-400">
          © 2026 CourierMS
        </div>
      )}
    </div>
  );
};

export default Sidebar;
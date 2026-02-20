import { createBrowserRouter } from "react-router";
import { AdminLayout } from "../app/layouts/AdminLayout";
import { Dashboard } from "./pages/Dashboard";
import { Users } from "./pages/Users";
import { Vendors } from "./pages/Vendor";
import { Transactions } from "./pages/Transactions";
import { Inventory } from "./pages/Inventory";
import { Settings } from "./pages/Settings";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: AdminLayout,
    children: [
      { index: true, Component: Dashboard },
      { path: "users", Component: Users },
      { path: "vendors", Component: Vendors },
      { path: "transactions", Component: Transactions },
      { path: "inventory", Component: Inventory },
      { path: "settings", Component: Settings },
    ],
  },
]);

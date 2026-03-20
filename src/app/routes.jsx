// src/app/routes.jsx
import { createBrowserRouter } from "react-router";
import { AdminLayout } from "./layouts/AdminLayout.jsx";
import AdminRoute from "../components/AdminRoute.jsx"; // 1. Import the guard
import Splash from "./pages/Splash.jsx";
import Login from "./pages/Login.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import Users from "./pages/Users.jsx";
import Transactions from "./pages/Transactions.jsx";
import ContentControl from "./pages/ContentControl.jsx";
import Settings from "./pages/Settings.jsx";
import Profile from "./pages/Profile.jsx";

export const router = createBrowserRouter([
  // Public routes
  { path: "/",      Component: Splash },
  { path: "/login", Component: Splash },
  { path: "/signin", Component: Login },

  // Admin pages - Protected by AdminRoute
  {
    path: "/dashboard",
    // 2. Wrap AdminLayout with AdminRoute
    element: (
      <AdminRoute>
        <AdminLayout />
      </AdminRoute> 
    ),
    children: [
      { index: true,          Component: Dashboard      },
      { path: "users",        Component: Users          },
      { path: "transactions", Component: Transactions   },
      { path: "inventory",    Component: ContentControl },
      { path: "settings",     Component: Settings       },
      { path: "profile",      Component: Profile        },
    ],
  },
]);
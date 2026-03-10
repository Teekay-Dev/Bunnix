import { createBrowserRouter } from "react-router";
import { AdminLayout } from "../app/layouts/AdminLayout";
import Splash from "./pages/Splash";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Transactions from "./pages/Transactions";
import ContentControl from "./pages/ContentControl";
import Settings from "./pages/Settings";
import Profile from "./pages/Profile";

export const router = createBrowserRouter([
  // / and /login both show splash first, splash navigates to /login
  { path: "/",      Component: Splash },
  { path: "/login", Component: Splash }, // refreshing login shows splash then goes to login form

  // actual login form
  { path: "/signin", Component: Login },

  // Admin pages
  {
    path: "/dashboard",
    Component: AdminLayout,
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

import React from 'react';
import { Link, useLocation, Outlet } from 'react-router';
import { 
  LayoutDashboard, 
  Users as UsersIcon, 
  Store, 
  Receipt, 
  Package, 
  Settings as SettingsIcon,
  Bell,
  Search,
  LogOut,
  ChevronRight,
  ShieldCheck
} from 'lucide-react';
import { clsx,  } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs) {
  return twMerge(clsx(inputs));
}

const navItems = [
  { icon: LayoutDashboard, label: 'Dashboard', path: '/' },
  { icon: UsersIcon, label: 'Users', path: '/users' },
  { icon: Store, label: 'Vendors', path: '/vendors' },
  { icon: Receipt, label: 'Transactions', path: '/transactions' },
  { icon: Package, label: 'Content Control', path: '/inventory' },
  { icon: SettingsIcon, label: 'Settings', path: '/settings' },
];

export function AdminLayout() {
  const location = useLocation();

  return (
    <div className="flex h-screen bg-gray-50 text-slate-900 font-sans">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col shrink-0">
        <div className="p-6 flex items-center gap-3">
          <div className="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center text-white shadow-lg shadow-orange-200">
            <img src="/bunnix.png" alt="Logo" className="w-12 h-auto" />
          </div>
          <span className="font-bold text-xl tracking-tight">BUNNIX <span className="text-orange-500">Admin</span></span>
        </div>

        <nav className="flex-1 px-4 py-4 space-y-1">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={cn(
                  "flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group",
                  isActive 
                    ? "bg-orange-50 text-orange-600 font-semibold" 
                    : "text-slate-500 hover:bg-gray-100 hover:text-slate-900"
                )}
              >
                <item.icon size={20} className={cn(isActive ? "text-orange-600" : "text-slate-400 group-hover:text-slate-600")} />
                {item.label}
                {isActive && <div className="ml-auto w-1.5 h-1.5 rounded-full bg-orange-600" />}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-gray-100">
          <button className="flex items-center gap-3 px-4 py-3 w-full rounded-xl text-slate-500 hover:bg-red-50 hover:text-red-600 transition-colors">
            <LogOut size={20} />
            <span className="font-medium">Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        {/* Topbar */}
        <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-8 shrink-0">
          <div className="relative w-96">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input 
              type="text" 
              placeholder="Search users, orders, or vendors..."
              className="w-full pl-10 pr-4 py-2 bg-gray-100 border-none rounded-lg text-sm focus:ring-2 focus:ring-orange-500/20 transition-all outline-none"
            />
          </div>

          <div className="flex items-center gap-6">
            <button className="relative text-slate-500 hover:text-orange-600 transition-colors">
              <Bell size={20} />
              <span className="absolute -top-1 -right-1 w-4 h-4 bg-orange-500 text-white text-[10px] flex items-center justify-center rounded-full border-2 border-white font-bold">4</span>
            </button>
            <div className="flex items-center gap-3 pl-6 border-l border-gray-100">
              <div className="text-right">
                <p className="text-sm font-semibold">Admin User</p>
                <p className="text-[11px] text-slate-400 uppercase tracking-wider">Super Admin</p>
              </div>
              <div className="w-10 h-10 rounded-full bg-slate-200 overflow-hidden ring-2 ring-gray-50 ring-offset-2 ring-offset-white">
                <img src="https://images.unsplash.com/photo-1738750908048-14200459c3c9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwcm9mZXNzaW9uYWwlMjBidXNpbmVzcyUyMHBvcnRyYWl0JTIwYXZhdGFyfGVufDF8fHx8MTc3MTU2MjM5MXww&ixlib=rb-4.1.0&q=80&w=1080" alt="Admin" className="w-full h-full object-cover" />
              </div>
            </div>
          </div>
        </header>

        {/* Scrollable Area */}
        <div className="flex-1 overflow-y-auto p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

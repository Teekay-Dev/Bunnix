import React, { useState, useEffect } from 'react';
import { Link, useLocation, Outlet, useNavigate } from 'react-router';
import {
  LayoutDashboard, Users as UsersIcon, Receipt, Package,
  Settings as SettingsIcon, Bell, Search, LogOut, Sun, Moon,
  ChevronLeft, ChevronRight,
} from 'lucide-react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs) { return twMerge(clsx(inputs)); }

const navItems = [
  { icon: LayoutDashboard, label: 'Dashboard',      path: '/dashboard' },
  { icon: UsersIcon,       label: 'Users',           path: '/dashboard/users' },
  { icon: Receipt,         label: 'Transactions',    path: '/dashboard/transactions' },
  { icon: Package,         label: 'Content Control', path: '/dashboard/inventory' },
  { icon: SettingsIcon,    label: 'Settings',        path: '/dashboard/settings' },
];

const SEARCH_MAP = [
  { keywords: ['dashboard', 'home', 'overview'],                         path: '/dashboard' },
  { keywords: ['user', 'users', 'customer'],                             path: '/dashboard/users' },
  { keywords: ['transaction', 'transactions', 'payment', 'order'],       path: '/dashboard/transactions' },
  { keywords: ['content', 'inventory', 'product', 'service', 'listing'], path: '/dashboard/inventory' },
  { keywords: ['setting', 'settings', 'config'],                         path: '/dashboard/settings' },
  { keywords: ['profile', 'account'],                                    path: '/dashboard/profile' },
];

function findRoute(q) {
  const query = q.trim().toLowerCase();
  if (!query) return null;
  for (const { keywords, path } of SEARCH_MAP) {
    if (keywords.some(k => query.includes(k) || k.includes(query))) return path;
  }
  return null;
}

export function AdminLayout() {
  const location = useLocation();
  const navigate = useNavigate();

  const [dark, setDark] = useState(() =>
    localStorage.getItem('bunnix_theme') === 'dark' ||
    document.documentElement.classList.contains('dark')
  );

  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark);
    localStorage.setItem('bunnix_theme', dark ? 'dark' : 'light');
  }, [dark]);

  const [collapsed, setCollapsed] = useState(false);
  const [query, setQuery] = useState('');
  const [hint, setHint] = useState('');

  const handleSearch = (val) => {
    setQuery(val);
    const route = findRoute(val);
    if (route && val.trim()) {
      const label = route.split('/').pop().replace('-', ' ') || 'dashboard';
      setHint(`↵ Go to ${label.charAt(0).toUpperCase() + label.slice(1)}`);
    } else {
      setHint('');
    }
  };

  const handleSearchKey = (e) => {
    if (e.key === 'Enter') {
      const route = findRoute(query);
      if (route) { navigate(route); setQuery(''); setHint(''); }
    }
    if (e.key === 'Escape') { setQuery(''); setHint(''); }
  };

  return (
    <div className="flex h-screen bg-background text-foreground font-sans overflow-hidden">

      {/* SIDEBAR */}
      <aside className={cn(
        "flex flex-col shrink-0 bg-sidebar border-r border-sidebar-border transition-all duration-200",
        collapsed ? "w-16" : "w-64"
      )}>

        {/* Logo */}
        <div className={cn(
          "flex items-center gap-3 p-4 border-b border-sidebar-border",
          collapsed && "justify-center px-2"
        )}>
          <img
            src="/bunnix.png" alt="Logo"
            style={{
              width: 52, height: 52, flexShrink: 0,
              objectFit: 'contain', display: 'block',
              filter: 'drop-shadow(0 0 6px rgba(232,93,4,.8))',
            }}
          />
          {!collapsed && (
            <span className="font-bold text-lg tracking-tight whitespace-nowrap text-sidebar-foreground">
              BUNNIX <span className="text-orange-500">Admin</span>
            </span>
          )}
        </div>

        {/* Collapse toggle */}
        <div className={cn(
          "flex items-center px-3 py-2 border-b border-sidebar-border",
          collapsed ? "justify-center" : "justify-end"
        )}>
          <button
            onClick={() => setCollapsed(c => !c)}
            title={collapsed ? 'Expand' : 'Collapse'}
            className="flex items-center gap-1 text-xs font-medium text-muted-foreground hover:text-orange-500 transition-colors px-2 py-1"
          >
            {collapsed
              ? <ChevronRight size={14} />
              : <><ChevronLeft size={14} /><span>Collapse</span></>
            }
          </button>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-2 py-3 space-y-0.5 overflow-y-auto overflow-x-hidden">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                title={collapsed ? item.label : undefined}
                className={cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-150 group",
                  collapsed && "justify-center px-2",
                  isActive
                    ? "bg-orange-50 dark:bg-orange-500/10 text-orange-600 dark:text-orange-400 font-semibold"
                    : "text-sidebar-foreground/60 hover:bg-sidebar-accent hover:text-sidebar-foreground"
                )}
              >
                <item.icon size={20} className={cn(
                  "shrink-0 transition-colors",
                  isActive
                    ? "text-orange-600 dark:text-orange-400"
                    : "text-muted-foreground group-hover:text-sidebar-foreground"
                )} />
                {!collapsed && <span className="whitespace-nowrap text-sm">{item.label}</span>}
                {!collapsed && isActive && <div className="ml-auto w-1.5 h-1.5 rounded-full bg-orange-500" />}
              </Link>
            );
          })}
        </nav>

        {/* Footer */}
        <div className="p-2 border-t border-sidebar-border space-y-0.5">
          <button
            onClick={() => setDark(d => !d)}
            title={dark ? 'Light Mode' : 'Dark Mode'}
            className={cn(
              "flex items-center gap-3 px-3 py-2.5 w-full rounded-xl text-sidebar-foreground/60 hover:bg-sidebar-accent hover:text-sidebar-foreground transition-colors",
              collapsed && "justify-center px-2"
            )}
          >
            {dark ? <Sun size={20} className="shrink-0" /> : <Moon size={20} className="shrink-0" />}
            {!collapsed && <span className="text-sm font-medium">{dark ? 'Light Mode' : 'Dark Mode'}</span>}
          </button>

          <button
            onClick={() => navigate('/')}
            title={collapsed ? 'Sign Out' : undefined}
            className={cn(
              "flex items-center gap-3 px-3 py-2.5 w-full rounded-xl text-sidebar-foreground/60 hover:bg-red-50 dark:hover:bg-red-900/20 hover:text-red-600 dark:hover:text-red-400 transition-colors",
              collapsed && "justify-center px-2"
            )}
          >
            <LogOut size={20} className="shrink-0" />
            {!collapsed && <span className="text-sm font-medium">Sign Out</span>}
          </button>
        </div>
      </aside>

      {/* MAIN */}
      <main className="flex-1 flex flex-col overflow-hidden min-w-0">
        <header className="h-16 bg-card border-b border-border flex items-center justify-between px-6 shrink-0">
          <div className="relative w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" size={16} />
            <input
              type="search" value={query}
              onChange={e => handleSearch(e.target.value)}
              onKeyDown={handleSearchKey}
              placeholder="Search pages…"
              autoComplete="off" name="admin-search"
              className="w-full pl-9 pr-4 py-2 bg-input-background dark:bg-muted text-foreground placeholder:text-muted-foreground border border-border rounded-lg text-sm outline-none focus:ring-2 focus:ring-orange-500/20 focus:border-orange-400 transition-all"
            />
            {hint && (
              <span className="absolute right-2 top-1/2 -translate-y-1/2 text-[11px] text-orange-500 font-semibold bg-orange-50 dark:bg-orange-500/10 px-2 py-0.5 rounded-full whitespace-nowrap pointer-events-none">
                {hint}
              </span>
            )}
          </div>

          <div className="flex items-center gap-5">
            <button className="relative text-muted-foreground hover:text-orange-500 transition-colors">
              <Bell size={20} />
              <span className="absolute -top-1 -right-1 w-4 h-4 bg-orange-500 text-white text-[10px] flex items-center justify-center rounded-full border-2 border-card font-bold">4</span>
            </button>

            <div
              className="flex items-center gap-3 pl-5 border-l border-border cursor-pointer group"
              onClick={() => navigate('/dashboard/profile')}
              title="My Profile"
            >
              <div className="text-right">
                <p className="text-sm font-semibold text-foreground">Admin User</p>
                <p className="text-[11px] text-muted-foreground uppercase tracking-wider">Super Admin</p>
              </div>
              <div style={{
                width: 36, height: 36, borderRadius: '50%',
                background: '#E85D04', flexShrink: 0,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff', fontWeight: 700, fontSize: 13,
                boxShadow: '0 0 0 2px rgba(232,93,4,.3)',
              }}>
                AU
              </div>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-8 bg-background">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

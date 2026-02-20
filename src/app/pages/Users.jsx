import React, { useState } from 'react';
import { 
  Search, 
  Filter, 
  MoreVertical, 
  Mail, 
  Shield, 
  Ban, 
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  UserPlus
} from 'lucide-react';
import { motion } from 'motion/react';

const users = [
  { id: 1, name: 'Alice Thompson', email: 'alice@example.com', role: 'Customer', status: 'Active', joined: 'Oct 12, 2025', orders: 12 },
  { id: 2, name: 'Bolanle Silva', email: 'bolanle.s@bunnix.com', role: 'Vendor', status: 'Active', joined: 'Sep 28, 2025', orders: 84 },
  { id: 3, name: 'Chris Evans', email: 'chris@testmail.org', role: 'Customer', status: 'Suspended', joined: 'Nov 05, 2025', orders: 0 },
  { id: 4, name: 'Devon Miles', email: 'devon@miles.co', role: 'Vendor', status: 'Pending', joined: 'Feb 10, 2026', orders: 0 },
  { id: 5, name: 'Elena Gilbert', email: 'elena.g@myself.net', role: 'Customer', status: 'Active', joined: 'Dec 15, 2025', orders: 5 },
  { id: 6, name: 'Femi Kuti', email: 'femi@afrobeat.ng', role: 'Vendor', status: 'Active', joined: 'Jan 02, 2026', orders: 112 },
  { id: 7, name: 'Grace Hopper', email: 'grace.h@computing.com', role: 'Customer', status: 'Active', joined: 'Jan 15, 2026', orders: 2 },
  { id: 8, name: 'Henry Ford', email: 'henry@auto.net', role: 'Vendor', status: 'Inactive', joined: 'Aug 20, 2025', orders: 45 },
];

export function Users() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('All');

  const filteredUsers = users.filter(user => {
    const matchesSearch = user.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          user.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesFilter = filter === 'All' || user.role === filter;
    return matchesSearch && matchesFilter;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">User Management</h1>
          <p className="text-slate-500">Oversee all customer and vendor accounts on the platform.</p>
        </div>
        <button className="flex items-center justify-center gap-2 bg-orange-600 text-white px-4 py-2.5 rounded-xl font-semibold hover:bg-orange-700 transition-colors shadow-lg shadow-orange-200">
          <UserPlus size={18} />
          Add Internal User
        </button>
      </div>

      {/* Controls */}
      <div className="bg-white p-4 rounded-2xl border border-gray-200 shadow-sm flex flex-col md:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input 
            type="text" 
            placeholder="Search by name or email..."
            className="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-100 rounded-xl text-sm focus:ring-2 focus:ring-orange-500/20 outline-none"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <div className="flex bg-gray-50 p-1 rounded-xl border border-gray-100">
            {['All', 'Customer', 'Vendor'].map((f) => (
              <button
                key={f}
                onClick={() => setFilter(f)}
                className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-all ${
                  filter === f ? 'bg-white text-orange-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'
                }`}
              >
                {f}
              </button>
            ))}
          </div>
          <button className="p-2.5 bg-gray-50 border border-gray-100 rounded-xl text-slate-500 hover:bg-gray-100">
            <Filter size={18} />
          </button>
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-gray-50 text-slate-400 text-xs font-bold uppercase tracking-wider">
                <th className="px-6 py-4">User</th>
                <th className="px-6 py-4">Account Type</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Joined Date</th>
                <th className="px-6 py-4 text-center">Activity</th>
                <th className="px-6 py-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filteredUsers.map((user) => (
                <tr key={user.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-600 font-bold">
                        {user.name.charAt(0)}
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-900">{user.name}</p>
                        <p className="text-xs text-slate-500">{user.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 text-[11px] font-bold uppercase px-2 py-0.5 rounded-full ${
                      user.role === 'Vendor' ? 'bg-orange-50 text-orange-600' : 'bg-blue-50 text-blue-600'
                    }`}>
                      <Shield size={10} />
                      {user.role}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 px-2 py-1 rounded-lg text-xs font-medium ${
                      user.status === 'Active' ? 'bg-green-50 text-green-700' : 
                      user.status === 'Suspended' ? 'bg-red-50 text-red-700' : 'bg-yellow-50 text-yellow-700'
                    }`}>
                      {user.status === 'Active' && <CheckCircle2 size={12} />}
                      {user.status === 'Suspended' && <Ban size={12} />}
                      {user.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500">{user.joined}</td>
                  <td className="px-6 py-4 text-center">
                    <span className="text-sm font-bold text-slate-700">{user.orders}</span>
                    <span className="text-[10px] text-slate-400 block uppercase">Interactions</span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <button className="p-2 text-slate-400 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-all">
                        <Mail size={16} />
                      </button>
                      <button className="p-2 text-slate-400 hover:text-slate-600 hover:bg-gray-100 rounded-lg transition-all">
                        <MoreVertical size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
          <p className="text-sm text-slate-500">Showing <span className="font-semibold text-slate-900">1-8</span> of <span className="font-semibold text-slate-900">240</span> users</p>
          <div className="flex items-center gap-2">
            <button className="p-2 border border-gray-200 rounded-lg disabled:opacity-50" disabled>
              <ChevronLeft size={16} />
            </button>
            <button className="p-2 border border-gray-200 rounded-lg hover:bg-gray-50">
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

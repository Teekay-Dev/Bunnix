import React, { useState } from 'react';
import { Users, Store, CreditCard, ShieldAlert, TrendingUp, Activity, ArrowUpRight, ArrowDownRight, Clock, Zap, Package, AlertCircle } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';

const EMPTY_CHART = [
  {m:'Jan',v:0},{m:'Feb',v:0},{m:'Mar',v:0},{m:'Apr',v:0},
  {m:'May',v:0},{m:'Jun',v:0},{m:'Jul',v:0},
];

function CustomTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-white dark:bg-[#1a1a1a] border border-gray-200 dark:border-white/10 rounded-xl px-4 py-2.5 shadow-xl text-sm">
      <div className="font-semibold text-gray-500 dark:text-gray-400 mb-1">{label}</div>
      <div className="font-black text-orange-500 text-base">₦{Number(payload[0].value).toLocaleString()}</div>
    </div>
  );
}

// Glowing stat card with gradient top border
function StatCard({ icon: Icon, label, value, sub, gradient, iconBg, iconColor, valueColor, pulse }) {
  return (
    <div className="group relative bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm transition-all duration-300 hover:shadow-xl hover:-translate-y-1 overflow-hidden cursor-default">
      {/* Top gradient accent bar */}
      <div className={`absolute top-0 left-0 right-0 h-0.5 ${gradient}`} />
      {/* Background glow on hover */}
      <div className={`absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-300 ${iconBg} rounded-2xl`} style={{ opacity: 0 }} />

      <div className="relative">
        <div className="flex items-start justify-between mb-4">
          <div className={`w-11 h-11 rounded-xl flex items-center justify-center transition-transform duration-300 group-hover:scale-110 ${iconBg}`}>
            <Icon size={20} className={iconColor} />
          </div>
          {pulse && (
            <span className="flex items-center gap-1 text-[10px] font-bold text-orange-500 bg-orange-50 dark:bg-orange-500/10 px-2 py-0.5 rounded-full">
              <span className="w-1.5 h-1.5 rounded-full bg-orange-500 animate-pulse" />LIVE
            </span>
          )}
        </div>
        <div className={`text-3xl font-black mb-1 transition-colors ${valueColor}`}>{value ?? 0}</div>
        <div className="text-sm font-semibold text-gray-700 dark:text-gray-300">{label}</div>
        {sub && <div className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">{sub}</div>}
      </div>
    </div>
  );
}

// Quick action card
function QuickCard({ icon, label, desc, color, bg, onClick }) {
  return (
    <button onClick={onClick}
      className={`group w-full text-left p-4 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 hover:border-current`}
      style={{ '--hover-color': color }}>
      <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 ${bg} group-hover:scale-110 transition-transform`}>
        <span className="text-lg">{icon}</span>
      </div>
      <p className="font-bold text-sm text-gray-900 dark:text-gray-100">{label}</p>
      <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">{desc}</p>
    </button>
  );
}

// Activity item
function ActivityItem({ icon, title, time, type }) {
  const colors = { info:'bg-blue-50 dark:bg-blue-500/10 text-blue-500', warn:'bg-amber-50 dark:bg-amber-500/10 text-amber-500', success:'bg-green-50 dark:bg-green-500/10 text-green-500', danger:'bg-red-50 dark:bg-red-500/10 text-red-500' };
  return (
    <div className="flex items-start gap-3 py-3 border-b border-gray-100 dark:border-white/5 last:border-0">
      <div className={`w-8 h-8 rounded-xl flex items-center justify-center text-sm shrink-0 ${colors[type] || colors.info}`}>{icon}</div>
      <div className="flex-1 min-w-0">
        <p className="text-sm text-gray-700 dark:text-gray-300 font-medium leading-snug">{title}</p>
        <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5 flex items-center gap-1"><Clock size={10}/>{time}</p>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const now = new Date();
  const hour = now.getHours();
  const greeting = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening';

  return (
    <div>
      {/* Header */}
      <div className="flex items-start justify-between mb-8">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-2xl">{hour < 12 ? '🌅' : hour < 17 ? '☀️' : '🌙'}</span>
            <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">{greeting}, Admin</h1>
          </div>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {now.toLocaleDateString('en-NG', { weekday:'long', year:'numeric', month:'long', day:'numeric' })}
          </p>
        </div>
        {/* Live indicator */}
        <div className="flex items-center gap-2 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-xl px-3 py-2 shadow-sm">
          <span className="w-2 h-2 rounded-full bg-green-500 animate-pulse" />
          <span className="text-xs font-bold text-gray-600 dark:text-gray-400">System Online</span>
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard icon={Users}      label="Active Users"         value={0} sub="Customers on platform" gradient="bg-gradient-to-r from-blue-500 to-blue-400"   iconBg="bg-blue-50 dark:bg-blue-500/10"   iconColor="text-blue-600 dark:text-blue-400"   valueColor="text-blue-600 dark:text-blue-400"   pulse />
        <StatCard icon={Store}      label="Active Vendors"       value={0} sub="Approved & selling"    gradient="bg-gradient-to-r from-orange-500 to-amber-400" iconBg="bg-orange-50 dark:bg-orange-500/10" iconColor="text-orange-600 dark:text-orange-400" valueColor="text-orange-600 dark:text-orange-400" pulse />
        <StatCard icon={CreditCard} label="Pending Transactions" value={0} sub="Awaiting confirmation" gradient="bg-gradient-to-r from-amber-500 to-yellow-400" iconBg="bg-amber-50 dark:bg-amber-500/10"   iconColor="text-amber-600 dark:text-amber-400"   valueColor="text-amber-600 dark:text-amber-400"  />
        <StatCard icon={ShieldAlert} label="Flagged Items"       value={0} sub="Require moderation"   gradient="bg-gradient-to-r from-red-500 to-rose-400"    iconBg="bg-red-50 dark:bg-red-500/10"     iconColor="text-red-600 dark:text-red-400"     valueColor="text-red-600 dark:text-red-400"     />
      </div>

      {/* Chart + activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">

        {/* Revenue chart */}
        <div className="lg:col-span-2 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h3 className="font-black text-gray-900 dark:text-gray-100 flex items-center gap-2">
                <TrendingUp size={16} className="text-orange-500" /> Revenue Growth
              </h3>
              <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">Monthly transaction volume</p>
            </div>
            <div className="text-right">
              <div className="text-2xl font-black text-orange-500">₦0</div>
              <div className="text-xs text-gray-400 dark:text-gray-500">Total this year</div>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={190}>
            <AreaChart data={EMPTY_CHART} margin={{ top:4, right:4, bottom:0, left:-20 }}>
              <defs>
                <linearGradient id="rg" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor="#E85D04" stopOpacity={0.2}/>
                  <stop offset="95%" stopColor="#E85D04" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(128,128,128,.08)" />
              <XAxis dataKey="m" tick={{ fill:'#9ca3af', fontSize:11 }} axisLine={false} tickLine={false}/>
              <YAxis tick={{ fill:'#9ca3af', fontSize:11 }} axisLine={false} tickLine={false}/>
              <Tooltip content={<CustomTooltip />}/>
              <Area type="monotone" dataKey="v" stroke="#E85D04" strokeWidth={2.5} fill="url(#rg)" dot={false} activeDot={{ r:5, fill:'#E85D04', stroke:'#fff', strokeWidth:2 }}/>
            </AreaChart>
          </ResponsiveContainer>
          <div className="mt-3 pt-3 border-t border-gray-100 dark:border-white/5">
            <p className="text-[11px] text-gray-400 dark:text-gray-500 text-center flex items-center justify-center gap-1.5">
              <Activity size={11} /> Live data will appear once backend is connected
            </p>
          </div>
        </div>

        {/* Recent activity */}
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <h3 className="font-black text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
            <Zap size={15} className="text-orange-500" /> Recent Activity
          </h3>
          <div className="flex flex-col items-center justify-center py-8 text-center">
            <div className="w-12 h-12 rounded-2xl bg-gray-100 dark:bg-white/5 flex items-center justify-center mb-3">
              <Activity size={20} className="text-gray-400 dark:text-gray-500" />
            </div>
            <p className="font-bold text-sm text-gray-600 dark:text-gray-400">No activity yet</p>
            <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">Events will stream here in real time</p>
          </div>
        </div>
      </div>

      {/* Bottom row: Platform summary + Quick actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

        {/* Platform summary */}
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <h3 className="font-black text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
            <Package size={15} className="text-orange-500" /> Platform Summary
          </h3>
          <div className="space-y-3">
            {[
              { l:'Total Users',      v:0, color:'text-blue-500',   bg:'bg-blue-50 dark:bg-blue-500/10',    bar:'bg-blue-400' },
              { l:'Active Vendors',   v:0, color:'text-orange-500', bg:'bg-orange-50 dark:bg-orange-500/10',bar:'bg-orange-400' },
              { l:'Total Listings',   v:0, color:'text-purple-500', bg:'bg-purple-50 dark:bg-purple-500/10',bar:'bg-purple-400' },
              { l:'Transactions',     v:0, color:'text-green-500',  bg:'bg-green-50 dark:bg-green-500/10',  bar:'bg-green-400' },
            ].map(s => (
              <div key={s.l} className="flex items-center gap-3">
                <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 ${s.bg}`}>
                  <span className={`text-base font-black ${s.color}`}>{s.v}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-semibold text-gray-600 dark:text-gray-400">{s.l}</span>
                  </div>
                  <div className="h-1 bg-gray-100 dark:bg-white/5 rounded-full overflow-hidden">
                    <div className={`h-full w-0 rounded-full ${s.bar}`} />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick actions */}
        <div className="lg:col-span-2 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <h3 className="font-black text-gray-900 dark:text-gray-100 mb-4 flex items-center gap-2">
            <Zap size={15} className="text-orange-500" /> Quick Actions
          </h3>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <QuickCard icon="👥" label="Manage Users"    desc="View & suspend users"      color="#3b82f6" bg="bg-blue-50 dark:bg-blue-500/10" />
            <QuickCard icon="🏪" label="Vendor Queue"    desc="Approve vendor requests"   color="#E85D04" bg="bg-orange-50 dark:bg-orange-500/10" />
            <QuickCard icon="💳" label="Transactions"    desc="Review pending payments"   color="#f59e0b" bg="bg-amber-50 dark:bg-amber-500/10" />
            <QuickCard icon="🚩" label="Flagged Content" desc="Moderate flagged listings" color="#ef4444" bg="bg-red-50 dark:bg-red-500/10" />
          </div>

          {/* Status bar */}
          <div className="mt-4 pt-4 border-t border-gray-100 dark:border-white/5">
            <div className="grid grid-cols-3 gap-3">
              {[
                { label:'Server', status:'Operational', color:'text-green-500', dot:'bg-green-500' },
                { label:'Database', status:'Connected', color:'text-green-500', dot:'bg-green-500' },
                { label:'Payments', status:'Pending Setup', color:'text-amber-500', dot:'bg-amber-500' },
              ].map(s => (
                <div key={s.label} className="flex items-center gap-2 bg-gray-50 dark:bg-white/3 rounded-xl p-2.5">
                  <span className={`w-2 h-2 rounded-full shrink-0 ${s.dot} animate-pulse`} />
                  <div>
                    <p className="text-[10px] font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wide">{s.label}</p>
                    <p className={`text-xs font-bold ${s.color}`}>{s.status}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

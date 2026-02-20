import React from 'react';
import { 
  Users as UsersIcon, 
  Store, 
  Receipt, 
  TrendingUp, 
  TrendingDown, 
  Clock,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  AreaChart,
  Area
} from 'recharts';
import { motion } from 'motion/react';

const stats = [
  { 
    label: 'Total Revenue', 
    value: '₦4,250,000', 
    change: '+12.5%', 
    trend: 'up', 
    icon: Receipt, 
    color: 'bg-green-500',
    sub: 'From 1,240 sales'
  },
  { 
    label: 'Active Users', 
    value: '18,402', 
    change: '+3.2%', 
    trend: 'up', 
    icon: UsersIcon, 
    color: 'bg-blue-500',
    sub: 'Customers & Vendors'
  },
  { 
    label: 'Pending Approvals', 
    value: '24', 
    change: '-5', 
    trend: 'down', 
    icon: Store, 
    color: 'bg-orange-500',
    sub: 'New vendors waiting'
  },
  { 
    label: 'Payment Disputes', 
    value: '12', 
    change: '+2', 
    trend: 'up', 
    icon: AlertCircle, 
    color: 'bg-red-500',
    sub: 'Manual transfer reports'
  },
];

const revenueData = [
  { name: 'Jan', value: 4000 },
  { name: 'Feb', value: 3000 },
  { name: 'Mar', value: 5000 },
  { name: 'Apr', value: 4500 },
  { name: 'May', value: 6000 },
  { name: 'Jun', value: 5500 },
  { name: 'Jul', value: 7000 },
];

const recentOrders = [
  { id: 'ORD-7721', user: 'Sarah Jenkins', type: 'Product', amount: '₦12,500', status: 'Pending Verification', date: '2 mins ago' },
  { id: 'SRV-1029', user: 'Marcus Wright', type: 'Service', amount: '₦45,000', status: 'Confirmed', date: '15 mins ago' },
  { id: 'ORD-6610', user: 'Lina Rose', type: 'Product', amount: '₦8,200', status: 'Processing', date: '1 hour ago' },
  { id: 'SRV-0092', user: 'David Cole', type: 'Service', amount: '₦22,000', status: 'Awaiting Payment', date: '3 hours ago' },
];

export function Dashboard() {
  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Platform Overview</h1>
        <p className="text-slate-500">Welcome back. Here's what's happening with Bunnix today.</p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            key={stat.label} 
            className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow"
          >
            <div className="flex items-start justify-between mb-4">
              <div className={`${stat.color} p-3 rounded-xl text-white`}>
                <stat.icon size={24} />
              </div>
              <div className={`flex items-center gap-1 text-sm font-medium ${stat.trend === 'up' ? 'text-green-600' : 'text-red-600'}`}>
                {stat.trend === 'up' ? <TrendingUp size={16} /> : <TrendingDown size={16} />}
                {stat.change}
              </div>
            </div>
            <div>
              <p className="text-slate-500 text-sm font-medium">{stat.label}</p>
              <h3 className="text-2xl font-bold text-slate-900 mt-1">{stat.value}</h3>
              <p className="text-slate-400 text-xs mt-1">{stat.sub}</p>
            </div>
          </motion.div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Revenue Chart */}
        <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-lg font-bold">Revenue Growth</h2>
            <select className="bg-gray-50 border border-gray-200 rounded-lg text-sm px-3 py-1.5 outline-none focus:ring-2 focus:ring-orange-500/20">
              <option>Last 7 Months</option>
              <option>Last 12 Months</option>
            </select>
          </div>
          <div className="h-[300px] w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={revenueData}>
                <defs>
                  <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#f97316" stopOpacity={0.1}/>
                    <stop offset="95%" stopColor="#f97316" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{fill: '#94a3b8', fontSize: 12}} dy={10} />
                <YAxis axisLine={false} tickLine={false} tick={{fill: '#94a3b8', fontSize: 12}} />
                <Tooltip 
                  contentStyle={{ borderRadius: '12px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
                  cursor={{ stroke: '#f97316', strokeWidth: 2 }}
                />
                <Area type="monotone" dataKey="value" stroke="#f97316" strokeWidth={3} fillOpacity={1} fill="url(#colorValue)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Action Queue */}
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
          <h2 className="text-lg font-bold mb-6">Immediate Actions</h2>
          <div className="space-y-4">
            <div className="flex items-center gap-4 p-4 bg-orange-50 rounded-xl border border-orange-100">
              <div className="w-10 h-10 rounded-full bg-orange-500 flex items-center justify-center text-white shrink-0">
                <Store size={20} />
              </div>
              <div className="flex-1">
                <p className="text-sm font-semibold">Vendor Application</p>
                <p className="text-xs text-orange-600">"Tech Hub Store" waiting verification</p>
              </div>
              <button className="p-1 hover:bg-white rounded-md transition-colors">
                <ChevronRight size={18} className="text-orange-500" />
              </button>
            </div>

            <div className="flex items-center gap-4 p-4 bg-blue-50 rounded-xl border border-blue-100">
              <div className="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white shrink-0">
                <Receipt size={20} />
              </div>
              <div className="flex-1">
                <p className="text-sm font-semibold">Flagged Payment</p>
                <p className="text-xs text-blue-600">Dispute in Order #8821 (₦45,000)</p>
              </div>
              <button className="p-1 hover:bg-white rounded-md transition-colors">
                <ChevronRight size={18} className="text-blue-500" />
              </button>
            </div>

            <div className="flex items-center gap-4 p-4 bg-purple-50 rounded-xl border border-purple-100">
              <div className="w-10 h-10 rounded-full bg-purple-500 flex items-center justify-center text-white shrink-0">
                <AlertCircle size={20} />
              </div>
              <div className="flex-1">
                <p className="text-sm font-semibold">System Update</p>
                <p className="text-xs text-purple-600">Review new vendor fee policy</p>
              </div>
              <button className="p-1 hover:bg-white rounded-md transition-colors">
                <ChevronRight size={18} className="text-purple-500" />
              </button>
            </div>
          </div>
          <button className="w-full mt-6 py-2.5 text-sm font-semibold text-slate-600 hover:bg-gray-50 rounded-xl border border-gray-200 transition-colors">
            View All Tasks
          </button>
        </div>
      </div>

      {/* Recent Activity Table */}
      <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-gray-100 flex items-center justify-between">
          <h2 className="text-lg font-bold">Recent Transactions</h2>
          <button className="text-sm font-semibold text-orange-600 hover:underline">View All</button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-gray-50 text-slate-400 text-xs font-bold uppercase tracking-wider">
                <th className="px-6 py-4">Transaction ID</th>
                <th className="px-6 py-4">Customer</th>
                <th className="px-6 py-4">Type</th>
                <th className="px-6 py-4">Amount</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Date</th>
                <th className="px-6 py-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {recentOrders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-mono text-xs font-semibold text-slate-600">{order.id}</td>
                  <td className="px-6 py-4">
                    <span className="text-sm font-medium text-slate-900">{order.user}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-[10px] px-2 py-1 rounded-full font-bold uppercase ${order.type === 'Product' ? 'bg-indigo-50 text-indigo-600' : 'bg-pink-50 text-pink-600'}`}>
                      {order.type}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm font-bold">{order.amount}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-1.5">
                      <div className={`w-1.5 h-1.5 rounded-full ${
                        order.status === 'Confirmed' ? 'bg-green-500' : 
                        order.status === 'Pending Verification' ? 'bg-orange-500' : 'bg-gray-400'
                      }`} />
                      <span className="text-sm text-slate-600">{order.status}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-xs text-slate-500">{order.date}</td>
                  <td className="px-6 py-4">
                    <button className="text-xs font-bold text-slate-400 hover:text-orange-600 transition-colors">Review</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function ChevronRight(props) {
  return (
    <svg 
      {...props} 
      xmlns="http://www.w3.org/2000/svg" 
      width="24" 
      height="24" 
      viewBox="0 0 24 24" 
      fill="none" 
      stroke="currentColor" 
      strokeWidth="2" 
      strokeLinecap="round" 
      strokeLinejoin="round"
    >
      <path d="m9 18 6-6-6-6" />
    </svg>
  );
}

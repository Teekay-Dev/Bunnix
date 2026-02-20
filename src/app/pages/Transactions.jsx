import React from 'react';
import { 
  CreditCard, 
  ArrowUpRight, 
  ArrowDownLeft, 
  Search, 
  Download, 
  Eye, 
  CheckCircle,
  AlertTriangle,
  FileText
} from 'lucide-react';

const transactions = [
  { id: 'TX-9901', customer: 'David Mark', vendor: 'Gadget Store', amount: '₦150,000', method: 'Bank Transfer', status: 'Flagged', date: 'Today, 10:30 AM', proof: 'receipt_01.jpg' },
  { id: 'TX-9902', customer: 'Sola Ahmed', vendor: 'Home Cooks', amount: '₦5,500', method: 'Cash on Service', status: 'Completed', date: 'Today, 09:15 AM', proof: null },
  { id: 'TX-9903', customer: 'Lina Rose', vendor: 'Fashionista', amount: '₦22,000', method: 'Bank Transfer', status: 'Pending Review', date: 'Yesterday', proof: 'receipt_92.png' },
  { id: 'TX-9904', customer: 'Mike Ross', vendor: 'Tech Repair', amount: '₦45,000', method: 'Bank Transfer', status: 'Completed', date: 'Feb 19, 2026', proof: 'receipt_88.jpg' },
  { id: 'TX-9905', customer: 'Jane Doe', vendor: 'Green Grocers', amount: '₦12,300', method: 'Bank Transfer', status: 'Completed', date: 'Feb 19, 2026', proof: 'receipt_77.png' },
];

export function Transactions() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Transaction Monitoring</h1>
          <p className="text-slate-500">Track manual payment confirmations and handle disputes.</p>
        </div>
        <div className="flex gap-2">
          <button className="flex items-center gap-2 px-4 py-2 border border-gray-200 rounded-xl font-bold text-slate-600 hover:bg-gray-50 transition-colors">
            <Download size={18} />
            Export CSV
          </button>
          <button className="bg-orange-600 text-white px-4 py-2 rounded-xl font-bold hover:bg-orange-700 transition-colors shadow-lg shadow-orange-100">
            Manual Reconciliation
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm col-span-1 md:col-span-1">
          <div className="w-12 h-12 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center mb-4">
            <CreditCard size={24} />
          </div>
          <p className="text-slate-500 text-sm font-medium">Total Volume (MTD)</p>
          <h3 className="text-2xl font-bold text-slate-900 mt-1">₦8.4M</h3>
          <div className="flex items-center gap-1 text-green-600 text-xs mt-1 font-bold">
            <ArrowUpRight size={14} />
            15% vs last month
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm col-span-1 md:col-span-1">
          <div className="w-12 h-12 bg-yellow-50 text-yellow-600 rounded-xl flex items-center justify-center mb-4">
            <AlertTriangle size={24} />
          </div>
          <p className="text-slate-500 text-sm font-medium">Pending Review</p>
          <h3 className="text-2xl font-bold text-slate-900 mt-1">42</h3>
          <p className="text-slate-400 text-xs mt-1">Requires admin attention</p>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm col-span-1 md:col-span-2">
          <p className="text-slate-500 text-sm font-medium mb-4">Payment Methods Mix</p>
          <div className="space-y-3">
            <div>
              <div className="flex justify-between text-xs font-bold mb-1">
                <span>BANK TRANSFER</span>
                <span>85%</span>
              </div>
              <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                <div className="h-full bg-orange-500 rounded-full" style={{ width: '85%' }} />
              </div>
            </div>
            <div>
              <div className="flex justify-between text-xs font-bold mb-1">
                <span>CASH / PHYSICAL</span>
                <span>15%</span>
              </div>
              <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                <div className="h-full bg-slate-400 rounded-full" style={{ width: '15%' }} />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-gray-100 flex items-center justify-between">
          <div className="relative w-80">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={16} />
            <input 
              type="text" 
              placeholder="Filter by Transaction ID..."
              className="w-full pl-9 pr-4 py-2 bg-gray-50 border border-gray-100 rounded-lg text-sm focus:ring-2 focus:ring-orange-500/20 outline-none"
            />
          </div>
          <div className="flex gap-2">
            <button className="px-3 py-1.5 text-xs font-bold bg-orange-50 text-orange-600 rounded-lg">All</button>
            <button className="px-3 py-1.5 text-xs font-bold text-slate-500 hover:bg-gray-50 rounded-lg">Flagged</button>
            <button className="px-3 py-1.5 text-xs font-bold text-slate-500 hover:bg-gray-50 rounded-lg">Awaiting Proof</button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="bg-gray-50 text-slate-400 text-[10px] font-bold uppercase tracking-wider">
                <th className="px-6 py-4">Transaction ID</th>
                <th className="px-6 py-4">Parties</th>
                <th className="px-6 py-4">Amount</th>
                <th className="px-6 py-4">Method</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4">Date</th>
                <th className="px-6 py-4 text-center">Receipt Proof</th>
                <th className="px-6 py-4">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {transactions.map((tx) => (
                <tr key={tx.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 font-mono font-semibold text-slate-600">{tx.id}</td>
                  <td className="px-6 py-4">
                    <div className="flex flex-col">
                      <span className="font-bold text-slate-900">{tx.customer}</span>
                      <span className="text-xs text-slate-400">to {tx.vendor}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 font-bold text-slate-900">{tx.amount}</td>
                  <td className="px-6 py-4">
                    <span className="text-slate-500">{tx.method}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 px-2 py-1 rounded-lg text-xs font-bold ${
                      tx.status === 'Completed' ? 'bg-green-50 text-green-700' : 
                      tx.status === 'Flagged' ? 'bg-red-50 text-red-700 animate-pulse' : 'bg-orange-50 text-orange-700'
                    }`}>
                      {tx.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-xs text-slate-400">{tx.date}</td>
                  <td className="px-6 py-4 text-center">
                    {tx.proof ? (
                      <button className="text-orange-600 hover:bg-orange-50 p-2 rounded-lg transition-colors inline-flex items-center gap-1 font-bold">
                        <FileText size={16} />
                        View
                      </button>
                    ) : (
                      <span className="text-slate-300 text-xs">—</span>
                    )}
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex gap-2">
                      <button className="p-2 text-slate-400 hover:text-blue-600"><Eye size={16} /></button>
                      <button className="p-2 text-slate-400 hover:text-green-600"><CheckCircle size={16} /></button>
                    </div>
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

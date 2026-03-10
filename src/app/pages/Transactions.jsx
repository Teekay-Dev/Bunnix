import React, { useState } from 'react';
import { Download, RefreshCw, Search, CreditCard, AlertTriangle, CheckCircle } from 'lucide-react';

export default function Transactions() {
  const [txList, setTxList] = useState([]);

  const [tab, setTab] = useState('All');
  const [q,   setQ]   = useState('');

  const list = txList.filter(t => {
    const mT = tab === 'All'
      || (tab === 'Flagged'       && t.status === 'Flagged')
      || (tab === 'Awaiting Proof' && t.status === 'Pending Verification');
    const mQ = !q
      || t.id?.toLowerCase().includes(q.toLowerCase())
      || t.customer?.toLowerCase().includes(q.toLowerCase());
    return mT && mQ;
  });

  const pending  = txList.filter(t => t.status === 'Pending Verification' || t.status === 'Awaiting Payment').length;
  const flagged  = txList.filter(t => t.status === 'Flagged').length;
  const bankPct  = txList.length ? Math.round(txList.filter(t => t.method?.includes('Bank')).length / txList.length * 100) : 0;

  const confirmTx = (id) => {
    setTxList(p => p.map(t => t.id === id ? { ...t, status: 'Confirmed' } : t));
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-start justify-between mb-6 flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">Transaction Monitoring</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Track manual payment confirmations and handle disputes.</p>
        </div>
        <div className="flex items-center gap-2">
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-gray-600 dark:text-gray-400 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 hover:border-orange-400 transition-colors">
            <Download size={14}/> Export CSV
          </button>
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold text-white bg-orange-500 hover:bg-orange-600 transition-colors shadow-sm shadow-orange-200 dark:shadow-orange-900/20">
            <RefreshCw size={14}/> Manual Reconciliation
          </button>
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center">
              <CreditCard size={18} className="text-blue-600 dark:text-blue-400"/>
            </div>
            <span className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider">Total Volume (MTD)</span>
          </div>
          <div className="text-2xl font-black text-gray-900 dark:text-gray-100">{txList.length === 0 ? '₦0' : '—'}</div>
          <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">{txList.length} transactions total</div>
        </div>

        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-xl bg-amber-50 dark:bg-amber-500/10 flex items-center justify-center">
              <AlertTriangle size={18} className="text-amber-600 dark:text-amber-400"/>
            </div>
            <span className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider">Pending Review</span>
          </div>
          <div className="text-2xl font-black text-gray-900 dark:text-gray-100">{pending + flagged}</div>
          <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">Requires admin attention</div>
        </div>

        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <p className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider mb-4">Payment Methods</p>
          {[
            { l:'Bank Transfer',   p: bankPct,       color:'bg-orange-500' },
            { l:'Cash / Physical', p: 100 - bankPct, color:'bg-gray-300 dark:bg-white/10' },
          ].map(m => (
            <div key={m.l} className="mb-3">
              <div className="flex justify-between text-xs font-semibold text-gray-600 dark:text-gray-400 mb-1.5">
                <span>{m.l}</span><span>{m.p}%</span>
              </div>
              <div className="h-1.5 bg-gray-100 dark:bg-white/5 rounded-full overflow-hidden">
                <div className={`h-full rounded-full transition-all ${m.color}`} style={{ width:`${m.p}%` }}/>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Table */}
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl shadow-sm overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100 dark:border-white/8">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500" size={15}/>
            <input
              type="search" value={q} onChange={e => setQ(e.target.value)}
              placeholder="Filter by Transaction ID or customer…" autoComplete="off"
              className="w-full max-w-sm pl-9 pr-4 py-2 bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 rounded-xl text-sm outline-none focus:ring-2 focus:ring-orange-500/20 border border-transparent focus:border-orange-300 dark:focus:border-orange-600 transition-all"
            />
          </div>
        </div>

        {/* Filter tabs */}
        <div className="flex gap-0 border-b border-gray-100 dark:border-white/8 px-5">
          {['All','Flagged','Awaiting Proof'].map(t => (
            <button key={t} onClick={() => setTab(t)}
              className={`py-2.5 px-4 text-sm font-medium border-b-2 transition-all -mb-px ${
                tab === t
                  ? 'text-orange-600 dark:text-orange-400 border-orange-500'
                  : 'text-gray-500 dark:text-gray-400 border-transparent hover:text-gray-800 dark:hover:text-gray-200'
              }`}
            >{t}</button>
          ))}
        </div>

        {list.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 text-center">
            <div className="text-4xl mb-3">💳</div>
            <p className="font-semibold text-sm text-gray-700 dark:text-gray-300 mb-1">No transactions yet</p>
            <p className="text-xs text-gray-400 dark:text-gray-500">Transactions from your platform will appear here.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-100 dark:border-white/8">
                  {['ID','CUSTOMER','AMOUNT','METHOD','STATUS','DATE','RECEIPT','ACTIONS'].map(h => (
                    <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider uppercase">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {list.map(t => (
                  <tr key={t.id} className="border-b border-gray-100 dark:border-white/8 hover:bg-gray-50 dark:hover:bg-white/5/50 transition-colors">
                    <td className="py-3.5 px-4 font-mono text-xs font-semibold text-gray-700 dark:text-gray-300">{t.id}</td>
                    <td className="py-3.5 px-4 text-sm font-medium text-gray-800 dark:text-gray-200">{t.customer}</td>
                    <td className="py-3.5 px-4 text-sm font-bold text-gray-900 dark:text-gray-100">{t.amount}</td>
                    <td className="py-3.5 px-4">
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${t.method?.includes('Bank') ? 'bg-blue-50 dark:bg-blue-500/10 text-blue-600 dark:text-blue-400' : 'bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400'}`}>
                        {t.method}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className={`text-xs font-bold px-2.5 py-1 rounded-full flex items-center gap-1 w-fit ${
                        t.status === 'Confirmed'           ? 'bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400' :
                        t.status === 'Processing'          ? 'bg-blue-50 dark:bg-blue-500/10 text-blue-600 dark:text-blue-400' :
                        t.status === 'Flagged'             ? 'bg-red-50 dark:bg-red-500/10 text-red-600 dark:text-red-400' :
                        t.status === 'Awaiting Payment'    ? 'bg-gray-100 dark:bg-white/5 text-gray-500 dark:text-gray-400' :
                                                             'bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400'
                      }`}>
                        <span className="w-1.5 h-1.5 rounded-full bg-current"/>{t.status}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-xs text-gray-400 dark:text-gray-500">{t.date}</td>
                    <td className="py-3.5 px-4 text-xs">
                      {t.hasReceipt
                        ? <span className="text-green-600 dark:text-green-400 font-semibold flex items-center gap-1"><CheckCircle size={12}/> Uploaded</span>
                        : <span className="text-gray-400 dark:text-gray-500">—</span>
                      }
                    </td>
                    <td className="py-3.5 px-4">
                      {(t.status === 'Pending Verification' || t.status === 'Flagged') && (
                        <button onClick={() => confirmTx(t.id)} className="px-3 py-1.5 rounded-lg text-xs font-bold bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400 border border-green-200 dark:border-green-800 hover:bg-green-100 dark:hover:bg-green-900/30 transition-colors">
                          Confirm
                        </button>
                      )}
                      {t.status === 'Confirmed' && <span className="text-xs text-gray-400 dark:text-gray-500">Settled</span>}
                      {(t.status === 'Processing' || t.status === 'Awaiting Payment') && (
                        <button className="px-3 py-1.5 rounded-lg text-xs font-semibold text-gray-600 dark:text-gray-400 border border-gray-200 dark:border-white/8 hover:border-orange-400 transition-colors">
                          Review
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

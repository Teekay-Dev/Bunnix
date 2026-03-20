// src/app/pages/Transactions.jsx
import React, { useState, useEffect } from 'react';
import { Download, Search, Star, MessageSquare } from 'lucide-react';
import { db } from '../../firebase';
import { collection, onSnapshot } from 'firebase/firestore'; // Added onSnapshot

export default function Transactions() {
  const [reviewsList, setReviewsList] = useState([]);
  const [loading, setLoading] = useState(true);

  // --- REAL-TIME DATA FETCHING ---
  useEffect(() => {
    const unsub = onSnapshot(collection(db, 'reviews'), (snapshot) => {
      const list = snapshot.docs.map(doc => {
        const data = doc.data();
        return {
          id: doc.id,
          customer: data.customerId || 'Unknown User',
          vendor: data.vendorId || 'Unknown Vendor',
          rating: data.rating || 0,
          comment: data.comment || 'No comment',
          date: data.timestamp?.toDate().toLocaleDateString() || 'N/A',
          status: 'Confirmed'
        };
      });
      setReviewsList(list);
      setLoading(false);
    });

    return () => unsub();
  }, []);

  const [tab, setTab] = useState('All');
  const [q,   setQ]   = useState('');

  // ... [Keep existing Filter logic] ...
  const list = reviewsList.filter(t => {
    const mT = tab === 'All' || (tab === '5 Star' && t.rating === 5);
    const mQ = !q || t.id?.toLowerCase().includes(q.toLowerCase()) || t.customer?.toLowerCase().includes(q.toLowerCase());
    return mT && mQ;
  });

  // ... [Keep existing JSX Return] ...
  return (
    <div>
      {/* Header */}
      <div className="flex items-start justify-between mb-6 flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">Reviews & Receipts</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Monitor platform feedback and transaction slips.</p>
        </div>
        <div className="flex items-center gap-2">
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold text-gray-600 dark:text-gray-400 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 hover:border-orange-400 transition-colors">
            <Download size={14}/> Export CSV
          </button>
        </div>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center">
              <MessageSquare size={18} className="text-blue-600 dark:text-blue-400"/>
            </div>
            <span className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider">Total Reviews</span>
          </div>
          <div className="text-2xl font-black text-gray-900 dark:text-gray-100">{reviewsList.length}</div>
          <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">All time feedback</div>
        </div>

        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 shadow-sm">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-9 h-9 rounded-xl bg-amber-50 dark:bg-amber-500/10 flex items-center justify-center">
              <Star size={18} className="text-amber-600 dark:text-amber-400"/>
            </div>
            <span className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider">Average Rating</span>
          </div>
          <div className="text-2xl font-black text-gray-900 dark:text-gray-100">
            {reviewsList.length > 0 ? (reviewsList.reduce((a, b) => a + b.rating, 0) / reviewsList.length).toFixed(1) : 0} ⭐
          </div>
          <div className="text-xs text-gray-400 dark:text-gray-500 mt-1">Platform health</div>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl shadow-sm overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100 dark:border-white/8">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500" size={15}/>
            <input
              type="search" value={q} onChange={e => setQ(e.target.value)}
              placeholder="Search by Customer ID..." autoComplete="off"
              className="w-full max-w-sm pl-9 pr-4 py-2 bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 rounded-xl text-sm outline-none focus:ring-2 focus:ring-orange-500/20 border border-transparent focus:border-orange-300 dark:focus:border-orange-600 transition-all"
            />
          </div>
        </div>

        {list.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 text-center">
            <div className="text-4xl mb-3">⭐</div>
            <p className="font-semibold text-sm text-gray-700 dark:text-gray-300 mb-1">No reviews yet</p>
            <p className="text-xs text-gray-400 dark:text-gray-500">Reviews from customers will appear here.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-100 dark:border-white/8">
                  {['REVIEW ID','CUSTOMER','VENDOR','RATING','COMMENT','DATE'].map(h => (
                    <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider uppercase">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {list.map(t => (
                  <tr key={t.id} className="border-b border-gray-100 dark:border-white/8 hover:bg-gray-50 dark:hover:bg-white/5/50 transition-colors">
                    <td className="py-3.5 px-4 font-mono text-xs font-semibold text-gray-700 dark:text-gray-300">{t.id.slice(0,8)}...</td>
                    <td className="py-3.5 px-4 text-sm font-medium text-gray-800 dark:text-gray-200">{t.customer}</td>
                    <td className="py-3.5 px-4 text-sm font-medium text-gray-800 dark:text-gray-200">{t.vendor}</td>
                    <td className="py-3.5 px-4">
                      <span className="flex items-center gap-1 text-amber-500 font-bold">
                        <Star size={12} fill="#f59e0b" stroke="#f59e0b"/> {t.rating}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-xs text-gray-500 dark:text-gray-400 max-w-xs truncate">{t.comment}</td>
                    <td className="py-3.5 px-4 text-xs text-gray-400 dark:text-gray-500">{t.date}</td>
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
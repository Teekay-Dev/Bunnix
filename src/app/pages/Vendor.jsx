import React from 'react';
import { 
  Search, 
  MapPin, 
  Calendar, 
  ExternalLink, 
  ShieldCheck, 
  Clock, 
  CheckCircle2, 
  XCircle,
  Briefcase
} from 'lucide-react';
import { motion } from 'motion/react';

const pendingVendors = [
  { 
    id: 1, 
    businessName: 'Tech Hub Solutions', 
    owner: 'Samuel Okafor', 
    category: 'Electronics & Repair', 
    location: 'Lagos, NG', 
    date: 'Feb 18, 2026',
    documents: ['ID Card', 'Business Reg', 'Bank Proof']
  },
  { 
    id: 2, 
    businessName: 'Green Groceries', 
    owner: 'Fatima Yusuf', 
    category: 'Food & Organic', 
    location: 'Abuja, NG', 
    date: 'Feb 19, 2026',
    documents: ['ID Card', 'Tax Clearance']
  },
  { 
    id: 3, 
    businessName: 'Urban Stylist', 
    owner: 'James Carter', 
    category: 'Professional Services', 
    location: 'Port Harcourt, NG', 
    date: 'Feb 20, 2026',
    documents: ['ID Card', 'Certifications']
  }
];

export function Vendors() {
  return (
    <div className="space-y-8">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Vendor Approvals</h1>
          <p className="text-slate-500">Verify and onboard new businesses to the Bunnix ecosystem.</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="bg-orange-50 text-orange-700 px-4 py-2 rounded-xl border border-orange-100 flex items-center gap-2">
            <Clock size={18} />
            <span className="font-semibold">{pendingVendors.length} Pending Verifications</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6">
        {pendingVendors.map((vendor, i) => (
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.1 }}
            key={vendor.id} 
            className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden flex flex-col md:flex-row"
          >
            <div className="p-6 flex-1 border-r border-gray-100">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-4">
                  <div className="w-14 h-14 bg-orange-100 rounded-2xl flex items-center justify-center text-orange-600">
                    <Briefcase size={28} />
                  </div>
                  <div>
                    <h3 className="text-lg font-bold text-slate-900">{vendor.businessName}</h3>
                    <div className="flex items-center gap-3 text-sm text-slate-500 mt-1">
                      <span className="flex items-center gap-1"><MapPin size={14} /> {vendor.location}</span>
                      <span className="flex items-center gap-1"><Calendar size={14} /> Applied {vendor.date}</span>
                    </div>
                  </div>
                </div>
                <span className="px-3 py-1 bg-gray-100 text-slate-600 text-xs font-bold rounded-full uppercase">
                  {vendor.category}
                </span>
              </div>

              <div className="mt-6 grid grid-cols-2 gap-4">
                <div className="p-4 bg-gray-50 rounded-xl">
                  <p className="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">Owner Details</p>
                  <p className="font-semibold text-slate-800">{vendor.owner}</p>
                </div>
                <div className="p-4 bg-gray-50 rounded-xl">
                  <p className="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">Verification Status</p>
                  <div className="flex items-center gap-1.5 text-orange-600 font-bold text-sm">
                    <ShieldCheck size={16} />
                    Document Review
                  </div>
                </div>
              </div>
            </div>

            <div className="w-full md:w-80 p-6 bg-slate-50/50 flex flex-col justify-between">
              <div>
                <p className="text-xs text-slate-400 uppercase font-bold tracking-wider mb-3">Submitted Files</p>
                <div className="space-y-2">
                  {vendor.documents.map(doc => (
                    <div key={doc} className="flex items-center justify-between p-2 bg-white rounded-lg border border-gray-100 shadow-sm text-sm group cursor-pointer hover:border-orange-300 transition-colors">
                      <span className="text-slate-600 font-medium">{doc}.pdf</span>
                      <ExternalLink size={14} className="text-slate-400 group-hover:text-orange-500" />
                    </div>
                  ))}
                </div>
              </div>

              <div className="mt-6 grid grid-cols-2 gap-3">
                <button className="flex items-center justify-center gap-2 py-2.5 rounded-xl border border-red-200 text-red-600 font-bold hover:bg-red-50 transition-colors">
                  <XCircle size={18} />
                  Reject
                </button>
                <button className="flex items-center justify-center gap-2 py-2.5 rounded-xl bg-green-600 text-white font-bold hover:bg-green-700 transition-colors shadow-lg shadow-green-100">
                  <CheckCircle2 size={18} />
                  Approve
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Stats Section */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
          <p className="text-slate-500 text-sm font-medium">Average Approval Time</p>
          <h3 className="text-2xl font-bold text-slate-900 mt-1">14.2 Hours</h3>
          <p className="text-green-600 text-xs mt-1 font-bold">↑ 8% faster than last month</p>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
          <p className="text-slate-500 text-sm font-medium">Top Growth Category</p>
          <h3 className="text-2xl font-bold text-slate-900 mt-1">Food Services</h3>
          <p className="text-slate-400 text-xs mt-1 font-bold">12 new vendors this week</p>
        </div>
        <div className="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm">
          <p className="text-slate-500 text-sm font-medium">Rejection Rate</p>
          <h3 className="text-2xl font-bold text-slate-900 mt-1">4.5%</h3>
          <p className="text-slate-400 text-xs mt-1 font-bold">Main reason: Invalid ID</p>
        </div>
      </div>
    </div>
  );
}

import React, { useState, useEffect } from 'react';
import {
  Package,
  Wrench,
  Flag,
  Eye,
  Trash2,
  CheckCircle,
  XCircle,
  ArrowLeft,
  Search,
  Filter,
  ShoppingBag,
  Scissors,
  AlertTriangle,
  LayoutGrid,
} from 'lucide-react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';
import { db } from '../../firebase'; // Import your Firebase config
import { collection, onSnapshot, doc, updateDoc, deleteDoc } from 'firebase/firestore';

function cn(...inputs) { return twMerge(clsx(inputs)); }

// ── Empty state component ────────────────────────────
function EmptyState({ icon: Icon, title, desc }) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center">
      <div className="w-16 h-16 rounded-2xl bg-gray-100 dark:bg-white/5 flex items-center justify-center mb-4">
        <Icon size={28} className="text-gray-400 dark:text-gray-500" />
      </div>
      <p className="font-semibold text-gray-700 dark:text-gray-300 mb-1">{title}</p>
      <p className="text-sm text-gray-400 dark:text-gray-500 max-w-xs">{desc}</p>
    </div>
  );
}

// ── Stat card ────────────────────────────────────────
function StatCard({ icon: Icon, label, value, color, onClick, active }) {
  return (
    <button
      onClick={onClick}
      className={cn(
        "w-full text-left p-5 rounded-2xl border transition-all duration-200 hover:shadow-lg hover:-translate-y-0.5 group",
        active
          ? "border-orange-400 dark:border-orange-500 bg-orange-50 dark:bg-orange-500/10 shadow-lg shadow-orange-100 dark:shadow-orange-900/20"
          : "border-gray-200 dark:border-white/8 bg-white dark:bg-[#111] hover:border-orange-300 dark:hover:border-orange-700"
      )}
    >
      <div className={cn("w-10 h-10 rounded-xl flex items-center justify-center mb-3", color.bg)}>
        <Icon size={20} className={color.icon} />
      </div>
      <div className={cn("text-2xl font-black mb-1", active ? "text-orange-600 dark:text-orange-400" : "text-gray-900 dark:text-gray-100")}>
        {value}
      </div>
      <div className={cn("text-sm font-semibold", active ? "text-orange-600 dark:text-orange-400" : "text-gray-500 dark:text-gray-400")}>
        {label}
      </div>
    </button>
  );
}

// ── Product / Service row ────────────────────────────
function ItemRow({ item, onFlag, onRemove, onApprove }) {
  return (
    <tr className="border-b border-gray-100 dark:border-white/8 hover:bg-gray-50 dark:hover:bg-white/5/50 transition-colors">
      <td className="py-3.5 px-4">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gray-100 dark:bg-white/5 flex items-center justify-center text-lg shrink-0">
            {item.type === 'service' ? '✂️' : '🛍'}
          </div>
          <div>
            <p className="font-semibold text-sm text-gray-900 dark:text-gray-100">{item.name}</p>
            <p className="text-xs text-gray-400 dark:text-gray-500">{item.category || 'Uncategorized'}</p>
          </div>
        </div>
      </td>
      <td className="py-3.5 px-4 text-sm text-gray-600 dark:text-gray-300">{item.vendorName || 'Unknown Vendor'}</td>
      <td className="py-3.5 px-4">
        <span className={cn(
          "text-xs font-bold px-2.5 py-1 rounded-full",
          item.type === 'service'
            ? "bg-purple-50 dark:bg-purple-500/10 text-purple-600 dark:text-purple-400"
            : "bg-blue-50 dark:bg-blue-500/10 text-blue-600 dark:text-blue-400"
        )}>
          {item.type ? item.type.toUpperCase() : 'PRODUCT'}
        </span>
      </td>
      <td className="py-3.5 px-4 text-sm font-semibold text-gray-900 dark:text-gray-100">₦{item.price?.toLocaleString() || 0}</td>
      <td className="py-3.5 px-4">
        <span className={cn(
          "text-xs font-bold px-2.5 py-1 rounded-full flex items-center gap-1 w-fit",
          item.status === 'Active'
            ? "bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400"
            : item.status === 'Pending'
            ? "bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400"
            : "bg-red-50 dark:bg-red-500/10 text-red-600 dark:text-red-400"
        )}>
          <span className="w-1.5 h-1.5 rounded-full bg-current" />
          {item.status || 'Active'}
        </span>
      </td>
      <td className="py-3.5 px-4">
        <div className="flex items-center gap-2">
          <button title="View" className="p-1.5 rounded-lg text-gray-400 dark:text-gray-500 hover:text-gray-700 dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-white/5 transition-colors">
            <Eye size={15} />
          </button>
          {item.status === 'Pending' && (
            <button onClick={() => onApprove(item.id)} title="Approve" className="p-1.5 rounded-lg text-gray-400 dark:text-gray-500 hover:text-green-600 dark:hover:text-green-400 hover:bg-green-50 dark:hover:bg-green-900/20 transition-colors">
              <CheckCircle size={15} />
            </button>
          )}
          <button onClick={() => onFlag(item.id, item.status)} title={item.status === 'Flagged' ? 'Unflag' : 'Flag'} className={cn("p-1.5 rounded-lg transition-colors", item.status === 'Flagged' ? "text-orange-500 dark:text-orange-400 bg-orange-50 dark:bg-orange-500/10" : "text-gray-400 dark:text-gray-500 hover:text-orange-500 dark:hover:text-orange-400 hover:bg-orange-50 dark:hover:bg-orange-900/20")}>
            <Flag size={15} />
          </button>
          <button onClick={() => onRemove(item.id)} title="Remove" className="p-1.5 rounded-lg text-gray-400 dark:text-gray-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors">
            <Trash2 size={15} />
          </button>
        </div>
      </td>
    </tr>
  );
}

// ── Sub page: listing table ──────────────────────────
function ListingPage({ title, icon: Icon, color, items, onFlag, onRemove, onApprove, onBack, filterType }) {
  const [q, setQ] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('All');

  // Extract unique categories from items
  const categories = ['All', ...new Set(items.map(i => i.category).filter(Boolean))];

  const filtered = items.filter(i => {
    const matchesSearch = i.name?.toLowerCase().includes(q.toLowerCase()) ||
                          i.vendorName?.toLowerCase().includes(q.toLowerCase());
    const matchesCategory = categoryFilter === 'All' || i.category === categoryFilter;
    
    // If this is a specific view (Products/Services), filter by type as well
    const matchesType = !filterType || i.type === filterType;

    return matchesSearch && matchesCategory && matchesType;
  });

  return (
    <div>
      {/* Back + header */}
      <div className="flex items-center gap-3 mb-6">
        <button
          onClick={onBack}
          className="p-2 rounded-xl text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-gray-100 transition-colors"
        >
          <ArrowLeft size={18} />
        </button>
        <div className={cn("w-9 h-9 rounded-xl flex items-center justify-center", color.bg)}>
          <Icon size={18} className={color.icon} />
        </div>
        <div>
          <h2 className="text-xl font-black text-gray-900 dark:text-gray-100">{title}</h2>
          <p className="text-xs text-gray-400 dark:text-gray-500">{filtered.length} item{filtered.length !== 1 ? 's' : ''}</p>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap items-center gap-3 mb-4">
        {/* Search */}
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 dark:text-gray-500" size={15} />
          <input
            type="search"
            value={q}
            onChange={e => setQ(e.target.value)}
            placeholder={`Search ${title.toLowerCase()}…`}
            autoComplete="off"
            className="w-full pl-9 pr-4 py-2 bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 rounded-xl text-sm outline-none focus:ring-2 focus:ring-orange-500/20 border border-transparent focus:border-orange-300 dark:focus:border-orange-600 transition-all"
          />
        </div>

        {/* Category Filter */}
        <div className="relative">
          <Filter size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <select 
            value={categoryFilter} 
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="pl-8 pr-4 py-2 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/10 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 outline-none focus:border-orange-400 appearance-none cursor-pointer"
          >
            {categories.map(cat => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl overflow-hidden shadow-sm">
        {filtered.length === 0 ? (
          <EmptyState
            icon={Icon}
            title={`No ${title.toLowerCase()} yet`}
            desc={`${title} listed by vendors on the platform will appear here.`}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-100 dark:border-white/8">
                  {['ITEM', 'VENDOR', 'TYPE', 'PRICE', 'STATUS', 'ACTIONS'].map(h => (
                    <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider uppercase">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map(item => (
                  <ItemRow key={item.id} item={item} onFlag={onFlag} onRemove={onRemove} onApprove={onApprove} />
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

// ════════════════════════════════════════════════════
// MAIN ContentControl PAGE
// ════════════════════════════════════════════════════
export default function ContentControl() {
  const [view, setView] = useState('overview');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  // --- REAL-TIME DATA FETCHING ---
  useEffect(() => {
    const unsubscribe = onSnapshot(collection(db, 'products'), (snapshot) => {
      const data = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data(),
        // Defaults for missing fields to prevent crashes
        name: doc.data().name || 'Unnamed Item',
        vendorName: doc.data().vendorName || 'Unknown',
        category: doc.data().category || 'General',
        status: doc.data().status || 'Active',
        type: doc.data().type || 'product'
      }));
      setItems(data);
      setLoading(false);
    }, (error) => {
      console.error("Error fetching products:", error);
      setLoading(false);
    });

    return () => unsubscribe(); // Cleanup listener on unmount
  }, []);

  // --- LOGIC: FILTERS ---
  // Note: In the specific views, we pass a filterType prop to ListingPage
  const products = items.filter(i => i.type === 'product');
  const services = items.filter(i => i.type === 'service');
  const flagged  = items.filter(i => i.status === 'Flagged');
  const pending  = items.filter(i => i.status === 'Pending');

  // --- LOGIC: ACTIONS ---
  const handleFlag = async (id, currentStatus) => {
    const newStatus = currentStatus === 'Flagged' ? 'Active' : 'Flagged';
    try {
      await updateDoc(doc(db, 'products', id), { status: newStatus });
    } catch (err) {
      console.error("Error updating status:", err);
    }
  };

  const handleRemove = async (id) => {
    if(window.confirm("Are you sure you want to delete this item?")) {
      try {
        await deleteDoc(doc(db, 'products', id));
      } catch (err) {
        console.error("Error deleting item:", err);
      }
    }
  };

  const handleApprove = async (id) => {
    try {
      await updateDoc(doc(db, 'products', id), { status: 'Active' });
    } catch (err) {
      console.error("Error approving item:", err);
    }
  };

  // ── SUB-VIEWS ROUTING ──────────────────────────────
  if (view === 'products') {
    return (
      <ListingPage
        title="Products"
        icon={ShoppingBag}
        color={{ bg: 'bg-blue-50 dark:bg-blue-500/10', icon: 'text-blue-600 dark:text-blue-400' }}
        items={products}
        filterType="product" // Ensure only products show
        onFlag={handleFlag}
        onRemove={handleRemove}
        onApprove={handleApprove}
        onBack={() => setView('overview')}
      />
    );
  }

  if (view === 'services') {
    return (
      <ListingPage
        title="Services"
        icon={Scissors}
        color={{ bg: 'bg-purple-50 dark:bg-purple-500/10', icon: 'text-purple-600 dark:text-purple-400' }}
        items={services}
        filterType="service"
        onFlag={handleFlag}
        onRemove={handleRemove}
        onApprove={handleApprove}
        onBack={() => setView('overview')}
      />
    );
  }

  if (view === 'flagged') {
    return (
      <ListingPage
        title="Flagged Items"
        icon={AlertTriangle}
        color={{ bg: 'bg-red-50 dark:bg-red-500/10', icon: 'text-red-600 dark:text-red-400' }}
        items={flagged}
        onFlag={handleFlag}
        onRemove={handleRemove}
        onApprove={handleApprove}
        onBack={() => setView('overview')}
      />
    );
  }

  // ── OVERVIEW (DEFAULT) ──────────────────────────
  return (
    <div>
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">Content Control</h1>
        <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">
          Monitor and moderate all products and services on the platform.
        </p>
      </div>

      {/* ── STAT CARDS ── */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard
          icon={LayoutGrid}
          label="All Listings"
          value={items.length}
          color={{ bg: 'bg-gray-100 dark:bg-white/5', icon: 'text-gray-600 dark:text-gray-400' }}
          onClick={() => {}}
          active={false}
        />
        <StatCard
          icon={ShoppingBag}
          label="Products"
          value={products.length}
          color={{ bg: 'bg-blue-50 dark:bg-blue-500/10', icon: 'text-blue-600 dark:text-blue-400' }}
          onClick={() => setView('products')}
          active={false}
        />
        <StatCard
          icon={Scissors}
          label="Services"
          value={services.length}
          color={{ bg: 'bg-purple-50 dark:bg-purple-500/10', icon: 'text-purple-600 dark:text-purple-400' }}
          onClick={() => setView('services')}
          active={false}
        />
        <StatCard
          icon={AlertTriangle}
          label="Flagged"
          value={flagged.length}
          color={{ bg: 'bg-red-50 dark:bg-red-500/10', icon: 'text-red-600 dark:text-red-400' }}
          onClick={() => setView('flagged')}
          active={false}
        />
      </div>

      {/* ── PENDING ALERT ── */}
      {pending.length > 0 && (
        <div className="mb-6 p-4 bg-amber-50 dark:bg-amber-500/10 border border-amber-200 dark:border-amber-500/20 rounded-2xl flex items-center justify-between">
          <div className="flex items-center gap-3">
            <AlertTriangle size={18} className="text-amber-600 dark:text-amber-400 shrink-0" />
            <span className="text-sm font-semibold text-amber-700 dark:text-amber-300">
              {pending.length} listing{pending.length > 1 ? 's' : ''} pending approval
            </span>
          </div>
          <button 
            onClick={() => setView('products')} // Ideally go to a 'Pending' view, but products is close enough
            className="text-xs font-bold text-amber-600 hover:text-amber-800"
          >
            Review Now →
          </button>
        </div>
      )}

      {/* ── QUICK ACCESS CARDS ── */}
      <h2 className="text-sm font-bold text-gray-400 dark:text-gray-500 uppercase tracking-wider mb-4">Quick Access</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

        {/* Products card */}
        <button
          onClick={() => setView('products')}
          className="group text-left p-6 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl hover:border-blue-400 dark:hover:border-blue-500 hover:shadow-xl hover:shadow-blue-100 dark:hover:shadow-blue-900/30 hover:-translate-y-1 transition-all duration-200"
        >
          <div className="w-12 h-12 rounded-2xl bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
            <ShoppingBag size={24} className="text-blue-600 dark:text-blue-400" />
          </div>
          <h3 className="font-bold text-gray-900 dark:text-gray-100 mb-1">Products</h3>
          <p className="text-sm text-gray-400 dark:text-gray-500 mb-3">
            View, approve and moderate physical products listed by vendors.
          </p>
          <div className="flex items-center justify-between">
            <span className="text-2xl font-black text-blue-600 dark:text-blue-400">{products.length}</span>
            <span className="text-xs font-semibold text-blue-500 dark:text-blue-400 bg-blue-50 dark:bg-blue-500/10 px-3 py-1 rounded-full">
              View All →
            </span>
          </div>
        </button>

        {/* Services card */}
        <button
          onClick={() => setView('services')}
          className="group text-left p-6 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl hover:border-purple-400 dark:hover:border-purple-500 hover:shadow-xl hover:shadow-purple-100 dark:hover:shadow-purple-900/30 hover:-translate-y-1 transition-all duration-200"
        >
          <div className="w-12 h-12 rounded-2xl bg-purple-50 dark:bg-purple-500/10 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
            <Scissors size={24} className="text-purple-600 dark:text-purple-400" />
          </div>
          <h3 className="font-bold text-gray-900 dark:text-gray-100 mb-1">Services</h3>
          <p className="text-sm text-gray-400 dark:text-gray-500 mb-3">
            Review and manage service offerings from vendors on the platform.
          </p>
          <div className="flex items-center justify-between">
            <span className="text-2xl font-black text-purple-600 dark:text-purple-400">{services.length}</span>
            <span className="text-xs font-semibold text-purple-500 dark:text-purple-400 bg-purple-50 dark:bg-purple-500/10 px-3 py-1 rounded-full">
              View All →
            </span>
          </div>
        </button>

        {/* Flagged card */}
        <button
          onClick={() => setView('flagged')}
          className="group text-left p-6 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl hover:border-red-400 dark:hover:border-red-500 hover:shadow-xl hover:shadow-red-100 dark:hover:shadow-red-900/30 hover:-translate-y-1 transition-all duration-200"
        >
          <div className="w-12 h-12 rounded-2xl bg-red-50 dark:bg-red-500/10 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
            <AlertTriangle size={24} className="text-red-600 dark:text-red-400" />
          </div>
          <h3 className="font-bold text-gray-900 dark:text-gray-100 mb-1">Flagged Items</h3>
          <p className="text-sm text-gray-400 dark:text-gray-500 mb-3">
            Items reported or automatically flagged for review and moderation.
          </p>
          <div className="flex items-center justify-between">
            <span className="text-2xl font-black text-red-600 dark:text-red-400">{flagged.length}</span>
            <span className={cn(
              "text-xs font-semibold px-3 py-1 rounded-full",
              flagged.length > 0
                ? "text-red-500 dark:text-red-400 bg-red-50 dark:bg-red-500/10"
                : "text-gray-400 dark:text-gray-500 bg-gray-100 dark:bg-white/5"
            )}>
              {flagged.length > 0 ? 'Needs Review →' : 'All Clear'}
            </span>
          </div>
        </button>
      </div>
    </div>
  );
}
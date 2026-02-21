import React from 'react';
import { 
  Package, 
  Search, 
  Trash2, 
  AlertCircle, 
  Eye, 
  Filter, 
  MoreVertical,
  Star,
  Layers,
  ShoppingBag,
  Wrench
} from 'lucide-react';

const products = [
  { id: 1, name: 'Premium Leather Boots', vendor: 'Fashionista Hub', category: 'Products', price: '₦45,000', stock: 12, rating: 4.8, status: 'Active' },
  { id: 2, name: 'AC Repair Service', vendor: 'Cooling Pros', category: 'Services', price: '₦15,000', stock: 'Unlimited', rating: 4.5, status: 'Active' },
  { id: 3, name: 'iPhone 15 Pro Max', vendor: 'Tech Central', category: 'Products', price: '₦1,200,000', stock: 3, rating: 5.0, status: 'Under Review' },
  { id: 4, name: 'Wedding Photography', vendor: 'Flash Studio', category: 'Services', price: '₦250,000', stock: 'By Booking', rating: 4.9, status: 'Active' },
  { id: 5, name: 'Smart Watch Series 9', vendor: 'Gizmo Store', category: 'Products', price: '₦85,000', stock: 0, rating: 4.2, status: 'Out of Stock' },
  { id: 6, name: 'Home Cleaning', vendor: 'Pure Services', category: 'Services', price: '₦25,000', stock: 'Unlimited', rating: 4.6, status: 'Active' },
];

export function Inventory() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Content Moderation</h1>
          <p className="text-slate-500">Monitor products and services listed on the marketplace.</p>
        </div>
        <div className="flex items-center gap-2 bg-gray-100 p-1 rounded-xl">
          <button className="flex items-center gap-2 px-4 py-2 bg-white rounded-lg text-sm font-bold text-orange-600 shadow-sm">
            <ShoppingBag size={16} />
            Products
          </button>
          <button className="flex items-center gap-2 px-4 py-2 text-sm font-bold text-slate-500 hover:text-slate-700 transition-colors">
            <Wrench size={16} />
            Services
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-indigo-600 text-white p-6 rounded-2xl shadow-lg shadow-indigo-100 relative overflow-hidden group">
          <div className="relative z-10">
            <p className="opacity-80 text-sm font-medium">Total Listings</p>
            <h3 className="text-3xl font-bold mt-1">12,840</h3>
            <p className="text-xs mt-4 bg-white/20 inline-block px-2 py-1 rounded-lg">+145 this week</p>
          </div>
          <Layers className="absolute -bottom-4 -right-4 w-24 h-24 opacity-10 group-hover:scale-110 transition-transform duration-500" />
        </div>
        <div className="bg-orange-600 text-white p-6 rounded-2xl shadow-lg shadow-orange-100 relative overflow-hidden group">
          <div className="relative z-10">
            <p className="opacity-80 text-sm font-medium">Flagged for Review</p>
            <h3 className="text-3xl font-bold mt-1">18</h3>
            <p className="text-xs mt-4 bg-white/20 inline-block px-2 py-1 rounded-lg">Requires moderation</p>
          </div>
          <AlertCircle className="absolute -bottom-4 -right-4 w-24 h-24 opacity-10 group-hover:scale-110 transition-transform duration-500" />
        </div>
        <div className="bg-slate-900 text-white p-6 rounded-2xl shadow-lg shadow-slate-200 relative overflow-hidden group">
          <div className="relative z-10">
            <p className="opacity-80 text-sm font-medium">Total Vendors</p>
            <h3 className="text-3xl font-bold mt-1">2,410</h3>
            <p className="text-xs mt-4 bg-white/20 inline-block px-2 py-1 rounded-lg">88% active rate</p>
          </div>
          <Package className="absolute -bottom-4 -right-4 w-24 h-24 opacity-10 group-hover:scale-110 transition-transform duration-500" />
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-200 shadow-sm">
        <div className="p-4 border-b border-gray-100 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input 
              type="text" 
              placeholder="Search by product name, SKU or vendor..."
              className="w-full pl-10 pr-4 py-2 bg-gray-50 border border-gray-100 rounded-xl text-sm focus:ring-2 focus:ring-orange-500/20 outline-none"
            />
          </div>
          <button className="flex items-center gap-2 px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm font-semibold text-slate-600 hover:bg-gray-100 transition-colors">
            <Filter size={18} />
            Advanced Filters
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-gray-50 text-slate-400 text-xs font-bold uppercase tracking-wider">
                <th className="px-6 py-4">Item Details</th>
                <th className="px-6 py-4">Vendor</th>
                <th className="px-6 py-4">Category</th>
                <th className="px-6 py-4">Price</th>
                <th className="px-6 py-4 text-center">Stock/Units</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {products.map((item) => (
                <tr key={item.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-lg bg-gray-100 flex items-center justify-center text-slate-400">
                        {item.category === 'Products' ? <ShoppingBag size={20} /> : <Wrench size={20} />}
                      </div>
                      <div>
                        <p className="text-sm font-bold text-slate-900">{item.name}</p>
                        <div className="flex items-center gap-1 mt-0.5">
                          <Star size={10} className="fill-yellow-400 text-yellow-400" />
                          <span className="text-[10px] text-slate-500 font-bold">{item.rating}</span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm font-medium text-slate-600">{item.vendor}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`text-[10px] font-bold uppercase px-2 py-0.5 rounded-full ${
                      item.category === 'Products' ? 'bg-blue-50 text-blue-600' : 'bg-pink-50 text-pink-600'
                    }`}>
                      {item.category}
                    </span>
                  </td>
                  <td className="px-6 py-4 font-bold text-slate-900 text-sm">{item.price}</td>
                  <td className="px-6 py-4 text-center">
                    <span className={`text-sm font-semibold ${item.stock === 0 ? 'text-red-500' : 'text-slate-600'}`}>
                      {item.stock}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 rounded-lg text-xs font-bold ${
                      item.status === 'Active' ? 'bg-green-50 text-green-700' : 
                      item.status === 'Under Review' ? 'bg-orange-50 text-orange-700' : 'bg-red-50 text-red-700'
                    }`}>
                      {item.status}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center justify-center gap-1">
                      <button className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"><Eye size={16} /></button>
                      <button className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"><Trash2 size={16} /></button>
                      <button className="p-2 text-slate-400 hover:text-slate-600 hover:bg-gray-100 rounded-lg transition-all"><MoreVertical size={16} /></button>
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

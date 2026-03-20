// src/app/pages/Users.jsx
import React, { useState, useEffect } from 'react';
import { db } from '../../firebase';
import { collection, query, where, updateDoc, deleteDoc, doc, onSnapshot } from 'firebase/firestore';
import toast from 'react-hot-toast'; // Import Toast
import {
  Users as UsersIcon, Store, UserCheck, Trash2,
  ArrowLeft, Search, CheckCircle, XCircle,
  Clock, ShieldCheck, Briefcase
} from 'lucide-react';

// --- Helper Components ---

const AV_COLORS = ['#E85D04','#2563eb','#7c3aed','#16a34a','#d97706','#0891b2'];
function avColor(name = '') {
  let h = 0; for (const c of name) h = (h + c.charCodeAt(0)) % AV_COLORS.length;
  return AV_COLORS[h];
}
function initials(name = '') {
  return name.split(' ').filter(Boolean).map(x => x[0]).join('').slice(0, 2).toUpperCase() || '?';
}

function StatCard({ icon: Icon, label, value, bg, color, onClick }) {
  return (
    <button onClick={onClick}
      className="w-full text-left bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-5 transition-all duration-200 cursor-pointer hover:shadow-lg hover:-translate-y-0.5 hover:border-orange-300 dark:hover:border-orange-700 group">
      <div className={`w-11 h-11 rounded-xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform ${bg}`}>
        <Icon size={20} className={color} />
      </div>
      <div className={`text-3xl font-black mb-1 ${color}`}>{value}</div>
      <div className="text-sm font-semibold text-gray-600 dark:text-gray-400">{label}</div>
    </button>
  );
}

function Empty({ icon: Icon, title, desc }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-14 h-14 rounded-2xl bg-gray-100 dark:bg-white/5 flex items-center justify-center mb-4">
        <Icon size={24} className="text-gray-400 dark:text-gray-500" />
      </div>
      <p className="font-bold text-sm text-gray-700 dark:text-gray-300 mb-1">{title}</p>
      <p className="text-xs text-gray-400 dark:text-gray-500">{desc}</p>
    </div>
  );
}

function Badge({ status }) {
  const map = {
    Active:    'bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400',
    Approved:  'bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400',
    Pending:   'bg-amber-50 dark:bg-amber-500/10 text-amber-600 dark:text-amber-400',
    Rejected:  'bg-red-50 dark:bg-red-500/10 text-red-500 dark:text-red-400',
    Suspended: 'bg-red-50 dark:bg-red-500/10 text-red-500 dark:text-red-400',
  };
  return (
    <span className={`text-xs font-bold px-2.5 py-1 rounded-full flex items-center gap-1 w-fit ${map[status] || 'bg-gray-100 dark:bg-white/5 text-gray-500'}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current" />{status}
    </span>
  );
}

function Btn({ onClick, icon: Icon, label, danger, success }) {
  return (
    <button onClick={onClick} title={label}
      className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-bold border transition-colors
        ${danger  ? 'text-red-500 border-red-200 dark:border-red-800/50 hover:bg-red-50 dark:hover:bg-red-900/20'
        : success ? 'text-green-600 border-green-200 dark:border-green-800/50 hover:bg-green-50 dark:hover:bg-green-900/20'
        :           'text-gray-500 border-gray-200 dark:border-white/8 hover:bg-gray-100 dark:hover:bg-white/5'}`}>
      <Icon size={12} />{label}
    </button>
  );
}

function PageHeader({ onBack, icon: Icon, bg, color, title, count }) {
  return (
    <div className="flex items-center gap-3 mb-6">
      <button onClick={onBack} className="p-2 rounded-xl text-gray-500 hover:bg-gray-100 dark:hover:bg-white/5 transition-colors">
        <ArrowLeft size={18} />
      </button>
      <div className={`w-9 h-9 rounded-xl flex items-center justify-center ${bg}`}>
        <Icon size={18} className={color} />
      </div>
      <div>
        <h2 className="text-xl font-black text-gray-900 dark:text-gray-100">{title}</h2>
        <p className="text-xs text-gray-400 dark:text-gray-500">{count} total</p>
      </div>
    </div>
  );
}

function SearchBar({ value, onChange, placeholder }) {
  return (
    <div className="relative mb-4 max-w-sm">
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={14} />
      <input type="search" value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder}
        className="w-full pl-9 pr-4 py-2 bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 placeholder-gray-400 rounded-xl text-sm outline-none border border-transparent focus:border-orange-400 focus:ring-2 focus:ring-orange-500/10 transition-all" />
    </div>
  );
}

// --- Page Components ---

// 1. All Users Page
function AllUsersPage({ users, onSuspend, onDelete, onBack }) {
  const [q, setQ] = useState('');
  const list = users.filter(u => !q || u.name?.toLowerCase().includes(q.toLowerCase()) || u.email?.toLowerCase().includes(q.toLowerCase()));
  return (
    <div>
      <PageHeader onBack={onBack} icon={UsersIcon} bg="bg-blue-50 dark:bg-blue-500/10" color="text-blue-600 dark:text-blue-400" title="All Users" count={users.length} />
      <SearchBar value={q} onChange={setQ} placeholder="Search users…" />
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl overflow-hidden shadow-sm">
        {list.length === 0 ? <Empty icon={UsersIcon} title="No users yet" desc="Registered users will appear here." /> : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead><tr className="border-b border-gray-100 dark:border-white/5">
                {['USER','EMAIL','STATUS','JOINED','ACTIONS'].map(h => <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider">{h}</th>)}
              </tr></thead>
              <tbody>{list.map(u => (
                <tr key={u.id} className="border-b border-gray-100 dark:border-white/5 hover:bg-gray-50 dark:hover:bg-white/3 transition-colors">
                  <td className="py-3.5 px-4">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold shrink-0" style={{ background: avColor(u.name) }}>{initials(u.name)}</div>
                      <span className="font-semibold text-sm text-gray-900 dark:text-gray-100">{u.name}</span>
                    </div>
                  </td>
                  <td className="py-3.5 px-4 text-sm text-gray-500 dark:text-gray-400">{u.email}</td>
                  <td className="py-3.5 px-4"><Badge status={u.status} /></td>
                  <td className="py-3.5 px-4 text-xs text-gray-400 dark:text-gray-500">{u.joined}</td>
                  <td className="py-3.5 px-4"><div className="flex items-center gap-2">
                    <Btn onClick={() => onSuspend(u.id, u.status)} icon={u.status === 'Suspended' ? CheckCircle : XCircle} label={u.status === 'Suspended' ? 'Reactivate' : 'Suspend'} danger={u.status !== 'Suspended'} success={u.status === 'Suspended'} />
                    <Btn onClick={() => onDelete(u.id)} icon={Trash2} label="Delete" danger />
                  </div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

// 2. Vendor Verification Page
function VendorVerificationPage({ vendors, onApprove, onReject, onDelete, onBack }) {
  const [q, setQ] = useState('');
  const list = vendors.filter(v => !q || v.name?.toLowerCase().includes(q.toLowerCase()));
  return (
    <div>
      <PageHeader onBack={onBack} icon={Store} bg="bg-orange-50 dark:bg-orange-500/10" color="text-orange-600 dark:text-orange-400" title="Vendor Verification" count={vendors.length} />
      <SearchBar value={q} onChange={setQ} placeholder="Search pending vendors…" />
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl overflow-hidden shadow-sm">
        {list.length === 0 ? <Empty icon={Store} title="No pending vendors" desc="Vendor applications will appear here." /> : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead><tr className="border-b border-gray-100 dark:border-white/5">
                {['VENDOR','EMAIL','CATEGORY','APPLIED','ACTIONS'].map(h => <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider">{h}</th>)}
              </tr></thead>
              <tbody>{list.map(v => (
                <tr key={v.id} className="border-b border-gray-100 dark:border-white/5 hover:bg-gray-50 dark:hover:bg-white/3 transition-colors">
                  <td className="py-3.5 px-4">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-xl bg-orange-50 dark:bg-orange-500/10 flex items-center justify-center text-base shrink-0">🏪</div>
                      <span className="font-semibold text-sm text-gray-900 dark:text-gray-100">{v.name}</span>
                    </div>
                  </td>
                  <td className="py-3.5 px-4 text-sm text-gray-500 dark:text-gray-400">{v.email}</td>
                  <td className="py-3.5 px-4">{v.category && <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-orange-50 dark:bg-orange-500/10 text-orange-600 dark:text-orange-400">{v.category}</span>}</td>
                  <td className="py-3.5 px-4 text-xs text-gray-400 dark:text-gray-500"><span className="flex items-center gap-1"><Clock size={11}/>{v.appliedDate}</span></td>
                  <td className="py-3.5 px-4"><div className="flex items-center gap-2">
                    <Btn onClick={() => onApprove(v.id)} icon={CheckCircle} label="Verify" success />
                    <Btn onClick={() => onReject(v.id)}  icon={XCircle}     label="Reject"  danger />
                    <Btn onClick={() => onDelete(v.id)}  icon={Trash2}      label="Delete"  danger />
                  </div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

// 3. All Vendors Page
function AllVendorsPage({ vendors, onSuspend, onDelete, onBack }) {
  const [q, setQ] = useState('');
  const list = vendors.filter(v => !q || v.name?.toLowerCase().includes(q.toLowerCase()));
  
  return (
    <div>
      <PageHeader onBack={onBack} icon={Briefcase} bg="bg-green-50 dark:bg-green-500/10" color="text-green-600 dark:text-green-400" title="All Active Vendors" count={vendors.length} />
      <SearchBar value={q} onChange={setQ} placeholder="Search vendors…" />
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl overflow-hidden shadow-sm">
        {list.length === 0 ? <Empty icon={Briefcase} title="No active vendors" desc="Approved vendors will appear here." /> : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead><tr className="border-b border-gray-100 dark:border-white/5">
                {['VENDOR','EMAIL','CATEGORY','STATUS','ACTIONS'].map(h => <th key={h} className="text-left py-3 px-4 text-[11px] font-bold text-gray-400 dark:text-gray-500 tracking-wider uppercase">{h}</th>)}
              </tr></thead>
              <tbody>{list.map(v => (
                <tr key={v.id} className="border-b border-gray-100 dark:border-white/5 hover:bg-gray-50 dark:hover:bg-white/3 transition-colors">
                  <td className="py-3.5 px-4">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-xl bg-green-50 dark:bg-green-500/10 flex items-center justify-center text-base shrink-0">🏪</div>
                      <span className="font-semibold text-sm text-gray-900 dark:text-gray-100">{v.name}</span>
                    </div>
                  </td>
                  <td className="py-3.5 px-4 text-sm text-gray-500 dark:text-gray-400">{v.email}</td>
                  <td className="py-3.5 px-4">{v.category && <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-green-50 dark:bg-green-500/10 text-green-600 dark:text-green-400">{v.category}</span>}</td>
                  <td className="py-3.5 px-4"><Badge status={v.status} /></td>
                  <td className="py-3.5 px-4"><div className="flex items-center gap-2">
                    <Btn onClick={() => onSuspend(v.id, v.status)} icon={v.status === 'Suspended' ? CheckCircle : XCircle} label={v.status === 'Suspended' ? 'Reactivate' : 'Suspend'} danger={v.status !== 'Suspended'} success={v.status === 'Suspended'} />
                    <Btn onClick={() => onDelete(v.id)} icon={Trash2} label="Delete" danger />
                  </div></td>
                </tr>
              ))}</tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

// ════════════════════════════════════════════════════
// MAIN COMPONENT
// ════════════════════════════════════════════════════
export default function Users() {
  const [view, setView] = useState('overview');
  const [users, setUsers] = useState([]);
  const [pendingVendors, setPendingVendors] = useState([]);
  const [activeVendors, setActiveVendors] = useState([]);

  // --- REAL-TIME LISTENERS ---
  useEffect(() => {
    const unsubUsers = onSnapshot(collection(db, 'users'), (snapshot) => {
      const usersList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data(), name: doc.data().name || 'No Name', status: doc.data().status || 'Active', joined: doc.data().createdAt?.toDate().toLocaleDateString() || 'N/A' }));
      setUsers(usersList);
    });

    const pendingQuery = query(collection(db, 'vendorProfiles'), where('status', '==', 'pending'));
    const unsubPending = onSnapshot(pendingQuery, (snapshot) => {
      const pendingList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data(), name: doc.data().companyName || 'Unknown', appliedDate: doc.data().createdAt?.toDate().toLocaleDateString() || 'N/A' }));
      setPendingVendors(pendingList);
    });

    const activeQuery = query(collection(db, 'vendorProfiles'), where('status', '!=', 'pending'));
    const unsubActive = onSnapshot(activeQuery, (snapshot) => {
      const activeList = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data(), name: doc.data().companyName || 'Unknown', status: doc.data().status || 'Active', joined: doc.data().createdAt?.toDate().toLocaleDateString() || 'N/A' }));
      setActiveVendors(activeList);
    });

    return () => { unsubUsers(); unsubPending(); unsubActive(); };
  }, []);

  // --- LOGIC: Users ---
  const suspendUser = async (id, currentStatus) => {
    const newStatus = currentStatus === 'Suspended' ? 'Active' : 'Suspended';
    const actionText = newStatus === 'Suspended' ? 'Suspended' : 'Reactivated';
    try {
      await updateDoc(doc(db, 'users', id), { status: newStatus });
      toast.success(`User has been ${actionText}`);
    } catch (err) {
      toast.error("Failed to update user");
    }
  };

  const deleteUser = async (id) => {
    if(window.confirm('Delete this user permanently?')) {
      try {
        await deleteDoc(doc(db, 'users', id));
        toast.success("User deleted successfully");
      } catch (err) {
        toast.error("Failed to delete user");
      }
    }
  };

  // --- LOGIC: Vendors ---
  const approveVendor = async (id) => {
    const vendor = pendingVendors.find(v => v.id === id);
    const businessName = vendor?.name || 'Vendor';
    try {
      await updateDoc(doc(db, 'vendorProfiles', id), { status: 'Active' });
      toast.success(`${businessName} has been Verified!`);
    } catch (err) {
      toast.error("Failed to verify vendor");
    }
  };

  const rejectVendor = async (id) => {
    const vendor = pendingVendors.find(v => v.id === id);
    const businessName = vendor?.name || 'Vendor';
    if(window.confirm(`Are you sure you want to reject ${businessName}?`)) {
      try {
        await updateDoc(doc(db, 'vendorProfiles', id), { status: 'rejected' });
        toast(`${businessName} has been Rejected`, { icon: '🚫' });
      } catch (err) {
        toast.error("Failed to reject vendor");
      }
    }
  };

  const deletePendingVendor = async (id) => {
    if(window.confirm('Delete this vendor request?')) {
      try {
        await deleteDoc(doc(db, 'vendorProfiles', id));
        toast.success("Vendor request deleted");
      } catch (err) {
        toast.error("Failed to delete request");
      }
    }
  };

  const suspendVendor = async (id, currentStatus) => {
    const newStatus = currentStatus === 'Suspended' ? 'Active' : 'Suspended';
    const vendor = activeVendors.find(v => v.id === id);
    const businessName = vendor?.name || 'Vendor';
    try {
      await updateDoc(doc(db, 'vendorProfiles', id), { status: newStatus });
      toast.success(`${businessName} has been ${newStatus}`);
    } catch (err) {
      toast.error("Failed to update vendor");
    }
  };

  const deleteActiveVendor = async (id) => {
    if(window.confirm('Delete this vendor permanently?')) {
      try {
        await deleteDoc(doc(db, 'vendorProfiles', id));
        toast.success("Vendor deleted permanently");
      } catch (err) {
        toast.error("Failed to delete vendor");
      }
    }
  };

  // --- ROUTING ---
  if (view === 'all') return <AllUsersPage users={users} onSuspend={suspendUser} onDelete={deleteUser} onBack={() => setView('overview')} />;
  if (view === 'vendorQueue') return <VendorVerificationPage vendors={pendingVendors} onApprove={approveVendor} onReject={rejectVendor} onDelete={deletePendingVendor} onBack={() => setView('overview')} />;
  if (view === 'allVendors') return <AllVendorsPage vendors={activeVendors} onSuspend={suspendVendor} onDelete={deleteActiveVendor} onBack={() => setView('overview')} />;

  const activeUsers = users.filter(u => u.status === 'Active').length;
  const activeVendorsCount = activeVendors.filter(v => v.status === 'Active').length;

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">User Management</h1>
        <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Manage users and verify vendors.</p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon={UsersIcon}      label="All Users"        value={users.length}          bg="bg-blue-50 dark:bg-blue-500/10"   color="text-blue-600 dark:text-blue-400"     onClick={() => setView('all')} />
        <StatCard icon={ShieldCheck} label="Active Users"     value={activeUsers}           bg="bg-green-50 dark:bg-green-500/10" color="text-green-600 dark:text-green-400"   onClick={() => setView('all')} />
        <StatCard icon={Store}      label="Vendor Queue"     value={pendingVendors.length} bg="bg-orange-50 dark:bg-orange-500/10" color="text-orange-600 dark:text-orange-400" onClick={() => setView('vendorQueue')} />
        <StatCard icon={Briefcase}  label="Active Vendors"   value={activeVendorsCount}    bg="bg-purple-50 dark:bg-purple-500/10" color="text-purple-600 dark:text-purple-400" onClick={() => setView('allVendors')} />
      </div>

      <h2 className="text-xs font-bold text-gray-400 dark:text-gray-500 uppercase tracking-widest mb-4">Quick Access</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {[
          { view:'all', icon:UsersIcon, bg:'bg-blue-50 dark:bg-blue-500/10', color:'text-blue-600 dark:text-blue-400', border:'hover:border-blue-400', title:'All Users', desc:'View, suspend and manage all registered users.', value:users.length },
          { view:'vendorQueue', icon:Store, bg:'bg-orange-50 dark:bg-orange-500/10', color:'text-orange-600 dark:text-orange-400', border:'hover:border-orange-400', title:'Vendor Queue', desc:'Verify new vendor applications.', value:pendingVendors.length },
          { view:'allVendors', icon:Briefcase, bg:'bg-purple-50 dark:bg-purple-500/10', color:'text-purple-600 dark:text-purple-400', border:'hover:border-purple-400', title:'Manage Vendors', desc:'Suspend or delete active vendors.', value:activeVendors.length },
        ].map(c => (
          <button key={c.view} onClick={() => setView(c.view)}
            className={`group text-left p-6 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl hover:shadow-xl hover:-translate-y-1 transition-all duration-200 ${c.border}`}>
            <div className={`w-12 h-12 rounded-2xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform ${c.bg}`}>
              <c.icon size={22} className={c.color} />
            </div>
            <h3 className="font-bold text-gray-900 dark:text-gray-100 mb-1">{c.title}</h3>
            <p className="text-sm text-gray-400 dark:text-gray-500 mb-4">{c.desc}</p>
            <div className="flex items-center justify-between">
              <span className={`text-2xl font-black ${c.color}`}>{c.value}</span>
              <span className={`text-xs font-bold px-3 py-1 rounded-full ${c.bg} ${c.color}`}>View All →</span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
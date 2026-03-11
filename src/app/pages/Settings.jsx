import React, { useState } from 'react';
import { Globe, Bell, Shield, RefreshCw, Map, ChevronRight, UserPlus } from 'lucide-react';
import { db } from '../../firebase';
import { collection, query, where, getDocs, setDoc, doc } from 'firebase/firestore';
import toast from 'react-hot-toast';

const TABS = [
  { key:'general',       label:'General',       Icon: Globe },
  { key:'notifications', label:'Notifications', Icon: Bell },
  { key:'security',      label:'Security',      Icon: Shield },
  { key:'admins',        label:'Admin Users',   Icon: UserPlus },
  { key:'backend',       label:'Backend Sync',  Icon: RefreshCw },
  { key:'regional',      label:'Regional',      Icon: Map },
];

function SettingRow({ title, desc, right, onClick }) {
  return (
    <div className={`flex items-center justify-between py-4 border-b border-gray-100 dark:border-white/8 last:border-0 ${onClick ? 'cursor-pointer hover:bg-gray-50 dark:hover:bg-white/5/50 -mx-6 px-6 transition-colors rounded-xl' : ''}`} onClick={onClick}>
      <div>
        <p className="text-sm font-semibold text-gray-800 dark:text-gray-200">{title}</p>
        {desc && <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">{desc}</p>}
      </div>
      <div className="flex items-center gap-2 shrink-0 ml-4">
        <span className="text-sm text-gray-500 dark:text-gray-400 font-medium">{right}</span>
        {onClick && <ChevronRight size={15} className="text-gray-400 dark:text-gray-500"/>}
      </div>
    </div>
  );
}

function ToggleRow({ title, desc, on, onToggle }) {
  return (
    <div className="flex items-center justify-between py-4 border-b border-gray-100 dark:border-white/8 last:border-0">
      <div>
        <p className="text-sm font-semibold text-gray-800 dark:text-gray-200">{title}</p>
        {desc && <p className="text-xs text-gray-400 dark:text-gray-500 mt-0.5">{desc}</p>}
      </div>
      <button onClick={onToggle} className={`relative rounded-full transition-colors shrink-0 ml-4 ${on ? 'bg-orange-500' : 'bg-gray-200 dark:bg-white/10'}`} style={{ width: 40, height: 22 }}>
        <span className={`absolute top-0.5 bg-white rounded-full shadow transition-all`} style={{ width: 18, height: 18, top: 2, left: on ? 20 : 2 }}/>
      </button>
    </div>
  );
}

function SectionTitle({ children }) {
  return (
    <div className="flex items-center gap-2 mb-4 pb-3 border-b border-gray-100 dark:border-white/8">
      <span className="font-bold text-base text-gray-900 dark:text-gray-100">{children}</span>
    </div>
  );
}

export default function Settings() {
  const [tab,     setTab]     = useState('general');
  const [notifs,  setNotifs]  = useState({ email:true, push:true, sms:false, weekly:true });
  const [security, setSec]    = useState({ twofa:true, ipWhite:false });
  const [saved,   setSaved]   = useState(false);
  
  // Admin State
  const [newAdminEmail, setNewAdminEmail] = useState('');
  const [isAdding, setIsAdding] = useState(false); // Loading state

  const save = () => { setSaved(true); setTimeout(() => setSaved(false), 2500); };

  // --- IMPROVED LOGIC: ADD ADMIN BY EMAIL ---
  const handleAddAdmin = async () => {
    const emailToAdd = newAdminEmail.trim().toLowerCase(); // Clean input

    if(!emailToAdd) {
      toast.error("Please enter an email address.");
      return;
    }

    setIsAdding(true); // Start loading

    try {
      console.log(`Searching for user: ${emailToAdd}`);

      // 1. Search 'users' collection for this email
      const usersRef = collection(db, 'users');
      const q = query(usersRef, where('email', '==', emailToAdd));
      const snapshot = await getDocs(q);

      if (snapshot.empty) {
        toast.error("User not found. They must sign up first.");
        console.error("No user found with email:", emailToAdd);
        setIsAdding(false);
        return;
      }

      // 2. Get the User ID (UID)
      const userDoc = snapshot.docs[0];
      const userId = userDoc.id;
      console.log(`Found User ID: ${userId}`);

      // 3. Add to 'admins' collection
      await setDoc(doc(db, 'admins', userId), {
        email: emailToAdd,
        role: 'admin',
        addedAt: new Date()
      });

      toast.success(`${emailToAdd} is now an Admin!`);
      setNewAdminEmail(''); // Clear input
      
    } catch (err) {
      console.error("Error adding admin:", err);
      toast.error("Something went wrong. Check console.");
    } finally {
      setIsAdding(false); // Stop loading
    }
  };

  return (
    <div>
      {/* Header */}
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">System Settings</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Configure global platform rules and security parameters.</p>
        </div>
        <button onClick={save} className={`px-5 py-2 rounded-xl text-sm font-bold text-white transition-all ${saved ? 'bg-green-500' : 'bg-orange-500 hover:bg-orange-600'}`}>
          {saved ? '✓ Saved!' : 'Save Changes'}
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-5">
        {/* Sub-nav */}
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-2 h-fit shadow-sm">
          {TABS.map(({ key, label, Icon }) => (
            <button key={key} onClick={() => setTab(key)}
              className={`flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm transition-colors ${
                tab === key
                  ? 'bg-orange-50 dark:bg-orange-500/10 text-orange-600 dark:text-orange-400 font-semibold'
                  : 'text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-800 dark:hover:text-gray-200'
              }`}>
              <Icon size={15} className="shrink-0"/> {label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="lg:col-span-3 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-6 shadow-sm">

          {tab === 'general' && (
            <>
              <SectionTitle>🗂 Platform Configuration</SectionTitle>
              <SettingRow title="Marketplace Fees"        desc="Set percentage commission" right="10%"              onClick={save}/>
              <SettingRow title="Vendor Payout Threshold" desc="Minimum balance"         right="₦5,000"           onClick={save}/>
            </>
          )}

          {tab === 'admins' && (
            <>
              <SectionTitle>👤 Admin Management</SectionTitle>
              <p className="text-sm text-gray-500 mb-4">Grant admin access to a user by their registered email address.</p>
              
              <div className="flex gap-2 mb-4">
                <input 
                  type="email" 
                  placeholder="Enter User Email (e.g. user@example.com)" 
                  value={newAdminEmail} 
                  onChange={(e) => setNewAdminEmail(e.target.value)}
                  className="flex-1 px-3 py-2 rounded-xl text-sm outline-none border bg-gray-100 dark:bg-white/5 border-transparent focus:border-orange-400"
                />
                <button 
                  onClick={handleAddAdmin} 
                  disabled={isAdding}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold text-white transition-colors ${isAdding ? 'bg-gray-400' : 'bg-orange-500 hover:bg-orange-600'}`}
                >
                  {isAdding ? 'Adding...' : <><UserPlus size={14}/> Add Admin</>}
                </button>
              </div>
              
              <div className="mt-6 border-t border-gray-100 dark:border-white/8 pt-4">
                 <p className="text-xs text-gray-400">Note: The user must already be registered in the 'users' database before you can make them an admin.</p>
              </div>
            </>
          )}

          {tab === 'notifications' && (
            <>
              <SectionTitle>🔔 Notification Preferences</SectionTitle>
              <ToggleRow title="Email Notifications"   desc="Send admin alerts"   on={notifs.email}  onToggle={() => setNotifs(n => ({...n, email:!n.email}))}/>
              <ToggleRow title="Push Notifications"    desc="Mobile alerts"         on={notifs.push}   onToggle={() => setNotifs(n => ({...n, push:!n.push}))}/>
            </>
          )}
          
          {tab === 'security' && (
            <>
              <SectionTitle>🔒 Security & Access</SectionTitle>
              <ToggleRow title="Two-Factor Authentication" desc="Require TOTP"          on={security.twofa}    onToggle={() => setSec(s => ({...s, twofa:!s.twofa}))}/>
            </>
          )}

          {tab === 'backend' && (
            <>
              <SectionTitle>🔄 Backend Sync Settings</SectionTitle>
              <SettingRow title="Database Region"  desc="Primary region"               right="af-south-1"  onClick={save}/>
            </>
          )}

          {tab === 'regional' && (
            <>
              <SectionTitle>🌍 Regional Settings</SectionTitle>
              <SettingRow title="Base Currency"       desc="Transaction currency"                 right="Nigerian Naira (₦)"              onClick={save}/>
              <SettingRow title="Time Zone"           desc="Portal display"                     right="Africa/Lagos (WAT)"              onClick={save}/>
            </>
          )}

        </div>
      </div>
    </div>
  );
}
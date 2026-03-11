import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router';
import { auth, db } from '../../firebase';
import { updateProfile, updateEmail, updatePassword, EmailAuthProvider, reauthenticateWithCredential } from 'firebase/auth';
import { doc, getDoc, updateDoc } from 'firebase/firestore';
import { Camera, User, Mail, Phone, MapPin, FileText, Lock, Eye, EyeOff, Save, Shield, LogOut, CheckCircle, Briefcase, Trash2 } from 'lucide-react';

function Field({ label, Icon, value, onChange, type='text', placeholder, disabled, hint }) {
  return (
    <div className="mb-4">
      <label className="block text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-1.5">{label}</label>
      <div className="relative">
        {Icon && <Icon size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-orange-500 pointer-events-none"/>}
        <input
          type={type} value={value} onChange={e => onChange(e.target.value)}
          placeholder={placeholder} disabled={disabled}
          className={`w-full ${Icon ? 'pl-9' : 'pl-3'} pr-3 py-2.5 rounded-xl text-sm outline-none transition-all border
            ${disabled
              ? 'bg-gray-100 dark:bg-white/5 text-gray-400 dark:text-gray-500 border-gray-200 dark:border-white/8 cursor-not-allowed'
              : 'bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 border-transparent focus:border-orange-400 dark:focus:border-orange-500 focus:ring-2 focus:ring-orange-500/10'
            }`}
        />
      </div>
      {hint && <p className="text-xs text-gray-400 dark:text-gray-500 mt-1">{hint}</p>}
    </div>
  );
}

function PwField({ label, value, onChange, placeholder }) {
  const [show, setShow] = useState(false);
  return (
    <div className="mb-4">
      <label className="block text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-1.5">{label}</label>
      <div className="relative">
        <Lock size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-orange-500 pointer-events-none"/>
        <input
          type={show ? 'text' : 'password'} value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder}
          className="w-full pl-9 pr-9 py-2.5 rounded-xl text-sm outline-none transition-all border bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 border-transparent focus:border-orange-400 dark:focus:border-orange-500 focus:ring-2 focus:ring-orange-500/10"
        />
        <button type="button" onClick={() => setShow(s => !s)} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-orange-500 transition-colors">
          {show ? <EyeOff size={14}/> : <Eye size={14}/>}
        </button>
      </div>
    </div>
  );
}

function Section({ title, Icon, children, danger }) {
  return (
    <div className={`bg-white dark:bg-[#111] rounded-2xl p-6 mb-4 shadow-sm border ${danger ? 'border-red-200 dark:border-red-900/40' : 'border-gray-200 dark:border-white/8'}`}>
      <div className={`flex items-center gap-2.5 mb-5 pb-4 border-b ${danger ? 'border-red-100 dark:border-red-900/30' : 'border-gray-100 dark:border-white/8'}`}>
        <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${danger ? 'bg-red-50 dark:bg-red-900/20' : 'bg-orange-50 dark:bg-orange-500/10'}`}>
          <Icon size={15} className={danger ? 'text-red-500' : 'text-orange-500'}/>
        </div>
        <span className={`font-bold text-sm ${danger ? 'text-red-600 dark:text-red-400' : 'text-gray-900 dark:text-gray-100'}`}>{title}</span>
      </div>
      {children}
    </div>
  );
}

export default function Profile() {
  const navigate  = useNavigate();
  const fileRef   = useRef(null);
  const currentUser = auth.currentUser;

  const [name, setName]     = useState('');
  const [email, setEmail]    = useState('');
  const [phone, setPhone]    = useState('');
  const [location, setLocation] = useState('');
  const [bio, setBio]      = useState('');
  const [role, setRole]     = useState('ADMIN');
  const [photo, setPhoto]    = useState(null);
  const [curPw,    setCurPw]    = useState('');
  const [newPw,    setNewPw]    = useState('');
  const [confPw,   setConfPw]   = useState('');
  const [saving,   setSaving]   = useState(false);
  const [saved,    setSaved]    = useState(false);
  const [error,    setError]    = useState('');

  useEffect(() => {
    if(currentUser) {
      setName(currentUser.displayName || '');
      setEmail(currentUser.email || '');
      // Fetch extra data from Firestore
      const fetchProfile = async () => {
        const docRef = doc(db, 'users', currentUser.uid);
        const docSnap = await getDoc(docRef);
        if (docSnap.exists()) {
          const data = docSnap.data();
          setPhone(data.phone || '');
          setLocation(data.location || '');
          setBio(data.bio || '');
          setRole(data.role || 'ADMIN');
          setPhoto(data.photoURL || currentUser.photoURL);
        }
      };
      fetchProfile();
    }
  }, [currentUser]);

  const initials = name.split(' ').filter(Boolean).map(x => x[0]).join('').slice(0,2).toUpperCase() || 'AU';

  const saveProfile = async () => {
    if (!name.trim()) return;
    setSaving(true);
    setError('');
    try {
      // 1. Update Firebase Auth Profile
      if (currentUser.displayName !== name) {
        await updateProfile(currentUser, { displayName: name });
      }

      // 2. Update Firestore Document
      const userRef = doc(db, 'users', currentUser.uid);
      await updateDoc(userRef, {
        name,
        phone,
        location,
        bio,
        photoURL: photo
      });

      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    } catch (err) {
      console.error(err);
      setError("Failed to update profile");
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async () => {
    if(!curPw || !newPw || newPw !== confPw) return;
    try {
      const credential = EmailAuthProvider.credential(currentUser.email, curPw);
      await reauthenticateWithCredential(currentUser, credential);
      await updatePassword(currentUser, newPw);
      alert("Password updated successfully!");
      setCurPw(''); setNewPw(''); setConfPw('');
    } catch (err) {
      console.error(err);
      setError("Failed to update password. Check current password.");
    }
  };

  return (
    <div style={{ maxWidth: 760, margin: '0 auto' }}>
      <div className="flex items-start justify-between mb-6">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">My Profile</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Manage your account information.</p>
        </div>
        <button onClick={saveProfile} disabled={saving}
          className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-bold text-white transition-all ${saved ? 'bg-green-500' : 'bg-orange-500 hover:bg-orange-600'} disabled:opacity-60`}>
          {saving ? <><span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"/></> : saved ? <><CheckCircle size={14}/> Saved!</> : <><Save size={14}/> Save Changes</>}
        </button>
      </div>

      {error && <div className="bg-red-100 text-red-700 p-3 rounded-xl mb-4 text-sm font-medium">{error}</div>}

      {/* Photo card */}
      <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-6 mb-4 shadow-sm">
        <div className="flex items-center gap-5 flex-wrap">
          <div className="relative shrink-0">
            <div style={{ width:88, height:88, borderRadius:'50%', overflow:'hidden', border:'3px solid #E85D04', boxShadow:'0 0 0 4px rgba(232,93,4,.15)', background: photo ? 'transparent' : 'linear-gradient(135deg,#E85D04,#c94e03)', display:'flex', alignItems:'center', justifyContent:'center', color:'#fff', fontSize:28, fontWeight:800 }}>
              {photo ? <img src={photo} alt="Profile" style={{ width:'100%', height:'100%', objectFit:'cover' }}/> : initials}
            </div>
            <button onClick={() => fileRef.current?.click()} style={{ position:'absolute', bottom:0, right:0, width:26, height:26, borderRadius:'50%', background:'#E85D04', border:'2px solid white', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
              <Camera size={12} color="#fff"/>
            </button>
            <input ref={fileRef} type="file" accept="image/*" style={{ display:'none' }} onChange={(e) => {
              const file = e.target.files[0];
              if(file) {
                const reader = new FileReader();
                reader.onload = (ev) => setPhoto(ev.target.result);
                reader.readAsDataURL(file);
              }
            }}/>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xl font-black text-gray-900 dark:text-gray-100">{name || 'Admin User'}</p>
            <p className="text-xs font-bold text-orange-500 mt-1 uppercase tracking-wider">{role}</p>
          </div>
        </div>
      </div>

      {/* Personal Info */}
      <Section title="Personal Information" Icon={User}>
        <div className="grid grid-cols-2 gap-x-4">
          <Field label="Full Name"     Icon={User}   value={name}     onChange={setName}     placeholder="Your full name"/>
          <Field label="Email Address" Icon={Mail}   value={email}    onChange={setEmail}    placeholder="admin@bunnix.ng" type="email" disabled/>
          <Field label="Phone Number"  Icon={Phone}  value={phone}    onChange={setPhone}    placeholder="+234 800 000 0000"/>
          <Field label="Location"      Icon={MapPin} value={location} onChange={setLocation} placeholder="Lagos, Nigeria"/>
        </div>
        <div>
          <label className="block text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-1.5">Bio</label>
          <textarea value={bio} onChange={e => setBio(e.target.value)} maxLength={200} rows={3} placeholder="Write a short bio…"
            className="w-full p-3 rounded-xl text-sm outline-none resize-none border bg-gray-100 dark:bg-white/5 text-gray-900 dark:text-gray-100 border-transparent focus:border-orange-400"/>
        </div>
      </Section>

      {/* Change Password */}
      <Section title="Change Password" Icon={Lock}>
        <div className="grid grid-cols-3 gap-x-4">
          <PwField label="Current Password" value={curPw}  onChange={setCurPw}  placeholder="Current password"/>
          <PwField label="New Password"     value={newPw}  onChange={setNewPw}  placeholder="Min. 6 characters"/>
          <PwField label="Confirm Password" value={confPw} onChange={setConfPw} placeholder="Repeat new password"/>
        </div>
        <button onClick={handleChangePassword} disabled={!curPw || !newPw || newPw !== confPw}
          className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold text-white bg-orange-500 hover:bg-orange-600 disabled:opacity-40 disabled:cursor-not-allowed transition-colors">
          <Lock size={13}/> Update Password
        </button>
      </Section>
    </div>
  );
}
import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';
import { auth } from '../../firebase'; 
import { signInWithEmailAndPassword } from 'firebase/auth';

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [pass,  setPass]  = useState('');
  const [show,  setShow]  = useState(false);
  const [busy,  setBusy]  = useState(false);
  const [err,   setErr]   = useState('');

  const submit = async (e) => {
    e.preventDefault();
    if (!email.trim() || !pass.trim()) { setErr('Please fill in all fields.'); return; }
    setErr(''); 
    setBusy(true);

    try {
      await signInWithEmailAndPassword(auth, email, pass);
      navigate('/dashboard');
    } catch (error) {
      console.error("Login Error:", error);
      if (error.code === 'auth/user-not-found' || error.code === 'auth/wrong-password' || error.code === 'auth/invalid-credential') {
        setErr('Invalid email or password.');
      } else if (error.code === 'auth/invalid-email') {
        setErr('Please enter a valid email address.');
      } else {
        setErr('Login failed. Please try again.');
      }
    } finally {
      setBusy(false);
    }
  };

  return (
    <div style={{
      minHeight:'100vh', display:'flex', alignItems:'center', justifyContent:'center', padding:20,
      background:'#f4f5f7', // Light gray background
    }}>
      <div style={{
        width:'100%', maxWidth:360,
        background:'#fff', // White card
        border:'1px solid rgba(0,0,0,.06)', 
        borderRadius:20,
        padding:'44px 36px', 
        boxShadow:'0 10px 40px rgba(0,0,0,.08)', // Soft light shadow
        animation:'slideUp .4s cubic-bezier(.34,1.3,.64,1)',
      }}>
        {/* Logo */}
        <div style={{ display:'flex', flexDirection:'column', alignItems:'center', marginBottom:36 }}>
          <img
            src="/bunnix.png"
            alt="Bunnix"
            style={{
              width:130, height:130,
              objectFit:'contain', display:'block',
              marginBottom:16,
              // Subtle dark drop shadow for white background
              filter:'drop-shadow(0 4px 6px rgba(0,0,0,.1))', 
            }}
          />
          <span style={{ fontSize:26, fontWeight:900, color:'#E85D04', letterSpacing:5 }}>
            BUNNIX
          </span>
        </div>

        {err && (
          <div style={{ marginBottom:16, padding:'10px 14px', borderRadius:10,
            background:'rgba(220,38,38,.06)', border:'1px solid rgba(220,38,38,.15)',
            color:'#b91c1c', fontSize:13, fontWeight:500 }}>{err}</div>
        )}

        <form onSubmit={submit} style={{ display:'flex', flexDirection:'column', gap:12 }}>
          <div style={{ position:'relative' }}>
            <Mail size={15} style={{ position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
              color:'#E85D04', pointerEvents:'none' }} />
            <input type="email" value={email} onChange={e=>setEmail(e.target.value)}
              placeholder="Email address" autoComplete="email"
              style={{ width:'100%', padding:'13px 13px 13px 38px', boxSizing:'border-box',
                background:'#f9fafb', border:'1px solid #e5e7eb', // Light input bg
                borderRadius:10, color:'#111827', fontSize:14, outline:'none' }}
              onFocus={e=>e.target.style.borderColor='#E85D04'}
              onBlur={e=>e.target.style.borderColor='#e5e7eb'} />
          </div>
          <div style={{ position:'relative' }}>
            <Lock size={15} style={{ position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
              color:'#E85D04', pointerEvents:'none' }} />
            <input type={show?'text':'password'} value={pass} onChange={e=>setPass(e.target.value)}
              placeholder="Password" autoComplete="current-password"
              style={{ width:'100%', padding:'13px 40px 13px 38px', boxSizing:'border-box',
                background:'#f9fafb', border:'1px solid #e5e7eb',
                borderRadius:10, color:'#111827', fontSize:14, outline:'none' }}
              onFocus={e=>e.target.style.borderColor='#E85D04'}
              onBlur={e=>e.target.style.borderColor='#e5e7eb'} />
            <button type="button" onClick={()=>setShow(s=>!s)} style={{
              position:'absolute', right:12, top:'50%', transform:'translateY(-50%)',
              background:'none', border:'none', cursor:'pointer',
              color:'#9ca3af', display:'flex', padding:2 }}>
              {show ? <EyeOff size={15}/> : <Eye size={15}/>}
            </button>
          </div>
          <button type="submit" disabled={busy} style={{
            marginTop:8, padding:'14px', background:'#E85D04', border:'none',
            borderRadius:10, color:'#fff', fontSize:15, fontWeight:700,
            cursor:busy?'not-allowed':'pointer', opacity:busy?.7:1 }}
            onMouseEnter={e=>{if(!busy)e.currentTarget.style.background='#c94e03'}}
            onMouseLeave={e=>{e.currentTarget.style.background='#E85D04'}}>
            {busy
              ? <span style={{display:'flex',alignItems:'center',justifyContent:'center',gap:8}}>
                  <span style={{width:14,height:14,border:'2px solid rgba(255,255,255,.3)',
                    borderTopColor:'#fff',borderRadius:'50%',animation:'spin .7s linear infinite',
                    display:'inline-block'}}/>Signing in…</span>
              : 'Sign In'}
          </button>
        </form>
      </div>
      <style>{`
        @keyframes slideUp{from{opacity:0;transform:translateY(24px)}to{opacity:1;transform:translateY(0)}}
        @keyframes spin{to{transform:rotate(360deg)}}
        input::placeholder{color:#9ca3af}
      `}</style>
    </div>
  );
}
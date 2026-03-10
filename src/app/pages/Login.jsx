import React, { useState } from 'react';
import { useNavigate } from 'react-router';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';

export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [pass,  setPass]  = useState('');
  const [show,  setShow]  = useState(false);
  const [busy,  setBusy]  = useState(false);
  const [err,   setErr]   = useState('');

  const submit = (e) => {
    e.preventDefault();
    if (!email.trim() || !pass.trim()) { setErr('Please fill in all fields.'); return; }
    setErr(''); setBusy(true);
    setTimeout(() => { setBusy(false); navigate('/dashboard'); }, 900);
  };

  return (
    <div style={{
      minHeight:'100vh', display:'flex', alignItems:'center', justifyContent:'center', padding:20,
      background:'linear-gradient(145deg,#0a0a0a 0%,#111111 50%,#0f0800 100%)',
    }}>
      <div style={{
        width:'100%', maxWidth:360,
        background:'rgba(255,255,255,.04)', backdropFilter:'blur(24px)',
        border:'1px solid rgba(255,255,255,.08)', borderRadius:20,
        padding:'44px 36px', boxShadow:'0 24px 64px rgba(0,0,0,.7)',
        animation:'slideUp .4s cubic-bezier(.34,1.3,.64,1)',
      }}>
        {/* Logo — just the image as-is, subtle glow only */}
        <div style={{ display:'flex', flexDirection:'column', alignItems:'center', marginBottom:36 }}>
          <img
            src="/bunnix.png"
            alt="Bunnix"
            style={{
              width:130, height:130,
              objectFit:'contain', display:'block',
              marginBottom:16,
              filter:'drop-shadow(0 0 12px rgba(232,93,4,.8)) drop-shadow(0 0 28px rgba(232,93,4,.4))',
            }}
          />
          <span style={{ fontSize:26, fontWeight:900, color:'#E85D04', letterSpacing:5,
            textShadow:'0 0 20px rgba(232,93,4,.4)' }}>BUNNIX</span>
        </div>

        {err && (
          <div style={{ marginBottom:16, padding:'10px 14px', borderRadius:10,
            background:'rgba(220,38,38,.12)', border:'1px solid rgba(220,38,38,.25)',
            color:'#fca5a5', fontSize:13 }}>{err}</div>
        )}

        <form onSubmit={submit} style={{ display:'flex', flexDirection:'column', gap:12 }}>
          <div style={{ position:'relative' }}>
            <Mail size={15} style={{ position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
              color:'#E85D04', pointerEvents:'none' }} />
            <input type="email" value={email} onChange={e=>setEmail(e.target.value)}
              placeholder="Email address" autoComplete="email"
              style={{ width:'100%', padding:'13px 13px 13px 38px', boxSizing:'border-box',
                background:'rgba(255,255,255,.06)', border:'1px solid rgba(255,255,255,.1)',
                borderRadius:10, color:'#fff', fontSize:14, outline:'none' }}
              onFocus={e=>e.target.style.borderColor='#E85D04'}
              onBlur={e=>e.target.style.borderColor='rgba(255,255,255,.1)'} />
          </div>
          <div style={{ position:'relative' }}>
            <Lock size={15} style={{ position:'absolute', left:12, top:'50%', transform:'translateY(-50%)',
              color:'#E85D04', pointerEvents:'none' }} />
            <input type={show?'text':'password'} value={pass} onChange={e=>setPass(e.target.value)}
              placeholder="Password" autoComplete="current-password"
              style={{ width:'100%', padding:'13px 40px 13px 38px', boxSizing:'border-box',
                background:'rgba(255,255,255,.06)', border:'1px solid rgba(255,255,255,.1)',
                borderRadius:10, color:'#fff', fontSize:14, outline:'none' }}
              onFocus={e=>e.target.style.borderColor='#E85D04'}
              onBlur={e=>e.target.style.borderColor='rgba(255,255,255,.1)'} />
            <button type="button" onClick={()=>setShow(s=>!s)} style={{
              position:'absolute', right:12, top:'50%', transform:'translateY(-50%)',
              background:'none', border:'none', cursor:'pointer',
              color:'rgba(255,255,255,.35)', display:'flex', padding:2 }}>
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
        input::placeholder{color:rgba(255,255,255,.22)}
      `}</style>
    </div>
  );
}

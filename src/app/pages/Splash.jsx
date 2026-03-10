import React, { useEffect } from 'react';
import { useNavigate } from 'react-router';

export default function Splash() {
  const navigate = useNavigate();
  useEffect(() => {
    const t = setTimeout(() => navigate('/signin'), 2800);
    return () => clearTimeout(t);
  }, [navigate]);

  return (
    <div style={{
      position:'fixed', inset:0, zIndex:9999,
      display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center',
      background:'linear-gradient(145deg,#0a0a0a 0%,#111111 50%,#0f0800 100%)',
    }}>
      {/* Just the image — no wrapper, no circle, just glow */}
      <div style={{ animation:'splashPop .85s cubic-bezier(.34,1.56,.64,1)' }}>
        <img
          src="/bunnix.png"
          alt="Bunnix"
          style={{
            width:180, height:180,
            objectFit:'contain', display:'block',
            filter:'drop-shadow(0 0 20px rgba(232,93,4,.9)) drop-shadow(0 0 45px rgba(232,93,4,.5))',
          }}
        />
      </div>

      <div style={{ animation:'fadeUp .5s .4s both', marginTop:28 }}>
        <span style={{ fontSize:34, fontWeight:900, color:'#E85D04',
          letterSpacing:7, textShadow:'0 0 24px rgba(232,93,4,.6)' }}>BUNNIX</span>
      </div>
      <p style={{ animation:'fadeUp .5s .55s both', color:'rgba(255,255,255,.3)',
        fontSize:11, letterSpacing:4, marginTop:8, textTransform:'uppercase' }}>Admin Portal</p>

      <div style={{ marginTop:50, width:110, height:2,
        background:'rgba(255,255,255,.07)', borderRadius:99,
        overflow:'hidden', animation:'fadeUp .4s .7s both' }}>
        <div style={{ height:'100%', background:'linear-gradient(90deg,#E85D04,#ff8c00)',
          borderRadius:99, width:0, animation:'load 2.1s .85s ease forwards' }}/>
      </div>

      <style>{`
        @keyframes splashPop{0%{opacity:0;transform:scale(.08) rotate(-10deg)}60%{transform:scale(1.08) rotate(2deg)}100%{opacity:1;transform:scale(1) rotate(0)}}
        @keyframes fadeUp{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:translateY(0)}}
        @keyframes load{from{width:0}to{width:100%}}
      `}</style>
    </div>
  );
}

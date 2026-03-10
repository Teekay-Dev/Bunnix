import React, { useState } from 'react';
import { Globe, Bell, Shield, RefreshCw, Map, ChevronRight } from 'lucide-react';

const TABS = [
  { key:'general',       label:'General',       Icon: Globe },
  { key:'notifications', label:'Notifications', Icon: Bell },
  { key:'security',      label:'Security',      Icon: Shield },
  { key:'backend',       label:'Backend Sync',  Icon: RefreshCw },
  { key:'regional',      label:'Regional',      Icon: Map },
];

function SettingRow({ title, desc, right, onClick }) {
  return (
    <div
      className={`flex items-center justify-between py-4 border-b border-gray-100 dark:border-white/8 last:border-0 ${onClick ? 'cursor-pointer hover:bg-gray-50 dark:hover:bg-white/5/50 -mx-6 px-6 transition-colors rounded-xl' : ''}`}
      onClick={onClick}
    >
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
      <button
        onClick={onToggle}
        className={`relative w-10 h-5.5 rounded-full transition-colors shrink-0 ml-4 ${on ? 'bg-orange-500' : 'bg-gray-200 dark:bg-white/10'}`}
        style={{ width: 40, height: 22 }}
      >
        <span className={`absolute top-0.5 w-4.5 h-4.5 bg-white rounded-full shadow transition-all ${on ? 'left-[18px]' : 'left-0.5'}`}
          style={{ width: 18, height: 18, top: 2, left: on ? 20 : 2 }}
        />
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

  const save = () => { setSaved(true); setTimeout(() => setSaved(false), 2500); };

  return (
    <div>
      {/* Header */}
      <div className="flex items-start justify-between mb-8">
        <div>
          <h1 className="text-2xl font-black text-gray-900 dark:text-gray-100">System Settings</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">Configure global platform rules and security parameters.</p>
        </div>
        <button
          onClick={save}
          className={`px-5 py-2 rounded-xl text-sm font-bold text-white transition-all ${saved ? 'bg-green-500' : 'bg-orange-500 hover:bg-orange-600'}`}
        >
          {saved ? '✓ Saved!' : 'Save Changes'}
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-5">
        {/* Sub-nav */}
        <div className="bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-2 h-fit shadow-sm">
          {TABS.map(({ key, label, Icon }) => (
            <button
              key={key}
              onClick={() => setTab(key)}
              className={`flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm transition-colors ${
                tab === key
                  ? 'bg-orange-50 dark:bg-orange-500/10 text-orange-600 dark:text-orange-400 font-semibold'
                  : 'text-gray-500 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-white/5 hover:text-gray-800 dark:hover:text-gray-200'
              }`}
            >
              <Icon size={15} className="shrink-0"/> {label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="lg:col-span-3 bg-white dark:bg-[#111] border border-gray-200 dark:border-white/8 rounded-2xl p-6 shadow-sm">

          {tab === 'general' && (
            <>
              <SectionTitle>🗂 Platform Configuration</SectionTitle>
              <SettingRow title="Marketplace Fees"        desc="Set percentage commission for products & services" right="10%"              onClick={save}/>
              <SettingRow title="Vendor Payout Threshold" desc="Minimum balance for manual bank transfers"         right="₦5,000"           onClick={save}/>
              <SettingRow title="Default Language"        desc="System-wide default for notifications"             right="English (UK)"     onClick={save}/>
              <div className="my-5 border-t border-gray-100 dark:border-white/8"/>
              <SectionTitle>⚡ API & Integrations</SectionTitle>
              <SettingRow title="Push Notifications"  desc="FCM integration for mobile alerts"            right={<span className="text-green-600 dark:text-green-400 font-bold">Connected</span>} onClick={save}/>
              <SettingRow title="Storage Bucket"      desc="External media hosting for product images"    right="AWS S3"    onClick={save}/>
              <SettingRow title="Payment Webhook"     desc="Bank transfer webhook endpoint"               right="Active"   onClick={save}/>
            </>
          )}

          {tab === 'notifications' && (
            <>
              <SectionTitle>🔔 Notification Preferences</SectionTitle>
              <ToggleRow title="Email Notifications"   desc="Send admin alerts to your registered email"   on={notifs.email}  onToggle={() => setNotifs(n => ({...n, email:!n.email}))}/>
              <ToggleRow title="Push Notifications"    desc="In-app and mobile push notifications"         on={notifs.push}   onToggle={() => setNotifs(n => ({...n, push:!n.push}))}/>
              <ToggleRow title="SMS Alerts"            desc="Critical alerts via SMS (extra charges apply)" on={notifs.sms}   onToggle={() => setNotifs(n => ({...n, sms:!n.sms}))}/>
              <ToggleRow title="Weekly Summary Report" desc="Digest email every Monday morning"            on={notifs.weekly} onToggle={() => setNotifs(n => ({...n, weekly:!n.weekly}))}/>
              <div className="mt-5">
                <SettingRow title="Alert Threshold"    desc="Min transaction amount to trigger alert"     right="₦50,000"           onClick={save}/>
                <SettingRow title="Dispute Alert Email" desc="Destination email for payment disputes"     right="disputes@bunnix.ng" onClick={save}/>
              </div>
            </>
          )}

          {tab === 'security' && (
            <>
              <SectionTitle>🔒 Security & Access</SectionTitle>
              <ToggleRow title="Two-Factor Authentication" desc="Require TOTP for all admin logins"          on={security.twofa}    onToggle={() => setSec(s => ({...s, twofa:!s.twofa}))}/>
              <ToggleRow title="IP Whitelisting"           desc="Restrict admin access to specific IP ranges" on={security.ipWhite} onToggle={() => setSec(s => ({...s, ipWhite:!s.ipWhite}))}/>
              <SettingRow title="Session Timeout"  desc="Automatic logout after inactivity"          right="30 mins" onClick={save}/>
              <SettingRow title="Password Policy"  desc="Minimum password strength requirements"    right="Strong"  onClick={save}/>
              <SettingRow title="Audit Log Retention" desc="How long admin action logs are kept"    right="90 days" onClick={save}/>
            </>
          )}

          {tab === 'backend' && (
            <>
              <SectionTitle>🔄 Backend Sync Settings</SectionTitle>
              <SettingRow title="Sync Frequency"   desc="How often dashboard data refreshes"        right="Every 5 mins"        onClick={save}/>
              <SettingRow title="Database Region"  desc="Primary data storage region"               right="af-south-1 (Lagos)"  onClick={save}/>
              <SettingRow title="Cache Strategy"   desc="Data caching layer"                        right="Redis"               onClick={save}/>
              <div className="mt-5">
                <button
                  onClick={save}
                  className="flex items-center gap-2 w-full px-4 py-3 rounded-xl text-sm font-semibold text-gray-600 dark:text-gray-400 border border-gray-200 dark:border-white/8 hover:border-orange-400 dark:hover:border-orange-500 hover:text-orange-500 transition-colors justify-center"
                >
                  <RefreshCw size={14}/> Trigger Manual Sync
                </button>
              </div>
            </>
          )}

          {tab === 'regional' && (
            <>
              <SectionTitle>🌍 Regional Settings</SectionTitle>
              <SettingRow title="Base Currency"       desc="Platform transaction currency"                 right="Nigerian Naira (₦)"              onClick={save}/>
              <SettingRow title="Time Zone"           desc="Admin portal time display"                     right="Africa/Lagos (WAT)"              onClick={save}/>
              <SettingRow title="Date Format"         desc="How dates are displayed across the platform"   right="DD/MM/YYYY"                      onClick={save}/>
              <SettingRow title="Supported Countries" desc="Where Bunnix operates"                         right="Nigeria · Ghana · Kenya"         onClick={save}/>
              <SettingRow title="Language Options"    desc="Languages available to platform users"         right="English · Yoruba · Igbo · Hausa" onClick={save}/>
            </>
          )}

        </div>
      </div>
    </div>
  );
}

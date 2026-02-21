import React from 'react';
import { 
  Bell, 
  Lock, 
  Globe, 
  Database, 
  Shield, 
  CreditCard,
  AppWindow,
  Zap,
  ChevronRight,
  Info
} from 'lucide-react';

export function Settings() {
  const settingsSections = [
    {
      title: 'Platform Configuration',
      icon: AppWindow,
      items: [
        { label: 'Marketplace Fees', desc: 'Set percentage commission for products & services', value: '10%' },
        { label: 'Vendor Payout Threshold', desc: 'Minimum balance for manual bank transfers', value: '₦5,000' },
        { label: 'Default Language', desc: 'System-wide default for notifications', value: 'English (UK)' },
      ]
    },
    {
      title: 'Security & Access',
      icon: Shield,
      items: [
        { label: 'Two-Factor Authentication', desc: 'Mandatory for all Admin accounts', value: 'Enabled' },
        { label: 'Session Timeout', desc: 'Automatic logout after inactivity', value: '30 mins' },
        { label: 'IP Whitelisting', desc: 'Restrict admin access to specific IP ranges', value: 'Disabled' },
      ]
    },
    {
      title: 'API & Integrations',
      icon: Zap,
      items: [
        { label: 'Push Notifications', desc: 'FCM integration for mobile alerts', value: 'Connected' },
        { label: 'Storage Bucket', desc: 'External media hosting for product images', value: 'AWS S3' },
      ]
    }
  ];

  return (
    <div className="space-y-8 max-w-5xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">System Settings</h1>
        <p className="text-slate-500">Configure global platform rules and security parameters.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        <aside className="md:col-span-1 space-y-1">
          <button className="w-full text-left px-4 py-3 bg-white border border-gray-200 rounded-xl font-bold text-orange-600 shadow-sm flex items-center gap-3">
            <AppWindow size={18} />
            General
          </button>
          <button className="w-full text-left px-4 py-3 text-slate-500 hover:bg-gray-100 rounded-xl font-semibold flex items-center gap-3 transition-colors">
            <Bell size={18} />
            Notifications
          </button>
          <button className="w-full text-left px-4 py-3 text-slate-500 hover:bg-gray-100 rounded-xl font-semibold flex items-center gap-3 transition-colors">
            <Lock size={18} />
            Security
          </button>
          <button className="w-full text-left px-4 py-3 text-slate-500 hover:bg-gray-100 rounded-xl font-semibold flex items-center gap-3 transition-colors">
            <Database size={18} />
            Backend Sync
          </button>
          <button className="w-full text-left px-4 py-3 text-slate-500 hover:bg-gray-100 rounded-xl font-semibold flex items-center gap-3 transition-colors">
            <Globe size={18} />
            Regional
          </button>
        </aside>

        <div className="md:col-span-3 space-y-6">
          {settingsSections.map((section, idx) => (
            <div key={idx} className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-100 flex items-center gap-2 bg-gray-50/50">
                <section.icon size={18} className="text-slate-400" />
                <h2 className="font-bold text-slate-800">{section.title}</h2>
              </div>
              <div className="divide-y divide-gray-100">
                {section.items.map((item, i) => (
                  <div key={i} className="px-6 py-5 flex items-center justify-between hover:bg-gray-50/50 transition-colors cursor-pointer group">
                    <div className="space-y-1">
                      <p className="text-sm font-bold text-slate-900">{item.label}</p>
                      <p className="text-xs text-slate-500">{item.desc}</p>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-sm font-semibold text-slate-400 group-hover:text-orange-600 transition-colors">
                        {item.value}
                      </span>
                      <ChevronRight size={16} className="text-slate-300" />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}

          <div className="bg-orange-50 border border-orange-100 rounded-2xl p-6 flex items-start gap-4">
            <div className="bg-orange-100 p-2 rounded-lg text-orange-600">
              <Info size={20} />
            </div>
            <div className="flex-1">
              <h4 className="text-sm font-bold text-orange-800">Production Environment</h4>
              <p className="text-xs text-orange-700 mt-1 leading-relaxed">
                You are currently managing the production environment for Bunnix Nigeria. All changes made here are immediate and will reflect on the live mobile applications.
              </p>
            </div>
            <button className="px-4 py-2 bg-orange-600 text-white text-xs font-bold rounded-xl hover:bg-orange-700 transition-colors shadow-lg shadow-orange-200">
              Commit Changes
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

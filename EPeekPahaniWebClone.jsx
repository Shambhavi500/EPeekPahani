import React, { useState, useEffect } from 'react';
import { 
  ChevronLeft, WifiOff, Camera, MapPin, CheckCircle, 
  Search, FileText, ChevronRight, Check, Battery, Wifi, 
  Signal, AlertTriangle, User, Map, Image as ImageIcon
} from 'lucide-react';

const CustomStyles = () => (
  <style>{`
    @keyframes slideInRight {
      from { transform: translateX(20px); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
    .animate-slide-in {
      animation: slideInRight 0.3s ease-out forwards;
    }
    @keyframes zoomIn {
      from { transform: scale(0.9); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }
    .animate-zoom-in {
      animation: zoomIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    }
    .hide-scrollbar::-webkit-scrollbar {
      display: none;
    }
    .hide-scrollbar {
      -ms-overflow-style: none;
      scrollbar-width: none;
    }
  `}</style>
);

export default function EPeekPahaniApp() {
  const [screen, setScreen] = useState(0);
  const [isOffline, setIsOffline] = useState(false);
  
  // Specific screen states
  const [splashProgress, setSplashProgress] = useState(0);
  const [otpSent, setOtpSent] = useState(false);
  const [otp, setOtp] = useState(['', '', '', '']);
  const [loginErrors, setLoginErrors] = useState({});
  const [cropErrors, setCropErrors] = useState({});
  const [gpsStatus, setGpsStatus] = useState(0); // 0: init, 1: loading, 2: done
  const [photos, setPhotos] = useState([false, false]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [refNo, setRefNo] = useState('');
  
  // Dummy Records Data
  const records = [
    { id: 1, khata: '142', gut: '87', area: '1.20', owner: 'राजेश विठ्ठल पाटील' },
    { id: 2, khata: '142', gut: '88', area: '0.80', owner: 'राजेश विठ्ठल पाटील' },
    { id: 3, khata: '145', gut: '92', area: '2.50', owner: 'सुनिता विठ्ठल पाटील' },
  ];

  // Form Data State
  const [data, setData] = useState({
    name: 'राजेश विठ्ठल पाटील',
    mobile: '9876543210',
    division: 'पुणे',
    district: 'पुणे',
    taluka: 'हवेली',
    village: 'उरुळी कांचन',
    selectedRecord: null,
    season: 'खरीप 2025',
    crop: 'सोयाबीन',
    sowingDate: '2025-06-15',
    harvestDate: '2025-10-15',
    cropType: 'एकल पीक',
    area: '1.20',
    consent: false
  });

  const nextScreen = () => setScreen(s => Math.min(8, s + 1));
  const prevScreen = () => setScreen(s => Math.max(1, s - 1));

  // Splash Screen Logic
  useEffect(() => {
    if (screen === 0) {
      const interval = setInterval(() => {
        setSplashProgress(p => {
          if (p >= 100) {
            clearInterval(interval);
            setTimeout(() => setScreen(1), 300);
            return 100;
          }
          return p + 4; // roughly 2s total
        });
      }, 80);
      return () => clearInterval(interval);
    }
  }, [screen]);

  // GPS Simulation Logic
  useEffect(() => {
    if (screen === 6 && gpsStatus === 0) {
      setGpsStatus(1);
      const timer = setTimeout(() => setGpsStatus(2), 2000);
      return () => clearTimeout(timer);
    }
  }, [screen, gpsStatus]);

  const handleLoginSubmit = () => {
    let errs = {};
    if (!data.name.trim()) errs.name = 'कृपया तुमचे पूर्ण नाव टाका';
    if (!data.mobile.trim()) errs.mobile = 'कृपया मोबाईल क्रमांक टाका';
    
    if (Object.keys(errs).length > 0) {
      setLoginErrors(errs);
      return;
    }
    setLoginErrors({});
    
    if (!otpSent) {
      setOtpSent(true);
    } else {
      if (otp.join('').length < 4) {
        setLoginErrors({ otp: 'कृपया ४ अंकी OTP टाका' });
        return;
      }
      nextScreen();
    }
  };

  const handleOtpChange = (index, value) => {
    if (value.length > 1) return; // Prevent multiple chars
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);
    if (value && index < 3) {
      document.getElementById(`otp-${index + 1}`)?.focus();
    }
  };

  const handleCropSubmit = () => {
    let errs = {};
    if (!data.crop) errs.crop = 'कृपया पिकाचे नाव निवडा';
    if (!data.area || Number(data.area) <= 0) errs.area = 'कृपया वैध क्षेत्र टाका';
    
    if (Object.keys(errs).length > 0) {
      setCropErrors(errs);
      return;
    }
    setCropErrors({});
    nextScreen();
  };

  const handleFinalSubmit = () => {
    setIsSubmitting(true);
    setTimeout(() => {
      setIsSubmitting(false);
      setRefNo(`DCS2025-PUNE-${Math.floor(1000 + Math.random() * 9000)}`);
      nextScreen();
    }, 1500);
  };

  const renderScreen = () => {
    switch(screen) {
      case 0:
        return (
          <div className="flex-1 flex flex-col items-center justify-center bg-white p-6 relative">
            <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10 pointer-events-none"></div>
            <div className="w-28 h-28 rounded-full bg-gradient-to-br from-[#1a6b3a] to-[#0f4022] flex items-center justify-center mb-8 shadow-2xl border-4 border-green-50 animate-zoom-in">
              <div className="w-20 h-20 rounded-full border-[3px] border-white flex items-center justify-center bg-[#003087] shadow-inner">
                <span className="text-white font-bold text-3xl tracking-widest font-sans">MH</span>
              </div>
            </div>
            <h1 className="text-3xl font-bold text-[#1a6b3a] mb-2 text-center drop-shadow-sm font-['Noto_Sans_Devanagari']">डिजिटल पीक पाहणी</h1>
            <p className="text-[#f7941d] font-bold text-lg mb-16 tracking-wider">E-Peek Pahani</p>
            
            <div className="w-64 h-3 bg-gray-100 rounded-full overflow-hidden shadow-inner relative">
              <div className="absolute top-0 bottom-0 left-0 bg-gradient-to-r from-[#1a6b3a] to-[#2cb461] transition-all duration-75 ease-linear rounded-full" style={{width: `${splashProgress}%`}}></div>
            </div>
            <p className="mt-12 text-sm text-gray-500 font-bold tracking-wide uppercase">महाराष्ट्र शासन (Govt of Maharashtra)</p>
          </div>
        );
      case 1:
        return (
          <div className="flex-1 flex flex-col p-6 bg-white animate-slide-in overflow-y-auto hide-scrollbar">
            <h2 className="text-xl font-bold text-[#003087] mb-8 border-b-2 border-gray-100 pb-4 flex items-center gap-3">
              <span className="bg-blue-50 p-2.5 rounded-xl"><User size={22} className="text-[#003087]" /></span>
              शेतकरी नोंदणी
            </h2>
            <div className="space-y-6 flex-1">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1.5">पूर्ण नाव</label>
                <input type="text" value={data.name} onChange={e => setData({...data, name: e.target.value})} className={`w-full border-2 ${loginErrors.name ? 'border-red-400 bg-red-50' : 'border-gray-200'} rounded-xl p-3.5 focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 focus:outline-none transition-all shadow-sm text-gray-800 font-medium`} placeholder="तुमचे नाव टाका" />
                <div className="flex justify-between mt-1.5">
                  <span className="text-[11px] text-gray-400 font-bold uppercase tracking-wider">Full Name</span>
                  {loginErrors.name && <span className="text-xs text-red-500 font-bold">{loginErrors.name}</span>}
                </div>
              </div>
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-1.5">मोबाईल क्रमांक</label>
                <div className="relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 font-bold">+91</span>
                  <input type="tel" value={data.mobile} onChange={e => setData({...data, mobile: e.target.value})} className={`w-full border-2 ${loginErrors.mobile ? 'border-red-400 bg-red-50' : 'border-gray-200'} rounded-xl p-3.5 pl-12 focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 focus:outline-none transition-all shadow-sm text-gray-800 font-medium`} placeholder="10 अंकी क्रमांक" maxLength={10} />
                </div>
                <div className="flex justify-between mt-1.5">
                  <span className="text-[11px] text-gray-400 font-bold uppercase tracking-wider">Mobile Number</span>
                  {loginErrors.mobile && <span className="text-xs text-red-500 font-bold">{loginErrors.mobile}</span>}
                </div>
              </div>
              {otpSent && (
                <div className="pt-6 border-t border-gray-100 animate-slide-in">
                  <label className="block text-sm font-bold text-[#1a6b3a] mb-4 text-center bg-green-50 py-2 rounded-lg">OTP प्रविष्ट करा (4-digit)</label>
                  <div className="flex justify-center gap-4">
                    {[0,1,2,3].map(i => (
                      <input key={i} id={`otp-${i}`} type="text" maxLength={1} value={otp[i]} onChange={e => handleOtpChange(i, e.target.value)} className="w-14 h-16 text-center text-2xl font-bold border-2 border-gray-300 rounded-xl focus:border-[#f7941d] focus:ring-4 focus:ring-orange-100 focus:outline-none bg-gray-50 transition-all shadow-inner text-[#003087]" />
                    ))}
                  </div>
                  {loginErrors.otp && <p className="text-center text-red-500 text-sm font-bold mt-3">{loginErrors.otp}</p>}
                </div>
              )}
            </div>
            <div className="pt-4 pb-2">
              <button onClick={handleLoginSubmit} className="w-full bg-[#1a6b3a] text-white py-4 rounded-xl font-bold text-lg shadow-lg shadow-green-900/20 hover:bg-[#124d29] transition-all active:scale-[0.98] flex items-center justify-center gap-2">
                {otpSent ? 'पुष्टी करा' : 'OTP पाठवा'} <ChevronRight size={20} />
              </button>
            </div>
          </div>
        );
      case 2:
        return (
          <div className="flex-1 flex flex-col p-6 bg-gray-50 animate-slide-in overflow-y-auto hide-scrollbar">
            <h2 className="text-lg font-bold text-[#003087] mb-5 flex items-center gap-2"><Map size={20} /> प्रशासकीय विभाग निवड</h2>
            <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 space-y-5 flex-1 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-blue-50 rounded-bl-[100px] -z-10 opacity-50"></div>
              {['division:विभाग:Division', 'district:जिल्हा:District', 'taluka:तालुका:Taluka', 'village:गाव:Village'].map(f => {
                const [key, mr, en] = f.split(':');
                return (
                  <div key={key} className="relative">
                    <label className="block text-sm font-bold text-gray-700 mb-1.5">{mr}</label>
                    <select value={data[key]} onChange={e => setData({...data, [key]: e.target.value})} className="w-full border-2 border-gray-200 rounded-xl p-3.5 bg-gray-50 focus:bg-white focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 focus:outline-none transition-all text-gray-800 font-medium appearance-none">
                      <option>{data[key]}</option>
                    </select>
                    <div className="absolute right-4 top-10 pointer-events-none text-gray-400">▼</div>
                    <span className="text-[11px] text-gray-400 font-bold uppercase tracking-wider mt-1 block">{en}</span>
                  </div>
                )
              })}
            </div>
            <div className="mt-6 pb-2">
              <button onClick={nextScreen} className="w-full bg-[#1a6b3a] text-white py-4 rounded-xl font-bold text-lg shadow-lg shadow-green-900/20 active:scale-[0.98] transition-transform flex items-center justify-center gap-2">
                पुढे जा <ChevronRight size={20} />
              </button>
            </div>
          </div>
        );
      case 3:
        return (
          <div className="flex-1 flex flex-col bg-gray-50 animate-slide-in overflow-hidden">
            <div className="p-5 bg-white shadow-sm z-10 border-b border-gray-100">
              <h2 className="text-lg font-bold text-[#003087] mb-3">खातेदार निवड</h2>
              <div className="relative">
                <input type="text" placeholder="मालकाचे नाव किंवा गट नंबर शोधा" className="w-full border-2 border-gray-200 rounded-xl pl-12 pr-4 py-3.5 focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 focus:outline-none font-medium text-gray-700 transition-all bg-gray-50 focus:bg-white" />
                <Search className="absolute left-4 top-4 text-gray-400" size={20} />
              </div>
            </div>
            <div className="flex-1 p-5 overflow-y-auto hide-scrollbar space-y-4 bg-gray-50">
              {records.map(r => (
                <div key={r.id} onClick={() => setData({...data, selectedRecord: r})} className={`p-4 rounded-2xl border-2 transition-all cursor-pointer relative overflow-hidden ${data.selectedRecord?.id === r.id ? 'border-[#1a6b3a] bg-[#f0fdf4] shadow-md transform scale-[1.02]' : 'border-gray-200 bg-white shadow-sm hover:border-gray-300'}`}>
                  {data.selectedRecord?.id === r.id && (
                    <div className="absolute top-0 right-0 bg-[#1a6b3a] text-white p-1 rounded-bl-lg">
                      <Check size={16} />
                    </div>
                  )}
                  <div className="flex justify-between items-start mb-3">
                    <div className="bg-blue-50 px-3 py-1.5 rounded-lg border border-blue-100">
                      <span className="text-[10px] font-bold text-blue-600 uppercase block mb-0.5">Khata No</span>
                      <div className="font-bold text-[#003087] text-sm">खाता: {r.khata}</div>
                    </div>
                    <div className="bg-orange-50 px-3 py-1.5 rounded-lg border border-orange-100 text-right">
                      <span className="text-[10px] font-bold text-orange-600 uppercase block mb-0.5">Gut No</span>
                      <div className="font-bold text-[#f7941d] text-sm">गट: {r.gut}</div>
                    </div>
                  </div>
                  <div className="h-px bg-gray-100 my-3"></div>
                  <div className="flex justify-between items-end">
                    <div>
                      <span className="text-[10px] text-gray-400 font-bold uppercase block mb-0.5">Owner</span>
                      <div className="text-sm font-bold text-gray-800">{r.owner}</div>
                    </div>
                    <div className="text-sm font-bold text-white bg-[#1a6b3a] px-3 py-1 rounded-lg shadow-sm">
                      {r.area} हेक्टर
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <div className="p-5 bg-white border-t border-gray-200 z-10">
              <button disabled={!data.selectedRecord} onClick={nextScreen} className="w-full bg-[#1a6b3a] disabled:bg-gray-200 disabled:text-gray-400 text-white py-4 rounded-xl font-bold text-lg shadow-lg transition-all active:scale-[0.98] disabled:active:scale-100">
                निवड करा
              </button>
            </div>
          </div>
        );
      case 4:
        return (
          <div className="flex-1 flex flex-col p-5 bg-gray-50 animate-slide-in overflow-y-auto hide-scrollbar">
            <div className="bg-white rounded-2xl shadow-sm border border-gray-200 mb-6 overflow-hidden relative">
              <div className="absolute top-0 right-0 w-24 h-24 bg-blue-50 rounded-bl-full -z-10"></div>
              <div className="bg-[#003087] text-white p-4 text-center font-bold flex items-center justify-center gap-2">
                <FileText size={18} /> ७/१२ उतारा तपशील
              </div>
              <div className="p-5 space-y-4">
                <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                  <span className="text-gray-500 text-sm font-medium">गाव (Village)</span>
                  <span className="font-bold text-gray-800">{data.village}</span>
                </div>
                <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                  <span className="text-gray-500 text-sm font-medium">खाता क्र (Khata)</span>
                  <span className="font-bold text-gray-800 bg-gray-100 px-2 py-0.5 rounded">{data.selectedRecord?.khata || '142'}</span>
                </div>
                <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                  <span className="text-gray-500 text-sm font-medium">गट क्र (Gut)</span>
                  <span className="font-bold text-[#f7941d] bg-orange-50 px-2 py-0.5 rounded border border-orange-100">{data.selectedRecord?.gut || '87'}</span>
                </div>
                <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                  <span className="text-gray-500 text-sm font-medium">क्षेत्र (Area)</span>
                  <span className="font-bold text-white bg-[#1a6b3a] px-2 py-0.5 rounded shadow-sm">{data.selectedRecord?.area || '1.20'} हेक्टर</span>
                </div>
                <div className="flex justify-between items-start pt-1">
                  <span className="text-gray-500 text-sm font-medium">मालक (Owner)</span>
                  <span className="font-bold text-gray-800 text-right w-1/2 leading-tight">{data.selectedRecord?.owner || data.name}</span>
                </div>
              </div>
            </div>

            <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-200 flex-1">
              <label className="block text-sm font-bold text-[#003087] mb-3">हंगाम निवडा (Season)</label>
              <div className="grid grid-cols-2 gap-3">
                {['खरीप 2025', 'रब्बी 2025'].map(s => (
                  <button key={s} onClick={() => setData({...data, season: s})} className={`py-3.5 rounded-xl border-2 font-bold transition-all ${data.season === s ? 'border-[#f7941d] bg-orange-50 text-[#f7941d] shadow-sm transform scale-[1.02]' : 'border-gray-200 text-gray-500 hover:border-gray-300'}`}>
                    {s}
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-6 pb-2">
              <button onClick={nextScreen} className="w-full bg-[#1a6b3a] text-white py-4 rounded-xl font-bold text-lg shadow-lg shadow-green-900/20 active:scale-[0.98] transition-transform">
                पीक नोंदणी सुरू करा
              </button>
            </div>
          </div>
        );
      case 5:
        return (
          <div className="flex-1 flex flex-col bg-gray-50 animate-slide-in overflow-hidden">
            <div className="flex-1 p-5 overflow-y-auto hide-scrollbar space-y-6">
              <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
                <label className="block text-sm font-bold text-gray-700 mb-2">पिकाचे नाव</label>
                <div className="relative">
                  <select value={data.crop} onChange={e => setData({...data, crop: e.target.value})} className={`w-full border-2 ${cropErrors.crop ? 'border-red-400' : 'border-gray-200'} rounded-xl p-3.5 focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 bg-gray-50 focus:bg-white appearance-none font-medium text-gray-800 transition-all`}>
                    <option value="">पीक निवडा...</option>
                    {['गहू', 'ज्वारी', 'बाजरी', 'तूर', 'हरभरा', 'सोयाबीन', 'कापूस', 'ऊस', 'भात', 'मका'].map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                  <div className="absolute right-4 top-4 pointer-events-none text-gray-400">▼</div>
                </div>
                <div className="flex justify-between mt-1.5">
                  <span className="text-[11px] text-gray-400 font-bold uppercase tracking-wider">Crop Name</span>
                  {cropErrors.crop && <span className="text-xs text-red-500 font-bold">{cropErrors.crop}</span>}
                </div>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
                  <label className="block text-sm font-bold text-gray-700 mb-2">पेरणी तारीख</label>
                  <input type="date" value={data.sowingDate} onChange={e => setData({...data, sowingDate: e.target.value})} className="w-full border-2 border-gray-200 rounded-xl p-3 text-sm focus:border-[#1a6b3a] focus:ring-2 focus:ring-green-50 bg-gray-50 font-medium text-gray-700" />
                  <span className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-1.5 block">Sowing Date</span>
                </div>
                <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100">
                  <label className="block text-sm font-bold text-gray-700 mb-2">काढणी तारीख</label>
                  <input type="date" value={data.harvestDate} onChange={e => setData({...data, harvestDate: e.target.value})} className="w-full border-2 border-gray-200 rounded-xl p-3 text-sm focus:border-[#1a6b3a] focus:ring-2 focus:ring-green-50 bg-gray-50 font-medium text-gray-700" />
                  <span className="text-[10px] text-gray-400 font-bold uppercase tracking-wider mt-1.5 block">Harvest Date</span>
                </div>
              </div>

              <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100">
                <label className="block text-sm font-bold text-[#003087] mb-3">पीक प्रकार (Crop Type)</label>
                <div className="flex flex-col gap-3">
                  {['एकल पीक (Single)', 'मिश्र पीक (Mixed)', 'पडीक (Fallow)'].map(t => {
                    const val = t.split(' ')[0] + (t.includes('पीक') ? ' पीक' : '');
                    const isSelected = data.cropType === val;
                    return (
                      <label key={t} className={`flex items-center p-3.5 border-2 rounded-xl cursor-pointer transition-all ${isSelected ? 'border-[#1a6b3a] bg-green-50' : 'border-gray-200 bg-gray-50 hover:border-gray-300'}`}>
                        <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center mr-3 ${isSelected ? 'border-[#1a6b3a]' : 'border-gray-400'}`}>
                          {isSelected && <div className="w-2.5 h-2.5 bg-[#1a6b3a] rounded-full"></div>}
                        </div>
                        <input type="radio" name="croptype" className="hidden" checked={isSelected} onChange={() => setData({...data, cropType: val})} />
                        <span className={`font-bold ${isSelected ? 'text-[#1a6b3a]' : 'text-gray-700'}`}>{t}</span>
                      </label>
                  )})}
                </div>
              </div>

              <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 mb-6">
                <label className="block text-sm font-bold text-gray-700 mb-2">क्षेत्र (हेक्टरमध्ये)</label>
                <div className="relative">
                  <input type="number" step="0.01" value={data.area} onChange={e => setData({...data, area: e.target.value})} className={`w-full border-2 ${cropErrors.area ? 'border-red-400' : 'border-gray-200'} rounded-xl p-3.5 pr-16 focus:border-[#1a6b3a] focus:ring-4 focus:ring-green-50 bg-gray-50 font-bold text-gray-800 text-lg transition-all`} />
                  <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 font-bold">हेक्टर</span>
                </div>
                <div className="flex justify-between mt-1.5">
                  <span className="text-[11px] text-gray-400 font-bold uppercase tracking-wider">Area in Hectares</span>
                  {cropErrors.area && <span className="text-xs text-red-500 font-bold">{cropErrors.area}</span>}
                </div>
              </div>
            </div>
            <div className="p-5 bg-white border-t border-gray-200 shadow-[0_-10px_20px_-10px_rgba(0,0,0,0.05)] z-10">
              <button onClick={handleCropSubmit} className="w-full bg-[#1a6b3a] text-white py-4 rounded-xl font-bold text-lg shadow-lg shadow-green-900/20 active:scale-[0.98] transition-transform flex items-center justify-center gap-2">
                पुढे: जीपीएस व फोटो <ChevronRight size={20} />
              </button>
            </div>
          </div>
        );
      case 6:
        return (
          <div className="flex-1 flex flex-col bg-gray-50 animate-slide-in overflow-y-auto hide-scrollbar">
            <div className="p-5 space-y-5 flex-1">
              {/* Map Placeholder */}
              <div className="h-44 rounded-2xl overflow-hidden relative border-4 border-white shadow-md bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] bg-[#e5e5f7]">
                <div className="absolute inset-0 bg-green-900/10 mix-blend-multiply"></div>
                {gpsStatus === 2 && (
                  <>
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-32 h-32 bg-[#1a6b3a]/20 rounded-full animate-ping"></div>
                    <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-12 h-12 bg-white rounded-full flex items-center justify-center shadow-lg border-2 border-[#1a6b3a] z-10">
                      <MapPin size={24} className="text-[#1a6b3a]" fill="currentColor" />
                    </div>
                  </>
                )}
                {gpsStatus !== 2 && (
                   <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-gray-400 opacity-50">
                     <MapPin size={48} />
                   </div>
                )}
                <div className="absolute bottom-2 right-2 bg-white/90 backdrop-blur px-2 py-1 rounded shadow-sm text-[10px] font-bold text-gray-600 uppercase tracking-wider">Map Data</div>
              </div>

              {/* GPS Status */}
              <div className={`p-4 rounded-2xl border-2 flex items-center gap-4 transition-all duration-500 shadow-sm ${gpsStatus === 2 ? 'bg-[#f0fdf4] border-[#1a6b3a] text-[#1a6b3a]' : 'bg-blue-50 border-blue-200 text-blue-800'}`}>
                {gpsStatus === 1 ? (
                  <><div className="animate-spin rounded-full h-6 w-6 border-3 border-b-transparent border-blue-600"></div> <span className="font-bold">📍 स्थान मिळवत आहे...</span></>
                ) : gpsStatus === 2 ? (
                  <><div className="bg-[#1a6b3a] rounded-full p-1"><CheckCircle size={24} className="text-white" /></div> 
                  <div>
                    <div className="font-bold text-sm">स्थान यशस्वीरित्या मिळाले</div>
                    <div className="text-xs font-mono mt-1 text-gray-700 bg-white/50 px-2 py-0.5 rounded border border-[#1a6b3a]/20 inline-block">18.5204° N, 73.8567° E</div>
                    <div className="text-[10px] mt-1 font-bold tracking-wider uppercase opacity-80">अचूकता: ±5 मीटर</div>
                  </div>
                  </>
                ) : (
                  <span className="font-bold">GPS सुरू करा</span>
                )}
              </div>

              {/* Photos */}
              <div>
                <h3 className="font-bold text-[#003087] mb-3 flex items-center gap-2"><ImageIcon size={18} /> पिकाचे फोटो (Photos)</h3>
                <div className="grid grid-cols-2 gap-4">
                  {[0, 1].map(i => (
                    <div key={i} onClick={() => {
                      if(gpsStatus !== 2) return;
                      const newPhotos = [...photos];
                      newPhotos[i] = true;
                      setPhotos(newPhotos);
                    }} className={`aspect-square rounded-2xl border-2 flex flex-col items-center justify-center p-4 transition-all ${gpsStatus !== 2 ? 'opacity-50 cursor-not-allowed bg-gray-100 border-gray-200' : 'cursor-pointer'} ${photos[i] ? 'bg-green-50 border-[#1a6b3a] text-green-700 relative overflow-hidden shadow-sm transform scale-[1.02]' : 'bg-white border-dashed border-gray-300 text-gray-400 hover:border-[#1a6b3a] hover:text-[#1a6b3a] hover:bg-green-50/30'}`}>
                      {photos[i] ? (
                        <>
                          <div className="absolute inset-0 bg-black/30 z-10"></div>
                          <img src={`https://picsum.photos/seed/${i+20}/300/300`} className="absolute inset-0 w-full h-full object-cover" alt="crop" />
                          <div className="relative z-20 bg-white/90 backdrop-blur rounded-full p-1.5 mb-2 shadow-lg">
                            <CheckCircle size={28} className="text-[#1a6b3a]" fill="currentColor" />
                          </div>
                          <span className="relative z-20 font-bold text-white drop-shadow-md text-sm bg-black/40 px-2 py-0.5 rounded">फोटो घेतला</span>
                        </>
                      ) : (
                        <>
                          <Camera size={36} className="mb-3 opacity-80" />
                          <span className="font-bold text-sm text-center leading-tight">फोटो {i+1} काढा</span>
                        </>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            </div>
            
            <div className="p-5 bg-white border-t border-gray-200 shadow-[0_-10px_20px_-10px_rgba(0,0,0,0.05)]">
              <button disabled={!photos[0] || !photos[1] || gpsStatus !== 2} onClick={nextScreen} className="w-full bg-[#1a6b3a] disabled:bg-gray-200 disabled:text-gray-400 text-white py-4 rounded-xl font-bold text-lg shadow-lg disabled:shadow-none transition-all active:scale-[0.98] disabled:active:scale-100 flex items-center justify-center gap-2">
                पुढे: पुनरावलोकन <ChevronRight size={20} />
              </button>
            </div>
          </div>
        );
      case 7:
        return (
          <div className="flex-1 flex flex-col bg-gray-50 animate-slide-in overflow-hidden">
            <div className="flex-1 p-5 overflow-y-auto hide-scrollbar">
              <h2 className="text-lg font-bold text-[#003087] mb-4">माहितीचे पुनरावलोकन</h2>
              
              <div className="bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden relative">
                <div className="absolute top-0 right-0 w-32 h-32 bg-orange-50 rounded-bl-[100px] -z-10"></div>
                <div className="bg-[#f7941d] text-white p-3.5 font-bold text-center flex items-center justify-center gap-2 shadow-sm">
                  <FileText size={18} /> सारांश (Summary)
                </div>
                <div className="p-5 space-y-3.5 text-sm">
                  <div className="flex justify-between items-start border-b border-gray-100 pb-3">
                    <span className="text-gray-500 font-medium">शेतकरी</span>
                    <span className="font-bold text-gray-800 text-right w-2/3 leading-tight">{data.name}</span>
                  </div>
                  <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                    <span className="text-gray-500 font-medium">गाव / गट</span>
                    <span className="font-bold text-gray-800 text-right"><span className="bg-gray-100 px-2 py-0.5 rounded mr-1">{data.village}</span> / <span className="bg-orange-50 text-[#f7941d] border border-orange-100 px-2 py-0.5 rounded ml-1">{data.selectedRecord?.gut || '87'}</span></span>
                  </div>
                  <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                    <span className="text-gray-500 font-medium">हंगाम / पीक</span>
                    <span className="font-bold text-gray-800 text-right">{data.season} - <span className="text-white bg-[#1a6b3a] px-2 py-0.5 rounded shadow-sm">{data.crop}</span></span>
                  </div>
                  <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                    <span className="text-gray-500 font-medium">क्षेत्र</span>
                    <span className="font-bold text-gray-800 text-right">{data.area} हेक्टर</span>
                  </div>
                  <div className="flex justify-between items-center border-b border-gray-100 pb-3">
                    <span className="text-gray-500 font-medium">पेरणी</span>
                    <span className="font-bold text-gray-800 text-right">{data.sowingDate}</span>
                  </div>
                  <div className="flex justify-between items-center pb-2">
                    <span className="text-gray-500 font-medium">GPS</span>
                    <span className="font-mono text-[11px] font-bold text-[#003087] bg-blue-50 border border-blue-100 px-2 py-1 rounded">18.5204° N, 73.8567° E</span>
                  </div>
                  <div className="flex gap-3 pt-3 mt-2 border-t border-gray-100">
                    <div className="h-20 flex-1 rounded-xl bg-gray-200 overflow-hidden relative shadow-sm border-2 border-white"><img src="https://picsum.photos/seed/20/300/300" className="object-cover w-full h-full" alt="pic1" /></div>
                    <div className="h-20 flex-1 rounded-xl bg-gray-200 overflow-hidden relative shadow-sm border-2 border-white"><img src="https://picsum.photos/seed/21/300/300" className="object-cover w-full h-full" alt="pic2" /></div>
                  </div>
                </div>
              </div>

              <label className={`flex items-start mt-6 p-4 border-2 rounded-2xl cursor-pointer transition-all shadow-sm ${data.consent ? 'bg-[#f0fdf4] border-[#1a6b3a]' : 'bg-white border-gray-200 hover:border-gray-300'}`}>
                <div className={`mt-0.5 shrink-0 w-6 h-6 rounded border-2 flex items-center justify-center mr-3 transition-colors ${data.consent ? 'bg-[#1a6b3a] border-[#1a6b3a]' : 'bg-white border-gray-300'}`}>
                  {data.consent && <Check size={16} strokeWidth={3} className="text-white" />}
                </div>
                <input type="checkbox" checked={data.consent} onChange={e => setData({...data, consent: e.target.checked})} className="hidden" />
                <span className={`text-sm font-bold leading-snug ${data.consent ? 'text-[#1a6b3a]' : 'text-gray-600'}`}>मी प्रमाणित करतो की वरील माहिती अचूक आहे व माझ्या स्वतःच्या शेतातील आहे.</span>
              </label>
            </div>
            
            <div className="p-5 bg-white border-t border-gray-200 shadow-[0_-10px_20px_-10px_rgba(0,0,0,0.05)] z-10">
              <button disabled={!data.consent || isSubmitting} onClick={handleFinalSubmit} className="w-full bg-[#1a6b3a] disabled:bg-gray-200 disabled:text-gray-400 text-white py-4 rounded-xl font-bold text-lg shadow-lg disabled:shadow-none transition-all active:scale-[0.98] disabled:active:scale-100 relative overflow-hidden">
                {isSubmitting ? (
                  <div className="flex items-center justify-center gap-3">
                    <div className="animate-spin rounded-full h-5 w-5 border-3 border-b-transparent border-white"></div>
                    <span>प्रक्रिया करत आहे...</span>
                  </div>
                ) : 'सर्वेक्षण सादर करा'}
              </button>
            </div>
          </div>
        );
      case 8:
        return (
          <div className="flex-1 flex flex-col items-center justify-center bg-white p-6 animate-zoom-in relative overflow-hidden">
            <div className="absolute -top-40 -right-40 w-80 h-80 bg-green-50 rounded-full mix-blend-multiply filter blur-3xl opacity-70"></div>
            <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-blue-50 rounded-full mix-blend-multiply filter blur-3xl opacity-70"></div>
            
            <div className="w-28 h-28 bg-[#f0fdf4] rounded-full flex items-center justify-center mb-8 shadow-xl border-4 border-green-100 animate-[bounce_2s_infinite]">
              <div className="w-20 h-20 bg-[#1a6b3a] rounded-full flex items-center justify-center shadow-inner">
                <CheckCircle size={48} className="text-white" />
              </div>
            </div>
            
            <h2 className="text-2xl font-bold text-[#1a6b3a] mb-3 text-center drop-shadow-sm leading-tight">सर्वेक्षण यशस्वीरित्या<br/>सादर झाले!</h2>
            <p className="text-gray-500 text-center mb-8 font-medium">तुमची पीक पाहणी नोंदणी पूर्ण झाली आहे.</p>
            
            <div className="bg-gray-50 w-full p-6 rounded-2xl border-2 border-dashed border-gray-300 mb-10 text-center shadow-sm relative">
              <div className="absolute -top-3 left-1/2 -translate-x-1/2 bg-white px-3 text-xs text-gray-500 font-bold uppercase tracking-wider">संदर्भ क्रमांक (Ref No)</div>
              <div className="text-xl font-mono font-bold text-[#003087] tracking-widest">{refNo}</div>
            </div>

            <div className="w-full space-y-4 z-10">
              <button onClick={() => { setScreen(1); setData({...data, consent: false, selectedRecord: null}); setPhotos([false,false]); setGpsStatus(0); setRefNo(''); }} className="w-full bg-[#1a6b3a] text-white py-4 rounded-xl font-bold text-lg shadow-lg hover:bg-[#124d29] transition-all active:scale-[0.98]">
                नवीन नोंदणी करा
              </button>
              <button className="w-full bg-white border-2 border-[#1a6b3a] text-[#1a6b3a] py-4 rounded-xl font-bold text-lg shadow-sm hover:bg-[#f0fdf4] transition-all active:scale-[0.98]">
                माझ्या नोंदी पाहा
              </button>
            </div>
          </div>
        );
      default: return null;
    }
  }

  return (
    <>
      <CustomStyles />
      <div className="flex items-center justify-center min-h-screen bg-neutral-900 p-4 sm:p-8 font-sans selection:bg-green-200">
        <div className="relative w-[390px] h-[844px] bg-white rounded-[45px] shadow-2xl overflow-hidden border-[12px] border-black flex flex-col ring-4 ring-gray-800">
          
          {/* Notch simulation */}
          <div className="absolute top-0 inset-x-0 h-6 flex justify-center z-[60]">
            <div className="w-40 h-6 bg-black rounded-b-3xl"></div>
          </div>

          {/* Status Bar */}
          <div className={`h-12 w-full flex justify-between items-end px-6 pb-2 text-xs font-bold z-50 transition-colors ${screen === 0 || screen === 8 ? 'bg-transparent absolute top-0 text-gray-800' : 'bg-[#1a6b3a] text-white'}`}>
            <span className="tracking-wider mt-1">09:41</span>
            <div className="flex gap-2.5 items-center mb-0.5">
              <Signal size={14} fill="currentColor" />
              <Wifi size={14} strokeWidth={3} />
              <Battery size={16} fill="currentColor" />
            </div>
          </div>

          {/* Header - Hidden on Splash and Success */}
          {screen > 0 && screen < 8 && (
            <div className="bg-[#1a6b3a] text-white px-4 py-3.5 flex items-center shadow-md z-40 relative">
              <div className="absolute bottom-0 left-0 right-0 h-1 bg-gradient-to-r from-transparent via-white/20 to-transparent opacity-50"></div>
              {screen > 1 ? (
                <button onClick={prevScreen} className="p-1.5 -ml-1.5 mr-2 hover:bg-white/20 rounded-full transition-colors active:scale-90">
                  <ChevronLeft size={24} />
                </button>
              ) : (
                <div className="w-9 h-9 mr-3"></div> // Spacer to keep alignment
              )}
              
              <div className="flex-1 flex items-center justify-center -ml-8">
                <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center mr-2 shadow-sm border border-green-700 shrink-0">
                  <span className="text-[#003087] font-bold text-xs tracking-tighter">MH</span>
                </div>
                <h1 className="font-bold text-lg font-['Noto_Sans_Devanagari'] drop-shadow-sm tracking-wide">ई-पीक पाहणी</h1>
              </div>
              
              <button onClick={() => setIsOffline(!isOffline)} className={`p-2 -mr-2 rounded-full transition-colors active:scale-90 ${isOffline ? 'bg-red-500 text-white shadow-inner' : 'hover:bg-white/20'}`}>
                {isOffline ? <WifiOff size={20} /> : <Wifi size={20} />}
              </button>
            </div>
          )}

          {/* Offline Banner */}
          {isOffline && screen > 0 && screen < 8 && (
            <div className="bg-gradient-to-r from-yellow-400 to-yellow-500 text-yellow-900 px-4 py-2 text-[11px] font-bold flex items-center justify-center gap-2 z-30 shadow-md">
              <AlertTriangle size={14} className="animate-pulse" /> 📵 इंटरनेट नाही - स्थानिक संचयन (Offline Mode)
            </div>
          )}

          {/* Progress Stepper - shown on screens 1 to 7 */}
          {screen >= 1 && screen <= 7 && (
            <div className="bg-white px-5 py-3 z-30 shadow-[0_4px_10px_-5px_rgba(0,0,0,0.1)] relative">
              <div className="flex justify-between items-center relative">
                <div className="absolute top-1/2 left-0 right-0 h-1 bg-gray-100 -z-10 -translate-y-1/2 rounded-full"></div>
                <div className="absolute top-1/2 left-0 h-1 bg-[#1a6b3a] -z-10 -translate-y-1/2 transition-all duration-500 ease-out rounded-full" style={{width: `${((screen-1)/6)*100}%`}}></div>
                
                {[1,2,3,4,5,6,7].map(step => (
                  <div key={step} className={`w-5 h-5 sm:w-6 sm:h-6 rounded-full flex items-center justify-center text-[9px] sm:text-[10px] font-bold border-2 transition-all duration-500 ${screen > step ? 'bg-[#1a6b3a] border-[#1a6b3a] text-white shadow-sm' : screen === step ? 'bg-white border-[#1a6b3a] text-[#1a6b3a] ring-4 ring-green-50 shadow-sm scale-110' : 'bg-white border-gray-200 text-gray-300'}`}>
                    {screen > step ? <Check size={12} strokeWidth={4} /> : step}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Dynamic Screen Content */}
          <div className="flex-1 flex flex-col overflow-hidden relative bg-white">
            {renderScreen()}
          </div>

          {/* Home Indicator line */}
          <div className="h-7 bg-white w-full flex items-center justify-center pb-2 z-50">
            <div className="w-1/3 h-1.5 bg-gray-300 rounded-full"></div>
          </div>
        </div>
      </div>
    </>
  );
}

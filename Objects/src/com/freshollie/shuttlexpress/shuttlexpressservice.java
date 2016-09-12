package com.freshollie.shuttlexpress;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class shuttlexpressservice extends android.app.Service {
	public static class shuttlexpressservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, shuttlexpressservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static shuttlexpressservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return shuttlexpressservice.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "com.freshollie.shuttlexpress", "com.freshollie.shuttlexpress.shuttlexpressservice");
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        processBA.setActivityPaused(false);
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "com.freshollie.shuttlexpress.shuttlexpressservice", processBA, _service);
		}
        BA.LogInfo("** Service (shuttlexpressservice) Create **");
        processBA.raiseEvent(null, "service_create");
        processBA.runHook("oncreate", this, null);
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		handleStart(intent);
    }
    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
    	handleStart(intent);
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (shuttlexpressservice) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
        BA.LogInfo("** Service (shuttlexpressservice) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
        processBA.runHook("ondestroy", this, null);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper _usbmngr = null;
public static anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper _usbrequest = null;
public static int _usbpid = 0;
public static int _usbvid = 0;
public static int _packetsize = 0;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper _inendpoint = null;
public static anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper _connection = null;
public static com.rootsoft.broadcastreceiver.BroadCastReceiver _receiver = null;
public static int[] _lastcontrolvalues = null;
public static boolean _firstinput = false;
public static boolean _connected = false;
public static anywheresoftware.b4a.objects.NotificationWrapper _notification = null;
public static int _numthreads = 0;
public static long _ringrightdowntime = 0L;
public static long _ringleftdowntime = 0L;
public static long[] _buttondowntimes = null;
public static corsaro.sucommand.library.SuCommand _su = null;
public com.freshollie.shuttlexpress.main _main = null;
public static String  _broadcastreceiver_onreceive(String _action,Object _i) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
 //BA.debugLineNum = 50;BA.debugLine="Sub BroadcastReceiver_OnReceive(Action As String,i";
 //BA.debugLineNum = 51;BA.debugLine="Dim Intent1 As Intent = i";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
_intent1.setObject((android.content.Intent)(_i));
 //BA.debugLineNum = 52;BA.debugLine="Log(Action)";
anywheresoftware.b4a.keywords.Common.Log(_action);
 //BA.debugLineNum = 54;BA.debugLine="Select(Action)";
switch (BA.switchObjectToInt((_action),"android.hardware.usb.action.USB_DEVICE_DETACHED")) {
case 0:
 //BA.debugLineNum = 58;BA.debugLine="If Connection.IsInitialized Then";
if (_connection.IsInitialized()) { 
 //BA.debugLineNum = 59;BA.debugLine="CloseShuttle";
_closeshuttle();
 };
 break;
}
;
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _buttondown(int _button) throws Exception{
 //BA.debugLineNum = 240;BA.debugLine="Sub ButtonDown(Button As Int)";
 //BA.debugLineNum = 241;BA.debugLine="ButtonDownTimes(Button - 1) = DateTime.Now";
_buttondowntimes[(int) (_button-1)] = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 242;BA.debugLine="Log(\"ButtonDown\" & Button)";
anywheresoftware.b4a.keywords.Common.Log("ButtonDown"+BA.NumberToString(_button));
 //BA.debugLineNum = 243;BA.debugLine="End Sub";
return "";
}
public static String  _buttonup(int _button) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _in = null;
anywheresoftware.b4a.phone.PackageManagerWrapper _pm = null;
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread1 = null;
 //BA.debugLineNum = 245;BA.debugLine="Sub ButtonUp(Button As Int)";
 //BA.debugLineNum = 247;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 248;BA.debugLine="Dim pm As PackageManager";
_pm = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 250;BA.debugLine="Select(Button)";
switch (BA.switchObjectToInt((_button),(int) ((1)),(int) ((2)),(int) ((3)),(int) ((4)),(int) ((5)))) {
case 0:
 //BA.debugLineNum = 253;BA.debugLine="If (DateTime.Now - ButtonDownTimes(0)) < 300 The";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (0)])<300) { 
 //BA.debugLineNum = 254;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 255;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_ENTER";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_ENTER);
 //BA.debugLineNum = 257;BA.debugLine="Dim thread1 As Thread";
_thread1 = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 258;BA.debugLine="thread1.Initialise(\"KeySub\")";
_thread1.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 259;BA.debugLine="thread1.Start(\"KeySub\", Args)";
_thread1.Start("KeySub",_args);
 }else {
 //BA.debugLineNum = 262;BA.debugLine="in = pm.GetApplicationIntent(\"com.apple.android";
_in = _pm.GetApplicationIntent("com.apple.android.music");
 //BA.debugLineNum = 263;BA.debugLine="in.Action = in.ACTION_MAIN";
_in.setAction(_in.ACTION_MAIN);
 //BA.debugLineNum = 264;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 265;BA.debugLine="in.AddCategory(\"android.intent.category.LAUNCHE";
_in.AddCategory("android.intent.category.LAUNCHER");
 //BA.debugLineNum = 266;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 267;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 1:
 //BA.debugLineNum = 273;BA.debugLine="in = pm.GetApplicationIntent(\"com.freshollie.rad";
_in = _pm.GetApplicationIntent("com.freshollie.radioapp");
 //BA.debugLineNum = 274;BA.debugLine="in.Action = in.ACTION_MAIN";
_in.setAction(_in.ACTION_MAIN);
 //BA.debugLineNum = 275;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 276;BA.debugLine="in.AddCategory(\"android.intent.category.LAUNCHER";
_in.AddCategory("android.intent.category.LAUNCHER");
 //BA.debugLineNum = 277;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 278;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 break;
case 2:
 //BA.debugLineNum = 283;BA.debugLine="If (DateTime.Now - ButtonDownTimes(2)) > 3000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (2)])>3000) { 
 //BA.debugLineNum = 284;BA.debugLine="in = pm.GetApplicationIntent(\"au.com.shiftyjell";
_in = _pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts");
 //BA.debugLineNum = 285;BA.debugLine="in.Action = in.ACTION_MAIN";
_in.setAction(_in.ACTION_MAIN);
 //BA.debugLineNum = 286;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 287;BA.debugLine="in.AddCategory(\"android.intent.category.LAUNCHE";
_in.AddCategory("android.intent.category.LAUNCHER");
 //BA.debugLineNum = 288;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 289;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 }else {
 //BA.debugLineNum = 294;BA.debugLine="in = pm.GetApplicationIntent(\"au.com.shiftyjell";
_in = _pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts");
 //BA.debugLineNum = 295;BA.debugLine="in.Action = in.ACTION_MAIN";
_in.setAction(_in.ACTION_MAIN);
 //BA.debugLineNum = 296;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 298;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 299;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 3:
 //BA.debugLineNum = 305;BA.debugLine="If (DateTime.Now - ButtonDownTimes(3)) > 3000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (3)])>3000) { 
 //BA.debugLineNum = 306;BA.debugLine="in.Initialize(in.ACTION_VIEW, \"google.navigatio";
_in.Initialize(_in.ACTION_VIEW,"google.navigation:/?free=1&mode=d&entry=fnls");
 //BA.debugLineNum = 307;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 308;BA.debugLine="in.SetComponent(\"com.google.android.apps.maps/";
_in.SetComponent("com.google.android.apps.maps/com.google.android.maps.MapsActivity");
 //BA.debugLineNum = 309;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 }else {
 //BA.debugLineNum = 315;BA.debugLine="in = pm.GetApplicationIntent(\"com.google.androi";
_in = _pm.GetApplicationIntent("com.google.android.apps.maps");
 //BA.debugLineNum = 316;BA.debugLine="in.Action = in.ACTION_MAIN";
_in.setAction(_in.ACTION_MAIN);
 //BA.debugLineNum = 317;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 318;BA.debugLine="in.AddCategory(\"android.intent.category.LAUNCHE";
_in.AddCategory("android.intent.category.LAUNCHER");
 //BA.debugLineNum = 319;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 320;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 4:
 //BA.debugLineNum = 327;BA.debugLine="If (DateTime.Now - ButtonDownTimes(4)) < 1000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (4)])<1000) { 
 //BA.debugLineNum = 328;BA.debugLine="in.Initialize(in.ACTION_MAIN, \"\")";
_in.Initialize(_in.ACTION_MAIN,"");
 //BA.debugLineNum = 329;BA.debugLine="in.AddCategory(\"android.intent.category.HOME\")";
_in.AddCategory("android.intent.category.HOME");
 //BA.debugLineNum = 330;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 331;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 }else {
 //BA.debugLineNum = 334;BA.debugLine="in.Initialize(\"android.intent.action.VOICE_ASSI";
_in.Initialize("android.intent.action.VOICE_ASSIST","");
 //BA.debugLineNum = 335;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 break;
}
;
 //BA.debugLineNum = 340;BA.debugLine="Log(\"ButtonUp\" & Button)";
anywheresoftware.b4a.keywords.Common.Log("ButtonUp"+BA.NumberToString(_button));
 //BA.debugLineNum = 341;BA.debugLine="End Sub";
return "";
}
public static String  _closeshuttle() throws Exception{
 //BA.debugLineNum = 421;BA.debugLine="Sub CloseShuttle";
 //BA.debugLineNum = 422;BA.debugLine="If Connection.IsInitialized Then";
if (_connection.IsInitialized()) { 
 //BA.debugLineNum = 423;BA.debugLine="Connection.CloseSynchronous";
_connection.CloseSynchronous();
 //BA.debugLineNum = 424;BA.debugLine="Service.StopForeground(1)";
mostCurrent._service.StopForeground((int) (1));
 //BA.debugLineNum = 425;BA.debugLine="Notification.Cancel(1)";
_notification.Cancel((int) (1));
 };
 //BA.debugLineNum = 427;BA.debugLine="End Sub";
return "";
}
public static String  _connection_newdata(anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper _request,boolean _indirection) throws Exception{
int _i = 0;
byte[] _emptybuffer = null;
 //BA.debugLineNum = 137;BA.debugLine="Sub Connection_NewData (Request As UsbRequest, InD";
 //BA.debugLineNum = 139;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 140;BA.debugLine="For i = 0 To Request.Buffer.Length -1";
{
final int step88 = 1;
final int limit88 = (int) (_request.getBuffer().length-1);
for (_i = (int) (0); (step88 > 0 && _i <= limit88) || (step88 < 0 && _i >= limit88); _i = ((int)(0 + _i + step88))) {
 }
};
 //BA.debugLineNum = 144;BA.debugLine="ProcessByte(Request.Buffer)";
_processbyte(_request.getBuffer());
 //BA.debugLineNum = 146;BA.debugLine="If Connection.IsInitialized = False Then Return '";
if (_connection.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 148;BA.debugLine="Dim emptyBuffer(PacketSize) As Byte";
_emptybuffer = new byte[_packetsize];
;
 //BA.debugLineNum = 149;BA.debugLine="emptyBuffer(0) = 0";
_emptybuffer[(int) (0)] = (byte) (0);
 //BA.debugLineNum = 151;BA.debugLine="Try";
try { //BA.debugLineNum = 152;BA.debugLine="UsbRequest.Queue(emptyBuffer, PacketSize)";
_usbrequest.Queue(_emptybuffer,_packetsize);
 //BA.debugLineNum = 153;BA.debugLine="Connection.ContinueListening";
_connection.ContinueListening();
 } 
       catch (Exception e98) {
			processBA.setLastException(e98); //BA.debugLineNum = 156;BA.debugLine="CloseShuttle";
_closeshuttle();
 };
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
public static String  _jogwheelleft() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread1 = null;
 //BA.debugLineNum = 229;BA.debugLine="Sub JogWheelLeft()";
 //BA.debugLineNum = 230;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 231;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_DPAD_UP";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_DPAD_UP);
 //BA.debugLineNum = 233;BA.debugLine="Dim thread1 As Thread";
_thread1 = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 234;BA.debugLine="thread1.Initialise(\"KeySub\")";
_thread1.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 235;BA.debugLine="thread1.Start(\"KeySub\", Args)";
_thread1.Start("KeySub",_args);
 //BA.debugLineNum = 237;BA.debugLine="Log(\"JogWheelLeft\")";
anywheresoftware.b4a.keywords.Common.Log("JogWheelLeft");
 //BA.debugLineNum = 238;BA.debugLine="End Sub";
return "";
}
public static String  _jogwheelright() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread1 = null;
 //BA.debugLineNum = 218;BA.debugLine="Sub JogWheelRight()";
 //BA.debugLineNum = 219;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 220;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_TAB";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_TAB);
 //BA.debugLineNum = 222;BA.debugLine="Dim thread1 As Thread";
_thread1 = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 223;BA.debugLine="thread1.Initialise(\"KeySub\")";
_thread1.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 224;BA.debugLine="thread1.Start(\"KeySub\", Args)";
_thread1.Start("KeySub",_args);
 //BA.debugLineNum = 226;BA.debugLine="Log(\"JogWheelRight\")";
anywheresoftware.b4a.keywords.Common.Log("JogWheelRight");
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
return "";
}
public static String  _keysub(Object _keycode) throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Private Sub KeySub(KeyCode As Object)";
 //BA.debugLineNum = 163;BA.debugLine="If Numthreads > 10 Then Return";
if (_numthreads>10) { 
if (true) return "";};
 //BA.debugLineNum = 164;BA.debugLine="Numthreads = Numthreads + 1";
_numthreads = (int) (_numthreads+1);
 //BA.debugLineNum = 166;BA.debugLine="Su.SuCommand(\"input keyevent \" & KeyCode)";
_su.SuCommand("input keyevent "+BA.ObjectToString(_keycode));
 //BA.debugLineNum = 167;BA.debugLine="Log(KeyCode)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_keycode));
 //BA.debugLineNum = 168;BA.debugLine="Numthreads = Numthreads - 1";
_numthreads = (int) (_numthreads-1);
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _openshuttle() throws Exception{
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper[] _usbdevices = null;
int _i = 0;
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper _usbdvc = null;
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbInterfaceWrapper _interface = null;
byte[] _emptybuffer = null;
 //BA.debugLineNum = 69;BA.debugLine="Sub OpenShuttle";
 //BA.debugLineNum = 70;BA.debugLine="UsbMngr.Initialize";
_usbmngr.Initialize();
 //BA.debugLineNum = 72;BA.debugLine="Dim UsbDevices() As UsbDevice";
_usbdevices = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper[(int) (0)];
{
int d0 = _usbdevices.length;
for (int i0 = 0;i0 < d0;i0++) {
_usbdevices[i0] = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper();
}
}
;
 //BA.debugLineNum = 74;BA.debugLine="UsbDevices = UsbMngr.GetDevices";
_usbdevices = _usbmngr.GetDevices();
 //BA.debugLineNum = 78;BA.debugLine="If UsbDevices.Length > 0 Then";
if (_usbdevices.length>0) { 
 //BA.debugLineNum = 79;BA.debugLine="Log(UsbDevices.Length)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdevices.length));
 //BA.debugLineNum = 81;BA.debugLine="For i = 0 To UsbDevices.Length - 1";
{
final int step47 = 1;
final int limit47 = (int) (_usbdevices.length-1);
for (_i = (int) (0); (step47 > 0 && _i <= limit47) || (step47 < 0 && _i >= limit47); _i = ((int)(0 + _i + step47))) {
 //BA.debugLineNum = 82;BA.debugLine="Dim UsbDvc As UsbDevice";
_usbdvc = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper();
 //BA.debugLineNum = 83;BA.debugLine="UsbDvc = UsbDevices(i)";
_usbdvc = _usbdevices[_i];
 //BA.debugLineNum = 84;BA.debugLine="Log(UsbDvc.ProductId)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getProductId()));
 //BA.debugLineNum = 85;BA.debugLine="Log(UsbDvc.VendorId)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getVendorId()));
 //BA.debugLineNum = 87;BA.debugLine="If (UsbDvc.ProductId = UsbPid) And (UsbDvc.Vend";
if ((_usbdvc.getProductId()==_usbpid) && (_usbdvc.getVendorId()==_usbvid)) { 
 //BA.debugLineNum = 89;BA.debugLine="If Not(UsbMngr.HasPermission(UsbDvc)) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_usbmngr.HasPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject())))) { 
 //BA.debugLineNum = 90;BA.debugLine="UsbMngr.RequestPermission(UsbDvc) 'Always ins";
_usbmngr.RequestPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject()));
 };
 //BA.debugLineNum = 93;BA.debugLine="If UsbMngr.HasPermission(UsbDvc) Then";
if (_usbmngr.HasPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject()))) { 
 //BA.debugLineNum = 94;BA.debugLine="Dim interface As UsbInterface";
_interface = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbInterfaceWrapper();
 //BA.debugLineNum = 95;BA.debugLine="interface = UsbDvc.GetInterface(0)";
_interface.setObject((android.hardware.usb.UsbInterface)(_usbdvc.GetInterface((int) (0))));
 //BA.debugLineNum = 96;BA.debugLine="Log(interface)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_interface));
 //BA.debugLineNum = 97;BA.debugLine="Log(UsbMngr.USB_CLASS_HID)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbmngr.USB_CLASS_HID));
 //BA.debugLineNum = 98;BA.debugLine="Log(UsbDvc.DeviceClass)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getDeviceClass()));
 //BA.debugLineNum = 100;BA.debugLine="Connection = UsbMngr.OpenDevice(UsbDvc, inter";
_connection = _usbmngr.OpenDevice((android.hardware.usb.UsbDevice)(_usbdvc.getObject()),(android.hardware.usb.UsbInterface)(_interface.getObject()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 101;BA.debugLine="inEndpoint = interface.GetEndpoint(0)";
_inendpoint = _interface.GetEndpoint((int) (0));
 //BA.debugLineNum = 102;BA.debugLine="PacketSize = inEndpoint.MaxPacketSize";
_packetsize = _inendpoint.getMaxPacketSize();
 //BA.debugLineNum = 104;BA.debugLine="Log(\"Starting connection\")";
anywheresoftware.b4a.keywords.Common.Log("Starting connection");
 //BA.debugLineNum = 105;BA.debugLine="Connection.StartListening(\"connection\")";
_connection.StartListening(processBA,"connection");
 //BA.debugLineNum = 107;BA.debugLine="FirstInput = True";
_firstinput = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 109;BA.debugLine="UsbRequest.Initialize(Connection, inEndpoint)";
_usbrequest.Initialize(_connection,(android.hardware.usb.UsbEndpoint)(_inendpoint.getObject()));
 //BA.debugLineNum = 111;BA.debugLine="Dim emptyBuffer(PacketSize) As Byte";
_emptybuffer = new byte[_packetsize];
;
 //BA.debugLineNum = 113;BA.debugLine="emptyBuffer(0) = 0";
_emptybuffer[(int) (0)] = (byte) (0);
 //BA.debugLineNum = 115;BA.debugLine="UsbRequest.Queue(emptyBuffer, PacketSize)";
_usbrequest.Queue(_emptybuffer,_packetsize);
 //BA.debugLineNum = 117;BA.debugLine="Notification.Initialize";
_notification.Initialize();
 //BA.debugLineNum = 118;BA.debugLine="Notification.Icon = \"icon\"";
_notification.setIcon("icon");
 //BA.debugLineNum = 119;BA.debugLine="Notification.SetInfo(\"Shuttle Driver is runni";
_notification.SetInfo(processBA,"Shuttle Driver is running","",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 120;BA.debugLine="Notification.Sound = False";
_notification.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 121;BA.debugLine="Notification.Vibrate = False";
_notification.setVibrate(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 122;BA.debugLine="Notification.Notify(1)";
_notification.Notify((int) (1));
 //BA.debugLineNum = 124;BA.debugLine="Service.StartForeground(1 ,Notification)";
mostCurrent._service.StartForeground((int) (1),(android.app.Notification)(_notification.getObject()));
 //BA.debugLineNum = 126;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 131;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 };
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Dim UsbMngr As UsbManager";
_usbmngr = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper();
 //BA.debugLineNum = 7;BA.debugLine="Dim UsbRequest As UsbRequest";
_usbrequest = new anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper();
 //BA.debugLineNum = 8;BA.debugLine="Dim UsbPid As Int = 32";
_usbpid = (int) (32);
 //BA.debugLineNum = 9;BA.debugLine="Dim UsbVid As Int = 2867";
_usbvid = (int) (2867);
 //BA.debugLineNum = 11;BA.debugLine="Dim PacketSize As Int";
_packetsize = 0;
 //BA.debugLineNum = 13;BA.debugLine="Dim inEndpoint As UsbEndpoint";
_inendpoint = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim Connection As UsbDeviceConnection";
_connection = new anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim Receiver As BroadCastReceiver";
_receiver = new com.rootsoft.broadcastreceiver.BroadCastReceiver();
 //BA.debugLineNum = 18;BA.debugLine="Dim LastControlValues(8) As Int";
_lastcontrolvalues = new int[(int) (8)];
;
 //BA.debugLineNum = 20;BA.debugLine="Dim FirstInput As Boolean";
_firstinput = false;
 //BA.debugLineNum = 21;BA.debugLine="Dim Connected As Boolean";
_connected = false;
 //BA.debugLineNum = 23;BA.debugLine="Dim Notification As Notification";
_notification = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Numthreads As Int";
_numthreads = 0;
 //BA.debugLineNum = 26;BA.debugLine="Dim RingRightDownTime, RingLeftDownTime, ButtonDo";
_ringrightdowntime = 0L;
_ringleftdowntime = 0L;
_buttondowntimes = new long[(int) (5)];
;
 //BA.debugLineNum = 28;BA.debugLine="Dim Su As SuCommand";
_su = new corsaro.sucommand.library.SuCommand();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _processbyte(byte[] _bytes) throws Exception{
int _ring = 0;
int _jogwheel = 0;
int _button1234 = 0;
int _button5 = 0;
 //BA.debugLineNum = 344;BA.debugLine="Sub ProcessByte(Bytes() As Byte)";
 //BA.debugLineNum = 345;BA.debugLine="Dim Ring, JogWheel, Button1234, Button5 As Int";
_ring = 0;
_jogwheel = 0;
_button1234 = 0;
_button5 = 0;
 //BA.debugLineNum = 347;BA.debugLine="Ring = Bytes(0)";
_ring = (int) (_bytes[(int) (0)]);
 //BA.debugLineNum = 348;BA.debugLine="JogWheel = Bytes(1)";
_jogwheel = (int) (_bytes[(int) (1)]);
 //BA.debugLineNum = 349;BA.debugLine="Button1234 = Bytes(3)";
_button1234 = (int) (_bytes[(int) (3)]);
 //BA.debugLineNum = 350;BA.debugLine="Button5 = Bytes(4)";
_button5 = (int) (_bytes[(int) (4)]);
 //BA.debugLineNum = 352;BA.debugLine="If FirstInput <> True Then";
if (_firstinput!=anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 354;BA.debugLine="If Ring = 7 And LastControlValues(0) <> 7 Then";
if (_ring==7 && _lastcontrolvalues[(int) (0)]!=7) { 
 //BA.debugLineNum = 355;BA.debugLine="RingRightDown";
_ringrightdown();
 }else if(_ring!=7 && _lastcontrolvalues[(int) (0)]==7) { 
 //BA.debugLineNum = 358;BA.debugLine="RingRightUp";
_ringrightup();
 }else if(_ring==-7 && _lastcontrolvalues[(int) (0)]!=-7) { 
 //BA.debugLineNum = 361;BA.debugLine="RingLeftDown";
_ringleftdown();
 }else if(_ring!=-7 && _lastcontrolvalues[(int) (0)]==-7) { 
 //BA.debugLineNum = 364;BA.debugLine="RingLeftUp";
_ringleftup();
 }else if(_jogwheel!=_lastcontrolvalues[(int) (1)]) { 
 //BA.debugLineNum = 367;BA.debugLine="If JogWheel< 0 And LastControlValues(1) > 0 The";
if (_jogwheel<0 && _lastcontrolvalues[(int) (1)]>0) { 
 //BA.debugLineNum = 368;BA.debugLine="JogWheelRight";
_jogwheelright();
 }else if(_jogwheel>0 && _lastcontrolvalues[(int) (1)]<0) { 
 //BA.debugLineNum = 370;BA.debugLine="JogWheelLeft";
_jogwheelleft();
 }else if(_jogwheel>_lastcontrolvalues[(int) (1)]) { 
 //BA.debugLineNum = 372;BA.debugLine="JogWheelRight";
_jogwheelright();
 }else {
 //BA.debugLineNum = 374;BA.debugLine="JogWheelLeft";
_jogwheelleft();
 };
 }else if(_button1234==16 && _lastcontrolvalues[(int) (3)]!=16) { 
 //BA.debugLineNum = 378;BA.debugLine="ButtonDown(1)";
_buttondown((int) (1));
 }else if(_button1234!=16 && _lastcontrolvalues[(int) (3)]==16) { 
 //BA.debugLineNum = 381;BA.debugLine="ButtonUp(1)";
_buttonup((int) (1));
 }else if(_button1234==32 && _lastcontrolvalues[(int) (3)]!=32) { 
 //BA.debugLineNum = 384;BA.debugLine="ButtonDown(2)";
_buttondown((int) (2));
 }else if(_button1234!=32 && _lastcontrolvalues[(int) (3)]==32) { 
 //BA.debugLineNum = 387;BA.debugLine="ButtonUp(2)";
_buttonup((int) (2));
 }else if(_button1234==64 && _lastcontrolvalues[(int) (3)]!=64) { 
 //BA.debugLineNum = 390;BA.debugLine="ButtonDown(3)";
_buttondown((int) (3));
 }else if(_button1234!=64 && _lastcontrolvalues[(int) (3)]==64) { 
 //BA.debugLineNum = 393;BA.debugLine="ButtonUp(3)";
_buttonup((int) (3));
 }else if(_button1234==-128 && _lastcontrolvalues[(int) (3)]!=-128) { 
 //BA.debugLineNum = 396;BA.debugLine="ButtonDown(4)";
_buttondown((int) (4));
 }else if(_button1234!=-128 && _lastcontrolvalues[(int) (3)]==-128) { 
 //BA.debugLineNum = 399;BA.debugLine="ButtonUp(4)";
_buttonup((int) (4));
 }else if(_button5!=0 && _lastcontrolvalues[(int) (4)]==0) { 
 //BA.debugLineNum = 402;BA.debugLine="ButtonDown(5)";
_buttondown((int) (5));
 }else if(_button5==0 && _lastcontrolvalues[(int) (4)]!=0) { 
 //BA.debugLineNum = 405;BA.debugLine="ButtonUp(5)";
_buttonup((int) (5));
 };
 }else {
 //BA.debugLineNum = 410;BA.debugLine="FirstInput = False";
_firstinput = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 413;BA.debugLine="LastControlValues(0) = Ring";
_lastcontrolvalues[(int) (0)] = _ring;
 //BA.debugLineNum = 414;BA.debugLine="LastControlValues(1) = JogWheel";
_lastcontrolvalues[(int) (1)] = _jogwheel;
 //BA.debugLineNum = 415;BA.debugLine="LastControlValues(3) = Button1234";
_lastcontrolvalues[(int) (3)] = _button1234;
 //BA.debugLineNum = 416;BA.debugLine="LastControlValues(4) = Button5";
_lastcontrolvalues[(int) (4)] = _button5;
 //BA.debugLineNum = 418;BA.debugLine="End Sub";
return "";
}
public static String  _ringleftdown() throws Exception{
 //BA.debugLineNum = 178;BA.debugLine="Sub RingLeftDown()";
 //BA.debugLineNum = 179;BA.debugLine="Log(\"RingLeftDown\")";
anywheresoftware.b4a.keywords.Common.Log("RingLeftDown");
 //BA.debugLineNum = 180;BA.debugLine="RingLeftDownTime = DateTime.Now";
_ringleftdowntime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _ringleftup() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread1 = null;
 //BA.debugLineNum = 199;BA.debugLine="Sub RingLeftUp()";
 //BA.debugLineNum = 200;BA.debugLine="Log(\"RingLeftUp\")";
anywheresoftware.b4a.keywords.Common.Log("RingLeftUp");
 //BA.debugLineNum = 201;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 203;BA.debugLine="If (DateTime.Now - RingLeftDownTime) > 1000 Then";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_ringleftdowntime)>1000) { 
 //BA.debugLineNum = 204;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_BACK";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK);
 }else {
 //BA.debugLineNum = 207;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_MEDIA_PREVIOUS";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MEDIA_PREVIOUS);
 };
 //BA.debugLineNum = 212;BA.debugLine="Dim thread1 As Thread";
_thread1 = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 213;BA.debugLine="thread1.Initialise(\"KeySub\")";
_thread1.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 214;BA.debugLine="thread1.Start(\"KeySub\", Args)";
_thread1.Start("KeySub",_args);
 //BA.debugLineNum = 216;BA.debugLine="End Sub";
return "";
}
public static String  _ringrightdown() throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub RingRightDown()";
 //BA.debugLineNum = 173;BA.debugLine="Log(\"RingRightDown\")";
anywheresoftware.b4a.keywords.Common.Log("RingRightDown");
 //BA.debugLineNum = 174;BA.debugLine="RingRightDownTime = DateTime.Now";
_ringrightdowntime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _ringrightup() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 183;BA.debugLine="Sub RingRightUp()";
 //BA.debugLineNum = 184;BA.debugLine="Log(\"RingRightUp\")";
anywheresoftware.b4a.keywords.Common.Log("RingRightUp");
 //BA.debugLineNum = 185;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 187;BA.debugLine="If (DateTime.Now - RingRightDownTime) > 1000 Then";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_ringrightdowntime)>1000) { 
 }else {
 //BA.debugLineNum = 190;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_MEDIA_NEXT";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MEDIA_NEXT);
 //BA.debugLineNum = 191;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 192;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 193;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 };
 //BA.debugLineNum = 197;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 33;BA.debugLine="Receiver.Initialize(\"BroadcastReceiver\")";
_receiver.Initialize(processBA,"BroadcastReceiver");
 //BA.debugLineNum = 34;BA.debugLine="Receiver.addAction(\"android.hardware.usb.action.U";
_receiver.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
 //BA.debugLineNum = 35;BA.debugLine="Receiver.registerReceiver(\"\")";
_receiver.registerReceiver("");
 //BA.debugLineNum = 36;BA.debugLine="Notification.Initialize";
_notification.Initialize();
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 42;BA.debugLine="If Connection.IsInitialized <> True Then";
if (_connection.IsInitialized()!=anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 43;BA.debugLine="Connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 44;BA.debugLine="OpenShuttle";
_openshuttle();
 };
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
}

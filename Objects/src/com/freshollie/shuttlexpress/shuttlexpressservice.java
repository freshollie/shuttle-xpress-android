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
public static anywheresoftware.b4a.objects.UsbSerial _usb = null;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper _usbmngr = null;
public static anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper _usbrequest = null;
public static anywheresoftware.b4a.agraham.byteconverter.ByteConverter _byteconverter = null;
public static int _usbpid = 0;
public static int _usbvid = 0;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper _outendpoint = null;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper _inendpoint = null;
public static anywheresoftware.b4a.randomaccessfile.AsyncStreams _astreams = null;
public static anywheresoftware.b4a.objects.Timer _mytimer = null;
public static anywheresoftware.b4a.objects.Timer _sysready = null;
public static anywheresoftware.b4a.objects.Timer _usbtimer = null;
public static com.rootsoft.broadcastreceiver.BroadCastReceiver _broadcast = null;
public static boolean _start = false;
public static boolean _connected = false;
public static boolean _googlemapsdrivingmode = false;
public static int[] _responsebytes = null;
public static int _dev = 0;
public static int _xindex = 0;
public static int _packetsize = 0;
public static anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper _connection = null;
public static anywheresoftware.b4a.objects.collections.List _inrequests = null;
public static anywheresoftware.b4a.objects.collections.List _outrequests = null;
public static int[] _lastcontrolvalues = null;
public static de.jondroid.keygen.Keygen _key = null;
public static boolean _firstinput = false;
public static boolean _firststart = false;
public static anywheresoftware.b4a.objects.NotificationWrapper _notification = null;
public static com.rootsoft.broadcastreceiver.BroadCastReceiver _receiver = null;
public static com.omnicorp.media.MediaController _mediakey = null;
public static int _numthreads = 0;
public static long _doubleclicktimer = 0L;
public static long _ringrightdowntime = 0L;
public static long _ringleftdowntime = 0L;
public static long[] _buttondowntimes = null;
public static corsaro.sucommand.library.SuCommand _su = null;
public com.freshollie.shuttlexpress.main _main = null;
public static String  _broadcastreceiver_onreceive(String _action,Object _i) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _intent1 = null;
 //BA.debugLineNum = 439;BA.debugLine="Sub BroadcastReceiver_OnReceive(Action As String,i";
 //BA.debugLineNum = 440;BA.debugLine="Dim Intent1 As Intent = i";
_intent1 = new anywheresoftware.b4a.objects.IntentWrapper();
_intent1.setObject((android.content.Intent)(_i));
 //BA.debugLineNum = 441;BA.debugLine="Log(Action)";
anywheresoftware.b4a.keywords.Common.Log(_action);
 //BA.debugLineNum = 443;BA.debugLine="If Intent1.HasExtra(\"device\") Then";
if (_intent1.HasExtra("device")) { 
 //BA.debugLineNum = 444;BA.debugLine="If USB.UsbPresent(Dev) = USB.USB_NONE Then";
if (_usb.UsbPresent(_dev)==_usb.USB_NONE) { 
 //BA.debugLineNum = 445;BA.debugLine="ExitApp";
_exitapp();
 };
 };
 //BA.debugLineNum = 450;BA.debugLine="If Action = \"com.freshollie.shuttlexpress.intent.";
if ((_action).equals("com.freshollie.shuttlexpress.intent.START")) { 
 //BA.debugLineNum = 451;BA.debugLine="OpenShuttle";
_openshuttle();
 };
 //BA.debugLineNum = 453;BA.debugLine="End Sub";
return "";
}
public static String  _buttondown(int _button) throws Exception{
 //BA.debugLineNum = 254;BA.debugLine="Sub ButtonDown(Button As Int)";
 //BA.debugLineNum = 255;BA.debugLine="ButtonDownTimes(Button - 1) = DateTime.Now";
_buttondowntimes[(int) (_button-1)] = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 256;BA.debugLine="Log(\"ButtonDown\" & Button)";
anywheresoftware.b4a.keywords.Common.Log("ButtonDown"+BA.NumberToString(_button));
 //BA.debugLineNum = 257;BA.debugLine="End Sub";
return "";
}
public static String  _buttonup(int _button) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _in = null;
anywheresoftware.b4a.phone.PackageManagerWrapper _pm = null;
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 259;BA.debugLine="Sub ButtonUp(Button As Int)";
 //BA.debugLineNum = 261;BA.debugLine="Dim in As Intent";
_in = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 262;BA.debugLine="Dim pm As PackageManager";
_pm = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 264;BA.debugLine="Select(Button)";
switch (BA.switchObjectToInt((_button),(int) ((1)),(int) ((2)),(int) ((3)),(int) ((4)),(int) ((5)))) {
case 0:
 //BA.debugLineNum = 267;BA.debugLine="If (DateTime.Now - ButtonDownTimes(0)) < 300 The";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (0)])<300) { 
 //BA.debugLineNum = 268;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 269;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_ENTER";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_ENTER);
 //BA.debugLineNum = 271;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 272;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 273;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 }else {
 //BA.debugLineNum = 276;BA.debugLine="in.Initialize(in.ACTION_MAIN, \"\")";
_in.Initialize(_in.ACTION_MAIN,"");
 //BA.debugLineNum = 277;BA.debugLine="in = pm.GetApplicationIntent(\"com.google.androi";
_in = _pm.GetApplicationIntent("com.google.android.music");
 //BA.debugLineNum = 278;BA.debugLine="in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRO";
_in.setFlags((int) (131072));
 //BA.debugLineNum = 279;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 280;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 1:
 //BA.debugLineNum = 286;BA.debugLine="in.Initialize(in.ACTION_MAIN, \"\")";
_in.Initialize(_in.ACTION_MAIN,"");
 //BA.debugLineNum = 287;BA.debugLine="in = pm.GetApplicationIntent(\"com.freshollie.rad";
_in = _pm.GetApplicationIntent("com.freshollie.radioapp");
 //BA.debugLineNum = 288;BA.debugLine="in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRON";
_in.setFlags((int) (131072));
 //BA.debugLineNum = 289;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 290;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 break;
case 2:
 //BA.debugLineNum = 295;BA.debugLine="If (DateTime.Now - ButtonDownTimes(2)) > 3000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (2)])>3000) { 
 //BA.debugLineNum = 296;BA.debugLine="in = pm.GetApplicationIntent(\"au.com.shiftyjell";
_in = _pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts");
 //BA.debugLineNum = 297;BA.debugLine="in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRO";
_in.setFlags((int) (131072));
 //BA.debugLineNum = 298;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 299;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 }else {
 //BA.debugLineNum = 304;BA.debugLine="in = pm.GetApplicationIntent(\"au.com.shiftyjell";
_in = _pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts");
 //BA.debugLineNum = 305;BA.debugLine="in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRO";
_in.setFlags((int) (131072));
 //BA.debugLineNum = 306;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 307;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 3:
 //BA.debugLineNum = 313;BA.debugLine="If (DateTime.Now - ButtonDownTimes(3)) > 3000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (3)])>3000) { 
 //BA.debugLineNum = 314;BA.debugLine="in.Initialize(in.ACTION_VIEW, \"google.navigatio";
_in.Initialize(_in.ACTION_VIEW,"google.navigation:/?free=1&mode=d&entry=fnls");
 //BA.debugLineNum = 315;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 316;BA.debugLine="in.SetComponent(\"com.google.android.apps.maps/";
_in.SetComponent("com.google.android.apps.maps/com.google.android.maps.MapsActivity");
 //BA.debugLineNum = 317;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 }else {
 //BA.debugLineNum = 323;BA.debugLine="in.Initialize(in.ACTION_MAIN, \"\")";
_in.Initialize(_in.ACTION_MAIN,"");
 //BA.debugLineNum = 324;BA.debugLine="in = pm.GetApplicationIntent(\"com.google.androi";
_in = _pm.GetApplicationIntent("com.google.android.apps.maps");
 //BA.debugLineNum = 325;BA.debugLine="in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRO";
_in.setFlags((int) (131072));
 //BA.debugLineNum = 326;BA.debugLine="If in.IsInitialized Then";
if (_in.IsInitialized()) { 
 //BA.debugLineNum = 327;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 };
 break;
case 4:
 //BA.debugLineNum = 334;BA.debugLine="If (DateTime.Now - ButtonDownTimes(4)) < 1000 Th";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_buttondowntimes[(int) (4)])<1000) { 
 //BA.debugLineNum = 335;BA.debugLine="in.Initialize(in.ACTION_MAIN, \"\")";
_in.Initialize(_in.ACTION_MAIN,"");
 //BA.debugLineNum = 336;BA.debugLine="in.AddCategory(\"android.intent.category.HOME\")";
_in.AddCategory("android.intent.category.HOME");
 //BA.debugLineNum = 337;BA.debugLine="in.Flags = 0x10000000";
_in.setFlags((int) (0x10000000));
 //BA.debugLineNum = 338;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 }else {
 //BA.debugLineNum = 341;BA.debugLine="in.Initialize(\"android.intent.action.VOICE_ASSI";
_in.Initialize("android.intent.action.VOICE_ASSIST","");
 //BA.debugLineNum = 342;BA.debugLine="StartActivity(in)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_in.getObject()));
 };
 break;
}
;
 //BA.debugLineNum = 347;BA.debugLine="Log(\"ButtonUp\" & Button)";
anywheresoftware.b4a.keywords.Common.Log("ButtonUp"+BA.NumberToString(_button));
 //BA.debugLineNum = 348;BA.debugLine="End Sub";
return "";
}
public static String  _connection_newdata(anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper _request,boolean _indirection) throws Exception{
int _i = 0;
byte[] _emptybuffer = null;
 //BA.debugLineNum = 148;BA.debugLine="Sub Connection_NewData (Request As UsbRequest, InD";
 //BA.debugLineNum = 150;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 151;BA.debugLine="For i = 0 To Request.Buffer.Length -1";
{
final int step103 = 1;
final int limit103 = (int) (_request.getBuffer().length-1);
for (_i = (int) (0); (step103 > 0 && _i <= limit103) || (step103 < 0 && _i >= limit103); _i = ((int)(0 + _i + step103))) {
 }
};
 //BA.debugLineNum = 155;BA.debugLine="ProcessByte(Request.Buffer)";
_processbyte(_request.getBuffer());
 //BA.debugLineNum = 157;BA.debugLine="If Connection.IsInitialized = False Then Return '";
if (_connection.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 159;BA.debugLine="Dim emptyBuffer(PacketSize) As Byte";
_emptybuffer = new byte[_packetsize];
;
 //BA.debugLineNum = 160;BA.debugLine="emptyBuffer(0) = 0";
_emptybuffer[(int) (0)] = (byte) (0);
 //BA.debugLineNum = 162;BA.debugLine="Try";
try { //BA.debugLineNum = 163;BA.debugLine="UsbRequest.Queue(emptyBuffer, PacketSize)";
_usbrequest.Queue(_emptybuffer,_packetsize);
 //BA.debugLineNum = 164;BA.debugLine="Connection.ContinueListening";
_connection.ContinueListening();
 } 
       catch (Exception e113) {
			processBA.setLastException(e113); //BA.debugLineNum = 167;BA.debugLine="ExitApp";
_exitapp();
 };
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
return "";
}
public static String  _executeasroot(String _command) throws Exception{
String _runner = "";
anywheresoftware.b4a.keywords.StringBuilderWrapper _stdout = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _stderr = null;
int _result = 0;
anywheresoftware.b4a.phone.Phone _ph = null;
 //BA.debugLineNum = 42;BA.debugLine="Sub ExecuteAsRoot (Command As String)";
 //BA.debugLineNum = 43;BA.debugLine="Dim Command, Runner As String";
_command = "";
_runner = "";
 //BA.debugLineNum = 44;BA.debugLine="Dim StdOut, StdErr As StringBuilder";
_stdout = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
_stderr = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 46;BA.debugLine="Dim Ph As Phone";
_ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 49;BA.debugLine="Runner = File.Combine(File.DirInternalCache, \"r";
_runner = anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"runner");
 //BA.debugLineNum = 50;BA.debugLine="Command = File.Combine(File.DirInternalCache, \"";
_command = anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"command");
 //BA.debugLineNum = 51;BA.debugLine="File.WriteString(File.DirInternalCache, \"runner";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"runner","su < "+_command);
 //BA.debugLineNum = 52;BA.debugLine="File.WriteString(File.DirInternalCache, \"comman";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"command",_command+anywheresoftware.b4a.keywords.Common.CRLF+"exit");
 //BA.debugLineNum = 53;BA.debugLine="StdOut.Initialize";
_stdout.Initialize();
 //BA.debugLineNum = 54;BA.debugLine="StdErr.Initialize";
_stderr.Initialize();
 //BA.debugLineNum = 55;BA.debugLine="Result = Ph.Shell(\"sh\", Array As String(Runner)";
_result = _ph.Shell("sh",new String[]{_runner},(java.lang.StringBuilder)(_stdout.getObject()),(java.lang.StringBuilder)(_stderr.getObject()));
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _exitapp() throws Exception{
 //BA.debugLineNum = 429;BA.debugLine="Sub ExitApp";
 //BA.debugLineNum = 430;BA.debugLine="If Connection.IsInitialized Then Connection.Close";
if (_connection.IsInitialized()) { 
_connection.CloseSynchronous();};
 //BA.debugLineNum = 431;BA.debugLine="Service.StopForeground(1)";
mostCurrent._service.StopForeground((int) (1));
 //BA.debugLineNum = 432;BA.debugLine="Notification.Cancel(1)";
_notification.Cancel((int) (1));
 //BA.debugLineNum = 433;BA.debugLine="Connection.CloseSynchronous";
_connection.CloseSynchronous();
 //BA.debugLineNum = 434;BA.debugLine="Log(\"Closing App\")";
anywheresoftware.b4a.keywords.Common.Log("Closing App");
 //BA.debugLineNum = 435;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 437;BA.debugLine="End Sub";
return "";
}
public static String  _jogwheelleft() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 243;BA.debugLine="Sub JogWheelLeft()";
 //BA.debugLineNum = 244;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 245;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_DPAD_UP";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_DPAD_UP);
 //BA.debugLineNum = 247;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 248;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 249;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 //BA.debugLineNum = 251;BA.debugLine="Log(\"JogWheelLeft\")";
anywheresoftware.b4a.keywords.Common.Log("JogWheelLeft");
 //BA.debugLineNum = 252;BA.debugLine="End Sub";
return "";
}
public static String  _jogwheelright() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 232;BA.debugLine="Sub JogWheelRight()";
 //BA.debugLineNum = 233;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 234;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_TAB";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_TAB);
 //BA.debugLineNum = 236;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 237;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 238;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 //BA.debugLineNum = 240;BA.debugLine="Log(\"JogWheelRight\")";
anywheresoftware.b4a.keywords.Common.Log("JogWheelRight");
 //BA.debugLineNum = 241;BA.debugLine="End Sub";
return "";
}
public static String  _keysub(Object _keycode) throws Exception{
 //BA.debugLineNum = 173;BA.debugLine="Private Sub KeySub(KeyCode As Object)";
 //BA.debugLineNum = 174;BA.debugLine="If Numthreads > 10 Then Return";
if (_numthreads>10) { 
if (true) return "";};
 //BA.debugLineNum = 175;BA.debugLine="Numthreads = Numthreads + 1";
_numthreads = (int) (_numthreads+1);
 //BA.debugLineNum = 177;BA.debugLine="Su.SuCommand(\"input keyevent \" & KeyCode)";
_su.SuCommand("input keyevent "+BA.ObjectToString(_keycode));
 //BA.debugLineNum = 178;BA.debugLine="Log(KeyCode)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_keycode));
 //BA.debugLineNum = 179;BA.debugLine="Numthreads = Numthreads - 1";
_numthreads = (int) (_numthreads-1);
 //BA.debugLineNum = 184;BA.debugLine="End Sub";
return "";
}
public static String  _openshuttle() throws Exception{
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper[] _usbdevices = null;
int _i = 0;
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper _usbdvc = null;
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbInterfaceWrapper _interface = null;
byte[] _emptybuffer = null;
 //BA.debugLineNum = 80;BA.debugLine="Sub OpenShuttle";
 //BA.debugLineNum = 81;BA.debugLine="UsbMngr.Initialize";
_usbmngr.Initialize();
 //BA.debugLineNum = 82;BA.debugLine="Dim UsbDevices() As UsbDevice  ' USB library";
_usbdevices = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper[(int) (0)];
{
int d0 = _usbdevices.length;
for (int i0 = 0;i0 < d0;i0++) {
_usbdevices[i0] = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper();
}
}
;
 //BA.debugLineNum = 84;BA.debugLine="UsbDevices = UsbMngr.GetDevices";
_usbdevices = _usbmngr.GetDevices();
 //BA.debugLineNum = 88;BA.debugLine="If UsbDevices.Length > 0 Then";
if (_usbdevices.length>0) { 
 //BA.debugLineNum = 89;BA.debugLine="Log(UsbDevices.Length)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdevices.length));
 //BA.debugLineNum = 91;BA.debugLine="For i = 0 To UsbDevices.Length - 1";
{
final int step60 = 1;
final int limit60 = (int) (_usbdevices.length-1);
for (_i = (int) (0); (step60 > 0 && _i <= limit60) || (step60 < 0 && _i >= limit60); _i = ((int)(0 + _i + step60))) {
 //BA.debugLineNum = 92;BA.debugLine="Dim UsbDvc As UsbDevice";
_usbdvc = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper();
 //BA.debugLineNum = 93;BA.debugLine="UsbDvc = UsbDevices(i)";
_usbdvc = _usbdevices[_i];
 //BA.debugLineNum = 94;BA.debugLine="Log(UsbDvc.ProductId)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getProductId()));
 //BA.debugLineNum = 95;BA.debugLine="Log(UsbDvc.VendorId)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getVendorId()));
 //BA.debugLineNum = 97;BA.debugLine="If (UsbDvc.ProductId = UsbPid) And (UsbDvc.Ven";
if ((_usbdvc.getProductId()==_usbpid) && (_usbdvc.getVendorId()==_usbvid)) { 
 //BA.debugLineNum = 98;BA.debugLine="USB.SetCustomDevice(USB.DRIVER_CDCACM, UsbVid,";
_usb.SetCustomDevice(_usb.DRIVER_CDCACM,_usbvid,_usbpid);
 //BA.debugLineNum = 100;BA.debugLine="If Not(UsbMngr.HasPermission(UsbDvc)) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_usbmngr.HasPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject())))) { 
 //BA.debugLineNum = 101;BA.debugLine="UsbMngr.RequestPermission(UsbDvc)";
_usbmngr.RequestPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject()));
 };
 //BA.debugLineNum = 104;BA.debugLine="If UsbMngr.HasPermission(UsbDvc) Then";
if (_usbmngr.HasPermission((android.hardware.usb.UsbDevice)(_usbdvc.getObject()))) { 
 //BA.debugLineNum = 105;BA.debugLine="Dim interface As UsbInterface";
_interface = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbInterfaceWrapper();
 //BA.debugLineNum = 106;BA.debugLine="interface = UsbDvc.GetInterface(i)";
_interface.setObject((android.hardware.usb.UsbInterface)(_usbdvc.GetInterface(_i)));
 //BA.debugLineNum = 107;BA.debugLine="Log(interface)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_interface));
 //BA.debugLineNum = 108;BA.debugLine="Log(UsbMngr.USB_CLASS_HID)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbmngr.USB_CLASS_HID));
 //BA.debugLineNum = 109;BA.debugLine="Log(UsbDvc.DeviceClass)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_usbdvc.getDeviceClass()));
 //BA.debugLineNum = 111;BA.debugLine="Connection = UsbMngr.OpenDevice(UsbDvc, inter";
_connection = _usbmngr.OpenDevice((android.hardware.usb.UsbDevice)(_usbdvc.getObject()),(android.hardware.usb.UsbInterface)(_interface.getObject()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="inEndpoint = interface.GetEndpoint(0)";
_inendpoint = _interface.GetEndpoint((int) (0));
 //BA.debugLineNum = 113;BA.debugLine="PacketSize = inEndpoint.MaxPacketSize";
_packetsize = _inendpoint.getMaxPacketSize();
 //BA.debugLineNum = 115;BA.debugLine="Log(\"Starting connection\")";
anywheresoftware.b4a.keywords.Common.Log("Starting connection");
 //BA.debugLineNum = 116;BA.debugLine="Connection.StartListening(\"connection\")";
_connection.StartListening(processBA,"connection");
 //BA.debugLineNum = 118;BA.debugLine="FirstInput = True";
_firstinput = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 120;BA.debugLine="UsbRequest.Initialize(Connection, inEndpoint)";
_usbrequest.Initialize(_connection,(android.hardware.usb.UsbEndpoint)(_inendpoint.getObject()));
 //BA.debugLineNum = 122;BA.debugLine="Dim emptyBuffer(PacketSize) As Byte";
_emptybuffer = new byte[_packetsize];
;
 //BA.debugLineNum = 124;BA.debugLine="emptyBuffer(0) = 0";
_emptybuffer[(int) (0)] = (byte) (0);
 //BA.debugLineNum = 125;BA.debugLine="UsbRequest.Queue(emptyBuffer, PacketSize)";
_usbrequest.Queue(_emptybuffer,_packetsize);
 //BA.debugLineNum = 127;BA.debugLine="Notification.Initialize";
_notification.Initialize();
 //BA.debugLineNum = 128;BA.debugLine="Notification.Icon = \"icon\"";
_notification.setIcon("icon");
 //BA.debugLineNum = 129;BA.debugLine="Notification.SetInfo(\"Shuttle Driver is runni";
_notification.SetInfo(processBA,"Shuttle Driver is running","",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 131;BA.debugLine="Notification.Sound = False";
_notification.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 132;BA.debugLine="Notification.Vibrate = False";
_notification.setVibrate(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 133;BA.debugLine="Notification.Notify(1)";
_notification.Notify((int) (1));
 //BA.debugLineNum = 135;BA.debugLine="Service.StartForeground(1 ,Notification)";
mostCurrent._service.StartForeground((int) (1),(android.app.Notification)(_notification.getObject()));
 //BA.debugLineNum = 136;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 141;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 };
 //BA.debugLineNum = 145;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Dim USB As UsbSerial";
_usb = new anywheresoftware.b4a.objects.UsbSerial();
 //BA.debugLineNum = 7;BA.debugLine="Dim UsbMngr As UsbManager";
_usbmngr = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper();
 //BA.debugLineNum = 8;BA.debugLine="Dim UsbRequest As UsbRequest";
_usbrequest = new anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper.UsbRequestWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim ByteConverter As ByteConverter";
_byteconverter = new anywheresoftware.b4a.agraham.byteconverter.ByteConverter();
 //BA.debugLineNum = 10;BA.debugLine="Dim UsbPid As Int = 32";
_usbpid = (int) (32);
 //BA.debugLineNum = 11;BA.debugLine="Dim UsbVid As Int = 2867";
_usbvid = (int) (2867);
 //BA.debugLineNum = 12;BA.debugLine="Dim outEndpoint, inEndpoint As UsbEndpoint";
_outendpoint = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper();
_inendpoint = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbEndpointWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim astreams As AsyncStreams";
_astreams = new anywheresoftware.b4a.randomaccessfile.AsyncStreams();
 //BA.debugLineNum = 15;BA.debugLine="Dim MyTimer, SysReady, USBTimer As Timer";
_mytimer = new anywheresoftware.b4a.objects.Timer();
_sysready = new anywheresoftware.b4a.objects.Timer();
_usbtimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 16;BA.debugLine="Dim Broadcast As BroadCastReceiver";
_broadcast = new com.rootsoft.broadcastreceiver.BroadCastReceiver();
 //BA.debugLineNum = 18;BA.debugLine="Dim Start, Connected, GoogleMapsDrivingMode As Bo";
_start = false;
_connected = false;
_googlemapsdrivingmode = false;
 //BA.debugLineNum = 19;BA.debugLine="Dim ResponseBytes(1024), Dev, xIndex, PacketSize";
_responsebytes = new int[(int) (1024)];
;
_dev = 0;
_xindex = 0;
_packetsize = 0;
 //BA.debugLineNum = 21;BA.debugLine="Dim Connection As UsbDeviceConnection";
_connection = new anywheresoftware.b4a.objects.usb.UsbDeviceConnectionWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim InRequests, OutRequests As List";
_inrequests = new anywheresoftware.b4a.objects.collections.List();
_outrequests = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="Dim LastControlValues(8) As Int";
_lastcontrolvalues = new int[(int) (8)];
;
 //BA.debugLineNum = 26;BA.debugLine="Dim Key As KeyGen";
_key = new de.jondroid.keygen.Keygen();
 //BA.debugLineNum = 28;BA.debugLine="Dim FirstInput, FirstStart As Boolean";
_firstinput = false;
_firststart = false;
 //BA.debugLineNum = 30;BA.debugLine="Dim Notification As Notification";
_notification = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim Receiver As BroadCastReceiver";
_receiver = new com.rootsoft.broadcastreceiver.BroadCastReceiver();
 //BA.debugLineNum = 32;BA.debugLine="Dim Mediakey As MediaController";
_mediakey = new com.omnicorp.media.MediaController();
 //BA.debugLineNum = 33;BA.debugLine="Dim Numthreads As Int";
_numthreads = 0;
 //BA.debugLineNum = 34;BA.debugLine="Dim DoubleClickTimer As Long";
_doubleclicktimer = 0L;
 //BA.debugLineNum = 36;BA.debugLine="Dim RingRightDownTime, RingLeftDownTime, ButtonDo";
_ringrightdowntime = 0L;
_ringleftdowntime = 0L;
_buttondowntimes = new long[(int) (5)];
;
 //BA.debugLineNum = 38;BA.debugLine="Dim Su As SuCommand";
_su = new corsaro.sucommand.library.SuCommand();
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
public static String  _processbyte(byte[] _bytes) throws Exception{
int _ring = 0;
int _jogwheel = 0;
int _button1234 = 0;
int _button5 = 0;
 //BA.debugLineNum = 351;BA.debugLine="Sub ProcessByte(Bytes() As Byte)";
 //BA.debugLineNum = 352;BA.debugLine="Dim Ring, JogWheel, Button1234, Button5 As Int";
_ring = 0;
_jogwheel = 0;
_button1234 = 0;
_button5 = 0;
 //BA.debugLineNum = 354;BA.debugLine="Ring = Bytes(0)";
_ring = (int) (_bytes[(int) (0)]);
 //BA.debugLineNum = 355;BA.debugLine="JogWheel = Bytes(1)";
_jogwheel = (int) (_bytes[(int) (1)]);
 //BA.debugLineNum = 356;BA.debugLine="Button1234 = Bytes(3)";
_button1234 = (int) (_bytes[(int) (3)]);
 //BA.debugLineNum = 357;BA.debugLine="Button5 = Bytes(4)";
_button5 = (int) (_bytes[(int) (4)]);
 //BA.debugLineNum = 359;BA.debugLine="If FirstInput <> True Then";
if (_firstinput!=anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 361;BA.debugLine="If Ring = 7 And LastControlValues(0) <> 7 Then";
if (_ring==7 && _lastcontrolvalues[(int) (0)]!=7) { 
 //BA.debugLineNum = 362;BA.debugLine="RingRightDown";
_ringrightdown();
 }else if(_ring!=7 && _lastcontrolvalues[(int) (0)]==7) { 
 //BA.debugLineNum = 365;BA.debugLine="RingRightUp";
_ringrightup();
 }else if(_ring==-7 && _lastcontrolvalues[(int) (0)]!=-7) { 
 //BA.debugLineNum = 368;BA.debugLine="RingLeftDown";
_ringleftdown();
 }else if(_ring!=-7 && _lastcontrolvalues[(int) (0)]==-7) { 
 //BA.debugLineNum = 371;BA.debugLine="RingLeftUp";
_ringleftup();
 }else if(_jogwheel!=_lastcontrolvalues[(int) (1)]) { 
 //BA.debugLineNum = 374;BA.debugLine="If JogWheel< 0 And LastControlValues(1) > 0 The";
if (_jogwheel<0 && _lastcontrolvalues[(int) (1)]>0) { 
 //BA.debugLineNum = 375;BA.debugLine="JogWheelRight";
_jogwheelright();
 }else if(_jogwheel>0 && _lastcontrolvalues[(int) (1)]<0) { 
 //BA.debugLineNum = 377;BA.debugLine="JogWheelLeft";
_jogwheelleft();
 }else if(_jogwheel>_lastcontrolvalues[(int) (1)]) { 
 //BA.debugLineNum = 379;BA.debugLine="JogWheelRight";
_jogwheelright();
 }else {
 //BA.debugLineNum = 381;BA.debugLine="JogWheelLeft";
_jogwheelleft();
 };
 }else if(_button1234==16 && _lastcontrolvalues[(int) (3)]!=16) { 
 //BA.debugLineNum = 385;BA.debugLine="ButtonDown(1)";
_buttondown((int) (1));
 }else if(_button1234!=16 && _lastcontrolvalues[(int) (3)]==16) { 
 //BA.debugLineNum = 388;BA.debugLine="ButtonUp(1)";
_buttonup((int) (1));
 }else if(_button1234==32 && _lastcontrolvalues[(int) (3)]!=32) { 
 //BA.debugLineNum = 391;BA.debugLine="ButtonDown(2)";
_buttondown((int) (2));
 }else if(_button1234!=32 && _lastcontrolvalues[(int) (3)]==32) { 
 //BA.debugLineNum = 394;BA.debugLine="ButtonUp(2)";
_buttonup((int) (2));
 }else if(_button1234==64 && _lastcontrolvalues[(int) (3)]!=64) { 
 //BA.debugLineNum = 397;BA.debugLine="ButtonDown(3)";
_buttondown((int) (3));
 }else if(_button1234!=64 && _lastcontrolvalues[(int) (3)]==64) { 
 //BA.debugLineNum = 400;BA.debugLine="ButtonUp(3)";
_buttonup((int) (3));
 }else if(_button1234==-128 && _lastcontrolvalues[(int) (3)]!=-128) { 
 //BA.debugLineNum = 403;BA.debugLine="ButtonDown(4)";
_buttondown((int) (4));
 }else if(_button1234!=-128 && _lastcontrolvalues[(int) (3)]==-128) { 
 //BA.debugLineNum = 406;BA.debugLine="ButtonUp(4)";
_buttonup((int) (4));
 }else if(_button5!=0 && _lastcontrolvalues[(int) (4)]==0) { 
 //BA.debugLineNum = 409;BA.debugLine="ButtonDown(5)";
_buttondown((int) (5));
 }else if(_button5==0 && _lastcontrolvalues[(int) (4)]!=0) { 
 //BA.debugLineNum = 412;BA.debugLine="ButtonUp(5)";
_buttonup((int) (5));
 };
 }else {
 //BA.debugLineNum = 417;BA.debugLine="FirstInput = False";
_firstinput = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 422;BA.debugLine="LastControlValues(0) = Ring";
_lastcontrolvalues[(int) (0)] = _ring;
 //BA.debugLineNum = 423;BA.debugLine="LastControlValues(1) = JogWheel";
_lastcontrolvalues[(int) (1)] = _jogwheel;
 //BA.debugLineNum = 424;BA.debugLine="LastControlValues(3) = Button1234";
_lastcontrolvalues[(int) (3)] = _button1234;
 //BA.debugLineNum = 425;BA.debugLine="LastControlValues(4) = Button5";
_lastcontrolvalues[(int) (4)] = _button5;
 //BA.debugLineNum = 427;BA.debugLine="End Sub";
return "";
}
public static String  _ringleftdown() throws Exception{
 //BA.debugLineNum = 192;BA.debugLine="Sub RingLeftDown()";
 //BA.debugLineNum = 193;BA.debugLine="Log(\"RingLeftDown\")";
anywheresoftware.b4a.keywords.Common.Log("RingLeftDown");
 //BA.debugLineNum = 194;BA.debugLine="RingLeftDownTime = DateTime.Now";
_ringleftdowntime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 195;BA.debugLine="End Sub";
return "";
}
public static String  _ringleftup() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 213;BA.debugLine="Sub RingLeftUp()";
 //BA.debugLineNum = 214;BA.debugLine="Log(\"RingLeftUp\")";
anywheresoftware.b4a.keywords.Common.Log("RingLeftUp");
 //BA.debugLineNum = 215;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 217;BA.debugLine="If (DateTime.Now - RingLeftDownTime) > 300 Then";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_ringleftdowntime)>300) { 
 //BA.debugLineNum = 218;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_BACK";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK);
 }else {
 //BA.debugLineNum = 221;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_MEDIA_PREVIOUS";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MEDIA_PREVIOUS);
 };
 //BA.debugLineNum = 226;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 227;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 228;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 //BA.debugLineNum = 230;BA.debugLine="End Sub";
return "";
}
public static String  _ringrightdown() throws Exception{
 //BA.debugLineNum = 186;BA.debugLine="Sub RingRightDown()";
 //BA.debugLineNum = 187;BA.debugLine="Log(\"RingRightDown\")";
anywheresoftware.b4a.keywords.Common.Log("RingRightDown");
 //BA.debugLineNum = 188;BA.debugLine="RingRightDownTime = DateTime.Now";
_ringrightdowntime = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 190;BA.debugLine="End Sub";
return "";
}
public static String  _ringrightup() throws Exception{
Object[] _args = null;
anywheresoftware.b4a.agraham.threading.Threading _thread = null;
 //BA.debugLineNum = 197;BA.debugLine="Sub RingRightUp()";
 //BA.debugLineNum = 198;BA.debugLine="Log(\"RingRightUp\")";
anywheresoftware.b4a.keywords.Common.Log("RingRightUp");
 //BA.debugLineNum = 199;BA.debugLine="Dim Args(1) As Object";
_args = new Object[(int) (1)];
{
int d0 = _args.length;
for (int i0 = 0;i0 < d0;i0++) {
_args[i0] = new Object();
}
}
;
 //BA.debugLineNum = 201;BA.debugLine="If (DateTime.Now - RingRightDownTime) > 300 Then";
if ((anywheresoftware.b4a.keywords.Common.DateTime.getNow()-_ringrightdowntime)>300) { 
 }else {
 //BA.debugLineNum = 204;BA.debugLine="Args(0) =  KeyCodes.KEYCODE_MEDIA_NEXT";
_args[(int) (0)] = (Object)(anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MEDIA_NEXT);
 //BA.debugLineNum = 205;BA.debugLine="Dim thread As Thread";
_thread = new anywheresoftware.b4a.agraham.threading.Threading();
 //BA.debugLineNum = 206;BA.debugLine="thread.Initialise(\"KeySub\")";
_thread.Initialise(processBA,"KeySub");
 //BA.debugLineNum = 207;BA.debugLine="thread.Start(\"KeySub\", Args)";
_thread.Start("KeySub",_args);
 };
 //BA.debugLineNum = 211;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 59;BA.debugLine="Notification.Initialize";
_notification.Initialize();
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 64;BA.debugLine="If Connection.IsInitialized <> True Then";
if (_connection.IsInitialized()!=anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 65;BA.debugLine="GoogleMapsDrivingMode = True";
_googlemapsdrivingmode = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 66;BA.debugLine="OpenShuttle";
_openshuttle();
 //BA.debugLineNum = 67;BA.debugLine="Receiver.Initialize(\"BroadcastReceiver\")";
_receiver.Initialize(processBA,"BroadcastReceiver");
 //BA.debugLineNum = 68;BA.debugLine="Receiver.addAction(\"android.hardware.usb.action.";
_receiver.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
 //BA.debugLineNum = 69;BA.debugLine="Receiver.addAction(\"com.freshollie.shuttlexpress";
_receiver.addAction("com.freshollie.shuttlexpress.intent.START");
 //BA.debugLineNum = 70;BA.debugLine="Receiver.registerReceiver(\"\")";
_receiver.registerReceiver("");
 };
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
}

Type=Service
Version=5.02
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
	Dim USB As UsbSerial 
	Dim UsbMngr As UsbManager 
	Dim UsbRequest As UsbRequest
	Dim ByteConverter As ByteConverter
	Dim UsbPid As Int = 32
	Dim UsbVid As Int = 2867
	Dim outEndpoint, inEndpoint As UsbEndpoint
	
	Dim astreams As AsyncStreams
	Dim MyTimer, SysReady, USBTimer As Timer
	Dim Broadcast As BroadCastReceiver
	
	Dim Start, Connected, GoogleMapsDrivingMode As Boolean
	Dim ResponseBytes(1024), Dev, xIndex, PacketSize As Int
	
	Dim Connection As UsbDeviceConnection
	Dim InRequests, OutRequests As List
	
	Dim LastControlValues(8) As Int
	
	Dim Key As KeyGen

	Dim FirstInput, FirstStart As Boolean
	
	Dim Notification As Notification
	Dim Receiver As BroadCastReceiver
	Dim Mediakey As MediaController
	Dim Numthreads As Int
	Dim DoubleClickTimer As Long
	
	Dim RingRightDownTime, RingLeftDownTime, ButtonDownTimes(5) As Long
	
	Dim Su As SuCommand

End Sub

Sub ExecuteAsRoot (Command As String)
	Dim Command, Runner As String
	Dim StdOut, StdErr As StringBuilder
	Dim Result As Int
	Dim Ph As Phone
   

   Runner = File.Combine(File.DirInternalCache, "runner")
   Command = File.Combine(File.DirInternalCache, "command")
   File.WriteString(File.DirInternalCache, "runner", "su < " & Command)
   File.WriteString(File.DirInternalCache, "command", Command & CRLF & "exit") 'Any commands via crlf, and exit at end 
   StdOut.Initialize
   StdErr.Initialize
   Result = Ph.Shell("sh", Array As String(Runner), StdOut, StdErr)
End Sub

Sub Service_Create
	Notification.Initialize

End Sub

Sub Service_Start (StartingIntent As Intent)
	If Connection.IsInitialized <> True Then
		GoogleMapsDrivingMode = True
		OpenShuttle
		Receiver.Initialize("BroadcastReceiver")
		Receiver.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED")
		Receiver.addAction("com.freshollie.shuttlexpress.intent.START")
		Receiver.registerReceiver("")
		
	End If
	
End Sub

Sub Service_Destroy
	
End Sub

Sub OpenShuttle
    UsbMngr.Initialize
    Dim UsbDevices() As UsbDevice  ' USB library
	
    UsbDevices = UsbMngr.GetDevices
	
    'Iterate over USB devices and find the correct one
	
    If UsbDevices.Length > 0 Then
		Log(UsbDevices.Length)
		
        For i = 0 To UsbDevices.Length - 1
            Dim UsbDvc As UsbDevice
            UsbDvc = UsbDevices(i)
			Log(UsbDvc.ProductId)
			Log(UsbDvc.VendorId)
			
	 		If (UsbDvc.ProductId = UsbPid) And (UsbDvc.VendorId = UsbVid) Then
				USB.SetCustomDevice(USB.DRIVER_CDCACM, UsbVid, UsbPid)
				
				If Not(UsbMngr.HasPermission(UsbDvc)) Then 
					UsbMngr.RequestPermission(UsbDvc)
				End If
				
				If UsbMngr.HasPermission(UsbDvc) Then
					Dim interface As UsbInterface
					interface = UsbDvc.GetInterface(i)
					Log(interface)
					Log(UsbMngr.USB_CLASS_HID)
					Log(UsbDvc.DeviceClass)
				
					Connection = UsbMngr.OpenDevice(UsbDvc, interface, True)
					inEndpoint = interface.GetEndpoint(0)
					PacketSize = inEndpoint.MaxPacketSize
					
					Log("Starting connection")
					Connection.StartListening("connection")
					
					FirstInput = True
					
					UsbRequest.Initialize(Connection, inEndpoint)
					
					Dim emptyBuffer(PacketSize) As Byte
					
					emptyBuffer(0) = 0
					UsbRequest.Queue(emptyBuffer, PacketSize)
					
					Notification.Initialize
					Notification.Icon = "icon"
					Notification.SetInfo("Shuttle Driver is running", "", Main) 
					'Change Main (above) to "" if this code is in the main module.
					Notification.Sound = False
					Notification.Vibrate = False
					Notification.Notify(1) 
					
					Service.StartForeground(1 ,Notification)
					Return
					
					
				End If
				
				Exit
			End If
		Next
	End If
	ExitApplication
End Sub

Sub Connection_NewData (Request As UsbRequest, InDirection As Boolean)
	'Log(Request.Buffer.Length)
	Dim i As Int
	For i = 0 To Request.Buffer.Length -1
		'Log(Request.Buffer(i))
	Next
	
	ProcessByte(Request.Buffer)
	
	If Connection.IsInitialized = False Then Return 'Might happen after we close the connection
	
	Dim emptyBuffer(PacketSize) As Byte
	emptyBuffer(0) = 0
	
	Try
		UsbRequest.Queue(emptyBuffer, PacketSize)
		Connection.ContinueListening
		
	Catch
		ExitApp
		
	End Try
	
End Sub

Private Sub KeySub(KeyCode As Object)
	If Numthreads > 10 Then Return
	Numthreads = Numthreads + 1

	Su.SuCommand("input keyevent " & KeyCode)
	Log(KeyCode)
	Numthreads = Numthreads - 1
	'Phone.Shell("su",Null,Null,Null)
	'Phone.Shell("input",Array As String("keyevent", KeyCode),Null,Null)
	'Key.keygenerator(KeyCode)
	
End Sub

Sub RingRightDown()
	Log("RingRightDown")
	RingRightDownTime = DateTime.Now
	
End Sub

Sub RingLeftDown()
	Log("RingLeftDown")
	RingLeftDownTime = DateTime.Now
End Sub

Sub RingRightUp()
	Log("RingRightUp")
	Dim Args(1) As Object
	
	If (DateTime.Now - RingRightDownTime) > 300 Then
	
	Else
		Args(0) =  KeyCodes.KEYCODE_MEDIA_NEXT
		Dim thread As Thread
		thread.Initialise("KeySub")
		thread.Start("KeySub", Args)
	
	End If
	
End Sub

Sub RingLeftUp()
	Log("RingLeftUp")
	Dim Args(1) As Object
	
	If (DateTime.Now - RingLeftDownTime) > 300 Then
		Args(0) =  KeyCodes.KEYCODE_BACK
		
	Else
		Args(0) =  KeyCodes.KEYCODE_MEDIA_PREVIOUS
	
	End If
	
	
	Dim thread As Thread
	thread.Initialise("KeySub")
	thread.Start("KeySub", Args)
		
End Sub

Sub JogWheelRight()
	Dim Args(1) As Object
	Args(0) =  KeyCodes.KEYCODE_TAB
	
	Dim thread As Thread
	thread.Initialise("KeySub")
	thread.Start("KeySub", Args)
	
	Log("JogWheelRight")
End Sub

Sub JogWheelLeft()
	Dim Args(1) As Object
	Args(0) =  KeyCodes.KEYCODE_DPAD_UP
	
	Dim thread As Thread
	thread.Initialise("KeySub")
	thread.Start("KeySub", Args)
	
	Log("JogWheelLeft")
End Sub

Sub ButtonDown(Button As Int)
	ButtonDownTimes(Button - 1) = DateTime.Now
	Log("ButtonDown" & Button)
End Sub

Sub ButtonUp(Button As Int)

	Dim in As Intent
	Dim pm As PackageManager	
	
	Select(Button)
	
	Case(1)
		If (DateTime.Now - ButtonDownTimes(0)) < 300 Then
			Dim Args(1) As Object
			Args(0) =  KeyCodes.KEYCODE_ENTER
			
			Dim thread As Thread
			thread.Initialise("KeySub")
			thread.Start("KeySub", Args)
			
		Else
			in.Initialize(in.ACTION_MAIN, "")
			in = pm.GetApplicationIntent("com.google.android.music")
			in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRONT
			If in.IsInitialized Then
			   StartActivity(in)    

			End If	
		End If	
					
	Case(2)
		in.Initialize(in.ACTION_MAIN, "")
		in = pm.GetApplicationIntent("com.freshollie.radioapp")
		in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRONT
		If in.IsInitialized Then
		   StartActivity(in)    

		End If
	
	Case(3)
		If (DateTime.Now - ButtonDownTimes(2)) > 3000 Then
			in = pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts")
			in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRONT
			If in.IsInitialized Then
			   StartActivity(in)    

			End If
			
		Else
			in = pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts")
			in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRONT
			If in.IsInitialized Then
			   StartActivity(in)    

			End If
		End If
	
	Case(4)
		If (DateTime.Now - ButtonDownTimes(3)) > 3000 Then
			in.Initialize(in.ACTION_VIEW, "google.navigation:/?free=1&mode=d&entry=fnls")
			If in.IsInitialized Then
				in.SetComponent("com.google.android.apps.maps/com.google.android.maps.MapsActivity")
				StartActivity(in)
				

			End If
			
		Else
			in.Initialize(in.ACTION_MAIN, "")
			in = pm.GetApplicationIntent("com.google.android.apps.maps")
			in.Flags = 131072 'FLAG_ACTIVITY_REORDER_TO_FRONT
			If in.IsInitialized Then
			   StartActivity(in)    

			End If
			
		End If
	
	Case(5)
		If (DateTime.Now - ButtonDownTimes(4)) < 1000 Then
			in.Initialize(in.ACTION_MAIN, "")
			in.AddCategory("android.intent.category.HOME")
		    in.Flags = 0x10000000
		    StartActivity(in)
			
		Else
			in.Initialize("android.intent.action.VOICE_ASSIST", "")
			StartActivity(in)
		End If
		
		
	End Select
	Log("ButtonUp" & Button)
End Sub


Sub ProcessByte(Bytes() As Byte)
	Dim Ring, JogWheel, Button1234, Button5 As Int
	
	Ring = Bytes(0)
	JogWheel = Bytes(1)
	Button1234 = Bytes(3)
	Button5 = Bytes(4)
	
	If FirstInput <> True Then
	
		If Ring = 7 And LastControlValues(0) <> 7 Then
			RingRightDown
			
		ELse If Ring <> 7 And LastControlValues(0) = 7 Then
			RingRightUp
			
		Else if Ring = -7 And LastControlValues(0) <> -7 Then
			RingLeftDown
			
		Else if Ring <> -7 And LastControlValues(0) = -7 Then
			RingLeftUp
			
		Else if JogWheel <> LastControlValues(1) Then
			If JogWheel< 0 And LastControlValues(1) > 0 Then
				JogWheelRight
			Else if JogWheel > 0 And LastControlValues(1) < 0 Then
				JogWheelLeft
			Else if JogWheel > LastControlValues(1) Then
				JogWheelRight
			Else
				JogWheelLeft
			End If
			
		Else If Button1234 = 16 And LastControlValues(3) <> 16 Then
			ButtonDown(1)
			
		Else If Button1234 <> 16 And LastControlValues(3) = 16 Then
			ButtonUp(1)
		
		Else If Button1234 = 32 And LastControlValues(3) <> 32 Then
			ButtonDown(2)
		
		Else If Button1234 <> 32 And LastControlValues(3) = 32 Then
			ButtonUp(2)
		
		Else If Button1234 = 64 And LastControlValues(3) <> 64 Then
			ButtonDown(3)
			
		Else If Button1234 <> 64 And LastControlValues(3) = 64 Then
			ButtonUp(3)
		
		Else If Button1234 = -128 And LastControlValues(3) <> -128 Then
			ButtonDown(4)
			
		Else If Button1234 <> -128 And LastControlValues(3) = -128 Then
			ButtonUp(4)
			
		Else If Button5 <> 0 And LastControlValues(4) = 0 Then
			ButtonDown(5)
			
		Else If Button5 = 0 And LastControlValues(4) <> 0 Then
			ButtonUp(5)
		
		End If
		
	Else
		FirstInput = False
	End If
	
	
	
	LastControlValues(0) = Ring
	LastControlValues(1) = JogWheel
	LastControlValues(3) = Button1234
	LastControlValues(4) = Button5

End Sub	

Sub ExitApp
	If Connection.IsInitialized Then Connection.CloseSynchronous
	Service.StopForeground(1)
	Notification.Cancel(1)
	Connection.CloseSynchronous
	Log("Closing App")
	ExitApplication
	
End Sub

Sub BroadcastReceiver_OnReceive(Action As String,i As Object)
	Dim Intent1 As Intent = i
	Log(Action)
	
    If Intent1.HasExtra("device") Then
	  	If USB.UsbPresent(Dev) = USB.USB_NONE Then
			ExitApp
		End If
		
    End If
	
	If Action = "com.freshollie.shuttlexpress.intent.START" Then
		OpenShuttle
	End If
End Sub



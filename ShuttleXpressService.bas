Type=Service
Version=5.02
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: false
#End Region

Sub Process_Globals
	Dim UsbMngr As UsbManager 
	Dim UsbRequest As UsbRequest
	Dim UsbPid As Int = 32
	Dim UsbVid As Int = 2867
	
	Dim PacketSize As Int
	
	Dim inEndpoint As UsbEndpoint
	
	Dim Connection As UsbDeviceConnection
	Dim Receiver As BroadCastReceiver
	
	Dim LastControlValues(8) As Int

	Dim FirstInput As Boolean
	Dim Connected As Boolean
	
	Dim Notification As Notification
	Dim Numthreads As Int
	
	Dim RingRightDownTime, RingLeftDownTime, ButtonDownTimes(5) As Long
	
	Dim Su As SuCommand

End Sub

Sub Service_Create
	Receiver.Initialize("BroadcastReceiver")
	Receiver.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED")
	Receiver.registerReceiver("")
	Notification.Initialize
	

End Sub

Sub Service_Start (StartingIntent As Intent)
	If Connection.IsInitialized <> True Then
		Connected = False
		OpenShuttle
		
	End If
	
End Sub

Sub BroadcastReceiver_OnReceive(Action As String,i As Object)
	Dim Intent1 As Intent = i
	Log(Action)
	
	Select(Action)
	
	Case "android.hardware.usb.action.USB_DEVICE_DETACHED"
	
		If Connection.IsInitialized Then
			CloseShuttle
		
		End If
	End Select
	
End Sub

Sub Service_Destroy
End Sub

Sub OpenShuttle
	UsbMngr.Initialize
	
	Dim UsbDevices() As UsbDevice
	
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
			
				If Not(UsbMngr.HasPermission(UsbDvc)) Then 
					UsbMngr.RequestPermission(UsbDvc) 'Always instantly accepted using timurs kernel
				End If 
				
				If UsbMngr.HasPermission(UsbDvc) Then
					Dim interface As UsbInterface
					interface = UsbDvc.GetInterface(0)
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
		CloseShuttle
		
	End Try
	
End Sub

Private Sub KeySub(KeyCode As Object)
	If Numthreads > 10 Then Return
	Numthreads = Numthreads + 1

	Su.SuCommand("input keyevent " & KeyCode)
	Log(KeyCode)
	Numthreads = Numthreads - 1
	
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
	
	If (DateTime.Now - RingRightDownTime) > 1000 Then
	
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
	
	If (DateTime.Now - RingLeftDownTime) > 1000 Then
		Args(0) =  KeyCodes.KEYCODE_BACK
		
	Else
		Args(0) =  KeyCodes.KEYCODE_MEDIA_PREVIOUS
	
	End If
	
	
	Dim thread1 As Thread
	thread1.Initialise("KeySub")
	thread1.Start("KeySub", Args)
		
End Sub

Sub JogWheelRight()
	Dim Args(1) As Object
	Args(0) =  KeyCodes.KEYCODE_TAB
	
	Dim thread1 As Thread
	thread1.Initialise("KeySub")
	thread1.Start("KeySub", Args)
	
	Log("JogWheelRight")
End Sub

Sub JogWheelLeft()
	Dim Args(1) As Object
	Args(0) =  KeyCodes.KEYCODE_DPAD_UP
	
	Dim thread1 As Thread
	thread1.Initialise("KeySub")
	thread1.Start("KeySub", Args)
	
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
			
			Dim thread1 As Thread
			thread1.Initialise("KeySub")
			thread1.Start("KeySub", Args)
			
		Else
			in = pm.GetApplicationIntent("com.apple.android.music")
			in.Action = in.ACTION_MAIN
			in.Flags = 0x10000000
			in.AddCategory("android.intent.category.LAUNCHER")
			If in.IsInitialized Then
			   StartActivity(in)	

			End If	
		End If	
					
	Case(2)
		in = pm.GetApplicationIntent("com.freshollie.radioapp")
		in.Action = in.ACTION_MAIN
		in.Flags = 0x10000000
		in.AddCategory("android.intent.category.LAUNCHER")
		If in.IsInitialized Then
		   StartActivity(in)	

		End If
	
	Case(3)
		If (DateTime.Now - ButtonDownTimes(2)) > 3000 Then
			in = pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts")
			in.Action = in.ACTION_MAIN
			in.Flags = 0x10000000
			in.AddCategory("android.intent.category.LAUNCHER")
			If in.IsInitialized Then
			   StartActivity(in)	

			End If
			
		Else
			in = pm.GetApplicationIntent("au.com.shiftyjelly.pocketcasts")
			in.Action = in.ACTION_MAIN
			in.Flags = 0x10000000
			'in.AddCategory("android.intent.category.LAUNCHER")
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
			in = pm.GetApplicationIntent("com.google.android.apps.maps")
			in.Action = in.ACTION_MAIN
			in.Flags = 0x10000000
			in.AddCategory("android.intent.category.LAUNCHER")
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


Sub CloseShuttle
	If Connection.IsInitialized Then 
		Connection.CloseSynchronous
		Service.StopForeground(1)
		Notification.Cancel(1)
	End If
End Sub

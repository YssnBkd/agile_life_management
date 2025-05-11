2025-05-09 17:23:41.994  7827-7827  TransactionExecutor     com.example.agilelifemanagement      E  Failed to execute the transaction: tId:-736130477 ClientTransaction{
tId:-736130477   transactionItems=[
tId:-736130477     LaunchActivityItem{activityToken=android.os.BinderProxy@4c7284c,intent=Intent { act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10200000 xflg=0x4 cmp=com.example.agilelifemanagement/.MainActivity bnds=[824,1873][997,2068] },ident=89544270,info=ActivityInfo{1d27d5a com.example.agilelifemanagement.MainActivity},curConfig={1.0 310mcc260mnc [en_US] ldltr sw411dp w411dp h914dp 420dpi nrml long port finger qwerty/v/v dpad/v winConfig={ mBounds=Rect(0, 0 - 1080, 2400) mAppBounds=Rect(0, 0 - 1080, 2400) mMaxBounds=Rect(0, 0 - 1080, 2400) mDisplayRotation=ROTATION_0 mWindowingMode=fullscreen mActivityType=undefined mAlwaysOnTop=undefined mRotation=ROTATION_0} s.38 fontWeightAdjustment=0},overrideConfig={1.0 310mcc260mnc [en_US] ldltr sw411dp w411dp h914dp 420dpi nrml long port finger qwerty/v/v dpad/v winConfig={ mBounds=Rect(0, 0 - 1080, 2400) mAppBounds=Rect(0, 0 - 1080, 2400) mMaxBounds=Rect(0, 0 - 1080, 2400) mDisplayRotation=ROTATION_0 mWindowingMode=fullscreen mActivityType=standard mAlwaysOnTop=undefined mRotation=ROTATION_0} s.2 fontWeightAdjustment=0},deviceId=0,referrer=null,procState=2,state=null,persistentState=null,pendingResults=null,pendingNewIntents=null,sceneTransitionInfo=null,profilerInfo=null,assistToken=android.os.BinderProxy@588576f,shareableActivityToken=android.os.BinderProxy@48b3c7c,activityWindowInfo=ActivityWindowInfo{isEmbedded=false, taskBounds=Rect(0, 0 - 1080, 2400), taskFragmentBounds=Rect(0, 0 - 1080, 2400)}}
tId:-736130477     ResumeActivityItem{mActivityToken=android.os.BinderProxy@4c7284c,procState=-1,isForward=true,shouldSendCompatFakeFocus=false}
tId:-736130477     Target activity: com.example.agilelifemanagement.MainActivity
tId:-736130477   ]
tId:-736130477 }
2025-05-09 17:23:41.996  7827-7827  AndroidRuntime          com.example.agilelifemanagement      E  FATAL EXCEPTION: main (Ask Gemini)
Process: com.example.agilelifemanagement, PID: 7827
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.agilelifemanagement/com.example.agilelifemanagement.MainActivity}: java.lang.IllegalStateException: Hilt Activity must be attached to an @HiltAndroidApp Application. Did you forget to specify your Application's class name in your manifest's <application />'s android:name attribute?
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4280)
	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4467)
	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222)
	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133)
	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103)
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2823)
	at android.os.Handler.dispatchMessage(Handler.java:110)
	at android.os.Looper.loopOnce(Looper.java:248)
	at android.os.Looper.loop(Looper.java:338)
	at android.app.ActivityThread.main(ActivityThread.java:9067)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:593)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:932)
Caused by: java.lang.IllegalStateException: Hilt Activity must be attached to an @HiltAndroidApp Application. Did you forget to specify your Application's class name in your manifest's <application />'s android:name attribute?
	at dagger.hilt.android.internal.managers.ActivityComponentManager.createComponent(ActivityComponentManager.java:80)
	at dagger.hilt.android.internal.managers.ActivityComponentManager.generatedComponent(ActivityComponentManager.java:66)
	at com.example.agilelifemanagement.Hilt_MainActivity.generatedComponent(Hilt_MainActivity.java:47)
	at com.example.agilelifemanagement.Hilt_MainActivity.inject(Hilt_MainActivity.java:69)
	at com.example.agilelifemanagement.Hilt_MainActivity$1.onContextAvailable(Hilt_MainActivity.java:40)
	at androidx.activity.contextaware.ContextAwareHelper.dispatchOnContextAvailable(ContextAwareHelper.kt:78)
	at androidx.activity.ComponentActivity.onCreate(ComponentActivity.kt:327)
	at com.example.agilelifemanagement.MainActivity.onCreate(MainActivity.kt:31)
	at android.app.Activity.performCreate(Activity.java:9155)
	at android.app.Activity.performCreate(Activity.java:9133)
	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1521)
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:4262)
	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4467) 
	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:222) 
	at android.app.servertransaction.TransactionExecutor.executeNonLifecycleItem(TransactionExecutor.java:133) 
	at android.app.servertransaction.TransactionExecutor.executeTransactionItems(TransactionExecutor.java:103) 
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:80) 
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2823) 
	at android.os.Handler.dispatchMessage(Handler.java:110) 
	at android.os.Looper.loopOnce(Looper.java:248) 
	at android.os.Looper.loop(Looper.java:338) 
	at android.app.ActivityThread.main(ActivityThread.java:9067) 
	at java.lang.reflect.Method.invoke(Native Method) 
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:593) 
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:932) 
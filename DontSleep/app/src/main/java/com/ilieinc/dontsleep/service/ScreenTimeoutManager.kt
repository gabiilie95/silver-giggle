package com.ilieinc.dontsleep.service


//object ScreenTimeoutManager {
//        val wakeLockStatusChanged = Event(WakeLockChangedEvent::class.java)
//
//        const val AWAKE_WAKELOCK_TAG = "DontSleep::AwakeWakeLogTag"
//        const val SLEEP_TIMER_WAKELOCK_TAG = "DontSleep::SleepTimerWakeLogTag"
//
//        private val wakeLocks = hashMapOf<String, WakeLock>()
//
//        fun isWakeLockActive(wakeLockName: String): Boolean {
//            return wakeLocks[wakeLockName]?.isHeld ?: false
//        }
//
//        fun setWakeLockStatus(
//            context: Context,
//            wakeLockName: String,
//            enabled: Boolean,
//            turnOffScreen: Boolean
//        ) {
//            wakeLocks[wakeLockName].let { wakeLock ->
//                if (wakeLock == null || enabled != wakeLock.isHeld) {
//                    toggleWakeLock(context, wakeLockName, turnOffScreen)
//                }
//            }
//        }
//
//        fun requestDrawOverPermission(context: Context) {
//            if (!Settings.canDrawOverlays(context)) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + context.packageName)
//                )
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(intent)
//            } else {
//                val serviceIntent = Intent(context, ScreenTimeoutService::class.java)
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    context.startForegroundService(serviceIntent)
//                }
//                else{
//                    context.startService(serviceIntent)
//                }
//            }
//        }
//
//        fun toggleWakeLock(context: Context, wakeLockName: String, turnOffScreen: Boolean) {
//            if (wakeLocks[wakeLockName] == null) {
//                wakeLocks[wakeLockName] =
//                    (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                        newWakeLock(
//                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
//                            wakeLockName
//                        )
//                    }
//            }
//            wakeLocks[wakeLockName]!!.let { currentWakeLock ->
//                if (!currentWakeLock.isHeld) {
//                    val timeout = SharedPreferenceManager.getInstance(context)
//                        .getLong(wakeLockName, 500000)
//                    wakeLocks[wakeLockName]!!.acquire(timeout)
//                    if (turnOffScreen) {
//                        val lockScreenDateTime = Calendar.getInstance()
//                        lockScreenDateTime.add(Calendar.MILLISECOND, timeout.toInt())
//                        TimerManager.setTimedTask<LockScreenWorker>(
//                            context,
//                            lockScreenDateTime.time,
//                            wakeLockName
//                        )
//                    }
//                } else {
//                    currentWakeLock.release()
//                    if (turnOffScreen) {
//                        TimerManager.cancelTask(context, wakeLockName)
//                    }
////                    wakeLocks.remove(wakeLockName)
//                }
//            }
//            wakeLockStatusChanged.invoke(
//                wakeLockName,
//                wakeLocks[wakeLockName]!!.isHeld
//            )
//        }
//}
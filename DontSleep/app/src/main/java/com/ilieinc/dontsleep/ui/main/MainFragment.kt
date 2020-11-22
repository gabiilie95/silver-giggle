package com.ilieinc.dontsleep.ui.main

//class MainFragment : Fragment(), DeviceAdminChangedEvent {
//
//    companion object {
//        fun newInstance() = MainFragment()
//    }
//
//    private lateinit var deviceManager: DevicePolicyManager
//    private lateinit var viewModel: MainViewModel
//    override fun <I : Any?, O : Any?> prepareCall(
//        contract: ActivityResultContract<I, O>,
//        callback: ActivityResultCallback<O>
//    ): ActivityResultLauncher<I> {
//        TODO("Not yet implemented")
//    }
//
//    override fun <I : Any?, O : Any?> prepareCall(
//        contract: ActivityResultContract<I, O>,
//        registry: ActivityResultRegistry,
//        callback: ActivityResultCallback<O>
//    ): ActivityResultLauncher<I> {
//        TODO("Not yet implemented")
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.main_fragment, container, false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        deviceManager = getSystemService(requireContext(), DevicePolicyManager::class.java)!!
//    }
//
//    override fun onStart() {
//        TimeoutService.serviceStatusChangedCallback = this::onServiceStatusChanged
//        SleepService.serviceStatusChangedCallback = this::onServiceStatusChanged
//        DeviceAdminReceiver.statusChangedEvent addSubscriber this
//        super.onStart()
//        initControls()
//    }
//
//    override fun onStop() {
//        TimeoutService.serviceStatusChangedCallback = null
//        SleepService.serviceStatusChangedCallback = null
//        DeviceAdminReceiver.statusChangedEvent removeSubscriber this
//        super.onStop()
//    }
//
//    private fun initControls() {
//        setTimeoutControls(PermissionHelper.shouldRequestDrawOverPermission(requireContext()))
//        setSleepTimerControls(PermissionHelper.shouldRequestAdminPermission(deviceManager))
//        initButtons()
//    }
//
//    private fun initButtons() {
//        draw_over_permission_help_button.setOnClickListener {
//            val dialog = StateHelper.getTimeoutInfoDialog(requireActivity()) {
//                PermissionHelper.requestDrawOverPermission(requireContext())
//            }
//            dialog.show()
//        }
//        helpButton.setOnClickListener {
//            val dialog = DeviceAdminHelper.getInfoDialog(requireActivity()) {
//                deviceManager.removeActiveAdmin(DeviceAdminHelper.componentName)
//            }
//            dialog.show()
//        }
//        closeButton.setOnClickListener {
//            activity?.finish()
//        }
//    }
//
//    private fun onServiceStatusChanged(serviceName: String, active: Boolean) {
//        activity?.runOnUiThread {
//            when (serviceName) {
//                TimeoutService::class.java.name -> {
//                    timeoutSwitch.isChecked = active
//                }
//                SleepService::class.java.name -> {
//                    sleepSwitch.isChecked = active
//                }
//            }
//        }
//    }
//
//    override fun onDeviceAdminStatusChanged(active: Boolean) {
//        setSleepTimerControls(!active)
//    }
//
//    private fun setStatus(
//        statusSwitch: SwitchMaterial,
//        checked: Boolean,
//        callback: (CompoundButton, Boolean) -> Unit
//    ) {
//        statusSwitch.isChecked = checked
//        statusSwitch.setOnCheckedChangeListener(callback)
//    }
//
//    private fun setTimePicker(timePicker: TimePicker, value: String) {
//        timePicker.setIs24HourView(true)
//        val timeout =
//            SharedPreferenceManager.getInstance(requireContext()).getLong(value, 900000) / 1000 / 60
//        timePicker.hour = (timeout / 60).toInt()
//        timePicker.minute = (timeout % 60).toInt()
//        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
//            viewModel.saveTimePickerSettings(view.context, value, Pair(hourOfDay, minute))
//        }
//    }
//
//    private fun setTimeoutControls(requiresPermission: Boolean) {
//        setTimeoutControlsVisibility(!requiresPermission)
//        if (requiresPermission) {
//            draw_over_permission_button.setOnClickListener {
//                val dialog = createDialog(
//                    requireContext(),
//                    requireContext().getString(R.string.draw_over_permission_grant_title),
//                    requireContext().getString(R.string.draw_over_permission_grant_body),
//                    requireContext().getString(R.string.yes),
//                    requireContext().getString(R.string.no)
//                ) {
//                    PermissionHelper.requestDrawOverPermission(requireContext())
//                }
//                dialog.show()
//            }
//        } else {
//            setStatus(
//                timeoutSwitch,
//                TimeoutService.isRunning(requireContext())
//            ) { _, checked ->
//                if (checked) {
//                    requireContext().startForegroundService<TimeoutService>()
//                } else {
//                    requireContext().stopService<TimeoutService>()
//                }
//            }
//            setTimePicker(timeoutTimePicker, TimeoutService.TIMEOUT_TAG)
//        }
//    }
//
//    private fun setSleepTimerControls(requiresPermission: Boolean) {
//        setSleepTimerControlsVisibility(!requiresPermission)
//        if (requiresPermission) {
//            permissionButton.setOnClickListener {
//                val dialog = createDialog(
//                    requireContext(),
//                    requireContext().getString(R.string.special_permission_grant_title),
//                    requireContext().getString(R.string.special_permission_grant_body),
//                    requireContext().getString(R.string.yes),
//                    requireContext().getString(R.string.no)
//                ) {
//                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
//                    intent.putExtra(
//                        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                        DeviceAdminHelper.componentName
//                    )
//                    intent.putExtra(
//                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                        "Please allow this permission to allow the application to turn off the screen."
//                    )
//                    startActivityForResult(intent, DeviceAdminHelper.REQUEST_CODE)
//                }
//                dialog.show()
//            }
//        } else {
//            setStatus(
//                sleepSwitch,
//                SleepService.isRunning(requireContext())
//            ) { _, checked ->
//                if (checked) {
//                    requireContext().startForegroundService<SleepService>()
//                } else {
//                    requireContext().stopService<SleepService>()
//                    SleepService.cancelLockWorker(requireContext())
//                }
//            }
//            setTimePicker(sleepTimerTimePicker, SleepService.SLEEP_TAG)
//        }
//    }
//
//    private fun setTimeoutControlsVisibility(enabled: Boolean) {
//        val statusFieldsVisibility = if (enabled) View.VISIBLE else View.GONE
//        val permissionButtonVisibility = if (enabled) View.GONE else View.VISIBLE
//        timeout_body_layout.visibility = statusFieldsVisibility
//        draw_over_permission_button.visibility = permissionButtonVisibility
//    }
//
//    private fun setSleepTimerControlsVisibility(enabled: Boolean) {
//        val statusFieldsVisibility = if (enabled) View.VISIBLE else View.GONE
//        val permissionButtonVisibility = if (enabled) View.GONE else View.VISIBLE
//        sleepTimerLayout.visibility = statusFieldsVisibility
//        permissionButton.visibility = permissionButtonVisibility
//    }
//}

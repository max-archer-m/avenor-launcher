package com.avenor.launcher

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

internal enum class LockRequestResult {
    Requested,
    ServiceDisconnected,
    ActionUnavailable,
    ActionRejected,
}

internal fun interface LockRequestPort {
    fun requestLock(): LockRequestResult
}

/** Debug-only connection seam used to validate the proposed fail-closed boundary. */
internal object AccessibilityLockProbeConnection : LockRequestPort {
    @Volatile
    private var connectedService: LockRequestPort? = null

    val isConnected: Boolean
        get() = connectedService != null

    override fun requestLock(): LockRequestResult =
        connectedService?.requestLock() ?: LockRequestResult.ServiceDisconnected

    fun connected(service: LockRequestPort) {
        connectedService = service
    }

    fun disconnected(service: LockRequestPort) {
        if (connectedService === service) connectedService = null
    }
}

internal class AccessibilityLockProbeService : AccessibilityService(), LockRequestPort {
    override fun onServiceConnected() {
        super.onServiceConnected()
        AccessibilityLockProbeConnection.connected(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        AccessibilityLockProbeConnection.disconnected(this)
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        AccessibilityLockProbeConnection.disconnected(this)
        super.onDestroy()
    }

    override fun requestLock(): LockRequestResult = requestLockAction(
        availableActionIds = systemActions.map { it.id },
        perform = { performGlobalAction(it) },
    )
}

internal fun requestLockAction(
    availableActionIds: Collection<Int>,
    perform: (Int) -> Boolean,
): LockRequestResult {
    val action = AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN
    if (action !in availableActionIds) return LockRequestResult.ActionUnavailable
    return if (perform(action)) LockRequestResult.Requested else LockRequestResult.ActionRejected
}

internal fun isAccessibilityLockProbeEnabled(context: Context): Boolean {
    val expected = ComponentName(context, AccessibilityLockProbeService::class.java)
    val manager = context.getSystemService(AccessibilityManager::class.java)
    return manager
        .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any { info ->
            val serviceInfo = info.resolveInfo?.serviceInfo ?: return@any false
            ComponentName(serviceInfo.packageName, serviceInfo.name) == expected
        }
}

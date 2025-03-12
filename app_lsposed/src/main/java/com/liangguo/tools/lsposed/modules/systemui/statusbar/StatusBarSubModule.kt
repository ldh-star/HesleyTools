package com.liangguo.tools.lsposed.modules.systemui.statusbar

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.ViewGroup
import com.liangguo.tools.lsposed.base.IDarkChangedReceiver
import com.liangguo.tools.lsposed.base.IModule
import com.liangguo.tools.lsposed.base.ISubViewModule
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author hesleyliang
 * 时间: 2024/9/27 19:58
 * 邮箱: liang.dh@outlook.com
 *
 * 状态栏展示信息的子模块
 */
abstract class StatusBarSubModule : IModule, ISubViewModule, IDarkChangedReceiver {

    override val hookPackageName = StatusBarInfoModule.hookPackageName

    /** handler发送消息的id，每创建一个本对象，这个id就会自增 */
    protected val messageId = BASE_MESSAGE_ID++

    protected var infoHandler: Handler? = null

    protected var infoJob: HandlerThread? = null

    /** 每次刷新的间隙，单位ms */
    protected open val loopGap = 1000

    override fun onCreate(parent: ViewGroup) {
        onCreateView(parent)
        startLoop()
    }

    override fun onStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
    }

    /** 开始进行循环刷新 */
    fun startLoop() {
        stopLoop()
        infoJob = HandlerThread(javaClass.name.toString()).also { thread ->
            thread.start()
            infoHandler = Handler(thread.looper, object : Handler.Callback {
                override fun handleMessage(msg: Message): Boolean {
                    if (messageId != msg.what) {
                        return true
                    }
                    onRefresh()
                    if (thread.isAlive && !thread.isInterrupted) {
                        infoHandler?.sendEmptyMessageDelayed(msg.what, loopGap.toLong())
                    }
                    return true
                }
            }).apply {
                sendEmptyMessage(messageId)
            }
        }
    }

    /** 停止进行循环刷新 */
    fun stopLoop() {
        infoJob?.let {
            // 如果之前的线程还在运行，则直接打断之前的，然后运行新的
            runCatching {
                it.interrupt()
                it.quit()
            }
            infoJob = null
        }
    }

    /** 子类请自行实现此方法 */
    protected abstract fun onCreateView(parent: ViewGroup)

    /**
     * 当信息刷新的时候调用。
     * 请注意：该方法在子线程中执行！！！
     */
    protected abstract fun onRefresh()

    companion object {
        private var BASE_MESSAGE_ID = 519845290
    }
}
package me.solar.apollo.apolloBukkitCore.runnables

import me.solar.apollo.apolloBukkitCore.ApolloPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

abstract class RunnableObject {

    private var taskId = -1
    private var cancelAction: Runnable? = null

    protected abstract fun action()

    private val runnable: Runnable
        get() = Runnable { action() }

    fun startTask(plugin: JavaPlugin, delay: Long) {
        if (taskId == -1) {
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay)
        }
    }

    fun startTask(delay: Long) {
        startTask(ApolloPlugin.instance, delay)
    }

    fun startTask() {
        startTask(ApolloPlugin.instance, 0)
    }

    fun startTask(plugin: JavaPlugin, delay: Long, repeatingDelay: Long) {
        if (taskId == -1) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, repeatingDelay)
        }
    }

    fun startTask(delay: Long, repeatingDelay: Long) {
        startTask(ApolloPlugin.instance, delay, repeatingDelay)
    }

    fun startAsynchronouslyTask() {
        Bukkit.getScheduler().runTaskAsynchronously(ApolloPlugin.instance, runnable)
    }

    fun cancelTask() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = -1
        }
        onCancel()
        cancelAction?.run()
    }

    open fun onCancel() {}

    fun withCancelAction(cancelAction: Runnable): RunnableObject {
        this.cancelAction = cancelAction
        return this
    }

    fun isRunning(): Boolean {
        return taskId != -1
    }

    companion object {
        fun create(runnable: Runnable): RunnableObject {
            return object : RunnableObject() {
                override fun action() {
                    runnable.run()
                }
            }
        }

        fun createAndStart(runnable: Runnable): RunnableObject {
            return createAndStart(ApolloPlugin.instance, runnable, 0)
        }

        fun createAndStart(runnable: Runnable, delay: Long): RunnableObject {
            return createAndStart(ApolloPlugin.instance, runnable, delay)
        }

        fun createAndStartRepeating(runnable: Runnable, initialDelay: Long, repeatingDelay: Long): RunnableObject {
            return createAndStartRepeating(ApolloPlugin.instance, runnable, initialDelay, repeatingDelay)
        }

        fun createAndStart(plugin: JavaPlugin, runnable: Runnable): RunnableObject {
            return createAndStart(plugin, runnable, 0)
        }

        fun createAndStart(plugin: JavaPlugin, runnable: Runnable, delay: Long): RunnableObject {
            val runnableObject = create(runnable)
            runnableObject.startTask(plugin, delay)
            return runnableObject
        }

        fun createAndStartRepeating(plugin: JavaPlugin, runnable: Runnable, initialDelay: Long, repeatingDelay: Long): RunnableObject {
            val runnableObject = create(runnable)
            runnableObject.startTask(plugin, initialDelay, repeatingDelay)
            return runnableObject
        }

        fun createAndStartAsynchronously(runnable: Runnable): RunnableObject {
            val runnableObject = create(runnable)
            runnableObject.startAsynchronouslyTask()
            return runnableObject
        }
    }
}
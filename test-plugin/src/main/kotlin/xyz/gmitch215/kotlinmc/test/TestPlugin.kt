package xyz.gmitch215.kotlinmc.test

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("kotlinx.coroutines.Job classloader: " + Job::class.java.classLoader)
        launch {
            delay(20)
            logger.info("ok on " + Thread.currentThread().name)
        }
    }
}

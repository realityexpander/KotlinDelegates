package com.realityexpander.kotlindelegates

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.math.BigDecimal
import kotlin.reflect.KProperty

class MainActivity : ComponentActivity(),
        AnalyticsLogger by AnalyticsLoggerImpl(),  // Creates an implementation delegate object for the AnalyticsLogger interface
        DeepLinkHandler by DeepLinkHandlerImpl(),
        OldWayAnalyticsLogger  // old way of doing it, just for comparison.
{
    val oldWayLogger = OldWayAnalyticsLoggerImpl()  // Old way of creating an object for an interface

    private val bigThing by MyLazy {
        println("Hello world")
        BigDecimal(500_000_000_000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerLifecycleOwner(this)
        println(bigThing)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(this, intent)
    }

    // Old way of calling an implemented interface
    override fun oldWayRegisterLifecycleOwner(owner: LifecycleOwner) {
        oldWayLogger.oldWayRegisterLifecycleOwner(owner)
    }
}

// Lazy delegation example
class MyLazy<out T: Any>(
    private val initialize: () -> T
) {
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if(value == null) {
            value = initialize()
            value!!
        } else value!!
    }
}

interface DeepLinkHandler {
    fun handleDeepLink(activity: Activity, intent: Intent?)
}

class DeepLinkHandlerImpl: DeepLinkHandler {
    override fun handleDeepLink(activity: Activity, intent: Intent?) {
        // Parse and handle the intent here
    }
}

interface OldWayAnalyticsLogger {
    fun oldWayRegisterLifecycleOwner(owner: LifecycleOwner)
}

interface AnalyticsLogger {
    fun registerLifecycleOwner(owner: LifecycleOwner)
}

class AnalyticsLoggerImpl: AnalyticsLogger, LifecycleEventObserver {
    override fun registerLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_RESUME -> println("User entered the screen")
            Lifecycle.Event.ON_PAUSE -> println("User left the screen")
            else -> Unit
        }
    }
}

class OldWayAnalyticsLoggerImpl: OldWayAnalyticsLogger, LifecycleEventObserver {
    override fun oldWayRegisterLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_RESUME -> println("User entered the screen")
            Lifecycle.Event.ON_PAUSE -> println("User left the screen")
            else -> Unit
        }
    }
}
/**
 *  Smart Bathroom Fan
 *
 *  Copyright 2018 Chris Roberts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Smart Bathroom Fan III",
    namespace: "m0ntecarloss",
    author: "Chris Roberts",
    description: "Work in progress.  Based on AirCycler switch with enhancements...",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

import java.text.SimpleDateFormat
import groovy.time.TimeCategory

preferences {

    // TODO: cleanup sections, descriptions, pages and stuff...
    // TODO: outdoor temp sensor / dew point type stuff
    section(hideable: true, "Debug") {
        input "debugEnabled", "bool", title: "Debug Enabled", required: true
    }
    
    section(hideable: true, "Hardware") {
        
        // bath fan switch to control smartly...
        input "fanSwitch",   "capability.switch", title: "Fan Switch", required: true
            
        // bath light switch to tie fan operation to
        input "lightSwitch", "capability.switch", title: "Light Switch", required: true
            
        // humidity sensor to override normal operation
        input "humiditySensor", "capability.relativeHumidityMeasurement", title: "Humidity Sensor", required: false
        
        // motion sensor to turn on fan and/or light
        input "motionSensor", "capability.motionSensor", title: "Motion Sensor", required: false
        
        // contact sensors to turn on the fan
        input "contactSensors", "capability.contactSensor", title: "Door/Window Sensors", required: false, multiple: true
    }
     
    section(hideable: true, "Controls or something") {
    
        input "fanMinPerHour", "number", title: "Hourly Budget", description: "How many minutes per hour to run fan for", required: true
        
        input "minAfterLightOn", "number", title: "Minutes After Light On", description: "How many minutes after light turned on before fan turns on", required: false
     
        input "minAfterLightOff", "number", title: "Minutes After Light Off", description: "How many minutes after light turned off before fan turns off", required: false
    }
    
    section(hideable: true, "Scheduling and stuff") {
        paragraph "This section will be implemented when I feel like it"
    }

}

//------------------------------------------------------------------------------

def installed() {
	DEBUG("installed")
	initialize()
}

//------------------------------------------------------------------------------

def updated() {
    DEBUG("updated")
	initialize()
}

//------------------------------------------------------------------------------

def initialize() {
    DEBUG("initialize")

    DEBUG("settings = ${settings}\n")

    unschedule()
    schedule("58 58 * * * ?", hourlyHandler)

    unsubscribe()
    subscribe (lightSwitch,    "switch.on",      lightHandler)
    subscribe (lightSwitch,    "switch.off",     lightHandler)
    subscribe (fanSwitch,      "switch.on",      fanOnHandler)
    subscribe (fanSwitch,      "switch.off",     fanOffHandler)
    subscribe (contactSensors, "contact.open",   contactOpenHandler)
	subscribe (contactSensors, "contact.closed", contactCloseHandler)
}

//------------------------------------------------------------------------------

def lightHandler(evt) {
    DEBUG("lightHandler")
    try {
    } catch (e) {
    }
}

//------------------------------------------------------------------------------

def fanOnHandler(evt) {
    DEBUG("fanOnHandler")
    try {
    } catch (e) {
    }
}

//------------------------------------------------------------------------------

def fanOffHandler(evt) {
    DEBUG("fanOffHandler")
    try {
    } catch (e) {
    }
}

//------------------------------------------------------------------------------

def contactOpenHandler(evt) {
    DEBUG("contactOpenHandler")
    try {
    } catch (e) {
    }
}

//------------------------------------------------------------------------------

def contactCloseHandler(evt) {
    DEBUG("contactCloseHandler")
    try {
    } catch (e) {
    }
}

//------------------------------------------------------------------------------

def hourlyHandler(evt) {
    
    DEBUG("hourlyHandler:")
   
    try {
    } catch(e) {
    }
    
    Dump_Debug()
}
            
//------------------------------------------------------------------------------

private def DEBUG(txt) {
    //log.debug ${app.label}
    if(state.debug_text == null) {
        log.debug("Need to initialize state.debug_text")
        state.debug_text = "initialized debug_text\n"
    }
    log.debug("${txt}")
    def junk = state.debug_text
    junk += txt + "\n"
    state.debug_text = junk
}

private def Dump_Debug() {
    log.debug("---------------------------------")
    log.debug(state.debug_text)
    log.debug("---------------------------------")
//    if( debugEnabled ) {
//        sendNotificationEvent("Smart Bathroom Fan: " + state.debug_text)
//    }
    state.debug_text=""
}

//------------------------------------------------------------------------------

/*
def OLDhourlyHandler(evt) {
    
    // TODO: This whole thing is no good.  We don't want to check after an hour is over to see
    //       how much time the fan SHOULD have been on in that hour.
    //
    //       What really needs to happen is we need to schedule the check
    //       at the beginning of the hour at the latest possible time
    //       we should turn the fan on in that hour.  Then each time
    //       the fan turns off we should unschedule/reschedule accordingly
    
    def debug_string = new String()
    def counter      = 0
    def cur          = new Date()
    def hour_ago     = new Date()
    def total_secs   = 0
    use(TimeCategory) {
        hour_ago = hour_ago - 1.hour
    }
    def last_event  = hour_ago
    
    debug_string += "--------------------------\n"
    debug_string += "current time = ${cur}\n"
    debug_string += "last hour    = ${hour_ago}\n"
    
    // TODO: need to check for and handle repeated events in the
    //       logic (i.e. two off events in a row for whatever reason)
    for(zzz in fanSwitch.eventsSince(hour_ago).reverse()) {
        if(zzz.value == "on" || zzz.value == "off") {
            counter += 1
            debug_string += "--------------------------\n"
            debug_string += "EVENT: ${counter}\n"
            debug_string += "       date            = ${zzz.date}\n"
            //debug_string += "       name            = ${zzz.name}\n"
            debug_string += "       device          = ${zzz.device.displayName}\n"
            debug_string += "       description     = ${zzz.description}\n"
            //debug_string += "       descriptionText = ${zzz.descriptionText}\n"
            debug_string += "       state_change    = ${zzz.isStateChange()}\n"
            //debug_string += "       physical        = ${zzz.isPhysical()}\n"
            debug_string += "       value           = ${zzz.value}\n"
            debug_string += "       source          = ${zzz.source}\n"
          
            if(zzz.value == "off") {
                def seconds_since_last_mark = (zzz.date.getTime() - last_event.getTime()) / 1000
                total_secs += seconds_since_last_mark
                debug_string += "       seconds since l = ${seconds_since_last_mark}\n"
            }
            
            last_event = zzz.date
        }
    }
    // If fan switch is still on, then reset the last on time to now since we've already
    // addressed this past hours runtime
    if(fanSwitch.currentSwitch == "on") {
        def fanRuntimeSeconds = (now() - state.last_fan_on_time) / 1000.0
        state.total_fan_runtime_this_hour_in_seconds = state.total_fan_runtime_this_hour_in_seconds + fanRuntimeSeconds
        
        debug_string += "Fan switch is still on so resetting last on time to now..."
        state.last_fan_on_time = now()
    } else {
        debug_string += "Fan switch is off.  No need to mess with last fan on time..."
    }
    
    debug_string += "TOTAL ON TIME: ${total_secs}\n"
    
    DEBUG("hourlyHandler:\n ${debug_string}")
}
*/

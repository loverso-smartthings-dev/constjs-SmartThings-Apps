/**
 *  DeviceMonitor SmartApp for SmartThings
 *
 *  Overview
 *  ----------------
 *  This SmartApp helps you monitor the status of your SmartThings devices.
 *
 */

definition(
    name: "DeviceMonitor",
    namespace: "jscgs350",
    author: "jscgs350",
    description: "SmartApp to monitor devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    page name:"pageStatus"
    page name:"pageConfigure"
}

// Show Status page
def pageStatus() {
    def pageProperties = [
        name:       "pageStatus",
        title:      "DeviceMonitor Status",
        nextPage:   null,
        install:    true,
        uninstall:  true
    ]

    if (settings.devices == null) {
        return pageConfigure()
    }
    
	def devicelist = ""
	    
	return dynamicPage(pageProperties) {
		settings.devices.each() {
            def lastTime = it.events(max: 1).date
			try {
                devicelist += "$it.displayName:\n          $lastTime\n"                
            } catch (e) {
            	log.trace "Caught error checking device status."
                log.trace e
                devicelist += "NOT OK - $it.displayName\n"
            }
        }

        if (devicelist) {
            section("Devices with current status") {
                paragraph devicelist.trim()
            }
		}

		section("Menu") {
            href "pageStatus", title:"Refresh", description:"Tap to refresh"
            href "pageConfigure", title:"Configure", description:"Tap to open"
        }
    }
}

// Show Configure Page
def pageConfigure() {
    def helpPage =
        "Select devices that you wish to monitor."

    def inputDevices = [
        name:           "devices",
        type:           "capability.Sensor",
        title:          "Which devices?",
        multiple:       true,
        required:       true
    ]

    def pageProperties = [
        name:           "pageConfigure",
        title:          "DeviceMonitor Configuration",
        nextPage:       "pageStatus",
        uninstall:      true
    ]

	return dynamicPage(pageProperties) {
        section("About") {
            paragraph helpPage
        }

		section("Devices") {
            input inputDevices
        }

        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
        }
    }
}

def installed() {
    initialize()
}

def updated() {
    unschedule()
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(devices, "Sensor", sensorHandler)
	runIn(60, updateSensorStatus)
}

def updateSensorStatus() {
//	log.debug "Just testing the app"
}


def sensorHandler(evt) {
	updateSensorStatus()
}

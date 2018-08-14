# Xiaomi ADB/Fastboot Tools

**Features:**

* **Debloater** \- Remove pre-installed apps and services on demand
* **Camera2 & EIS Enabler** \- Enable camera2 and EIS (TWRP required)
* **Read device properties** \- Retrieve tons of statistics and information about your phone
* **Flasher** \- Flash any partition with an image or boot to any image (unlocked bootloader required)
* **Wiper** \- Wipe the cache or perform a factory reset
* **OEM Unlocker & Locker** \- Lock or unlock the bootloader \(Unlocking is supported by the Android One phones only\)
* **Rebooter** \- Advanced rebooting options using ADB/Fastboot

[Screenshot](https://i.imgur.com/F0Pd5l0.png)

**Warning: Use the tool at your own risk. Removing factory apps which aren't in the tool may break your phone.**

Download the binary and the instructions from [here](https://github.com/Saki-EU/XiaomiADBFastbootTools/releases/latest).

**Frequently Asked Questions:**

**Q:** The tool doesn't launch on my computer, is there anything I should have installed?

* **A:** Yes, the tool was developed in Java and needs the Java Runtime Environment to run. You can download Java from [here](https://java.com/en/download/). On Linux, make sure adb and fastboot are installed system wide via APT.

**Q:** The tool on Windows doesn't detect my phone even though it's connected and USB debugging is enabled. What's the problem?

* **A:** Windows most likely does not recognise your phone in ADB. Install the universal ADB drivers from [here](http://dl.adbdriver.com/upload/adbdriver.zip).

**Q:** Do I need an unlocked bootloader or root access to use the tool?

* **A:** The Image Flasher, the Wiper and the Camera2 enabler require an unlocked bootloader but everything else works without rooting or unlocking.

**Q:** Do uninstalled system apps affect OTA updates?

* **A:** No, you are free to install updates without the fear of bricking your device or losing data.

**Q:** The tool is called Xiaomi ADB/Fastboot Tools. Does that mean it only works with Xiaomi devices?

* **A:** Well, ADB and Fastboot are universal interfaces for Android devices but some of the algorithms and methods used in the app are Xiaomi specific, so yes.

**Q:** Does this replace MiFlash or MiUnlock?

* **A:** No. Implementing their functionality in such a simple tool would only make it unnecessarily complex.

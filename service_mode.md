## AUTO PUSH SERVICE MODE ##

You have the ability to run auto push as a background non-graphical program. On Windows you do this by running this program as a service. For Linux and Mac platforms you simply run this program in non-gui mode as a background program.

### RUNNING AUTO PUSH PROGRAM AS A SERVICE IN WINDOWS ###

In Windows the auto push program can be setup to run as a Windows Service called **auto\_push**.

You can install, start, stop and remove auto\_push service directly from the auto push GUI:
  * Use **Service->Status** to view the status of auto\_push service
  * Use **Service->Install** to install the auto\_push service NOTE: This only installs the service, you then need to start it using entry below.
  * Use **Service->Start** to start the auto\_push service
  * Use **Service->Stop** to stop the auto\_push service
  * Use **Service->Remove** to remove the auto\_push service

Alternatively there is another way to control auto\_push service. Browse to the folder where you installed auto push and then go to **service\win32** folder. Here there are scripts to install, start, stop or uninstall the auto push service. For Windows XP simply double-click on the appropriate script to run it. **For Vista these may have to be run as Administrator in order to work, so you should right click and select "Run as administrator" to run them.**

  * **install-service.bat** Installs auto\_push as a service
  * **start-service.bat** Will start an already installed auto\_push service
  * **stop-service.bat** Stops a running auto\_push service (this doesn’t delete the service, merely stops it)
  * **uninstall-service.bat** Deletes the auto\_push service such that it won’t automatically start running again upon reboot

NOTE: If you have a service already running from a previous auto push installation you must uninstall the service using that installation first and then re-install using the new installation.

NOTE: All output messages will be logged in **auto\_push.log** file which resides in the main auto push installation folder (where auto\_push.jar file is located).

For more specific control you can get to the auto\_push service in Windows as follows:

  * **Control Panel->Administrative Tools->Services**
  * Scroll down and find **auto\_push** entry
  * Right click on **auto\_push** and stop service if it is running
  * Right click on **auto\_push** and select **Properties**

### CHANGING AUTO PUSH WINDOWS SERVICE STARTUP MODE ###

By default the auto\_push service is setup as **automatic** startup mode. This means that should you restart your computer the auto\_push service will automatically be started on bootup. If desired you can change the mode to manual (which means you will need to manually start and stop the service and by default the service will not start on bootup) as follows (Windows XP):

  * Control Panel – Administrative Tools – Services
  * Find the auto\_push service in the list and right click and select **Properties** and change **Startup Type** to **Manual**
  * Note that you can also stop and start the service from here if you wish


### CONTROLLING AUTO PUSH WINDOWS SERVICE FROM COMMAND LINE ###

You can control services from the command line using the “sc.exe” command. Examples:

  * sc query auto\_push
  * sc start auto\_push
  * sc stop auto\_push
  * sc delete auto\_push


### FURTHER DETAILS ON AUTO PUSH WINDOWS SERVICE ###

Java scripts cannot natively be run as services in windows. auto push uses a windows wrapper program which can interface with Windows services in order to run:

**`"<path>\service\win32\bin\wrapper.exe" -s "<path>\service\conf\wrapper.conf"`**

The **wrapper.conf** files sets up to run Java with **net.pyTivo.auto\_push.main.main -b** as arguments.

The **–b** option to indicates to run auto\_push in batch (non graphical) mode, running in an infinite loop and monitoring configured shares.


### SETTING UP AUTO PUSH AS A BACKGROUND JOB (MACINTOSH/LINUX) ###

On Mac & linux systems you can setup to run auto push as a background non-graphical job. You need to start auto push with **-b** argument to do this. You can use the **auto\_push** that comes with the installation and simply add -b argument when running it:

**auto\_push -b &**

If you reboot your system then you will need to re-start the program, or you can use the /etc/rc method to put a script in place that will automatically start the program in background mode with the **–a** option.
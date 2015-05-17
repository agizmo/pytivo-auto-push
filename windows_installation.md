# AUTO PUSH WINDOWS INSTALLATION INSTRUCTIONS #

## 1. INSTALL JAVA IF NEEDED ##

auto push is written in Java and supports Java 1.5 or later. You can download Java Runtime Environment (JRE) from here:

http://java.com/en/download/manual.jsp

## 2. DOWNLOAD AUTO PUSH INSTALLATION ZIP FILE ##

Download auto\_push installation zip file from:

http://code.google.com/p/pytivo-auto-push/downloads/list

  * **auto\_push\_vxxx.zip** file – This contains the platform independent auto push installation files

## 3. UNPACK ZIP FILE ##

  * Unpack **auto\_push\_vxxx.zip** file to your desired location. In this example: c:\auto\_push

### TIP ###
If upgrading from a previous Java auto push installation you can normally unzip over the previous installation or just replace the **auto\_push.jar** file of the old installation with the one from the new installation. That way you can preserve the auto push configuration and avoid having to re-configure. NOTE: In some cases there are other files  updated as part of a release, so you can also just unzip the file over the top of your previous installation to make sure you overwrite all files that make up an installation.

**NOTE: If you have older version of auto push service running you should shut down the service first and then unpack zip file over existing installation. If you don't do so then certain files cannot be overridden and hence installation will fail.**

**NOTE: If you are installing to a new location and use auto push service then make sure you remove the auto push service and install it again using the new auto push installation otherwise you will still be running older version.**

## 4. STARTING AUTO PUSH ##
Simply double click on **auto\_push.jar** file (c:\auto\_push\auto\_push.jar in this example) of the installation to start the program. (You can also create a shortcut to that file if you wish to make it easier).
  * The first time launch you will need to supply the full path to your **pyTivo.conf** file. You can double-click in the **pyTivo config file** field to bring up a file browser to help.
> NOTE: auto push detects Tivos on your network automatically using **Bonjour**, so you may have to grant permissions for this program to access your local network in order for the discovery to work.
  * Consult the [configuring\_auto\_push](http://code.google.com/p/pytivo-auto-push/wiki/configuring_auto_push) wiki page for details.
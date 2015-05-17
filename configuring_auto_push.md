# CONFIGURING AUTO PUSH #
**This step is very important and must be run at least the first time you start auto push.**

## PYTIVO SETUP ##
  * This program will only be able to push video files that are located under video shares you have defined in your pyTivo setup since it uses pyTivo to execute the push. Therefore you should make sure that there is a video share defined in pyTivo with a top level folder under which your videos will reside.
  * For example, say that you want this program to automatically push video files that are dropped into a folder called **c:\myvideos**. In pyTivo setup you should define a video share with any name you like and with the path set to **c:\myvideos**.
> NOTE: This program watches for video files from the top level folder down, so you can have a hierarchical folder structure if you wish, just make sure pyTivo has a share defined that points to the top level folder.

  * In order for pyTivo to be able to do "pushes" it needs your tivo.com login & password information. With pyTivo running you can use http://localhost:9032 in your browser to get to pyTivo **Web Configuration**. Under **Global Server Settings** you have to set **tivo\_username** and **tivo\_password** according to your tivo.com login & password. If you don't see entries there then just edit pyTivo.conf with a text editor and under **`[Server]`** section put 2 entries:
```
   tivo_username = ...
   tivo_password = ...
```

## TEST PYTIVO PUSH SETUP ##
Once you have pyTivo setup as detailed above you should test your setup within pyTivo, which you can do as follows:
  * Make sure pyTivo is running
  * With a browser connect to pyTivo web server: http://localhost:9032
  * Under **Push from video shares:** text click on one of your video shares
  * Locate and put a check mark by 1 of the videos in the share
  * By **Send to TiVo** button select a TiVo you would like to push the video to and then click on the button
  * Wait a little while and confirm that the video successfully pushes to the chosen TiVo.

## AUTO PUSH SETUP ##
  * If your pyTivo server is not already running make sure to start it.
  * Start auto push GUI if you have not done so already.
  * You need to supply full path to your pyTivo installation **pyTivo.conf** file. You can double-click in the **pyTivo config file** field to bring up a file browser to help you locate that file.
  * Be sure to press **Return** in the pyTivo.conf field once you have entered it (even if you used the browser this is still necessary).
  * Once you enter a valid pyTivo.conf file and press **Return** in the field, you should see listed in the table all the video share names and paths you have defined in your pyTivo setup.
  * This program uses Bonjour to automatically detect Tivos on your home network, so you should see your Tivos listed in the **Tivo** cyclic.
  * Select one of the entries in the table, then select the Tivo you want to push files to and then enable the **Auto Push** boolean. You will notice the name of the Tivo you selected now will display in the AUTO PUSH column. That's it, now the program will automatically monitor that video share for video files and push them automatically to the chosen Tivo.
  * Repeat above for any additional shares you wish to have this program monitor.
  * Select **File->Save** when done.
> NOTE: Your settings are saved to **config.ini** file located in same folder as auto\_push.jar file.

  * To disable monitoring of a share simply select the share then disable the **Auto Push** boolean. The AUTO PUSH column will be empty for any shares that are not being monitored.

## PUSHED VIDEO FILE TRACKING ##
  * In order to prevent the same video file from being repeatedly pushed to your Tivo(s) this program creates **auto\_push.txt** files at the top level video share folder with the names of video files that have been successfully pushed. Note that the paths to the file names are relative to the top level video share folder.
  * If there are any video files that you do not want pushed to your Tivos you can manually add them to the tracking file **BEFORE** you start monitoring that share.
### FOR EXAMPLE ###
  * Say you have a pyTivo share with the top level path define as **c:\myvideos**
  * Say you have a file under that share that you do not want to push to your Tivo: **c:\myvideos\movies\adventure\IndianaJones.mpg**
  * You would edit/create **c:\myvideos\auto\_push.txt** file and add the following line (remember that path has to be relative to location of the auto\_push.txt file):
> `movies\adventure\IndianaJones.mpg`

## RUNNING AUTO PUSH IN SERVICE/BACKGROUND NON-GRAPHICAL MODE ##
  * Once you have used the GUI to configure this program to your liking it is no longer necessary to run the program in graphical mode.
  * Consult [service\_mode](http://code.google.com/p/pytivo-auto-push/wiki/service_mode) wiki entry for details on running this program as a service in windows or background job for Linux/Mac systems.
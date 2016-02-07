-How use Switcher?
 Once configured, you will be shown a list with the environmnets, you just need
 to select one and hit the "Switch" button and restart the server.
 Yes, that's it!!!

-How configure Switcher?
  1- In your httpd.conf file, comment ALL the lines describing specific environmnet
	Example:
	#ProxyPass /appserver http://aio1ac14.devvpc.vocal-dev.com:8080/appserver
  2- In your httpd.conf file, add this line:  Include conf/domains.conf
  3- In Intellig, go to Configurations and clean the field VM options
  3- Open Swticher, go to File > Settings, and configure: 
       -JVM installation directory
       -Apache installation directory 
       -Tomcat installation directory

-How does Switcher works?  
  Switcher will not modify any of your existing configuration files.

  Instead it will create the file %APACHE%/conf/domains.conf
  (wich you previously included in your httpd.conf file)
  and then write the configurations needed to connect to the specified host.

  Also it will creates the file %TOMCAT%/bin/setenv.bat
  This file (if exist) is read by Tomcat to load JVM settings.
  So this will be used by switcher to set the JVM Options we used to
  specify in Intellij (including the specified host)
  
-Who to ask if need help with Switcher?
 You may contact the author at: Roberto.Rodriguez@vonage.com 

-Can I see the source code?
 Yes, you can find the source code at https://github.com/roberto-rodriguez/Switcher
 Pull Requests are accepted.

 
# WoWFeatureExtractor
Extraction of user interaction features from World of Warcraft
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This is a project for the practical course "Multimedia Information Retrieval"
at Fernuiversität in Hagen, summer of 2023
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Records your interactions in World of Warcraft with the integrated AddOn,
then converts the recorded data into an .xml file 
compatible with the GMAF framework (https://github.com/stefanwagenpfeil/gmaf)

How to use:
-------------
- Make sure JAVA_HOME is set to the JDK installation directory (Version 17)
- Start the Java application
  - Maven:
`mvn clean javafx:run`
  - alternatively: Run Launcher.main() in IDE
- Click "Install AddOn"
- Start World of Warcraft (you can close the Java application if you want to)
- Start or stop the recording by left-clicking the minimap button with the yellow camera or use the slash commands: /frt start, /frt stop
  - you can delete all previous recordings by right-clicking or by the slash command /frt clear
- Log out of the game (=> your recording(s) get saved)
- Back in the Java application, choose "Export to XML"

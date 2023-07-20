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
- Start the Java application
  - Maven:
`mvn clean javafx:run`
  - alternatively: Run Launcher.main() in IDE
- click "Install AddOn"
- Start World of Warcraft (you can close the Java application if you want to)
- recording is controlled by the minimap button with the yellow camera or by the slash commands: /frt start, /frt stop, /frt clear
- Exit / relog to save Addon data
- Back in the Java application, choose "Export to XML"

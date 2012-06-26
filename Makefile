compile:
	javac7 -cp . se/kth/maandree/noxutil/*.java
	jar -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class

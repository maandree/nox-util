all: noxutil

noxutil:
	javac7 -cp . se/kth/maandree/noxutil/*.java || javac -cp . se/kth/maandree/noxutil/*.java
	jar7 -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class || jar -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class

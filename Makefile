all: noxutil

noxutil:
	javac7 -cp . se/kth/maandree/noxutil/*.java || javac -cp . se/kth/maandree/noxutil/*.java
	jar7 -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class || jar -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class

install:
	install -D -m 755 'nox-alarm.jar' '22:00' '22;00.jar' {nox-alarm,quack,winise}{,.jar} "$(DESTDIR)/usr/bin/"

uninstall:
	unlink "$(DESTDIR)/usr/bin/nox-alarm.jar"
	unlink "$(DESTDIR)/usr/bin/22:00"
	unlink "$(DESTDIR)/usr/bin/22;00.jar"
	unlink "$(DESTDIR)/usr/bin/nox-alarm"
	unlink "$(DESTDIR)/usr/bin/nox-alarm.jar"
	unlink "$(DESTDIR)/usr/bin/quack"
	unlink "$(DESTDIR)/usr/bin/quack.jar"
	unlink "$(DESTDIR)/usr/bin/winise"
	unlink "$(DESTDIR)/usr/bin/winise.jar"

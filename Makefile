all: noxutil

noxutil:
	javac7 -cp . se/kth/maandree/noxutil/*.java || javac -cp . se/kth/maandree/noxutil/*.java
	jar7 -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class || jar -cfm nox-alarm.jar META-INF/MANIFEST.MF se/kth/maandree/noxutil/*.class

install:
	install -d "$(DESTDIR)/usr/share/nox-util/rfc/"
	install -d "$(DESTDIR)/usr/bin/"
	install -m 755 'nox-util.jar' '22:00' '22;00.jar' "$(DESTDIR)/usr/bin/"
	install -m 755 {nox-alarm,quack,winise,distinct,rfc,rfcdownloader}{,.jar} "$(DESTDIR)/usr/bin/"

uninstall:
	rm -r "$(DESTDIR)/usr/share/nox-util/"
	unlink "$(DESTDIR)/usr/bin/nox-util.jar"

	unlink "$(DESTDIR)/usr/bin/22:00"
	unlink "$(DESTDIR)/usr/bin/nox-alarm"
	unlink "$(DESTDIR)/usr/bin/quack"
	unlink "$(DESTDIR)/usr/bin/winise"
	unlink "$(DESTDIR)/usr/bin/distinct"
	unlink "$(DESTDIR)/usr/bin/rfc"
	unlink "$(DESTDIR)/usr/bin/rfcdownloader"

	unlink "$(DESTDIR)/usr/bin/22;00.jar"
	unlink "$(DESTDIR)/usr/bin/nox-alarm.jar"
	unlink "$(DESTDIR)/usr/bin/quack.jar"
	unlink "$(DESTDIR)/usr/bin/winise.jar"
	unlink "$(DESTDIR)/usr/bin/distinct.jar"
	unlink "$(DESTDIR)/usr/bin/rfc.jar"
	unlink "$(DESTDIR)/usr/bin/rfcdownloader.jar"

clean:
	rm se/kth/maandree/noxutil/*.class
	rm nox-alarm.jar


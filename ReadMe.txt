debug from emulator::

Connecting wear device to emulator:

adb forward tcp:4444 localabstract:/adb-hub
adb connect localhost:4444

uninstall wear app
adb -s localhost:4444 uninstall mpsc.smartar


Release apk deploy and file browse from PC:
	1. Create singed apk from android studio
	copy the .apk file to the download folder then install that apk on the smartphone and it will automatically install wear app on the watch

	To get access of the saved files on smartphone follow the following steps:

	1. run SSHDroid on smartphone and get the ip from there

	2. click start menu from linux and type nautilus and run nautilus cmd
		user: root
		pass: admin
		sftp://root@YOUR-IP-ADDRESS:PORT
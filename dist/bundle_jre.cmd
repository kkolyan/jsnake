@rem https://stackoverflow.com/questions/51403071/create-jre-from-openjdk-windows
jlink.exe --no-header-files --no-man-pages --compress=2 --add-modules ^
java.desktop,^
 --output %1

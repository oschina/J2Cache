## auto build script
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home
export J2CACHE_HOME=/Users/winterlau/Documents/workdir/J2Cache
cd $J2CACHE_HOME
$JAVA_HOME/bin/java -cp "$J2CACHE_HOME/lib/ant-launcher.jar:$J2CACHE_HOME/lib/ant.jar:$JAVA_HOME/lib/tools.jar" org.apache.tools.ant.launch.Launcher -lib $OSCHINA_HOME/packages $*

#clone sample-apps directory
git clone https://github.com/AppScale/sample-apps.git
#download sdk into java folder
echo "Please enter SDK version:"
read sdk_version
#download and unzip sdk
cd sample-apps/java
wget http://googleappengine.googlecode.com/files/appengine-java-sdk-${sdk_version}.zip
unzip appengine-java-sdk-${sdk_version}.zip
rm appengine-java-sdk-${sdk_version}.zip
#rename sdk to generic name
mv appengine-java-sdk-${sdk_version} appengine-java-sdk

#loop through each directory in the java sample apps folder
for i in `ls`
  do
    #dont do anything if it's one of these three files/dirs
    if [ "$i" != "build.xml" ] && [ "$i" != "appengine-java-sdk" ] && [ "$i" != "README" ]; then
        #copy the build.xml into the sample app
        cp ../../build.xml $i
        #remove all jars and compiled classes
        rm $i/war/WEB-INF/lib/*.jar
        rm -r $i/war/WEB-INF/classes/
        #cd into that sample app dir
        cd $i
        #run three ant targets
        ant copyjars
        ant compile
        ant datanucleusenhance
        #cleanup
        rm build.xml
        cd ..
    fi
done

rm -r appengine-java-sdk

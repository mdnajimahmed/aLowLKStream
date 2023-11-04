./gradlew build
java -jar build/libs/aLowLStreamApp-0.0.1-SNAPSHOT.jar
scp -i ~/Documents/a_low_l/secrets/myfirstawsaccount.pem ./build/libs/aLowLStreamApp-0.0.1-SNAPSHOT.jar ec2-user@ec2-54-179-170-224.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/
java -jar aLowLStreamApp-0.0.1-SNAPSHOT.jar

scp -i ~/Documents/a_low_l/secrets/myfirstawsaccount.pem ./build/libs/aLowLStreamApp-0.0.1-SNAPSHOT.jar ec2-user@18.141.118.198:/home/ec2-user/


aLowLCluster
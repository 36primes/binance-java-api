Kubernetes concepts
Kubernetes supports multiple virtual clusters backed by the same physical cluster. These virtual clusters are called namespaces. Namespaces provide a scope for names. Names of resources need to be unique within a namespace, but not across namespaces. Namespaces cannot be nested inside one another and each Kubernetes resource can only be in one namespace.

The Kubernetes command-line tool, kubectl, allows you to run commands against Kubernetes clusters. You can use kubectl to deploy applications, inspect and manage cluster resources, and view logs.

A custom resource is an extension of the Kubernetes API that is not necessarily available in a default Kubernetes installation. It represents a customization of a particular Kubernetes installation. Once a custom resource is installed, users can create and access its objects using kubectl, just as they do for built-in resources like Pods.


Minikube for development environment
Minikube is local Kubernetes, focusing on making it easy to learn and develop for Kubernetes. Minikube can be installed and run on your laptop. 

In this exercise, you'll install a functionally complete Confluent Platform on Minikube.



Minikube has already been installed on this lab environment. Confluent for Kubernetes supports Kubernetes version 1.20. 



Start the minikube cluster, using Kubernetes version 1.20:

minikube start --kubernetes-version=v1.20.9
Check that Minikube starts successfully by viewing status:

minikube status



Install Confluent for Kubernetes Operator
Now that Minikube is up and running, we'll install the Confluent for Kubernetes (CFK) Operator. We'll install it in a specific namespace - "confluent".

# Create namespace to use
kubectl create ns confluent

# Set namespace for current context to `confluent`. With this in place, all subsequent kubectl commands will assume that the namespace to use is `confluent`
kubectl config set-context --current --namespace confluent

# Check your kubectl context
kubectl config get-contexts

# Add the Confluent Helm repository. Helm is used to package the Confluent for Kubernetes(CFK) Operator and CRDs.
helm repo add confluentinc https://packages.confluent.io/helm

# Install CFK Operator
helm install cfk-operator confluentinc/confluent-for-kubernetes -n confluent

# Once install is successful, you'll see the installed chart
helm list -n confluent
NAME          NAMESPACE  REVISION  STATUS    CHART                              APP VERSION
cfk-operator  confluent  1         updated     confluent-for-kubernetes-0.174  2.0.2

# The Helm chart deploys the Confluent for Kubernetes  (CFK) Operator as a pod. You should see it up and running.
kubectl get pods -n confluent
NAME                                  READY   STATUS    RESTARTS   AGE
confluent-operator-66bcf88444-vd5gg   1/1     Running   0          14h
You should also now have the Confluent CRDs installed.

kubectl get crds
NAME                                          CREATED AT
confluentrolebindings.platform.confluent.io   2021-08-31T07:02:25Z
connects.platform.confluent.io                2021-08-31T07:02:25Z
controlcenters.platform.confluent.io          2021-08-31T07:02:25Z
kafkarestclasses.platform.confluent.io        2021-08-31T07:02:25Z
kafkas.platform.confluent.io                  2021-08-31T07:02:25Z
kafkatopics.platform.confluent.io             2021-08-31T07:02:25Z
ksqldbs.platform.confluent.io                 2021-08-31T07:02:25Z
migrationjobs.platform.confluent.io           2021-08-31T07:02:25Z
schemaregistries.platform.confluent.io        2021-08-31T07:02:25Z
zookeepers.platform.confluent.io              2021-08-31T07:02:25Z
As we discussed in the concepts section, a  CustomResourceDefinition (CRD) resource allows you to define custom resources. 

Confluent for Kubernetes includes a CRD for each Confluent Platform component, as well as a CRD for Topics and Confluent Role Based Access Control rolebindings.

Confluent for Kubernetes includes CustomResourceDefinitions (CRDs) for each Confluent Platform component service.

Install Confluent Platform
You'll install Confluent Platform in a single node configuration. This single node configuration is expressed in a declarative spec YAML.
# Take a look at the declarative spec YAML
vi /home/ubuntu/code/cfk-workshop/dev/confluent-platform-minikube.yaml

# Deploy the yaml
kubectl apply -f /home/ubuntu/code/cfk-workshop/dev/confluent-platform-minikube.yaml -n confluent

# Open up another terminal tab, and watch the pods come up
watch kubectl get pods
Since this is a single node development environment, the Confluent component pods will come up one after the other, in order of dependency. You'll see Zookeeper come up first, then Kafka, then the other component services.



Confluent for Kubernetes includes a kubectl plugin, that you can use to get information on your deployment.

# Check out the plugin CLI commands
kubectl confluent
# Look at the status of all services
kubectl confluent status -n confluent
# Check what versions have been installed. The latest Confluent Platform release is 6.2.0.
kubectl confluent version -n confluent

Concepts
Kafka Streams is an abstraction over producers and consumers that lets you ignore low-level details and focus on processing your Kafka data. Since it's declarative, processing code written in Kafka Streams is far more concise than the same code would be if written using the low-level Kafka clients.

Kafka Streams is a Java library: You write your code, create a JAR file, and then start your standalone application that streams records to and from Kafka (it doesn't run on the same node as the broker). You can run Kafka Streams on anything from a laptop all the way up to a large server.



Build the Application
The KStreams Java applications are architected using: 

Spring Boot
Spring Cloud Stream
Gradle for build management
We'll build two KStreams applications. The steps will be the same for each, executed in each application folder.



1) Build the Gradle Wrapper
To build a Gradle-based project, we need to have Gradle installed in our machine. However, if our installed version doesn't match with the project's version, we'll probably face many incompatibility problems.

Gradle Wrapper, also called Wrapper in short, solves this problem. It's a script that runs Gradle tasks with a declared version. If the declared version is not installed, Wrapper installs the required one.

cd /home/ubuntu/code/cfk-workshop/apps/datagen/
gradle wrapper


Once the command executes, you'll have a gradle wrapper script for your environment. 

ll /home/ubuntu/code/cfk-workshop/apps/datagen
...
-rwxrwxr-x 1 ubuntu ubuntu 5766 Aug 23 13:01 gradlew*
...


2) Build the application
# Use gradle wrapper to build
./gradlew clean assemble -x test --build-cache --quiet
# See the build output
ls /home/ubuntu/code/cfk-workshop/apps/datagen/build/libs


3) Build the application as a Docker image
# Build the Docker image locally (on this development machine)
./gradlew bootBuildImage --imageName=docker.io/<your_docker_id>/datagen

# Push the Docker image to Docker hub
docker push 

# View the images in local Docker
docker images
REPOSITORY                         TAG
rohit2b/datagen                    latest


4) Push your image to your Docker account
# Log in to your Docker account
docker login
Username: ...
Password: ***
# Push images to your Docker repository
docker push <your_docker_id>/datagen
Repeat steps 1-4 for the Wordcount application

cd /home/ubuntu/code/cfk-workshop/apps/wordcount/

gradle wrapper

./gradlew clean assemble -x test --build-cache --quiet

./gradlew bootBuildImage --imageName=docker.io/<your_docker_id>/wordcount

docker push <your_docker_id>/wordcount


Deploy application to Minikube
Deploy the Datagen application.

cd /home/ubuntu/code/cfk-workshop/apps

# Edit the datagen-app-deployment.yaml to use your Docker account, not `rohit2b`
vi datagen-app-deployment.yaml
...
       image: docker.io/<Your_docker_id>/datagen
...

kubectl apply -f /home/ubuntu/code/cfk-workshop/apps/datagen-app-secret.yaml
kubectl apply -f /home/ubuntu/code/cfk-workshop/apps/datagen-app-deployment.yaml
Your datagen application should be deployed.

# Check that application pod is up and running
kubectl get pods -n confluent

# Check that there are no errors in the application
kubectl logs-f datagen-quotes-0 -n confluent

# See that the topic has been created
kubectl get kafkaTopic -n confluent
Deploy the Wordcount application.

cd /home/ubuntu/code/cfk-workshop/apps

# Edit the wordcount-app-deployment.yaml to use your Docker account, not `rohit2b`
vi wordcount-app-deployment.yaml
...
       image: docker.io/<Your_docker_id>/wordcount
...

kubectl apply -f /home/ubuntu/code/cfk-workshop/apps/wordcount-app-secret.yaml
kubectl apply -f /home/ubuntu/code/cfk-workshop/apps/wordcount-app-deployment.yaml
Your wordcount application should be deployed.

# Check that application pod is up and running
kubectl get pods -n confluent

# Check that there are no errors in the application
kubectl logs-f wordcount-0 -n confluent

# See that the topic has been created
kubectl get kafkaTopic -n confluent
View messages being produced through CLI
The datagen application writes messages to topic "quotes". This topic has been defined as a Kubernetes CustomResource:
kubectl describe kafkatopic quotes
Now, let's actually look at the contents of the topic. You'll ssh exec into the Kafka pod to avail of CLI tools.

Exec in to the Kafka pod

kubectl exec kafka-0 -it bash
Run the CLI consumer to read all topic messages:

kafka-console-consumer --bootstrap-server kafka.confluent.svc.cluster.local:9092 -topic quotes --from-beginning
View messages in Control Center
Confluent Control Center is a GUI interface to monitor Confluent Platform.



Check to see what the Kubernetes service endpoint is for Control Center.

kubectl get svc
...
controlcenter                ClusterIP      None             <none>        9021/TCP,7203/TCP,7777/TCP,7778/TCP                              44m
controlcenter-0-internal     ClusterIP      10.108.70.244    <none>        9021/TCP,7203/TCP,7777/TCP,7778/TCP                              44m
...
Start a port forwarding to allow access to the Control Center GUI.

kubectl port-forward --address 0.0.0.0 svc/controlcenter-0-internal 9021:9021
Find the public IP address by clicking on top left > settings icon > Machine info 



Copy the public iP address, and put it in a new browser window:

http://54.185.198.222:9021/

Where "54.185.198.222" is the Public IP address.

You now should be in the Control Center app.

1) Click on the cluster tile

2) Click on Topic in the left nav

3) Click on the topic name "quotes"

4) Go to messages

5) Click on the top right `cards` option.

Then, look at the stream of messages being produced.

Do the same for topic "counts".



Review the applications and data
You've deployed two Kafka streams applications, that are built using Spring Boot.

The datagen application produces messages that contain quotes from a movie to a topic "quotes".

The wordcount application consumes those messages from the topic "quotes", processes them and determines the counts of specific words in the messages, and then writes the counts for each word to the topic "counts".

Review the code for both applications to get a more in-depth understanding:

vi /home/ubuntu/code/cfk-workshop/apps/datagen/src/main/java/io/confluent/developer/datagen/DatagenApplication.java
vi /home/ubuntu/code/cfk-workshop/apps/wordcount/src/main/java/io/confluent/developer/datagen/WordcountApplication.java


Learn More
Learn more about Kafka streams at Check out https://developer.confluent.io/learn-kafka/kafka-streams/get-started/

Learn more about Spring Boot at https://spring.io/projects/spring-boot

Learn more about Spring Cloud Stream at https://spring.io/projects/spring-cloud-stream

//
 General License info: https://docs.confluent.io/platform/current/installation/license.html#cp-proprietary-components-post-expire

This covers different license types available.
FYI A Developer License allows full use of the Confluent Platform free of charge for an indefinite duration, but the license is limited to a single broker.
Excuse me, use this link instead: https://docs.confluent.io/platform/current/installation/license.html
//

We have a production deployment with Confluent Platform on VMWare Tanzu.

See how the production deployment was set up - watch the short video in "My Lab" > "Video" (button in top center section). 



In this exercise, you'll pull data from a topic in the production cluster in to your development cluster.

To do this, you will:

- define a Cluster Link between the production cluster and the development cluster

- create a mirror topic on the development cluster to mirror all data in the production topic in to the mirror topic on the development cluster

Cluster Linking allows you to directly connect clusters together and mirror topics from one cluster to another. Cluster Linking makes it much easier to build multi-datacenter, multi-cluster, and hybrid cloud deployments.



Set up certificate authority truststore
The production cluster has TLS network encryption enabled. In order to connect to it, the development cluster will need to trust the certificate authority used to create the server certificates in production.

Add the certificate authority truststore to be used by the development cluster:
kubectl cp /home/ubuntu/code/cfk-workshop/dev/truststore.p12 kafka-0:/home/appuser/truststore.p12


Create the cluster link
You'll create a cluster link to pull data from the production cluster and in to the development cluster.In order to do that, you'll need to specify the Kafka properties to connect to the production cluster's Kafka listener.
This configuration includes the following:
bootstrap.servers: The production cluster's Kafka listener endpoint
sasl.jaas.config, sasl.mechanism, security.protocol: The SASL Plain configuration and credentials to use to connect to the production cluster
ssl.truststore.location, ssl.truststore.password: The truststore to use in order to trust the production certificate authority
Exec SSH in to the Kafka broker pod and use the CLI tools to create a cluster link:
kubectl exec kafka-0 -it bash

# Create configs
cat << EOF > kafka.properties
bootstrap.servers=kafka.cfk-demo.app:9092
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username=devuser password=dev-password;
sasl.mechanism=PLAIN
security.protocol=SASL_SSL
ssl.truststore.location=/home/appuser/truststore.p12
ssl.truststore.password=mystorepassword
EOF

# Create cluster link
kafka-cluster-links --bootstrap-server localhost:9092 --create --link-name datagen-link --config-file kafka.properties
# List the created cluster links
kafka-cluster-links --list --link-name datagen-link --include-topics --bootstrap-server localhost:9092
Create the mirror topic
Now that a cluster link is created, you will create a mirror topic on the development cluster.
A cluster link connects a mirror topic to its source topic. Any messages produced to the source topic are mirrored over the cluster link to the mirror topic.Mirror topics are byte-for-byte, offset-preserving asynchronous copies of their source topics. They are read-only; you can consume them the same as any other topic, but you cannot produce into them.
# Create a mirror topic
kafka-topics --bootstrap-server localhost:9092 --create --topic prod-quotes --link-name datagen-link --mirror-topic prod-quotes
# List the created cluster links, see mirrored topics list
kafka-cluster-links --list --link-name datagen-link --include-topics --bootstrap-server localhost:9092
Review the mirror topic
Log in to Control Center. You had set up a connection to Control Center in the prior exercise. Use the same URL.

Once logged in, navigate to the topic section. You should see the "prod-quotes" topic.


Explore the CLI
See all created cluster links:

kubectl exec kafka-0 -it bash
kafka-cluster-links --list --bootstrap-server localhost:9092
List all mirror topics for a cluster link:

kubectl exec kafka-0 -it bash
kafka-cluster-links --list --link-name datagen-link --include-topics --bootstrap-server localhost:9092
View the status of the mirror topic prod-quotes:

kubectl exec kafka-0 -it bash
kafka-mirrors --link-name datagen-link --bootstrap-server localhost:9092 --describe prod-quotes 
Promote  the cluster link - to be a real topic, where it can be written and managed outside the cluster link:

kafka-mirrors --promote --topics prod-quotes --bootstrap-server localhost:9092
> What is the max lag (in records) allowed to promote a mirror topic?     1000
> What is the max lag (in milliseconds) allowed to promote a mirror topic?        10000000
Once all mirror topics are promoted, you can delete the cluster link:

kafka-cluster-links --delete --link-name datagen-link --bootstrap-server localhost:9092
You can also now delete the topic.

Learn more
Confluent Cluster Linking: https://docs.confluent.io/platform/current/multi-dc-deployments/cluster-linking/index.html

Concepts
To secure Confluent Platform, think about it along the following dimensions:

Authentication - establish who is accessing the system
Network encryption  - protect data in transit
Authorization - ensure that one can do only what they have been alllowed to do
In this exercise, you'll configure authentication and network encryption for the Confluent Platform and deploy to minikube.



Deploy secure Confluent Platform
Remove the existing Confluent Platform install:

kubectl delete -f confluent-platform-minikube.yaml
kubectl get pods
You should see no Confluent Platform pods.



In order to have Confluent for Kubernetes provide auto-generated certificates,  you'll need to provide a Certificate Authority (CA). 

# Generate a CA
cd /home/ubuntu/code/cfk-workshop/dev
openssl genrsa -out ca-key.pem 2048
openssl req -new -key ca-key.pem -x509   -days 1000   -out ca.pem   -subj "/C=US/ST=CA/L=MountainView/O=Confluent/OU=Operator/CN=TestCA"
# Validate that the CA is valid
openssl x509 -in ca.pem -text -noout
Create a certificate from the files created above:

kubectl create secret tls ca-pair-sslcerts -n confluent   --cert=ca.pem   --key=ca-key.pem


You'll be using SASL/Plain mechanism for authentication. Clients use a username/password for authentication. The username/passwords are stored server-side in Kubernetes Secrets.

This file contains the username and password:

cd /home/ubuntu/code/cfk-workshop/dev

cat kafka-authentication-users.json
Create a Kubernetes secret from that file:

kubectl create secret generic credential --from-file=plain-users.json=kafka-authentication-users.json


Now that the CA and credentials are created, you can deploy the Confluent Platform deployment YAML that uses these secrets.

cat confluent-platform-secure-minikube.yaml
...
  tls:
    autoGeneratedCerts: true
  listeners:
    external:
      tls:
        enabled: true
      authentication:
        type: plain
        jaasConfig:
          secretRef: kafka-credentials
...

kubectl apply -f confluent-platform-secure-minikube.yaml

Review the Confluent Platform status
# Check that the pods all come up healthy
kubectl get pods -w
Now with Kafka up and running, take a look at the Kafka listeners. 

Kafka listeners are the interfaces made available  for client applications and other Confluent components communicate with Kafka.

kubectl confluent cluster kafka listeners
COMPONENT  NAME   LISTENER-NAME  ACCESS    ADDRESS                                 TLS    AUTH   AUTHORIZATION
Kafka      kafka  internal       INTERNAL  kafka.confluent.svc.cluster.local:9071  false         
Kafka      kafka  replication    INTERNAL  kafka.confluent.svc.cluster.local:9072  false         
Kafka      kafka  external       INTERNAL  kafka.confluent.svc.cluster.local:9092  true   plain


Update the apps to use security
Once the Confluent Platform is up, let's update the application deployments to use the secure channel to produce and consume.

The applications will connect to the Kafka listener on the endpoint "kafka.confluent.svc.cluster.local:9092".

cd /home/ubuntu/code/cfk-workshop/apps
kubectl apply -f datagen-app-secure-secret.yaml
kubectl apply -f datagen-app-secure-deployment.yaml


The client app Kafka configurations specify how to connect to the secure Kafka listener:

cat datagen-app-secure-secret.yaml
...
 application.yaml: |
    spring:
      kafka:
        properties:
          bootstrap.servers: kafka.confluent.svc.cluster.local:9092
          sasl.mechanism: PLAIN
          sasl.jaas.config: "org.apache.kafka.common.security.plain.PlainLoginModule required username='dev-client' password='dev_client-secret';"
          security.protocol: SASL_SSL
          ssl.truststore.location: "/mnt/sslcerts/truststore.jks"
          ssl.truststore.password: "mystorepassword"
...
Explore cluster and topics in Control Center
Confluent Control Center is a GUI interface to monitor Confluent Platform.

Check to see what the Kubernetes service endpoint is for Control Center.

kubectl get svc
...
controlcenter                ClusterIP      None             <none>        9021/TCP,7203/TCP,7777/TCP,7778/TCP                              44m
controlcenter-0-internal     ClusterIP      10.108.70.244    <none>        9021/TCP,7203/TCP,7777/TCP,7778/TCP                              44m
...
Start a port forwarding to allow access to the Control Center GUI.

kubectl port-forward --address 0.0.0.0 svc/controlcenter-0-internal 9021:9021
Find the public IP address by clicking on top left > settings icon > Machine info 



Copy the public iP address, and put it in a new browser window:

http://54.185.198.222:9021/

Where "54.185.198.222" is the Public IP address.

You now should be in the Control Center app.

In this exercise you will set up ArgoCD, and then deploy a Confluent for Kubernetes cluster through Github and ArgoCD.

Argo CD follows the GitOps pattern of using Git repositories as the source of truth for defining the desired application state. 

Argo CD automates the deployment of the desired application states in the specified target environments. Application deployments can track updates to branches, tags, or pinned to a specific version of manifests at a Git commit.



Set up ArgoCD on your minikube environment
First, create a namespace where you will install ArgoCD.
kubectl create namespace argocd
Second, apply this install script:
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
This command will complete quickly, but pods will still be spinning up on the back end. These need to be in a running state before you can move forward. Use the watch command to ensure the pods are running and ready.
watch kubectl get pods -n argocd
Since this is dev environment, use port-forward to expose a port to the service, and forward it to localhost.
kubectl port-forward --address 0.0.0.0 svc/argocd-server -n argocd 8080:443
Find the public IP address by clicking on top left > settings icon > Machine info 



Copy the public iP address, and put it in a new browser window:

http://54.185.198.222:8080/

Where "54.185.198.222" is the Public IP address.

You now should be in the ArgoCD app.

In this UI, you will not be able to log in yet. ArgoCD generates a custom password for every deploy. The following command will list the pods and format the output to provide just the line you want. It will have the format argocd-server-<number>-<number>.
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
To log in to the ArgoCD UI, the default username is admin and the default password is the output of the above command.
Log in through the Argo CLI. You will need to accept the server certificate error.
argocd login localhost:8080
Now you'll add the target Kubernetes cluster for ArgoCD to deploy to. This will be the minikube cluster you are on.

Get the Kubernetes contexts:
kubectl config get-contexts -o name
minikube
Add this context as the target
argocd cluster add minikube
Deploy CFK through ArgoCD
Create a namespace for the CFK through ArgoCD gitops deployment.
kubectl create ns gitops-confluent
Create a new application, using the ArgoCD UI.

Log in with `admin` username and the password you got from this command:
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
Click on "New App" in the top left of the UI screen.

Set the application name - for example cfk-minikube.

Select the `default` project.

Keep sync mode as `Manual`.

Set the Github repo URL - `https://github.com/confluentinc/cfk-workshop.git`

Set the path to the `gitops-dev` directory.

Select your target minikube cluster - `https://kubernetes.default.svc`.

Set the target namespace to the one you just created - `gitops-confluent`.

Click "Create".


In the UI, click on the `cfk-minikube` application tile.

From the top menu, click on `SYNC` button. This will sync the declarative spec files in https://github.com/confluentinc/cfk-workshop/tree/main/gitops-dev with the Kubernetes cluster.

When this is successful, you should see:

kubectl get pods -n gitops-confluent
NAME                                 READY   STATUS    RESTARTS   AGE
confluent-operator-c96b8bfff-t7g6x   1/1     Running   0          68m
connect-0                            1/1     Running   0          30m
controlcenter-0                      1/1     Running   0          30m
datagen-quotes-0                     1/1     Running   0          33m
kafka-0                              1/1     Running   1          32m
ksqldb-0                             1/1     Running   0          30m
schemaregistry-0                     1/1     Running   0          30m
zookeeper-0                          1/1     Running   0          33m

There's an issue in the word count app - it counts individual letters and counts them, not just words. Let's fix that!



The steps to this exercise are:

- Update the Wordcount code

- Re-build the jar and Docker image

- Push to your Docker repository

- Make your own Github repository and hook up to ArgoCD

- Push the wordcount yamls to your github repository



Update the Wordcount code
The source code to this Spring Boot app is at 

cd /home/ubuntu/code/cfk-workshop/apps/wordcount/src/main/java/io/confluent/developer/datagen
vi WordcountApplication.java
In that source code file, in the WordcountApplication::Function code, uncomment the line so that only words greater than 3 characters are counted.

Save the file.

Re-build the jar and Docker image
Build the application

# Use gradle wrapper to build
./gradlew clean assemble -x test --build-cache --quiet

# See the build output
ls /home/ubuntu/code/cfk-workshop/apps/wordcount/build/libs
Build the application as a Docker image

Log in to your Docker account

docker login
Username: ...
Password: ***
# Build the Docker image locally (on this development machine)
./gradlew bootBuildImage --imageName=docker.io/<your_docker_id>/wordcount

# Push the Docker image to Docker hub
docker push

# View the images in local Docker
docker images
REPOSITORY TAG
<your_docker_id>/datagen                    latest


Make your own Github repository and hook up to ArgoCD
Fork the repository https://github.com/confluentinc/cfk-workshop to your own Github repo.

If you haven't already set this up, follow the steps in the above Lab #6 to set up ArgoCD. 

Add a new application, using the same settings as from Lab #6 except for setting the Github repo URL to `https://github.com/<your-docker-account>/cfk-workshop.git`



Push the wordcount yamls to your github repository
Clone that forked repo onto the lab VM.

Update the deployment yaml to use your docker images.

cd /home/ubuntu/code/cfk-workshop/apps
vi wordcount-app-deployment.yaml
...
       image: docker.io/rohit2b/wordcount
# Change this to
       image: docker.io/<your-dockerhub-repo>/wordcount


Add the deployment yaml files for the wordcount application to your forked repo:

cp /home/ubuntu/code/cfk-workshop/apps/wordcount-app-deployment.yaml  <path-to-your-forked-repo>/gitops-dev/
cp /home/ubuntu/code/cfk-workshop/apps/wordcount-app-secret.yaml  <path-to-your-forked-repo>/gitops-dev/


Check in the code updates and push to your forked Git repo. You should now see the wordcount app deployed.



Once this is complete, you should see the wordcount app deployed to the minikube!

You can check the messages in topic `count` in Control Center UI to see the updated messages.
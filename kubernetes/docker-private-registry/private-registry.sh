# Clean up any existing running repository
docker container rm -f registry

# Running the Private Registry
docker run \
   --name registry \
   --privileged \
   -v $(pwd)/certs:/certs \
   -v `pwd`/config.yml:/etc/docker/registry/config.yml \
   -p 443:5000 \
  registry:2

 # Running the logs in tail fashion.
 docker logs -f registry
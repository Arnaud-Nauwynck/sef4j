#
# basic InfluxDB install+start script
# see also file "influxdb-test.txt" for manual testing
#
# see also https://github.com/influxdb/influxdb-java  ... for Java JUnit test starting inside Docker
#

if [ ! -e build ]
then
	if [ ! -e influxdb-latest.amd64.tar.gz ]
	then
		wget https://s3.amazonaws.com/influxdb/influxdb-latest.amd64.tar.gz
	else
		echo "skip already done ... wget https://s3.amazonaws.com/influxdb/influxdb-latest.amd64.tar.gz"
	fi

	tar zxf influxdb-latest.amd64.tar.gz
# unzipped content dir "build/":
# $ find build
# build
# build/config.toml
# build/influxdb-benchmark
# build/scripts
# build/scripts/influxdb-daemon.sh
# build/scripts/post_install.sh
# build/scripts/init.sh
# build/influxdb
# build/benchmark_config.toml


mkdir -p influxdb-data/db
mkdir -p influxdb-data/raft
mkdir -p influxdb-data/wal

else
	echo "skip already done ... wget https://s3.amazonaws.com/influxdb/influxdb-latest.amd64.tar.gz ; tar zxf influxdb-latest.amd64.tar.gz"
fi


# ./build/influxdb --help

# start using default configuration file:  user=root, password=root, http.port=8083, api.port=8086
# dirs influxdb-data/{db,raft,wal} relative to current dir, instead of default /opt/influxdb/... 

./build/influxdb -config=config.toml


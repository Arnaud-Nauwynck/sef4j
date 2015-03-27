#!/bin/bash

if [ ! -e build ]
then
  mkdir build
fi
cd build

if [ ! -e elasticsearch-1.5.0.tar.gz ]
then
	wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.5.0.tar.gz
fi

if [ ! -e elasticsearch-1.5.0 ]
then
	tar zxf elasticsearch-1.5.0.tar.gz

	./build/elasticsearch-1.5.0/bin/plugin -install mobz/elasticsearch-head

fi

./build/elasticsearch-1.5.0/bin/elasticsearch start


echo "... open your browser on  http://localhost:9200/_plugin/head/"
echo "... or open Kibana"
# gnome-open http://localhost:9200/_plugin/head/



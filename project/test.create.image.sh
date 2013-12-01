#!/bin/bash
DIR=test
mkdir -p $DIR
./scripts/livecg-create-image -input res/presets/polygons/Big.geom -output $DIR/geometry.png -output_format png -visualization geometry
./scripts/livecg-create-image -input res/presets/polygons/Big.geom -output $DIR/dcel.png -output_format png -visualization dcel
./scripts/livecg-create-image -input res/presets/polygons/Big.geom -output $DIR/monotone.png -output_format png -visualization monotone
./scripts/livecg-create-image -input res/presets/polygons/Big.geom -output $DIR/triangulation.png -output_format png -visualization triangulation
./scripts/livecg-create-image -input res/presets/polygons/Big.geom -output $DIR/spip.png -output_format png -visualization spip
./scripts/livecg-create-image -input res/presets/chan/Chan1.geom -output $DIR/chan.png -output_format png -visualization chan
./scripts/livecg-create-image -input res/presets/voronoi/Points1.geom -output $DIR/fortune.png -output_format png -visualization fortune
./scripts/livecg-create-image -input res/presets/frechet/Paper.geom -output $DIR/freespace.png -output_format png -visualization freespace
./scripts/livecg-create-image -input res/presets/frechet/Paper.geom -output $DIR/terrain.png -output_format png -visualization distanceterrain

#!/bin/bash

for format in png svg tikz ipe; do

	DIR="test/$format"
	mkdir -p $DIR

	./scripts/livecg-create-image -input res/presets/polygons/Big.geom \
		-output "$DIR/geometry.$format" -output_format "$format" -visualization geometry

	./scripts/livecg-create-image -input res/presets/polygons/Big.geom \
		-output "$DIR/dcel.$format" -output_format "$format" -visualization dcel

	./scripts/livecg-create-image -input res/presets/polygons/Big.geom \
		-output "$DIR/monotone.$format" -output_format "$format" -visualization monotone

	./scripts/livecg-create-image -input res/presets/polygons/Big.geom \
		-output "$DIR/triangulation.$format" -output_format "$format" -visualization triangulation

	./scripts/livecg-create-image -input res/presets/polygons/Big.geom \
		-output "$DIR/spip.$format" -output_format "$format" -visualization spip

	./scripts/livecg-create-image -input res/presets/chan/Chan1.geom \
		-output "$DIR/chan.$format" -output_format "$format" -visualization chan

	./scripts/livecg-create-image -input res/presets/voronoi/Points1.geom \
		-output "$DIR/fortune.$format" -output_format "$format" -visualization fortune

	./scripts/livecg-create-image -input res/presets/frechet/Paper.geom \
		-output "$DIR/freespace.$format" -output_format "$format" -visualization freespace

	./scripts/livecg-create-image -input res/presets/frechet/Paper.geom \
		-output "$DIR/terrain.$format" -output_format "$format" -visualization distanceterrain

done

for file in "test/ipe/"*.ipe; do
	echo $file
	ipetoipe -pdf "$file"
done

for file in "test/tikz/"*.tikz; do
	dir=$(dirname "$file")
	name=$(basename "$file" .tikz)
	latex="$dir/$name.latex"

	echo "Tikz: $file"
	echo "Latex: $latex"

	> "$latex"
	echo "\documentclass[a4paper]{article}" >> "$latex"
	echo "\usepackage{tikz}" >> "$latex"
	echo "\begin{document}" >> "$latex"
	echo "Test. This is a relatively long text block that only exists" >> "$latex"
	echo "to show how this paragraph will be laid out with respect to " >> "$latex"
	echo "the graphic below." >> "$latex"
	echo "\begin{center}" >> "$latex"
	echo "\input{$name.tikz}" >> "$latex"
	echo "\end{center}" >> "$latex"
	echo "Also there should be some text below. This text block" >> "$latex"
	echo "should also be a not too short, so that there will be a line" >> "$latex"
	echo "break within the paragraph." >> "$latex"
	echo "\end{document}" >> "$latex"

	pushd .
	cd "$dir"
	pdflatex "$name.latex"
	popd
done

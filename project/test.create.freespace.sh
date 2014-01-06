#!/bin/bash

for format in png svg tikz ipe; do

	DIR="test/frechet/$format"
	mkdir -p $DIR

	./scripts/livecg-create-image -input res/presets/frechet/Paper.geom \
		-output "$DIR/freespace1.$format" -output_format "$format" -visualization freespace

	./scripts/livecg-create-image -input res/presets/frechet/Paper.geom \
		-Dreachable-markers=true -Dreachable-space=true -Dfreespace-markers=true \
		-output "$DIR/freespace2.$format" -output_format "$format" -visualization freespace

done

if [ -d "test/frechet/ipe" ]; then
for file in "test/frechet/ipe/"*.ipe; do
	echo $file
	ipetoipe -pdf "$file"
done
fi

if [ -d "test/frechet/tikz" ]; then
for file in "test/frechet/tikz/"*.tikz; do
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
fi

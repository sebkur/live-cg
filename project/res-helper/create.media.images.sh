#!/bin/bash

IN="../res/images/source/oxygen-48/*.png"
OUT="../res/images/24x24"

for f in $IN; do
	b=$(basename "$f")
	echo "$f -> $OUT/$b"
	./resize.sh "$f" 24 "$OUT/$b"
done

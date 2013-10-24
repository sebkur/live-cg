#!/bin/bash
for f in *.svg; do
	base=$(basename "$f" .svg)
	pdf="$base.pdf"
	echo "$f -> $pdf"
	inkscape -A "$pdf" "$f"
done

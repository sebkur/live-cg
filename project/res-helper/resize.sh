#!/bin/bash

if [ "$#" -ne "3" ]; then                                                              
        echo "usage: $0 <input> <size> <output>"                           
        exit 1                                                                         
fi

convert "$1" -resize "$2" "$3"

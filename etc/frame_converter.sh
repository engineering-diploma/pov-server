#!/bin/bash

echo "Converting file $1"
echo "Converter home set to $CONVERTER_HOME"
cd $CONVERTER_HOME

if [ ! -d $CONVERTER_HOME/venv ]; then
  echo "No venv found in converter home directory. Creating new venv"
  virtualenv -p python3 venv
  . venv/bin/activate
  pip3 install -r requirements.txt
else
  echo "Found existing venv in converter home directory. Using this one"
  . venv/bin/activate
fi

python3 frame_converter.py "$1" "$2" "$3" "$4" "$5"
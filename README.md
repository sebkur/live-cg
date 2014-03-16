LiveCG
=======

LIVE Interactive Visualization Environment for Computational Geometry

This project aims to create a system for interactive visualization of 
algorithms from computational geometry.

# Building and running
## Building
Compiling:
`ant compile`

Building the distribution:
`ant dist`

## Running
Running from build directory:
`./scripts/livecg-ui`
`./scripts/livecg-create-image`
`./scripts/livecg-visualization`

Running from distribution:
`java -jar dist/livecg-ui.jar`
`java -jar dist/livecg-create-image.jar`
`java -jar dist/livecg-visualization.jar`

# Editor
## Key Bindings
### Main
File:

* `Ctrl + N`: New document
* `Ctrl + O`: Open document
* `Ctrl + S`: Save document
* `Ctrl + Q`: Quit

Tools:  

* `q`: select/move mode
* `w`: rotate mode
* `e`: scale mode
* `a`: rectangular selection mode
* `s`: add mode
* `d`: delete mode

Edit:
  
* `Ctrl + A`: Select all objects
* `Ctrl + Shift + A`: Select nothing

### Move mode
* `Ctrl`: while dragging nodes, snap to other nodes

### Add mode
* `left mouse button`: add a node
* `Ctrl + left mouse button`: close a ring

### With selected object
* `Ctrl + Shift + 'o'`: open / close a ring

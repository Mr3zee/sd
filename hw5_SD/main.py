from edges_graph import EdgesGraph
from matrix_graph import MatrixGraph
from pyglet_api import PygletDrawingApi
from pygame_api import PyGameDrawingApi

import sys


def main():
    api_arg = sys.argv[1]
    graph_arg = sys.argv[2]

    if api_arg == 'pyglet':
        api = PygletDrawingApi()
    elif api_arg == 'pygame':
        api = PyGameDrawingApi()
    else:
        raise Exception("invalid api_arg")

    if graph_arg == 'matrix':
        graph = MatrixGraph(api, "matrix.txt")
    elif graph_arg == 'edges':
        graph = EdgesGraph(api, "edges.txt")
    else:
        raise Exception("invalid graph_arg")

    graph.draw_graph()


if __name__ == '__main__':
    main()

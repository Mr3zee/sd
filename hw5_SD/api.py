from abc import ABC
from math import cos, sin, pi
from typing import List, Tuple


# (0, 0) x/w --->
# y/h
# |
# |
# v
class DrawingApi(ABC):
    black = (0, 0, 0)
    white = (255, 255, 255)

    def get_drawing_area_width(self) -> int:
        pass

    def get_drawing_area_height(self) -> int:
        pass

    def draw_circle(self, radius: float, x: float, y: float, i: int):
        pass

    def draw_line(self, x1: float, y1: float, x2: float, y2: float):
        pass

    def draw(self):
        pass


class Graph(ABC):
    def __init__(self, api: DrawingApi, filepath: str):
        self.w = api.get_drawing_area_width()
        self.h = api.get_drawing_area_height()
        self.api = api

        self.vertex_radius = 0
        self.vertexes_coordinates = []  # type: List[Tuple[float, float]]

        with open(filepath, "r") as file:
            lines = file.readlines()

        self.n = int(lines[0])
        self.edges = self.parse_file(self.n, lines[1:])

        self.calc_drawing_points(self.n)

    def parse_file(self, n: int, content: List[str]) -> List[Tuple[int, int]]:
        pass

    def calc_drawing_points(self, n: int):
        length = min(self.w, self.h)

        x_offset = (self.w - length) + length / 2.0
        y_offset = (self.h - length) + length / 2.0

        radius = length * 0.4
        angle = 2 * pi / n

        self.vertexes_coordinates = [
            (cos(angle * i) * radius + x_offset, sin(angle * i) * radius + y_offset)
            for i in range(n)
        ]

        self.vertex_radius = length * 0.05

    def draw_graph(self):
        # draw vertexes
        for i, (x, y) in enumerate(self.vertexes_coordinates):
            self.api.draw_circle(self.vertex_radius, x, y, i)

        for (i, j) in self.edges:
            (x1, y1) = self.vertexes_coordinates[i]
            (x2, y2) = self.vertexes_coordinates[j]
            self.api.draw_line(x1, y1, x2, y2)

        self.api.draw()

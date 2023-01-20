from typing import List, Tuple

from api import Graph


class MatrixGraph(Graph):
    def parse_file(self, n: int, content: List[str]) -> List[Tuple[int, int]]:
        edges = []  # type: List[Tuple[int, int]]
        for i in range(n):
            vals = list(map(lambda x: int(x), content[i].split(" ")))
            for j in range(n):
                if vals[j] == 1:
                    edges.append((i, j))
        return edges

from typing import List, Tuple

from api import Graph


class EdgesGraph(Graph):
    def parse_file(self, n: int, content: List[str]) -> List[Tuple[int, int]]:
        mapped = [list(map(lambda x: int(x), s.split(" "))) for s in content]
        return [(entry[0], entry[1]) for entry in mapped]

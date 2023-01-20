import sys
import pygame

from api import DrawingApi


class PyGameDrawingApi(DrawingApi):
    def __init__(self):
        w = self.get_drawing_area_width()
        h = self.get_drawing_area_height()
        self.screen = pygame.display.set_mode((w, h))
        self.screen.fill(self.white)
        pygame.init()
        self.font = pygame.font.Font(None, int(h * 0.05))

    def draw(self):
        pygame.display.flip()

        while True:
            for events in pygame.event.get():
                if events.type == pygame.QUIT:
                    sys.exit(0)

    def get_drawing_area_width(self) -> int:
        return 1024

    def get_drawing_area_height(self) -> int:
        return 1024

    def draw_circle(self, radius: float, x: float, y: float, i: int):
        pygame.draw.circle(self.screen, self.black, (x, y), radius)
        text = self.font.render(f'{i}', True, self.white)
        self.screen.blit(text, (x, y))

    def draw_line(self, x1: float, y1: float, x2: float, y2: float):
        pygame.draw.line(self.screen, self.black, (x1, y1), (x2, y2))
